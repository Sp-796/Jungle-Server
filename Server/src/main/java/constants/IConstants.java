package constants;

/**
 * Libreria central de constantes del juego.
 *
 * Regla del proyecto: ningun valor "magico" vive dentro de una clase de logica.
 * Todo valor fijo se declara aqui para que cliente y servidor usen exactamente
 * los mismos numeros.
 */
public interface IConstants {

    // ------------------------------------------------------------------
    // Estadisticas de los participantes
    // ------------------------------------------------------------------

    /** Energia inicial de cada animal del pack de un jugador. */
    int INITIAL_ENERGY_PLAYER = 20;

    /** Energia inicial de cada animal de un pack bot. */
    int INITIAL_ENERGY_BOT = 10;

    /** Energia maxima que puede alcanzar un animal de un jugador. */
    int MAX_ENERGY = 20;

    /** Dano inicial compartido por todos los animales del pack de un jugador. */
    int INITIAL_DAMAGE_PLAYER = 2;

    /** Dano inicial compartido por todos los animales de un pack bot. */
    int INITIAL_DAMAGE_BOT = 1;

    /** Dano maximo teorico del pack de un jugador: 2 iniciales + 10 tazas. */
    int MAX_DAMAGE_CAPACITY_PLAYER = 12;

    /** Los bots no aumentan su dano con el cafe. */
    int MAX_DAMAGE_CAPACITY_BOT = 1;

    /** Milisegundos minimos entre dos ataques consecutivos de un mismo animal. */
    int ATTACK_COOLDOWN_MS = 1000;

    // ------------------------------------------------------------------
    // Tamano de los packs
    // ------------------------------------------------------------------

    /** Cantidad de animales del pack de un jugador. */
    int MAX_TEAM_SIZE_PLAYER = 10;

    /** Cantidad de animales de cada pack bot. */
    int MAX_TEAM_SIZE_BOT = 4;

    /** Cantidad de packs bot: las 5 especies que ningun jugador eligio. */
    int BOT_PACK_COUNT = 5;

    /** Jugadores humanos que puede tener una partida. */
    int MAX_PLAYERS = 2;

    /** Colores de las banderas de cada jugador. */
    String FLAG_COLOR_PLAYER_ONE = "Guaria Morada";
    String FLAG_COLOR_PLAYER_TWO = "Yiguirro";

    // ------------------------------------------------------------------
    // Objetos del mapa
    // ------------------------------------------------------------------

    /** Tazas de cafe disponibles en toda la partida. */
    int MAX_AMOUNT_COFFEE = 10;

    /** Zonas totales del mapa. */
    int MAX_ZONES = 12;

    /** Cada cuantas bajas de bots el pack del jugador recibe energia. */
    int BOTS_PER_ENERGY_BONUS = 2;

    /** Energia que gana cada animal vivo del pack por el bonus de bots. */
    int ENERGY_BONUS_AMOUNT = 1;

    /** Dano que suma al pack cada taza de cafe recogida. */
    int CUP_DAMAGE_BONUS = 1;

    // ------------------------------------------------------------------
    // Fisica y deteccion
    // ------------------------------------------------------------------

    /** Velocidad constante de los animales, en unidades del mundo por segundo. */
    double ANIMAL_SPEED = 50.0;

    /** Distancia maxima para considerar que dos animales se encontraron. */
    double ENCOUNTER_RADIUS = 15.0;

    /** Distancia maxima para considerar que un animal toco una bandera. */
    double FLAG_RADIUS = 15.0;

    /** Distancia maxima para considerar que un animal recogio una taza. */
    double CUP_RADIUS = 12.0;

    /** Radio en el que un bot detecta enemigos para perseguirlos. */
    double BOT_DETECTION_RADIUS = 120.0;

    /** Multiplicador del margen usado al colocar la bandera lejos del borde. */
    double FLAG_MARGIN_MULTIPLIER = 1.5;

    /** Milisegundos que hay en un segundo (conversion de deltaTime). */
    double MILLIS_PER_SECOND = 1000.0;

    /** Nanosegundos que hay en un milisegundo (conversion del GameLoop). */
    long NANOS_PER_MILLI = 1_000_000L;

    // ------------------------------------------------------------------
    // Motivos de fin de partida (viajan en GAME_OVER.reason)
    // ------------------------------------------------------------------

    String FINISH_REASON_FLAG_TOUCHED = "FLAG_TOUCHED";
    String FINISH_REASON_PACK_DEFEATED = "PACK_DEFEATED";
    String FINISH_REASON_DISCONNECT = "DISCONNECT";
    String FINISH_REASON_OPPONENT_LEFT = "OPPONENT_LEFT";

    // ------------------------------------------------------------------
    // Ciclo del servidor
    // ------------------------------------------------------------------

    /** Milisegundos entre actualizaciones del motor del servidor. */
    int TICK_RATE_MS = 50;

    /** Se envia STATE_UPDATE cada N ticks (2 ticks = 100 ms). */
    int STATE_BROADCAST_EVERY_TICKS = 2;

    /** Puerto UDP por defecto. */
    int DEFAULT_SERVER_PORT = 8080;

    // ------------------------------------------------------------------
    // Transporte UDP
    // ------------------------------------------------------------------

    /** Milisegundos maximos que un cliente espera el HELLO_ACK al conectar. */
    int UDP_HANDSHAKE_TIMEOUT_MS = 5000;

    /** Milisegundos antes de reenviar un mensaje que aun no tiene ACK. */
    int UDP_RETRANSMIT_MS = 150;

    /** Milisegundos entre PING de mantenimiento del cliente. */
    int UDP_KEEPALIVE_MS = 2000;

    /** Milisegundos sin recibir nada tras los cuales se considera caido el cliente. */
    int UDP_CLIENT_TIMEOUT_MS = 15000;

    /** Maximo de mensajes enviados sin confirmar antes de descartar los nuevos. */
    int UDP_WINDOW_SIZE = 256;

    /** Maximo de mensajes adelantados que se guardan esperando al faltante. */
    int UDP_MAX_BUFFERED_MESSAGES = 1024;

    /** Maximo de retransmisiones por pasada de mantenimiento. */
    int UDP_MAX_RETRANSMITS_PER_TICK = 32;

    /** Tamano maximo de un datagrama (deja margen bajo el limite de 65507). */
    int UDP_MAX_DATAGRAM_SIZE = 60000;

    /** Buffer de recepcion del socket UDP. */
    int UDP_RECEIVE_BUFFER_SIZE = 1_048_576;

    /** Milisegundos entre pasadas de retransmision y deteccion de inactividad. */
    int UDP_HOUSEKEEPING_MS = 50;

    // ------------------------------------------------------------------
    // Codigo de partida
    // ------------------------------------------------------------------

    /** Longitud del codigo de partida. */
    int MATCH_CODE_LENGTH = 4;

    /** Alfabeto permitido para el codigo (mayusculas y digitos). */
    String MATCH_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // ------------------------------------------------------------------
    // Geometria del mundo
    // ------------------------------------------------------------------
    //
    // El mundo es un rectangulo dividido en una cuadricula de 4 columnas x 3
    // filas. Cada celda es una zona. Las zonas se conectan por caminos que se
    // definen en MapManager, no por su posicion en la cuadricula.

    double WORLD_WIDTH = 1280.0;
    double WORLD_HEIGHT = 720.0;
    int ZONE_COLUMNS = 4;
    int ZONE_ROWS = 3;

    double ZONE_WIDTH = WORLD_WIDTH / ZONE_COLUMNS;
    double ZONE_HEIGHT = WORLD_HEIGHT / ZONE_ROWS;

    /** Margen interno para no generar animales/tazas pegados al borde. */
    double ZONE_MARGIN = 40.0;

    // ------------------------------------------------------------------
    // Validaciones de red
    // ------------------------------------------------------------------

    /** Longitud maxima del nickname. */
    int MAX_NICKNAME_LENGTH = 20;

    /** Longitud maxima de una linea JSON recibida. */
    int MAX_MESSAGE_LENGTH = 64 * 1024;
}
