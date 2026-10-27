// Paquete de la logica del servidor: contiene el gestor de partidas (Singleton).
package server.logic;

// Importa TimeSource para el reloj.
import common.util.TimeSource;
// Importa ArrayList para listas.
import java.util.ArrayList;
// Importa Collection para colecciones.
import java.util.Collection;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa Random para aleatoriedad.
import java.util.Random;
// Importa ConcurrentHashMap para el mapa thread-safe.
import java.util.concurrent.ConcurrentHashMap;
// Importa Player del modelo.
import server.model.Player;

/**
 * Patron Singleton.
 *
 * Es el unico punto del servidor que conoce todas las partidas activas. El
 * mapa es ConcurrentHashMap porque lo leen y escriben hilos distintos: el
 * GameLoop, los ClientHandler y el hilo de aceptacion de conexiones.
 */
// Clase Singleton que gestiona las partidas activas.
public class MatchManager {

    // Instancia unica del Singleton.
    private static MatchManager instance;

    // Mapa de partidas activas.
    private final ConcurrentHashMap<String, GameSession> activeMatches = new ConcurrentHashMap<>();
    // Generador de codigos.
    private final MatchCodeGenerator codeGenerator;
    // Generador aleatorio.
    private final Random random;

    // Constructor privado (Singleton).
    private MatchManager() {
        // Delega con random por defecto.
        this(new Random());
    }

    // Constructor visible para pruebas con semilla fija.
    MatchManager(Random random) {
        // Asigna el random.
        this.random = random;
        // Crea el generador de codigos.
        this.codeGenerator = new MatchCodeGenerator(random);
    }

    // Obtiene la instancia unica (Singleton).
    public static synchronized MatchManager getInstance() {
        // Si no existe, la crea.
        if (instance == null) {
            instance = new MatchManager();
        }
        // Retorna la instancia.
        return instance;
    }

    // Crea una sala y devuelve su codigo.
    public String createMatch(Player player1) {
        // Genera un codigo unico.
        String code = codeGenerator.generateUnique(activeMatches.keySet());

        // Crea el builder.
        GameSessionBuilder builder = new GameSessionBuilder(random, TimeSource.system());
        // Reinicia con el codigo.
        builder.reset(code);
        // Agrega el jugador.
        builder.buildPlayer(player1);

        // Obtiene la sesion.
        GameSession session = builder.getResult();
        // La guarda en el mapa.
        activeMatches.put(code, session);

        // Retorna el codigo.
        return code;
    }

    // Une al segundo jugador a una sala.
    public GameSession joinMatch(String code, Player player2) {
        // Normaliza el codigo.
        String normalized = normalizeCode(code);
        // Obtiene la sesion.
        GameSession session = normalized == null ? null : activeMatches.get(normalized);

        // Si no existe, lanza excepcion.
        if (session == null) {
            throw new MatchException("No existe una partida con el codigo " + code);
        }

        // Agrega el segundo jugador.
        session.addPlayer(player2);
        // Retorna la sesion.
        return session;
    }

    // Obtiene una partida por codigo.
    public GameSession getMatch(String code) {
        // Normaliza el codigo.
        String normalized = normalizeCode(code);
        // Retorna la sesion o null.
        return normalized == null ? null : activeMatches.get(normalized);
    }

    // Remueve una partida.
    public void removeMatch(String code) {
        // Normaliza el codigo.
        String normalized = normalizeCode(code);
        // Si es valido, remueve.
        if (normalized != null) {
            activeMatches.remove(normalized);
        }
    }

    // Procesa una desconexion.
    public void handleDisconnect(GameSession session, Player player) {
        // Si la sesion es nula, no hace nada.
        if (session == null) {
            return;
        }
        // Maneja la desconexion en la sesion.
        session.handleDisconnect(player);

        // Si ya no esta en curso, remueve la sala.
        if (!session.isRunning()) {
            removeMatch(session.getMatchCode());
        }
    }

    // Retorna las partidas activas.
    public Collection<GameSession> getActiveMatches() {
        // Retorna vista inmutable.
        return Collections.unmodifiableCollection(activeMatches.values());
    }

    // Retorna la cantidad de partidas activas.
    public int getActiveMatchCount() {
        // Retorna el tamano.
        return activeMatches.size();
    }

    // Limpia el singleton entre pruebas.
    public synchronized void resetForTesting() {
        // Limpia el mapa.
        activeMatches.clear();
    }

    // Normaliza el codigo (mayusculas).
    public static String normalizeCode(String code) {
        // Si es nulo o vacio, retorna null.
        if (code == null || code.isBlank()) {
            return null;
        }
        // Retorna en mayusculas y sin espacios.
        return code.trim().toUpperCase();
    }

    // Copia segura de las partidas.
    public Collection<GameSession> snapshotMatches() {
        // Retorna una copia.
        return new ArrayList<>(activeMatches.values());
    }
}