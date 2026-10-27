package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.GameState;
import server.model.Player;
import server.model.Zone;
import server.support.TestFixtures;

class VisibilityServiceTest {

    private GameSession session;
    private VisibilityService visibility;
    private Player player;

    @BeforeEach
    void setUp() {
        session = TestFixtures.newRunningSession();
        visibility = new VisibilityService();
        player = TestFixtures.playerOne(session);
    }

    @Test
    void alInicioSoloSeVeLaZonaDondeEstaElPack() {
        GameState state = visibility.buildState(session, player);

        assertNotNull(state);
        assertEquals(player.getCurrentZone().getZoneId(), state.toJson().get("viewedZone")
                .getAsJsonObject().get("id").getAsString());

        List<Zone> available = visibility.zonesWithLivingAnimals(session, player);
        assertEquals(1, available.size());
        assertEquals(player.getCurrentZone(), available.get(0));
    }

    @Test
    void soloDevuelveAnimalesDeLaZonaVisible() {
        GameState state = visibility.buildState(session, player);
        JsonObject json = state.toJson();

        int visible = json.getAsJsonArray("animals").size();
        assertTrue(visible >= player.getPack().getAliveCount());

        for (var element : json.getAsJsonArray("animals")) {
            JsonObject animal = element.getAsJsonObject();
            assertEquals(player.getCurrentZone().getZoneId(),
                    animal.get("zoneId").getAsString());
        }

        // Los animales del rival estan en otra zona y no deben aparecer.
        for (Animal opponentAnimal : TestFixtures.playerTwo(session).getPack().getLivingAnimals()) {
            assertFalse(containsAnimal(json, opponentAnimal.getId()));
        }
    }

    @Test
    void laVistaCaeAUnaZonaValidaSiLaPedidaYaNoTieneAnimales() {
        for (Zone zone : session.getMapManager().getZones()) {
            if (!zone.getZoneId().equals(player.getCurrentZone().getZoneId())) {
                player.setViewedZoneId(zone.getZoneId());
                break;
            }
        }

        GameState state = visibility.buildState(session, player);

        assertNotNull(state.toJson().get("viewedZone"));
        assertEquals(player.getCurrentZone().getZoneId(), player.getViewedZoneId());
    }

    @Test
    void elJugadorPuedeVerTodasLasZonasDondeTengaAnimalesVivos() {
        Animal animal = player.getPack().getLivingAnimals().get(0);
        Zone destination = null;

        for (Zone zone : session.getMapManager().getZones()) {
            if (!zone.getZoneId().equals(animal.getZoneId())) {
                destination = zone;
                break;
            }
        }

        session.getMapManager().transferEntity(
                animal,
                session.getMapManager().getZone(animal.getZoneId()),
                destination
        );

        List<Zone> available = visibility.zonesWithLivingAnimals(session, player);
        assertEquals(2, available.size());
        assertTrue(available.contains(player.getCurrentZone()));
        assertTrue(available.contains(destination));
    }

    @Test
    void losAnimalesMuertosNoHabilitanZonas() {
        int previous = visibility.zonesWithLivingAnimals(session, player).size();

        for (Animal each : player.getPack().getAnimals()) {
            each.setCurrentEnergy(0);
        }

        assertEquals(0, visibility.zonesWithLivingAnimals(session, player).size());
        GameState state = visibility.buildState(session, player);
        assertNull(state.toJson().get("viewedZone"));
        assertEquals(1, previous);
    }

    @Test
    void laInstantaneaIncluyeDatosDelPackYDelRival() {
        JsonObject json = visibility.buildState(session, player).toJson();

        assertEquals(session.getMatchCode(), json.get("matchCode").getAsString());
        assertEquals(player.getAccountName(), json.get("playerName").getAsString());
        assertEquals(TestFixtures.playerTwo(session).getAccountName(),
                json.get("opponentName").getAsString());
        assertFalse(json.get("gameOver").getAsBoolean());
        assertFalse(json.get("playerWon").getAsBoolean());

        JsonObject pack = json.getAsJsonObject("myPack");
        assertEquals(player.getSelectedSpecies(), pack.get("species").getAsString());
        assertEquals(10, pack.get("total").getAsInt());
        assertEquals(10, pack.get("alive").getAsInt());
        assertEquals(2, pack.get("damageCapacity").getAsInt());

        assertEquals(10, json.getAsJsonArray("myAnimals").size());
    }

    private boolean containsAnimal(JsonObject json, int animalId) {
        for (var element : json.getAsJsonArray("animals")) {
            if (element.getAsJsonObject().get("id").getAsInt() == animalId) {
                return true;
            }
        }
        return false;
    }
}
