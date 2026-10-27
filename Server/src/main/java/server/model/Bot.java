// Paquete del modelo del servidor: contiene los bots (IA).
package server.model;

// Importa Point2D del paquete comun.
import common.model.Point2D;
// Importa las constantes del juego.
import constants.IConstants;
// Importa List para las zonas vecinas.
import java.util.List;
// Importa GameSession del servidor.
import server.logic.GameSession;
// Importa MapManager del servidor.
import server.logic.MapManager;

/**
 * Pack autonomo controlado por la IA del servidor.
 *
 * El bot solo decide a donde caminar. Los ataques automaticos los resuelve
 * {@link server.logic.CombatManager} cuando un animal del bot tiene un jugador
 * dentro del radio de encuentro.
 */
// Clase Bot que extiende Participant (herencia) y representa un pack controlado por IA.
public class Bot extends Participant {

    // Margen para patrullar dentro de una zona.
    private static final double PATROL_MARGIN = IConstants.ZONE_MARGIN;

    // Animal objetivo detectado (el mas cercano).
    private Animal targetCandidate;

    // Constructor del bot.
    public Bot(String participantId, String name) {
        // Llama al constructor de Participant.
        super(participantId, name);
    }

    // Indica que es un bot.
    @Override
    public boolean isBot() {
        // Retorna true.
        return true;
    }

    // Un tick de la IA.
    public void updateAI(GameSession session, double deltaSeconds) {
        // Si no tiene manada o esta derrotada, no hace nada.
        if (getPack() == null || getPack().isDefeated()) {
            return;
        }
        // Busca objetivos cercanos.
        searchTargets(session);
        // Mueve los animales.
        autoMove(session);
    }

    // Busca el animal de un jugador mas cercano dentro del radio de deteccion.
    public void searchTargets(GameSession session) {
        // Reinicia el objetivo.
        targetCandidate = null;
        // Mejor distancia encontrada.
        double best = Double.MAX_VALUE;

        // Recorre los jugadores.
        for (Player player : session.getPlayers()) {
            // Si no tiene manada, salta.
            if (player.getPack() == null) {
                continue;
            }
            // Recorre los animales vivos del jugador.
            for (Animal enemy : player.getPack().getLivingAnimals()) {
                // Calcula la distancia mas cercana.
                double distance = closestDistanceTo(enemy);
                // Si esta dentro del radio y es mejor, actualiza.
                if (distance <= IConstants.BOT_DETECTION_RADIUS && distance < best) {
                    best = distance;
                    targetCandidate = enemy;
                }
            }
        }
    }

    // Mueve los animales del bot.
    public void autoMove(GameSession session) {
        // Obtiene el gestor del mapa.
        MapManager mapManager = session.getMapManager();

        // Recorre los animales vivos del pack.
        for (Animal animal : getPack().getLivingAnimals()) {
            // Si ya se esta moviendo, no cambia destino.
            if (animal.isMoving()) {
                continue;
            }

            // Si hay objetivo, se dirige hacia el.
            if (targetCandidate != null && !targetCandidate.isDefeated()) {
                mapManager.commandMove(animal, targetCandidate.getX(), targetCandidate.getY());
                continue;
            }

            // Obtiene la zona actual del animal.
            Zone zone = mapManager.getZone(animal.getZoneId());
            // Si no existe la zona, salta.
            if (zone == null) {
                continue;
            }

            // Obtiene las zonas vecinas.
            List<Zone> neighbours = zone.getAdjacentNodes();
            // Si no hay vecinas, salta.
            if (neighbours.isEmpty()) {
                continue;
            }

            // Elige una zona vecina al azar.
            Zone next = neighbours.get(session.getRandom().nextInt(neighbours.size()));
            // Punto aleatorio dentro de esa zona.
            Point2D point = next.randomPointInside(session.getRandom(), PATROL_MARGIN);
            // Ordena mover al animal.
            mapManager.commandMove(animal, point.getX(), point.getY());
        }
    }

    // Calcula la distancia mas cercana entre el pack del bot y un enemigo.
    private double closestDistanceTo(Animal enemy) {
        // Mejor distancia.
        double best = Double.MAX_VALUE;
        // Recorre los animales propios.
        for (Animal own : getPack().getLivingAnimals()) {
            // Calcula la distancia.
            double distance = own.getPosition().distanceTo(enemy.getPosition());
            // Actualiza la mejor.
            if (distance < best) {
                best = distance;
            }
        }
        // Retorna la mejor distancia.
        return best;
    }
}