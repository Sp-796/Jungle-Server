package server.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.message.Message;
import common.message.MessageType;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.logic.MatchManager;
import server.network.udp.UdpProtocol;

/**
 * Prueba de integracion del transporte UDP real.
 *
 * Se levanta {@link NetworkServer} en un puerto efimero y se habla con el
 * usando datagramas: handshake, ida y vuelta de acciones, flujo de lobby y
 * retransmision cuando el ACK no llega.
 */
class UdpNetworkTest {

    private MatchManager matchManager;
    private NetworkServer server;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        matchManager = MatchManager.getInstance();
        matchManager.resetForTesting();

        MessageRouterServer router = new MessageRouterServer(new MessageHandler(matchManager));
        server = new NetworkServer(router);
        server.startServer(0);
        port = server.getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
        matchManager.resetForTesting();
    }

    @Test
    void flujoCompletoDeLobbyPorUdp() throws Exception {
        try (Peer first = new Peer(port)) {
            first.send(new Message(MessageType.CREATE_MATCH).put("nickname", "Ana"));

            Message created = first.await(3);
            assertEquals(MessageType.MATCH_CREATED, created.getType());
            assertEquals("Ana", created.getString("playerName"));

            String code = created.getString("matchCode");
            assertEquals(4, code.length());

            try (Peer second = new Peer(port)) {
                second.send(new Message(MessageType.JOIN_MATCH)
                        .put("nickname", "Beto")
                        .put("code", code));

                assertEquals(MessageType.MATCH_JOINED, first.await(3).getType());
                assertEquals(MessageType.MATCH_JOINED, second.await(3).getType());

                first.send(new Message(MessageType.SELECT_ANIMAL).put("species", "jaguar"));

                assertEquals(MessageType.ANIMAL_SELECTION, first.await(3).getType());
                assertEquals(MessageType.ANIMAL_SELECTION, second.await(3).getType());

                second.send(new Message(MessageType.SELECT_ANIMAL).put("species", "mono"));

                Message started = first.await(3);
                assertEquals(MessageType.GAME_STARTED, started.getType());
                assertEquals(12, started.getPayload().getAsJsonArray("zones").size());
                assertEquals(MessageType.GAME_STARTED, second.await(3).getType());
            }
        }
    }

    @Test
    void retransmiteElMensajeSiElClienteNoConfirma() throws Exception {
        try (Peer peer = new Peer(port)) {
            peer.ackEnabled = false;
            peer.send(new Message(MessageType.CREATE_MATCH).put("nickname", "Ana"));

            Message first = peer.await(3);
            Message retransmitted = peer.await(3);

            assertEquals(MessageType.MATCH_CREATED, first.getType());
            assertEquals(first.toJson(), retransmitted.toJson());

            peer.ackEnabled = true;
        }
    }

    /**
     * Cliente UDP minimo para las pruebas.
     */
    private static final class Peer implements AutoCloseable {

        private final DatagramSocket socket;
        private final InetSocketAddress serverAddress;
        private final BlockingQueue<Message> inbox = new LinkedBlockingQueue<>();
        private final AtomicReference<Throwable> failure = new AtomicReference<>();
        private final Thread receiver;

        private int nextSequence;
        private int expectedSequence;
        private volatile boolean ackEnabled = true;
        private volatile boolean closed;

        private Peer(int port) throws IOException {
            this.socket = new DatagramSocket();
            this.serverAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
            this.socket.connect(serverAddress);
            this.socket.setSoTimeout(200);
            handshake();

            receiver = new Thread(this::receiveLoop, "udp-test-peer");
            receiver.setDaemon(true);
            receiver.start();
        }

        private void handshake() throws IOException {
            byte[] hello = UdpProtocol.encode(
                    UdpProtocol.HELLO,
                    0,
                    0,
                    UdpProtocol.encodeLong(ThreadLocalRandom.current().nextLong()));

            long deadline = System.nanoTime() + 3_000_000_000L;
            byte[] buffer = new byte[UdpProtocol.MAX_DATAGRAM_SIZE];

            while (System.nanoTime() < deadline) {
                sendRaw(hello);
                try {
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);
                    UdpProtocol.Packet packet = UdpProtocol.decode(
                            response.getData(), response.getLength());
                    if (packet != null && packet.type() == UdpProtocol.HELLO_ACK) {
                        return;
                    }
                } catch (SocketTimeoutException timeout) {
                    // Reintentar.
                }
            }

            throw new IOException("El servidor de prueba no respondio al HELLO");
        }

        private void receiveLoop() {
            byte[] buffer = new byte[UdpProtocol.MAX_DATAGRAM_SIZE];

            while (!closed) {
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(datagram);
                    UdpProtocol.Packet packet = UdpProtocol.decode(
                            datagram.getData(), datagram.getLength());
                    if (packet == null) {
                        continue;
                    }

                    switch (packet.type()) {
                        case UdpProtocol.DATA -> {
                            if (packet.sequence() == expectedSequence) {
                                expectedSequence++;
                            }
                            if (ackEnabled) {
                                sendRaw(UdpProtocol.encode(
                                        UdpProtocol.ACK, 0, expectedSequence - 1, new byte[0]));
                            }
                            inbox.offer(Message.fromJson(new String(packet.payload(), 1, packet.payload().length - 1, StandardCharsets.UTF_8)));
                        }
                        case UdpProtocol.PING -> sendRaw(UdpProtocol.encode(
                                UdpProtocol.PONG, 0, 0, new byte[0]));
                        default -> {
                            // ACK y control: no hacen falta en el test.
                        }
                    }
                } catch (SocketTimeoutException timeout) {
                    // Normal.
                } catch (IOException | RuntimeException error) {
                    if (!closed) {
                        failure.set(error);
                    }
                    return;
                }
            }
        }

        private void send(Message message) throws IOException {
            byte[] json = message.toJson().getBytes(StandardCharsets.UTF_8);
            byte[] payload = new byte[json.length + 1];
            payload[0] = 1; // ultimo (y unico) trozo
            System.arraycopy(json, 0, payload, 1, json.length);
            sendRaw(UdpProtocol.encode(UdpProtocol.DATA, nextSequence++, 0, payload));
        }

        private void sendRaw(byte[] datagram) throws IOException {
            socket.send(new DatagramPacket(datagram, datagram.length));
        }

        private Message await(int seconds) throws InterruptedException {
            Message message = inbox.poll(seconds, TimeUnit.SECONDS);
            assertNotNull(message, () -> "No llego el mensaje UDP: " + failure.get());
            assertTrue(failure.get() == null, () -> "Fallo el peer UDP: " + failure.get());
            return message;
        }

        @Override
        public void close() {
            closed = true;
            try {
                sendRaw(UdpProtocol.encode(UdpProtocol.BYE, 0, 0, new byte[0]));
            } catch (IOException ignored) {
                // Se esta cerrando.
            }
            socket.close();
            try {
                receiver.join(1000);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
