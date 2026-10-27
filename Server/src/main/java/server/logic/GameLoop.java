// Paquete de la logica del servidor: contiene el bucle principal del juego.
package server.logic;

// Importa las constantes del juego.
import constants.IConstants;

/**
 * Hilo maestro del servidor.
 *
 * Duerme TICK_RATE_MS entre vueltas y calcula el delta real con nanoTime.
 * Usar el tiempo transcurrido (y no un valor fijo) hace que la velocidad de los
 * animales sea constante aunque un tick llegue con retraso.
 */
// Clase que implementa Runnable para ejecutar el bucle del juego en un hilo.
public class GameLoop implements Runnable {

    // Intervalo de tick en milisegundos.
    private final int tickRateMs;
    // Gestor de partidas.
    private final MatchManager matchManager;

    // Indica si el bucle esta corriendo.
    private volatile boolean running;
    // Hilo del bucle.
    private Thread thread;
    // Momento del ultimo tick en nanosegundos.
    private long lastTickNanos;

    // Constructor con solo tick rate (usa MatchManager singleton).
    public GameLoop(int tickRateMs) {
        // Delega al constructor completo.
        this(tickRateMs, MatchManager.getInstance());
    }

    // Constructor completo.
    public GameLoop(int tickRateMs, MatchManager matchManager) {
        // Asigna el tick rate.
        this.tickRateMs = tickRateMs;
        // Asigna el gestor de partidas.
        this.matchManager = matchManager;
    }

    // Inicia el bucle.
    public void start() {
        // Si ya esta corriendo, no hace nada.
        if (running) {
            return;
        }
        // Marca como corriendo.
        running = true;
        // Crea el hilo.
        thread = new Thread(this, "game-loop");
        // Hilo demonio.
        thread.setDaemon(true);
        // Inicia el hilo.
        thread.start();
    }

    // Detiene el bucle.
    public void stop() {
        // Marca como no corriendo.
        running = false;
        // Interrumpe el hilo si existe.
        if (thread != null) {
            thread.interrupt();
        }
    }

    // Ejecucion del hilo.
    @Override
    public void run() {
        // Inicializa el ultimo tick.
        lastTickNanos = System.nanoTime();

        // Mientras este corriendo.
        while (running) {
            // Duerme el intervalo.
            try {
                Thread.sleep(tickRateMs);
            } catch (InterruptedException interrupted) {
                // Restaura el flag de interrupcion.
                Thread.currentThread().interrupt();
                // Sale del bucle.
                break;
            }

            // Tiempo actual en nanos.
            long now = System.nanoTime();
            // Delta en milisegundos.
            int deltaMs = (int) ((now - lastTickNanos) / IConstants.NANOS_PER_MILLI);
            // Actualiza el ultimo tick.
            lastTickNanos = now;

            // Si hay delta positivo, actualiza.
            if (deltaMs > 0) {
                updateMatches(deltaMs);
            }
        }
    }

    // Actualiza todas las partidas activas.
    public void updateMatches(int deltaMs) {
        // Recorre las partidas.
        for (GameSession session : matchManager.snapshotMatches()) {
            // Intenta actualizar.
            try {
                // Actualiza la sesion.
                session.update(deltaMs);
                // Si termino, la remueve.
                if (session.isFinished()) {
                    matchManager.removeMatch(session.getMatchCode());
                }
            } catch (RuntimeException error) {
                // Imprime el error.
                System.err.println("Error en la partida " + session.getMatchCode()
                        + ": " + error.getMessage());
            }
        }
    }

    // Indica si esta corriendo.
    public boolean isRunning() {
        // Retorna running.
        return running;
    }
}