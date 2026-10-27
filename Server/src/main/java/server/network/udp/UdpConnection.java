// Paquete de red UDP del servidor: contiene la conexion UDP con un cliente.
package server.network.udp;

// Importa DatagramPacket para datagramas.
import java.net.DatagramPacket;
// Importa DatagramSocket para el socket.
import java.net.DatagramSocket;
// Importa InetSocketAddress para la direccion remota.
import java.net.InetSocketAddress;
// Importa BlockingQueue para la cola de entrada.
import java.util.concurrent.BlockingQueue;
// Importa LinkedBlockingQueue para la cola.
import java.util.concurrent.LinkedBlockingQueue;
// Importa ClientChannel para implementar la interfaz.
import server.network.ClientChannel;

/**
 * Conexion UDP con un cliente concreto.
 *
 * Envuelve el {@link ReliableUdpChannel} y expone una cola bloqueante de lines
 * JSON, que es lo unico que necesita {@code ClientHandler}. El identificador
 * de sesion permite distinguir un cliente nuevo de un HELLO repetido cuando
 * NAT reutiliza la misma direccion y puerto.
 */
// Clase final que implementa ClientChannel sobre UDP.
public final class UdpConnection implements ClientChannel {

    // Marcador de cierre.
    private static final String CLOSED = new String("\u0000closed");

    // Direccion remota.
    private final InetSocketAddress remote;
    // Id de sesion.
    private final long sessionId;
    // Canal confiable.
    private final ReliableUdpChannel channel;
    // Cola de entrada de mensajes.
    private final BlockingQueue<String> inbox = new LinkedBlockingQueue<>();

    // Indica si esta cerrado.
    private volatile boolean closed;
    // Accion a ejecutar al cerrar.
    private volatile Runnable closeAction = () -> {
    };

    // Constructor.
    public UdpConnection(DatagramSocket socket, InetSocketAddress remote, long sessionId) {
        // Asigna la direccion remota.
        this.remote = remote;
        // Asigna el id de sesion.
        this.sessionId = sessionId;
        // Crea el canal confiable.
        this.channel = new ReliableUdpChannel(datagram -> {
            // Envia el datagrama.
            DatagramPacket packet = new DatagramPacket(datagram, datagram.length, remote);
            socket.send(packet);
        });
        // Asigna el receptor a la cola.
        this.channel.setReceiver(inbox::offer);
    }

    // Acepta el handshake.
    public void acceptHandshake() {
        // Envia HELLO_ACK.
        channel.sendControl(UdpProtocol.HELLO_ACK);
    }

    // Procesa un paquete recibido.
    public void onPacket(UdpProtocol.Packet packet, long now) {
        // Delega al canal.
        channel.onPacket(packet, now);
    }

    // Tick de mantenimiento.
    public void tick(long now) {
        // Delega al canal.
        channel.tick(now);
    }

    // Retorna el momento del ultimo mensaje recibido.
    public long lastReceivedNanos() {
        // Delega al canal.
        return channel.lastReceivedNanos();
    }

    // Retorna el id de sesion.
    public long getSessionId() {
        // Retorna sessionId.
        return sessionId;
    }

    // Asigna la accion de cierre.
    public void setCloseAction(Runnable closeAction) {
        // Asigna la accion o una vacia.
        this.closeAction = closeAction == null ? () -> {
        } : closeAction;
    }

    // Recibe el siguiente mensaje.
    @Override
    public String receive() {
        // Intenta tomar de la cola.
        try {
            // Toma el siguiente mensaje.
            String line = inbox.take();
            // Si es el marcador de cierre, retorna null.
            return line == CLOSED ? null : line;
        } catch (InterruptedException interrupted) {
            // Restaura el flag.
            Thread.currentThread().interrupt();
            // Retorna null.
            return null;
        }
    }

    // Envia un mensaje.
    @Override
    public boolean send(String message) {
        // Si no esta cerrado, delega al canal.
        return !closed && channel.send(message);
    }

    // Cierra la conexion.
    @Override
    public void close() {
        // Si ya esta cerrado, no hace nada.
        if (closed) {
            return;
        }
        // Marca como cerrado.
        closed = true;
        // Cierra el canal.
        channel.close();
        // Ofrece el marcador de cierre.
        inbox.offer(CLOSED);

        // Ejecuta la accion de cierre.
        Runnable action = closeAction;
        closeAction = () -> {
        };
        action.run();
    }

    // Retorna la direccion remota.
    @Override
    public String remoteAddress() {
        // Retorna la direccion.
        return remote.toString();
    }
}