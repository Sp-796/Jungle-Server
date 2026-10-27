// Paquete de red del servidor: contiene el manejador de mensajes del cliente.
package server.network;

// Importa JsonArray para listas.
import com.google.gson.JsonArray;
// Importa JsonElement para elementos.
import com.google.gson.JsonElement;
// Importa JsonObject para payloads.
import com.google.gson.JsonObject;
// Importa Message para el mensaje.
import common.message.Message;
// Importa MessageType para los tipos.
import common.message.MessageType;
// Importa ArrayList para listas.
import java.util.ArrayList;
// Importa List para listas.
import java.util.List;
// Importa UUID para ids de jugador.
import java.util.UUID;
// Importa GameSession para la partida.
import server.logic.GameSession;
// Importa MatchException para errores.
import server.logic.MatchException;
// Importa MatchManager para gestionar partidas.
import server.logic.MatchManager;
// Importa SnapshotBuilder para payloads.
import server.logic.SnapshotBuilder;
// Importa Player del modelo.
import server.model.Player;

/**
 * Ejecuta la logica de cada mensaje del cliente.
 *
 * No habla sockets directamente: recibe el ClientHandler, valida la peticion y
 * llama a los metodos de la partida. Los errores se lanzan como MatchException
 * y el router los convierte en mensajes ERROR.
 */
// Clase que maneja los mensajes recibidos del cliente.
public class MessageHandler {

    // Gestor de partidas.
    private final MatchManager matchManager;
    // Constructor de payloads.
    private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();

    // Constructor.
    public MessageHandler(MatchManager matchManager) {
        // Asigna el gestor.
        this.matchManager = matchManager;
    }

    // Crea una nueva partida.
    public void handleCreateMatch(ClientHandler client, Message message) {
        // Si ya tiene jugador, lanza excepcion.
        if (client.getPlayer() != null) {
            throw new MatchException("Ya estas dentro de una partida");
        }

        // Obtiene y valida el nickname.
        String nickname = requireNickname(message.getString("nickname"));
        // Crea el jugador.
        Player player = new Player(newPlayerId(), nickname);

        // Crea la partida.
        String code = matchManager.createMatch(player);
        // Obtiene la sesion.
        GameSession session = matchManager.getMatch(code);

        // Asocia el jugador a la conexion.
        client.attach(player, session);
        // Registra el oyente.
        session.registerListener(player.getParticipantId(), client);

        // Crea el payload.
        JsonObject payload = new JsonObject();
        payload.addProperty("matchCode", code);
        payload.addProperty("playerId", player.getParticipantId());
        payload.addProperty("playerName", nickname);
        payload.addProperty("playerNumber", 1);

        // Envia MATCH_CREATED.
        client.sendMessage(MessageType.MATCH_CREATED, payload);
    }

    // Une a una partida existente.
    public void handleJoinMatch(ClientHandler client, Message message) {
        // Si ya tiene jugador, lanza excepcion.
        if (client.getPlayer() != null) {
            throw new MatchException("Ya estas dentro de una partida");
        }

        // Obtiene y valida el nickname.
        String nickname = requireNickname(message.getString("nickname"));
        // Obtiene el codigo.
        String code = message.getString("code");

        // Si no hay codigo, lanza excepcion.
        if (code == null || code.isBlank()) {
            throw new MatchException("Debes indicar el codigo de la partida");
        }

        // Crea el jugador.
        Player player = new Player(newPlayerId(), nickname);
        // Se une a la partida.
        GameSession session = matchManager.joinMatch(code, player);

        // Asocia el jugador.
        client.attach(player, session);
        // Registra el oyente.
        session.registerListener(player.getParticipantId(), client);

        // Ambos reciben el estado del lobby.
        for (Player each : session.getPlayers()) {
            session.sendToPlayer(each.getParticipantId(), MessageType.MATCH_JOINED,
                    snapshotBuilder.matchJoined(session, each));
        }
    }

    // Selecciona la especie.
    public void handleSelectAnimal(ClientHandler client, Message message) {
        // Obtiene la sesion.
        GameSession session = requireSession(client);
        // Obtiene el jugador.
        Player player = requirePlayer(client);

        // Selecciona la especie.
        session.selectSpecies(player, message.getString("species"));

        // Si la partida no arranco, envia la seleccion.
        if (!session.isRunning()) {
            // Obtiene el oponente.
            Player opponent = session.getOpponent(player);

            // Envia al oponente.
            if (opponent != null) {
                session.sendToPlayer(opponent.getParticipantId(), MessageType.ANIMAL_SELECTION,
                        snapshotBuilder.animalSelection(session, opponent));
            }
            // Envia al jugador.
            session.sendToPlayer(player.getParticipantId(), MessageType.ANIMAL_SELECTION,
                    snapshotBuilder.animalSelection(session, player));
        }
    }

    // Mueve animales.
    public void handleMoveAnimals(ClientHandler client, Message message) {
        // Obtiene la sesion.
        GameSession session = requireSession(client);
        // Obtiene el jugador.
        Player player = requirePlayer(client);

        // Valida las coordenadas.
        if (!message.getPayload().has("targetX") || !message.getPayload().has("targetY")) {
            throw new MatchException("Faltan las coordenadas de destino");
        }

        // Ejecuta el movimiento.
        session.commandMove(
                player,
                readAnimalIds(message),
                message.getDouble("targetX", 0),
                message.getDouble("targetY", 0)
        );
    }

    // Ataca a un objetivo.
    public void handleAttack(ClientHandler client, Message message) {
        // Obtiene la sesion.
        GameSession session = requireSession(client);
        // Obtiene el jugador.
        Player player = requirePlayer(client);

        // Obtiene el id del objetivo.
        int targetId = message.getInt("targetAnimalId", -1);
        // Si no hay objetivo, lanza excepcion.
        if (targetId < 0) {
            throw new MatchException("Falta el animal objetivo");
        }

        // Ejecuta el ataque.
        session.commandAttack(player, readAnimalIds(message), targetId);
    }

    // Cambia la zona vista.
    public void handleViewZone(ClientHandler client, Message message) {
        // Obtiene la sesion.
        GameSession session = requireSession(client);
        // Obtiene el jugador.
        Player player = requirePlayer(client);

        // Cambia la zona.
        session.viewZone(player, message.getString("zoneId"));
    }

    // Maneja la desconexion.
    public void handleDisconnect(ClientHandler client) {
        // Obtiene la sesion.
        GameSession session = client.getSession();
        // Obtiene el jugador.
        Player player = client.getPlayer();

        // Si ambos existen, procesa.
        if (session != null && player != null) {
            matchManager.handleDisconnect(session, player);
        }
    }

    // Valida la sesion.
    private GameSession requireSession(ClientHandler client) {
        // Obtiene la sesion.
        GameSession session = client.getSession();
        // Si es nula, lanza excepcion.
        if (session == null) {
            throw new MatchException("Primero debes crear o unirte a una partida");
        }
        // Retorna la sesion.
        return session;
    }

    // Valida el jugador.
    private Player requirePlayer(ClientHandler client) {
        // Obtiene el jugador.
        Player player = client.getPlayer();
        // Si es nulo, lanza excepcion.
        if (player == null) {
            throw new MatchException("No hay un jugador asociado a esta conexion");
        }
        // Retorna el jugador.
        return player;
    }

    // Valida el nickname.
    private String requireNickname(String nickname) {
        // Si no es valido, lanza excepcion.
        if (!Player.isValidNickname(nickname)) {
            throw new MatchException("El nickname es obligatorio y debe tener menos de 20 caracteres");
        }
        // Retorna el nickname recortado.
        return nickname.trim();
    }

    // Lee los ids de animales del mensaje.
    private List<Integer> readAnimalIds(Message message) {
        // Obtiene el array.
        JsonArray array = message.getArray("animalIds");
        // Lista de ids.
        List<Integer> ids = new ArrayList<>();

        // Recorre el array.
        for (JsonElement element : array) {
            // Si no es nulo, lo agrega.
            if (element != null && !element.isJsonNull()) {
                ids.add(element.getAsInt());
            }
        }

        // Retorna los ids.
        return ids;
    }

    // Genera un id de jugador.
    private String newPlayerId() {
        // Retorna un id unico.
        return "P-" + UUID.randomUUID();
    }
}