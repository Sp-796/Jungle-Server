package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Point2D;
import constants.IConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.CoffeeCup;
import server.model.Pack;
import server.model.Participant;
import server.model.Player;
import server.model.Zone;

class MapManagerTest {

    private MapManager mapManager;

    @BeforeEach
    void setUp() {
        mapManager = new MapManager();
    }

    // ------------------------------------------------------------------
    // Topologia
    // ------------------------------------------------------------------

    @Test
    void construyeLasDoceZonasDelMapa() {
        assertEquals(IConstants.MAX_ZONES, mapManager.getZones().size());

        Set<String> ids = new HashSet<>();
        for (Zone zone : mapManager.getZones()) {
            ids.add(zone.getZoneId());
            assertNotNull(zone.getDisplayName());
            assertNotNull(zone.getImageName());
        }

        assertEquals(IConstants.MAX_ZONES, ids.size());
        assertTrue(ids.contains("paramo"));
        assertTrue(ids.contains("bosque_profundo"));
        assertTrue(ids.contains("bosque_despejado"));
    }

    @Test
    void respetaLosCaminosDelEnunciado() {
        Zone manglar = mapManager.getZone("manglar");
        Zone bosqueSeco = mapManager.getZone("bosque_seco");
        Zone bosqueProfundo = mapManager.getZone("bosque_profundo");
        Zone paramo = mapManager.getZone("paramo");
        Zone robledales = mapManager.getZone("bosque_robledales");

        assertEquals(Set.of("bosque_seco", "bosque_profundo"), neighbourIds(manglar));
        assertEquals(Set.of("bosque_nuboso", "bosque_robledales"), neighbourIds(paramo));
        assertEquals(Set.of("zona_montanosa", "paramo"), neighbourIds(robledales));
        assertTrue(bosqueSeco.getAdjacentNodes().contains(bosqueProfundo) == false);
    }

    @Test
    void todosLosCaminosSonBidireccionales() {
        for (Zone zone : mapManager.getZones()) {
            for (Zone neighbour : zone.getAdjacentNodes()) {
                assertTrue(
                        neighbour.getAdjacentNodes().contains(zone),
                        zone.getZoneId() + " -> " + neighbour.getZoneId() + " sin regreso"
                );
            }
        }
    }

    private Set<String> neighbourIds(Zone zone) {
        Set<String> ids = new HashSet<>();
        for (Zone neighbour : zone.getAdjacentNodes()) {
            ids.add(neighbour.getZoneId());
        }
        return ids;
    }

    // ------------------------------------------------------------------
    // BFS
    // ------------------------------------------------------------------

    @Test
    void encuentraLaRutaMasCortaEntreDosZonas() {
        Zone start = mapManager.getZone("manglar");
        Zone destination = mapManager.getZone("paramo");

        List<Zone> route = mapManager.calculateRouteBFS(start, destination);

        assertEquals(List.of(
                "manglar",
                "bosque_profundo",
                "bosque_cavernas",
                "bosque_nuboso",
                "paramo"
        ), route.stream().map(Zone::getZoneId).toList());
    }

    @Test
    void laRutaDeUnaZonaASiMismaTieneUnSoloNodo() {
        Zone zone = mapManager.getZone("bosque_catarata");

        assertEquals(List.of(zone), mapManager.calculateRouteBFS(zone, zone));
    }

    @Test
    void devuelveRutaVaciaSiFaltaAlgunaZona() {
        assertTrue(mapManager.calculateRouteBFS(null, mapManager.getZone("paramo")).isEmpty());
        assertTrue(mapManager.calculateRouteBFS(mapManager.getZone("paramo"), null).isEmpty());
    }

    // ------------------------------------------------------------------
    // Consultas y movimiento
    // ------------------------------------------------------------------

    @Test
    void encuentraLaZonaQueContieneUnPunto() {
        for (Zone zone : mapManager.getZones()) {
            Point2D center = zone.getCenter();
            assertEquals(zone, mapManager.findZoneAt(center.getX(), center.getY()));
        }

        assertNull(mapManager.findZoneAt(-1, -1));
        assertNull(mapManager.findZoneAt(IConstants.WORLD_WIDTH + 1, 10));
    }

    @Test
    void mueveAlAnimalPorLosCentrosDeLasZonasIntermedias() {
        Zone start = mapManager.getZone("manglar");
        Zone destination = mapManager.getZone("paramo");
        Point2D startCenter = start.getCenter();
        Point2D target = destination.getCenter();

        Animal animal = animalAt(start, startCenter);

        assertTrue(mapManager.commandMove(animal, target.getX(), target.getY()));
        assertTrue(animal.isMoving());

        // manglar -> bosque_profundo -> cavernas -> nuboso -> paramo
        List<Point2D> route = animal.getRoute();
        assertEquals(4, route.size());
        assertEquals(mapManager.getZone("bosque_profundo").getCenter(), route.get(0));
        assertEquals(mapManager.getZone("bosque_cavernas").getCenter(), route.get(1));
        assertEquals(mapManager.getZone("bosque_nuboso").getCenter(), route.get(2));
        assertEquals(target, route.get(3));
        assertEquals(target.getX(), animal.getTargetX(), 0.0001);
        assertEquals(target.getY(), animal.getTargetY(), 0.0001);
    }

    @Test
    void dentroDeLaMismaZonaVaDirectoAlPuntoExacto() {
        Zone zone = mapManager.getZone("manglar");
        Animal animal = animalAt(zone, zone.getCenter());

        assertTrue(mapManager.commandMove(animal, 50, 500));

        assertEquals(List.of(new Point2D(50, 500)), animal.getRoute());
    }

    @Test
    void rechazaDestinosFueraDelMapa() {
        Zone zone = mapManager.getZone("manglar");
        Animal animal = animalAt(zone, zone.getCenter());

        assertFalse(mapManager.commandMove(animal, -100, 20));
        assertFalse(animal.isMoving());
    }

    // ------------------------------------------------------------------
    // Reparto inicial
    // ------------------------------------------------------------------

    @Test
    void reparteSietePacksEnZonasDistintas() {
        List<Participant> participants = participantsWithPacks(7);

        mapManager.assignStartingZones(participants, new Random(11));

        Set<String> assignedZones = new HashSet<>();
        for (Participant participant : participants) {
            Zone zone = participant.getCurrentZone();
            assertNotNull(zone);
            assignedZones.add(zone.getZoneId());

            for (Animal animal : participant.getPack().getAnimals()) {
                assertEquals(zone.getZoneId(), animal.getZoneId());
                assertTrue(zone.contains(animal.getX(), animal.getY()));
                assertFalse(animal.isMoving());
            }
        }

        assertEquals(7, assignedZones.size());
    }

    @Test
    void colocaDosBanderasYDiezTazas() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Player player = new Player("P-" + (i + 1), "Jugador " + (i + 1));
            Pack pack = new Pack("PACK-" + i, "jaguar", false);
            pack.addAnimal(new Animal("jaguar", 20, 0, 0, pack));
            player.setPack(pack);
            players.add(player);
        }

        mapManager.assignStartingZones(new ArrayList<>(players), new Random(3));
        mapManager.spawnItems(players, new Random(4));

        int flags = 0;
        int cups = 0;

        for (Zone zone : mapManager.getZones()) {
            if (zone.getFlag() != null) {
                flags++;
                assertEquals(zone.getZoneId(), zone.getFlag().getZoneId());
                assertTrue(zone.contains(zone.getFlag().getX(), zone.getFlag().getY()));
            }

            cups += zone.getCupsInside().size();
            for (CoffeeCup cup : zone.getCupsInside()) {
                assertEquals(zone.getZoneId(), cup.getZoneId());
                assertTrue(zone.contains(cup.getX(), cup.getY()));
                assertFalse(cup.isConsumed());
            }
        }

        assertEquals(2, flags);
        assertEquals(IConstants.MAX_AMOUNT_COFFEE, cups);

        // Cada jugador tiene su bandera en su zona inicial.
        for (Player player : players) {
            assertNotNull(mapManager.findFlagOf(player.getParticipantId()));
            assertEquals(player.getCurrentZone().getZoneId(),
                    mapManager.findFlagOf(player.getParticipantId()).getZoneId());
        }
    }

    @Test
    void mueveUnAnimalDeUnaZonaAOtraManteniendoLasListas() {
        Zone from = mapManager.getZone("manglar");
        Zone to = mapManager.getZone("paramo");
        Animal animal = animalAt(from, from.getCenter());

        mapManager.transferEntity(animal, from, to);

        assertFalse(from.getEntitiesInside().contains(animal));
        assertTrue(to.getEntitiesInside().contains(animal));
        assertEquals(to.getZoneId(), animal.getZoneId());
    }

    // ------------------------------------------------------------------
    // Utilidades de prueba
    // ------------------------------------------------------------------

    private Animal animalAt(Zone zone, Point2D point) {
        Pack pack = new Pack("PACK-TEST", "jaguar", false);
        Animal animal = new Animal("jaguar", IConstants.INITIAL_ENERGY_PLAYER,
                point.getX(), point.getY(), pack);
        pack.addAnimal(animal);
        animal.setZoneId(zone.getZoneId());
        zone.addEntity(animal);
        return animal;
    }

    private List<Participant> participantsWithPacks(int count) {
        List<Participant> participants = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Player player = new Player("P-" + (i + 1), "Jugador " + (i + 1));
            Pack pack = new Pack("PACK-" + i, "jaguar", false);
            Animal animal = new Animal("jaguar", 20, 0, 0, pack);
            pack.addAnimal(animal);
            player.setPack(pack);
            participants.add(player);
        }

        return participants;
    }
}
