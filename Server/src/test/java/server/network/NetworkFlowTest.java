package server.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.message.Message;
import common.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.logic.MatchManager;

/**
 * Prueba de integracion de la capa de red (sin transporte).
 *
 * {@link ClientHandler} trabaja contra {@link ClientChannel}, asi que aqui se
 * usa un canal en memoria: se comprueba el flujo completo crear, unir y elegir
 * especie sin abrir sockets. El transporte UDP real se prueba en
 * {@code UdpNetworkTest}.
 */
class NetworkFlowTest {

    private final List<Connection> connections = new ArrayList<>();

    private MatchManager matchManager;
    private MessageRouterServer router;

    @BeforeEach
    void setUp() {
        matchManager = MatchManager.getInstance();
        matchManager.resetForTesting();
        router = new MessageRouterServer(new MessageHandler(matchManager));
    }

    @AfterEach
    void tearDown() {
        for (Connection connection : connections) {
            connection.close();
        }
        connections.clear();
        matchManager.resetForTesting();
    }

    @Test
    void flujoCompletoDeLobbySeleccionEInicio() throws Exception {
        Connection first = open();
        router.routeMessage(first.handler, json(MessageType.CREATE_MATCH, "nickname", "Ana"));

        Message created = read(first);
        assertEquals(MessageType.MATCH_CREATED, created.getType());
        assertEquals("Ana", created.getString("playerName"));
        assertEquals(1, created.getInt("playerNumber", 0));

        String code = created.getString("matchCode");
        assertEquals(4, code.length());

        Connection second = open();
        router.routeMessage(second.handler,
                new Message(MessageType.JOIN_MATCH)
                        .put("nickname", "Beto")
                        .put("code", code.toLowerCase())
                        .toJson());

        Message firstJoined = read(first);
        Message secondJoined = read(second);

        assertEquals(MessageType.MATCH_JOINED, firstJoined.getType());
        assertEquals("Beto", firstJoined.getString("opponentName"));
        assertEquals(2, secondJoined.getInt("playerNumber", 0));
        assertEquals(7, secondJoined.getArray("availableSpecies").size());

        router.routeMessage(first.handler, json(MessageType.SELECT_ANIMAL, "species", "jaguar"));

        Message firstSelection = read(first);
        Message secondSeesSelection = read(second);

        assertEquals(MessageType.ANIMAL_SELECTION, firstSelection.getType());
        assertEquals("jaguar", firstSelection.getString("yourSpecies"));
        assertEquals(MessageType.ANIMAL_SELECTION, secondSeesSelection.getType());
        assertEquals(6, secondSeesSelection.getArray("availableSpecies").size());

        router.routeMessage(second.handler, json(MessageType.SELECT_ANIMAL, "species", "mono"));

        Message firstStarted = read(first);
        Message secondStarted = read(second);

        assertEquals(MessageType.GAME_STARTED, firstStarted.getType());
        assertEquals(MessageType.GAME_STARTED, secondStarted.getType());
        assertEquals(12, firstStarted.getPayload().getAsJsonArray("zones").size());
        assertEquals(10, firstStarted.getPayload()
                .getAsJsonObject("pack").getAsJsonArray("animals").size());
    }

    @Test
    void unMensajeDesconocidoRespondeConError() throws Exception {
        Connection connection = open();

        router.routeMessage(connection.handler, new Message("VOLAR").toJson());

        Message error = read(connection);

        assertEquals(MessageType.ERROR, error.getType());
        assertTrue(error.getString("message").contains("VOLAR"));
    }

    @Test
    void unCodigoInexistenteRespondeConError() throws Exception {
        Connection connection = open();

        router.routeMessage(connection.handler,
                new Message(MessageType.JOIN_MATCH)
                        .put("nickname", "Ana")
                        .put("code", "ZZZZ")
                        .toJson());

        Message error = read(connection);

        assertEquals(MessageType.ERROR, error.getType());
        assertTrue(error.getString("message").contains("ZZZZ"));
    }

    @Test
    void unNicknameVacioRespondeConError() throws Exception {
        Connection connection = open();

        router.routeMessage(connection.handler,
                new Message(MessageType.CREATE_MATCH).put("nickname", "   ").toJson());

        Message error = read(connection);

        assertEquals(MessageType.ERROR, error.getType());
        assertTrue(error.getString("message").contains("nickname"));
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private Connection open() {
        Connection connection = new Connection(router);
        connections.add(connection);
        return connection;
    }

    private String json(String type, String key, String value) {
        return new Message(type).put(key, value).toJson();
    }

    private Message read(Connection connection) throws InterruptedException {
        String line = connection.outbox.poll(3, TimeUnit.SECONDS);
        assertNotNull(line, "El servidor no envio ninguna respuesta");
        return Message.fromJson(line);
    }

    /** Canal en memoria: lo que el handler escribe se lee desde el test. */
    private static final class Connection implements ClientChannel {

        private final ClientHandler handler;
        private final BlockingQueue<String> outbox = new LinkedBlockingQueue<>();

        private Connection(MessageRouterServer router) {
            this.handler = new ClientHandler(this, router);
        }

        @Override
        public String receive() {
            // El test llama directamente a router.routeMessage.
            return null;
        }

        @Override
        public boolean send(String message) {
            return outbox.offer(message);
        }

        @Override
        public void close() {
            // Nada que liberar.
        }

        @Override
        public String remoteAddress() {
            return "memoria";
        }
    }
}
