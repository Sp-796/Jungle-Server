// Paquete de red UDP del servidor: contiene el canal confiable.
package server.network.udp;

// Importa las constantes del juego.
import constants.IConstants;
// Importa IOException para errores de IO.
import java.io.ByteArrayOutputStream;
import java.io.IOException;
// Importa StandardCharsets para UTF-8.
import java.nio.charset.StandardCharsets;
// Importa Iterator para iterar.
import java.util.Iterator;
// Importa LinkedHashMap para el mapa de pendientes.
import java.util.LinkedHashMap;
// Importa Map para mapas.
import java.util.Map;
// Importa TreeMap para el buffer ordenado.
import java.util.TreeMap;
// Importa Consumer para el receptor.
import java.util.function.Consumer;

/**
 * Canal UDP confiable y ordenado, extremo a extremo.
 *
 * UDP no garantiza entrega, orden ni ausencia de duplicados. Esta clase agrega
 * lo minimo necesario para el protocolo del juego:
 *
 *  - numeracion de secuencia por sentido;
 *  - ACK acumulativo (el receptor confirma hasta el ultimo seq en orden);
 *  - retransmision periodica de lo no confirmado;
 *  - reensamblado en orden con un buffer acotado para los que llegan
 *    adelantados;
 *  - descarte de duplicados.
 *
 * La entrega al consumidor es en orden. Si la ventana se llena (peer caido o
 * extremadamente lento) se descartan los mensajes nuevos: en este juego el
 * estado mas reciente reemplaza al anterior.
 */
// Clase final que implementa un canal UDP confiable.
final class ReliableUdpChannel {

    // Interfaz funcional para enviar datagramas.
    @FunctionalInterface
    interface DatagramSink {
        // Envia un datagrama.
        void send(byte[] datagram) throws IOException;
    }

    // Payload vacio.
    private static final byte[] EMPTY = new byte[0];

    // Bytes de mensaje por datagrama. Se mantiene bajo el MTU de Internet
    // (~1500) para que el datagrama nunca se fragmente a nivel IP: los
    // tuneles (playit.gg) y muchos routers descartan los datagramas
    // fragmentados, y el mensaje nunca llegaba al otro extremo.
    private static final int MAX_CHUNK = 1000;
    // Marca: este trozo continua en el siguiente DATA.
    private static final byte CHUNK_MORE = 0;
    // Marca: este es el ultimo trozo del mensaje.
    private static final byte CHUNK_LAST = 1;

    // Clase interna para mensajes pendientes de ACK.
    private static final class Pending {

        // Payload del mensaje.
        private final byte[] payload;
        // Momento del ultimo envio.
        private long lastSentNanos;
        // Intentos de envio.
        private int attempts;

        // Constructor.
        private Pending(byte[] payload, long now) {
            // Asigna el payload.
            this.payload = payload;
            // Asigna el momento.
            this.lastSentNanos = now;
        }
    }

    // Sink para enviar datagramas.
    private final DatagramSink sink;
    // Lock para sincronizacion.
    private final Object lock = new Object();
    // Mapa de mensajes pendientes de ACK.
    private final Map<Integer, Pending> pending = new LinkedHashMap<>();
    // Buffer de mensajes futuros (ordenado).
    private final TreeMap<Integer, byte[]> future = new TreeMap<>();
    // Trozos del mensaje que se esta reensamblando.
    private final ByteArrayOutputStream assembly = new ByteArrayOutputStream();

    // Receptor de mensajes.
    private Consumer<String> receiver = message -> {
    };

    // Siguiente numero de secuencia a enviar.
    private int nextSequence;
    // Numero de secuencia esperado.
    private int expectedSequence;
    // Momento del ultimo mensaje recibido.
    private volatile long lastReceivedNanos = System.nanoTime();
    // Indica si esta cerrado.
    private volatile boolean closed;

    // Constructor.
    ReliableUdpChannel(DatagramSink sink) {
        // Asigna el sink.
        this.sink = sink;
    }

    // Asigna el receptor.
    void setReceiver(Consumer<String> receiver) {
        // Asigna el receptor o uno vacio.
        this.receiver = receiver == null ? message -> {
        } : receiver;
    }

    // Retorna el momento del ultimo mensaje recibido.
    long lastReceivedNanos() {
        // Retorna lastReceivedNanos.
        return lastReceivedNanos;
    }

    // Envia un mensaje JSON de forma confiable.
    boolean send(String text) {
        // Si es nulo, retorna false.
        if (text == null) {
            return false;
        }

        // Convierte a bytes.
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        if (payload.length > UdpProtocol.MAX_DATAGRAM_SIZE - UdpProtocol.HEADER_SIZE) {
            return false;
        }

        // Cantidad de trozos necesarios (minimo 1).
        int chunks = Math.max(1, (payload.length + MAX_CHUNK - 1) / MAX_CHUNK);

        synchronized (lock) {
            if (closed || pending.size() + chunks > IConstants.UDP_WINDOW_SIZE) {
                return false;
            }

            for (int i = 0; i < chunks; i++) {
                int from = i * MAX_CHUNK;
                int to = Math.min(payload.length, from + MAX_CHUNK);
                byte[] body = new byte[1 + (to - from)];
                body[0] = i == chunks - 1 ? CHUNK_LAST : CHUNK_MORE;
                System.arraycopy(payload, from, body, 1, to - from);

                int sequence = nextSequence++;
                pending.put(sequence, new Pending(body, System.nanoTime()));
                transmit(sequence, body);
            }
            return true;
        }
    }

    // Envia un datagrama de control.
    void sendControl(byte type) {
        // Seccion critica.
        synchronized (lock) {
            // Si no esta cerrado, envia.
            if (!closed) {
                sendRaw(UdpProtocol.encode(type, 0, 0, EMPTY));
            }
        }
    }

    // Procesa un datagrama recibido.
    void onPacket(UdpProtocol.Packet packet, long now) {
        // Seccion critica.
        synchronized (lock) {
            // Si esta cerrado o el paquete es nulo, no hace nada.
            if (closed || packet == null) {
                return;
            }

            // Actualiza el momento de recepcion.
            lastReceivedNanos = now;

            // Enruta segun el tipo.
            switch (packet.type()) {
                case UdpProtocol.DATA -> onData(packet);
                case UdpProtocol.ACK -> onAck(packet.acknowledgement());
                case UdpProtocol.PING -> sendRaw(UdpProtocol.encode(UdpProtocol.PONG, 0, 0, EMPTY));
                default -> {
                    // PONG, HELLO, HELLO_ACK y BYE los maneja la capa superior.
                }
            }
        }
    }

    // Reenvia lo que siga sin ACK.
    void tick(long now) {
        // Seccion critica.
        synchronized (lock) {
            // Si esta cerrado o no hay pendientes, no hace nada.
            if (closed || pending.isEmpty()) {
                return;
            }

            // Tiempo de retransmision.
            long retransmitNanos = IConstants.UDP_RETRANSMIT_MS * 1_000_000L;
            // Contador de reenviados.
            int resent = 0;

            // Recorre los pendientes.
            for (Map.Entry<Integer, Pending> entry : pending.entrySet()) {
                // Obtiene el item.
                Pending item = entry.getValue();
                // Si se alcanzo el maximo, sale.
                if (resent >= IConstants.UDP_MAX_RETRANSMITS_PER_TICK) {
                    break;
                }
                // Si paso el tiempo, retransmite.
                if (now - item.lastSentNanos >= retransmitNanos) {
                    transmit(entry.getKey(), item.payload);
                    item.lastSentNanos = now;
                    item.attempts++;
                    resent++;
                }
            }
        }
    }

    // Cierra el canal.
    void close() {
        // Seccion critica.
        synchronized (lock) {
            // Marca como cerrado.
            closed = true;
            // Limpia los pendientes.
            pending.clear();
            // Limpia el buffer futuro.
            future.clear();
            assembly.reset();
        }
    }

    // Procesa un paquete DATA.
    private void onData(UdpProtocol.Packet packet) {
        int sequence = packet.sequence();

        if (sequence == expectedSequence) {
            acceptChunk(packet.payload());
            expectedSequence++;

            byte[] buffered;
            while ((buffered = future.remove(expectedSequence)) != null) {
                acceptChunk(buffered);
                expectedSequence++;
            }
        } else if (sequence > expectedSequence
                && future.size() < IConstants.UDP_MAX_BUFFERED_MESSAGES
                && !future.containsKey(sequence)) {
            future.put(sequence, packet.payload());
        }

        sendRaw(UdpProtocol.encode(UdpProtocol.ACK, 0, expectedSequence - 1, EMPTY));
    }

    // Agrega un trozo; al llegar el ultimo entrega el mensaje completo.
    private void acceptChunk(byte[] body) {
        if (body == null || body.length == 0) {
            return;
        }
        assembly.write(body, 1, body.length - 1);
        if (body[0] == CHUNK_LAST) {
            String message = new String(assembly.toByteArray(), StandardCharsets.UTF_8);
            assembly.reset();
            deliver(message);
        }
    }

    // Procesa un ACK.
    private void onAck(int acknowledgement) {
        // Iterador de pendientes.
        Iterator<Map.Entry<Integer, Pending>> iterator = pending.entrySet().iterator();
        // Remueve los confirmados.
        while (iterator.hasNext()) {
            if (iterator.next().getKey() <= acknowledgement) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    // Entrega un mensaje al receptor.
    private void deliver(String message) {
        // Intenta entregar.
        try {
            receiver.accept(message);
        } catch (RuntimeException error) {
            // Un consumidor defectuoso no tumba el hilo.
            System.err.println("Error entregando mensaje UDP: " + error.getMessage());
        }
    }

    // Transmite un mensaje DATA.
    private void transmit(int sequence, byte[] payload) {
        // Envia el datagrama DATA.
        sendRaw(UdpProtocol.encode(UdpProtocol.DATA, sequence, 0, payload));
    }

    // Envia un datagrama crudo.
    private void sendRaw(byte[] datagram) {
        // Intenta enviar.
        try {
            sink.send(datagram);
        } catch (IOException error) {
            // Se reintentara en el siguiente tick.
        }
    }
}