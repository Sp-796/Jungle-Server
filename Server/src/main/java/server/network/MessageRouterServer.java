// Paquete de red del servidor: contiene el enrutador de mensajes.
package server.network;

// Importa Message para deserializar.
import common.message.Message;
// Importa MessageType para los tipos.
import common.message.MessageType;
// Importa MatchException para errores de negocio.
import server.logic.MatchException;

/**
 * Enrutador de mensajes entrantes.
 *
 * Su unica responsabilidad es leer el campo "type" y delegar al metodo del
 * MessageHandler que corresponde. Si algo falla, convierte la excepcion en un
 * mensaje ERROR para que el cliente nunca se quede sin respuesta.
 */
// Clase que enruta los mensajes entrantes.
public class MessageRouterServer {

    // Manejador de mensajes.
    private final MessageHandler handler;

    // Constructor.
    public MessageRouterServer(MessageHandler handler) {
        // Asigna el manejador.
        this.handler = handler;
    }

    // Enruta un mensaje del cliente.
    public void routeMessage(ClientHandler client, String jsonMessage) {
        // Intenta procesar.
        try {
            // Deserializa el mensaje.
            Message message = Message.fromJson(jsonMessage);

            // Enruta segun el tipo.
            switch (message.getType()) {
                case MessageType.CREATE_MATCH -> handler.handleCreateMatch(client, message);
                case MessageType.JOIN_MATCH -> handler.handleJoinMatch(client, message);
                case MessageType.SELECT_ANIMAL -> handler.handleSelectAnimal(client, message);
                case MessageType.MOVE_ANIMALS -> handler.handleMoveAnimals(client, message);
                case MessageType.ATTACK -> handler.handleAttack(client, message);
                case MessageType.VIEW_ZONE -> handler.handleViewZone(client, message);
                default -> client.sendError("Tipo de mensaje desconocido: " + message.getType());
            }

        } catch (MatchException businessError) {
            // Error de negocio: envia el mensaje.
            client.sendError(businessError.getMessage());
        } catch (RuntimeException unexpected) {
            // Error inesperado: envia mensaje generico.
            client.sendError("Mensaje invalido: " + unexpected.getMessage());
        }
    }

    // Maneja la desconexion.
    public void onDisconnect(ClientHandler client) {
        // Delega al manejador.
        handler.handleDisconnect(client);
    }
}