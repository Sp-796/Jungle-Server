// Paquete de la logica del servidor: contiene el gestor del mapa.
package server.logic;

// Importa Point2D del paquete comun.
import common.model.Point2D;
// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayDeque para la cola de BFS.
import java.util.ArrayDeque;
// Importa ArrayList para listas internas.
import java.util.ArrayList;
// Importa Collection para colecciones.
import java.util.Collection;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa HashMap para mapas.
import java.util.HashMap;
// Importa HashSet para conjuntos.
import java.util.HashSet;
// Importa LinkedHashMap para el mapa del mundo.
import java.util.LinkedHashMap;
// Importa List para listas.
import java.util.List;
// Importa Map para mapas.
import java.util.Map;
// Importa Queue para la cola de BFS.
import java.util.Queue;
// Importa Random para aleatoriedad.
import java.util.Random;
// Importa Set para conjuntos.
import java.util.Set;
// Importa Animal del modelo.
import server.model.Animal;
// Importa CoffeeCup del modelo.
import server.model.CoffeeCup;
// Importa Flag del modelo.
import server.model.Flag;
// Importa Participant del modelo.
import server.model.Participant;
// Importa Player del modelo.
import server.model.Player;
// Importa Zone del modelo.
import server.model.Zone;

/**
 * Modulo delegado del mapa.
 *
 * Responsabilidades:
 *  - construir las 12 zonas y sus caminos (grafo),
 *  - buscar rutas con BFS,
 *  - saber en que zona esta un punto,
 *  - repartir zonas iniciales, banderas y tazas.
 *
 * El resto de clases nunca manipula la topologia directamente: le preguntan a
 * este manager.
 */
// Clase que gestiona el mapa del juego (grafo de zonas).
public class MapManager {

    // Mapa del mundo: id de zona -> zona.
    private final Map<String, Zone> worldMap = new LinkedHashMap<>();

    // Constructor que construye el mundo.
    public MapManager() {
        // Construye el mundo.
        buildWorld();
    }

    // Construye las 12 zonas y sus caminos.
    private void buildWorld() {

        // Cuadricula 4 columnas x 3 filas.
        // Fila 0 = zona alta, fila 2 = zona costera.
        Zone paramo          = zone("paramo", "Paramo", "paramo.svg", 0, 0);
        Zone bosqueNuboso    = zone("bosque_nuboso", "Bosque Nuboso", "bosquenuboso.svg", 1, 0);
        Zone zonaMontanosa   = zone("zona_montanosa", "Zona Montana", "zonamontana.svg", 2, 0);
        Zone robledales      = zone("bosque_robledales", "Bosque de Robledales de Altura", "bosquerodeado.svg", 3, 0);

        Zone bosqueHumedo    = zone("bosque_humedo", "Bosque Humedo", "bosquehumedo.svg", 0, 1);
        Zone catarata        = zone("bosque_catarata", "Bosque con Catarata", "bosquecatarata.svg", 1, 1);
        Zone cavernas        = zone("bosque_cavernas", "Bosque con Cavernas", "bosquecaverna.svg", 2, 1);
        Zone senderoRocoso   = zone("sendero_rocoso", "Sendero Rocoso", "senderorocoso.svg", 3, 1);

        Zone manglar         = zone("manglar", "Manglar", "manglar.svg", 0, 2);
        Zone bosqueSeco      = zone("bosque_seco", "Bosque Seco Tropical", "bosqueseco.svg", 1, 2);
        Zone bosqueProfundo  = zone("bosque_profundo", "Bosque Profundo", "bosqueprofundo.svg", 2, 2);
        Zone bosqueDespejado = zone("bosque_despejado", "Bosque Despejado", "bosquedespejado.svg", 3, 2);

        // Caminos segun Ideas-descriptivas.md.
        // Zone.connect es bidireccional.
        manglar.connect(bosqueSeco);
        manglar.connect(bosqueProfundo);

        bosqueSeco.connect(bosqueDespejado);
        bosqueSeco.connect(senderoRocoso);

        bosqueProfundo.connect(bosqueHumedo);
        bosqueProfundo.connect(cavernas);

        bosqueDespejado.connect(bosqueHumedo);
        bosqueDespejado.connect(catarata);

        bosqueHumedo.connect(catarata);

        catarata.connect(cavernas);
        catarata.connect(zonaMontanosa);

        cavernas.connect(bosqueNuboso);

        senderoRocoso.connect(zonaMontanosa);

        zonaMontanosa.connect(bosqueNuboso);
        zonaMontanosa.connect(robledales);

        bosqueNuboso.connect(paramo);

        robledales.connect(paramo);
    }

    // Crea una zona y la agrega al mapa.
    private Zone zone(String id, String name, String image, int column, int row) {
        // Crea la zona.
        Zone zone = new Zone(
                id,
                name,
                image,
                column * IConstants.ZONE_WIDTH,
                row * IConstants.ZONE_HEIGHT,
                IConstants.ZONE_WIDTH,
                IConstants.ZONE_HEIGHT
        );
        // La agrega al mapa.
        worldMap.put(id, zone);
        // La retorna.
        return zone;
    }

    // Retorna una zona por id.
    public Zone getZone(String zoneId) {
        // Si es nulo, retorna null.
        if (zoneId == null) {
            return null;
        }
        // Retorna la zona.
        return worldMap.get(zoneId);
    }

    // Retorna todas las zonas.
    public Collection<Zone> getZones() {
        // Retorna vista inmutable.
        return Collections.unmodifiableCollection(worldMap.values());
    }

    // Retorna el mapa completo (inmutable).
    public Map<String, Zone> getWorldMap() {
        // Retorna vista inmutable.
        return Collections.unmodifiableMap(worldMap);
    }

    // Zona que contiene el punto indicado.
    public Zone findZoneAt(double x, double y) {
        // Recorre las zonas.
        for (Zone zone : worldMap.values()) {
            // Si contiene el punto, la retorna.
            if (zone.contains(x, y)) {
                return zone;
            }
        }
        // No se encontro.
        return null;
    }

    // Ruta mas corta entre dos zonas con BFS.
    public List<Zone> calculateRouteBFS(Zone start, Zone destination) {
        // Si alguno es nulo, retorna vacio.
        if (start == null || destination == null) {
            return Collections.emptyList();
        }
        // Si es la misma zona, retorna lista con ella.
        if (start == destination) {
            return List.of(start);
        }

        // Mapa de predecesores.
        Map<Zone, Zone> previous = new HashMap<>();
        // Conjunto de visitadas.
        Set<Zone> visited = new HashSet<>();
        // Cola de BFS.
        Queue<Zone> queue = new ArrayDeque<>();

        // Inicia con la zona de salida.
        queue.add(start);
        visited.add(start);

        // Mientras haya zonas por procesar.
        while (!queue.isEmpty()) {
            // Toma la siguiente.
            Zone current = queue.poll();

            // Recorre las vecinas.
            for (Zone neighbour : current.getAdjacentNodes()) {
                // Si ya fue visitada, salta.
                if (visited.contains(neighbour)) {
                    continue;
                }

                // Marca como visitada.
                visited.add(neighbour);
                // Guarda el predecesor.
                previous.put(neighbour, current);

                // Si llego al destino, reconstruye la ruta.
                if (neighbour == destination) {
                    return rebuildRoute(previous, start, destination);
                }

                // Agrega a la cola.
                queue.add(neighbour);
            }
        }

        // No se encontro camino.
        return Collections.emptyList();
    }

    // Reconstruye la ruta a partir de los predecesores.
    private List<Zone> rebuildRoute(Map<Zone, Zone> previous, Zone start, Zone destination) {
        // Lista de la ruta.
        List<Zone> route = new ArrayList<>();
        // Comienza en el destino.
        Zone current = destination;

        // Retrocede hasta la salida.
        while (current != null && current != start) {
            // Agrega la zona.
            route.add(current);
            // Retrocede.
            current = previous.get(current);
        }
        // Agrega la salida.
        route.add(start);
        // Invierte para que vaya de salida a destino.
        Collections.reverse(route);
        // Retorna la ruta.
        return route;
    }

    // Convierte una ruta de zonas en puntos concretos.
    public boolean commandMove(Animal animal, double targetX, double targetY) {
        // Si el animal es nulo o esta derrotado, retorna false.
        if (animal == null || animal.isDefeated()) {
            return false;
        }

        // Zona de salida.
        Zone start = findZoneAt(animal.getX(), animal.getY());
        if (start == null) {
            start = getZone(animal.getZoneId());
        }

        // Zona de destino.
        Zone destination = findZoneAt(targetX, targetY);
        // Si no se encontraron, retorna false.
        if (start == null || destination == null) {
            return false;
        }

        // Calcula la ruta de zonas.
        List<Zone> zoneRoute = calculateRouteBFS(start, destination);
        // Si no hay ruta, retorna false.
        if (zoneRoute.isEmpty()) {
            return false;
        }

        // Lista de waypoints.
        List<Point2D> waypoints = new ArrayList<>();
        // Omite la zona de salida y la de destino.
        for (int i = 1; i < zoneRoute.size() - 1; i++) {
            waypoints.add(zoneRoute.get(i).getCenter());
        }
        // Agrega el punto exacto pedido.
        waypoints.add(new Point2D(targetX, targetY));

        // Asigna la ruta al animal.
        animal.setRoute(waypoints, targetX, targetY);
        // Retorna true.
        return true;
    }

    // Coloca a los participantes en zonas distintas.
    public void assignStartingZones(List<Participant> participants, Random random) {
        // Copia las zonas.
        List<Zone> zones = new ArrayList<>(worldMap.values());
        // Baraja las zonas.
        Collections.shuffle(zones, random);

        // Cantidad a repartir.
        int count = Math.min(participants.size(), zones.size());

        // Recorre los participantes.
        for (int i = 0; i < count; i++) {
            // Obtiene el participante.
            Participant participant = participants.get(i);
            // Obtiene la zona.
            Zone zone = zones.get(i);

            // Asigna la zona actual.
            participant.setCurrentZone(zone);

            // Si no tiene manada, salta.
            if (participant.getPack() == null) {
                continue;
            }

            // Coloca cada animal en la zona.
            for (Animal animal : participant.getPack().getAnimals()) {
                // Posicion aleatoria.
                Point2D position = zone.randomPointInside(random, IConstants.ZONE_MARGIN);
                // Asigna x.
                animal.setX(position.getX());
                // Asigna y.
                animal.setY(position.getY());
                // Asigna la zona.
                animal.setZoneId(zone.getZoneId());
                // Limpia la ruta.
                animal.clearRoute();
                // Agrega la entidad a la zona.
                zone.addEntity(animal);
            }
        }
    }

    // Coloca banderas y tazas.
    public void spawnItems(List<Player> players, Random random) {
        // Colores por defecto.
        String[] defaultColors = {
                IConstants.FLAG_COLOR_PLAYER_ONE,
                IConstants.FLAG_COLOR_PLAYER_TWO
        };

        // Contador de banderas.
        int flagNumber = 1;
        // Recorre los jugadores.
        for (int i = 0; i < players.size(); i++) {
            // Obtiene el jugador.
            Player player = players.get(i);
            // Obtiene su zona.
            Zone zone = player.getCurrentZone();
            // Si no tiene zona, salta.
            if (zone == null) {
                continue;
            }

            // Posicion aleatoria para la bandera.
            Point2D position = zone.randomPointInside(
                    random,
                    IConstants.ZONE_MARGIN * IConstants.FLAG_MARGIN_MULTIPLIER
            );
            // Color de la bandera.
            String color = player.getFlagColor() == null
                    ? defaultColors[i % defaultColors.length]
                    : player.getFlagColor();

            // Crea la bandera.
            Flag flag = new Flag(
                    "FLAG-" + flagNumber++,
                    player.getParticipantId(),
                    color,
                    zone.getZoneId(),
                    position.getX(),
                    position.getY()
            );
            // La coloca en la zona.
            zone.setFlag(flag);
        }

        // Lista de zonas.
        List<Zone> zones = new ArrayList<>(worldMap.values());
        // Coloca las tazas.
        for (int i = 0; i < IConstants.MAX_AMOUNT_COFFEE; i++) {
            // Zona aleatoria.
            Zone zone = zones.get(random.nextInt(zones.size()));
            // Posicion aleatoria.
            Point2D position = zone.randomPointInside(random, IConstants.ZONE_MARGIN);
            // Agrega la taza.
            zone.addCup(new CoffeeCup(
                    "CUP-" + (i + 1),
                    zone.getZoneId(),
                    position.getX(),
                    position.getY()
            ));
        }
    }

    // Busca la bandera de un jugador.
    public Flag findFlagOf(String ownerId) {
        // Si es nulo, retorna null.
        if (ownerId == null) {
            return null;
        }
        // Recorre las zonas.
        for (Zone zone : worldMap.values()) {
            // Obtiene la bandera.
            Flag flag = zone.getFlag();
            // Si coincide el dueno, la retorna.
            if (flag != null && flag.getOwnerId().equals(ownerId)) {
                return flag;
            }
        }
        // No se encontro.
        return null;
    }

    // Saca al animal de una zona y lo mete en otra.
    public void transferEntity(Animal animal, Zone from, Zone to) {
        // Valida nulos.
        if (animal == null || to == null) {
            return;
        }
        // Si hay zona origen distinta, remueve.
        if (from != null && from != to) {
            from.removeEntity(animal);
        }
        // Agrega a la nueva zona.
        to.addEntity(animal);
        // Actualiza el zoneId del animal.
        animal.setZoneId(to.getZoneId());
    }
}