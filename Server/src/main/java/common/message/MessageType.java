// Paquete comun de mensajes: contiene el catalogo de tipos de mensaje del protocolo.
package common.message;

/**
 * Catalogo de tipos de mensaje del protocolo.
 *
 * Se usan String y no un enum para que el JSON sea legible y para que un
 * mensaje desconocido no rompa la deserializacion.
 */
// Clase final con constantes String para los tipos de mensaje (no se instancia).
public final class MessageType {

    // Constructor privado: clase de constantes.
    private MessageType() {
        // Clase de constantes: no se instancia.
    }

    // Tipos Cliente -> Servidor.

    // Crea una nueva partida.
    public static final String CREATE_MATCH = "CREATE_MATCH";
    // Se une a una partida existente.
    public static final String JOIN_MATCH = "JOIN_MATCH";
    // Elige la especie.
    public static final String SELECT_ANIMAL = "SELECT_ANIMAL";
    // Mueve animales a un destino.
    public static final String MOVE_ANIMALS = "MOVE_ANIMALS";
    // Ataca a un objetivo.
    public static final String ATTACK = "ATTACK";
    // Cambia la zona visible.
    public static final String VIEW_ZONE = "VIEW_ZONE";

    // Tipos Servidor -> Cliente.

    // Partida creada.
    public static final String MATCH_CREATED = "MATCH_CREATED";
    // Partida unida.
    public static final String MATCH_JOINED = "MATCH_JOINED";
    // Seleccion de animales.
    public static final String ANIMAL_SELECTION = "ANIMAL_SELECTION";
    // Partida iniciada.
    public static final String GAME_STARTED = "GAME_STARTED";
    // Actualizacion de estado.
    public static final String STATE_UPDATE = "STATE_UPDATE";
    // Fin de partida.
    public static final String GAME_OVER = "GAME_OVER";
    // Error.
    public static final String ERROR = "ERROR";

    // Eventos puntuales.

    // Se recogio una taza de cafe.
    public static final String COFFEE_COLLECTED = "COFFEE_COLLECTED";
}