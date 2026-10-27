// Paquete del servidor: contiene el punto de entrada.
package server;

// Importa las constantes del juego.
import constants.IConstants;
// Importa IOException para errores de IO.
import java.io.IOException;
// Importa GameLoop para el bucle del juego.
import server.logic.GameLoop;
// Importa MatchManager para gestionar partidas.
import server.logic.MatchManager;
// Importa MessageHandler para manejar mensajes.
import server.network.MessageHandler;
// Importa MessageRouterServer para enrutar mensajes.
import server.network.MessageRouterServer;
// Importa NetworkServer para la red.
import server.network.NetworkServer;

/**
 * Punto de entrada del servidor.
 *
 * Ensambla las piezas (unica clase que conoce todas las demas) y arranca:
 *  1. el hilo del motor de juego (GameLoop),
 *  2. el servidor UDP (NetworkServer).
 *
 * Uso:
 *   java -jar jungle-attack-server.jar            -> puerto 8080
 *   java -jar jungle-attack-server.jar 9090       -> puerto 9090
 */
// Clase final de arranque del servidor.
public final class ServerMain {

    // Constructor privado: no se instancia.
    private ServerMain() {
        // Clase de arranque.
    }

    // Metodo principal.
    public static void main(String[] args) {
        // Resuelve el puerto.
        int port = resolvePort(args);

        // Obtiene el gestor de partidas.
        MatchManager matchManager = MatchManager.getInstance();
        // Crea el manejador de mensajes.
        MessageHandler messageHandler = new MessageHandler(matchManager);
        // Crea el enrutador.
        MessageRouterServer router = new MessageRouterServer(messageHandler);

        // Crea el bucle del juego.
        GameLoop gameLoop = new GameLoop(IConstants.TICK_RATE_MS, matchManager);
        // Inicia el bucle.
        gameLoop.start();

        // Crea el servidor de red.
        NetworkServer networkServer = new NetworkServer(router);

        // Intenta iniciar el servidor.
        try {
            networkServer.startServer(port);
        } catch (IOException error) {
            // Si falla, imprime el error.
            System.err.println("No se pudo abrir el puerto " + port + ": " + error.getMessage());
            // Detiene el bucle.
            gameLoop.stop();
            // Sale.
            return;
        }

        // Registra el hook de cierre.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Deteniendo servidor...");
            gameLoop.stop();
            networkServer.stop();
        }, "shutdown-hook"));

        // Imprime mensaje final.
        System.out.println("Jungle Attack server listo. Presiona Ctrl+C para detenerlo.");
    }

    // Decide el puerto a usar.
    static int resolvePort(String[] args) {
        // Si hay argumentos, usa el primero.
        if (args != null && args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException error) {
                System.err.println("Puerto invalido: " + args[0]);
            }
        }

        // Si hay propiedad del sistema, la usa.
        String property = System.getProperty("server.port");
        if (property != null) {
            try {
                return Integer.parseInt(property);
            } catch (NumberFormatException error) {
                System.err.println("server.port invalido: " + property);
            }
        }

        // Retorna el puerto por defecto.
        return IConstants.DEFAULT_SERVER_PORT;
    }
}