// Paquete de red del servidor: contiene el servidor UDP.
package server.network;

// Importa las constantes del juego.
import constants.IConstants;
// Importa IOException para errores de IO.
import java.io.IOException;
// Importa DatagramPacket para datagramas.
import java.net.DatagramPacket;
// Importa DatagramSocket para el socket.
import java.net.DatagramSocket;
// Importa InetSocketAddress para direcciones.
import java.net.InetSocketAddress;
// Importa ArrayList para listas.
import java.util.ArrayList;
// Importa List para listas.
import java.util.List;
// Importa Map para mapas.
import java.util.Map;
// Importa ConcurrentHashMap para el mapa de conexiones.
import java.util.concurrent.ConcurrentHashMap;
// Importa ExecutorService para el pool.
import java.util.concurrent.ExecutorService;
// Importa Executors para crear pools.
import java.util.concurrent.Executors;
// Importa ScheduledExecutorService para tareas periodicas.
import java.util.concurrent.ScheduledExecutorService;
// Importa TimeUnit para unidades de tiempo.
import java.util.concurrent.TimeUnit;
// Importa UdpConnection del subpaquete.
import server.network.udp.UdpConnection;
// Importa UdpProtocol del subpaquete.
import server.network.udp.UdpProtocol;

/**
 * Servidor UDP.
 *
 * Un unico hilo recibe todos los datagramas y los reparte entre las conexiones
 * (una por direccion remota). Cada conexion se atiende en un hilo del pool,
 * igual que antes, para que un cliente lento no bloquee a los demas.
 *
 * Sobre UDP se implementa un canal confiable con secuencia, ACK, retransmision
 * y deteccion de inactividad; ver {@link server.network.udp.ReliableUdpChannel}.
 */
// Clase que implementa el servidor UDP.
public class NetworkServer {

    // Enrutador de mensajes.
    private final MessageRouterServer router;
    // Pool de hilos para clientes.
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    // Ejecutor periodico para mantenimiento.
    private final ScheduledExecutorService housekeeping =
            Executors.newSingleThreadScheduledExecutor();
    // Mapa de conexiones por direccion.
    private final Map<InetSocketAddress, UdpConnection> connections = new ConcurrentHashMap<>();

    // Socket UDP.
    private DatagramSocket socket;
    // Indica si esta corriendo.
    private volatile boolean running;
    // Puerto del servidor.
    private volatile int port = -1;
    // Hilo de recepcion.
    private Thread receiveThread;

    // Constructor.
    public NetworkServer(MessageRouterServer router) {
        // Asigna el enrutador.
        this.router = router;
    }

    // Inicia el servidor.
    public void startServer(int port) throws IOException {
        // Crea el socket.
        DatagramSocket newSocket = new DatagramSocket(null);
        // Permite reusar la direccion.
        newSocket.setReuseAddress(true);
        // Vincula el puerto.
        newSocket.bind(new InetSocketAddress(port));
        // Intenta aumentar el buffer.
        try {
            newSocket.setReceiveBufferSize(IConstants.UDP_RECEIVE_BUFFER_SIZE);
        } catch (IOException ignored) {
            // El sistema puede limitar el buffer; no es fatal.
        }

        // Asigna el socket.
        this.socket = newSocket;
        // Asigna el puerto real.
        this.port = newSocket.getLocalPort();
        // Marca como corriendo.
        this.running = true;

        // Crea el hilo de recepcion.
        receiveThread = new Thread(this::receiveLoop, "udp-receive");
        // Inicia el hilo.
        receiveThread.start();

        // Programa el mantenimiento periodico.
        housekeeping.scheduleAtFixedRate(
                this::housekeepingTick,
                IConstants.UDP_HOUSEKEEPING_MS,
                IConstants.UDP_HOUSEKEEPING_MS,
                TimeUnit.MILLISECONDS);

        // Imprime el mensaje.
        System.out.println("Servidor UDP escuchando en el puerto " + this.port);
    }

    // Bucle de recepcion.
    private void receiveLoop() {
        // Buffer de recepcion.
        byte[] buffer = new byte[65535];

        // Mientras este corriendo.
        while (running) {
            // Crea el datagrama.
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            // Recibe.
            try {
                socket.receive(datagram);
            } catch (IOException error) {
                if (running) {
                    System.err.println("Error recibiendo datagrama: " + error.getMessage());
                }
                continue;
            }

            // Decodifica el paquete.
            UdpProtocol.Packet packet = UdpProtocol.decode(
                    datagram.getData(), datagram.getLength());
            // Si no es valido, salta.
            if (packet == null) {
                continue;
            }

            // Obtiene la direccion remota.
            InetSocketAddress from = (InetSocketAddress) datagram.getSocketAddress();

            // Si es HELLO, maneja el handshake.
            if (packet.type() == UdpProtocol.HELLO) {
                handleHello(from, packet);
                continue;
            }

            // Busca la conexion.
            UdpConnection connection = connections.get(from);
            // Si no existe, salta.
            if (connection == null) {
                continue;
            }

            // Si es BYE, cierra la conexion.
            if (packet.type() == UdpProtocol.BYE) {
                connection.close();
                continue;
            }

            // Procesa el paquete.
            connection.onPacket(packet, System.nanoTime());
        }
    }

    // Maneja el handshake HELLO.
    private void handleHello(InetSocketAddress from, UdpProtocol.Packet packet) {
        // Decodifica el id de sesion.
        long sessionId = UdpProtocol.decodeLong(packet.payload());

        // Busca la conexion existente.
        UdpConnection existing = connections.get(from);
        // Si existe y tiene el mismo id, repite el ACK.
        if (existing != null && existing.getSessionId() == sessionId) {
            existing.acceptHandshake();
            return;
        }

        // Si existe pero con otro id, la cierra.
        if (existing != null) {
            existing.close();
        }

        // Crea la nueva conexion.
        UdpConnection created = new UdpConnection(socket, from, sessionId);
        final UdpConnection connection = created;
        // La guarda en el mapa.
        connections.put(from, connection);
        // Asigna la accion de cierre.
        connection.setCloseAction(() -> connections.remove(from, connection));

        // Imprime el mensaje.
        System.out.println("Cliente conectado desde " + from);
        // Acepta el handshake.
        connection.acceptHandshake();

        // Ejecuta el ClientHandler.
        clientPool.execute(new ClientHandler(connection, router));
    }

    // Tarea periodica de mantenimiento.
    private void housekeepingTick() {
        // Intenta ejecutar.
        try {
            // Tiempo actual.
            long now = System.nanoTime();
            // Timeout en nanos.
            long timeoutNanos = IConstants.UDP_CLIENT_TIMEOUT_MS * 1_000_000L;

            // Recorre las conexiones.
            for (UdpConnection connection : connections.values()) {
                // Tick de la conexion.
                connection.tick(now);
                // Si esta inactiva, la cierra.
                if (now - connection.lastReceivedNanos() > timeoutNanos) {
                    System.out.println("Cliente inactivo: " + connection.remoteAddress());
                    connection.close();
                }
            }
        } catch (RuntimeException error) {
            // Una excepcion no cancela el scheduler.
            System.err.println("Error en mantenimiento UDP: " + error.getMessage());
        }
    }

    // Detiene el servidor.
    public void stop() {
        // Marca como no corriendo.
        running = false;

        // Cierra el socket.
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (RuntimeException ignored) {
            // Se esta cerrando.
        }

        // Cierra las conexiones.
        List<UdpConnection> snapshot = new ArrayList<>(connections.values());
        for (UdpConnection connection : snapshot) {
            connection.close();
        }
        // Limpia el mapa.
        connections.clear();

        // Apaga los ejecutores.
        housekeeping.shutdownNow();
        clientPool.shutdownNow();
    }

    // Getter del puerto.
    public int getPort() {
        // Retorna port.
        return port;
    }

    // Indica si esta corriendo.
    public boolean isRunning() {
        // Retorna running.
        return running;
    }
}