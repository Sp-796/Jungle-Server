// Paquete de red del servidor: contiene el manejador de cada cliente.
package server.network;

// Importa JsonObject para payloads.
import com.google.gson.JsonObject;
// Importa Message para serializar.
import common.message.Message;
// Importa MessageType para tipos.
import common.message.MessageType;
// Importa las constantes del juego.
import constants.IConstants;
// Importa GameListener para recibir mensajes de la partida.
import server.logic.GameListener;
// Importa GameSession para la partida.
import server.logic.GameSession;
// Importa Player del modelo.
import server.model.Player;

/**
 * Hilo que atiende a un cliente conectado.
 *
 * Lectura: un hilo propio por cliente, bloqueado en {@link ClientChannel#receive()}.
 * Escritura: cada mensaje es una linea JSON que el canal se encarga de enviar
 * (por UDP, de forma confiable).
 *
 * Implementa GameListener para que una partida pueda enviarle actualizaciones
 * sin conocer nada del transporte.
 */
// Clase que atiende a un cliente (implementa Runnable y GameListener).
public class ClientHandler implements Runnable, GameListener {

    // Canal de comunicacion.
    private final ClientChannel channel;
    // Enrutador de mensajes.
    private final MessageRouterServer router;

    // Jugador asociado.
    private Player player;
    // Partida asociada.
    private GameSession session;
    // Indica si ya se registro un fallo de envio.
    private boolean sendFailureLogged;

    // Constructor.
    public ClientHandler(ClientChannel channel, MessageRouterServer router) {
        // Asigna el canal.
        this.channel = channel;
        // Asigna el enrutador.
        this.router = router;
    }

    // Ejecucion del hilo.
    @Override
    public void run() {
        // Intenta leer mensajes.
        try {
            String line;
            // Lee mientras haya mensajes.
            while ((line = channel.receive()) != null) {
                // Si el mensaje es muy grande, envia error.
                if (line.length() > IConstants.MAX_MESSAGE_LENGTH) {
                    sendError("El mensaje es demasiado grande");
                    continue;
                }
                // Enruta el mensaje.
                router.routeMessage(this, line);
            }
        } catch (RuntimeException error) {
            // Imprime el error.
            System.err.println("Cliente desconectado: " + error.getMessage());
        } finally {
            // Notifica la desconexion.
            router.onDisconnect(this);
            // Cierra el canal.
            close();
        }
    }

    // Envia un mensaje al cliente.
    public synchronized void sendMessage(String type, JsonObject payload) {
        // Si no hay canal, no hace nada.
        if (channel == null) {
            return;
        }

        // Envia el mensaje serializado.
        boolean sent = channel.send(new Message(type, payload).toJson());
        // Si se envio, reinicia el flag.
        if (sent) {
            sendFailureLogged = false;
        } else if (!sendFailureLogged) {
            // Registra el fallo una sola vez.
            sendFailureLogged = true;
            System.err.println("No se pudo enviar " + type + " a " + channel.remoteAddress());
        }
    }

    // Envia un error al cliente.
    public void sendError(String description) {
        // Crea el payload.
        JsonObject payload = new JsonObject();
        // Agrega el mensaje.
        payload.addProperty("message", description);
        // Envia el error.
        sendMessage(MessageType.ERROR, payload);
    }

    // Implementa GameListener: envia el mensaje del juego.
    @Override
    public void onGameMessage(String type, JsonObject payload) {
        // Delega a sendMessage.
        sendMessage(type, payload);
    }

    // Cierra el canal.
    public void close() {
        // Si hay canal, lo cierra.
        if (channel != null) {
            channel.close();
        }
    }

    // Asocia el jugador y la partida.
    public void attach(Player player, GameSession session) {
        // Asigna el jugador.
        this.player = player;
        // Asigna la partida.
        this.session = session;
    }

    // Getter del jugador.
    public Player getPlayer() {
        // Retorna player.
        return player;
    }

    // Getter de la partida.
    public GameSession getSession() {
        // Retorna session.
        return session;
    }

    // Getter del codigo de la sala.
    public String getRoomCode() {
        // Retorna el codigo o null.
        return session == null ? null : session.getMatchCode();
    }
}