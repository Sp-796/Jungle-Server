package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constants.IConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.Player;
import server.support.TestFixtures;

class SnapshotBuilderTest {

    private GameSession session;
    private SnapshotBuilder builder;
    private Player playerOne;
    private Player playerTwo;

    @BeforeEach
    void setUp() {
        session = TestFixtures.newRunningSession();
        builder = new SnapshotBuilder();
        playerOne = TestFixtures.playerOne(session);
        playerTwo = TestFixtures.playerTwo(session);
    }

    @Test
    void elLobbyInformaAlRivalYLasEspeciesDisponibles() {
        JsonObject joined = builder.matchJoined(session, playerTwo);

        assertEquals(session.getMatchCode(), joined.get("matchCode").getAsString());
        assertEquals(2, joined.get("playerNumber").getAsInt());
        assertEquals(playerOne.getAccountName(), joined.get("opponentName").getAsString());
        assertEquals(5, joined.getAsJsonArray("availableSpecies").size());
    }

    @Test
    void laSeleccionMuestraTuEspecieYLaDelRival() {
        JsonObject selection = builder.animalSelection(session, playerOne);

        assertEquals("jaguar", selection.get("yourSpecies").getAsString());
        assertEquals("mono", selection.get("opponentSpecies").getAsString());
        assertEquals(5, selection.getAsJsonArray("availableSpecies").size());
        assertFalse(selection.getAsJsonArray("availableSpecies").toString().contains("jaguar"));
    }

    @Test
    void elInicioDePartidaIncluyeElMapaCompletoYElPack() {
        JsonObject started = builder.gameStarted(session, playerOne);

        assertEquals(session.getMatchCode(), started.get("matchCode").getAsString());
        assertEquals(playerOne.getParticipantId(), started.get("playerId").getAsString());
        assertEquals("jaguar", started.get("species").getAsString());
        assertEquals(playerOne.getCurrentZone().getZoneId(), started.get("yourZoneId").getAsString());
        assertEquals(IConstants.WORLD_WIDTH, started.get("worldWidth").getAsDouble(), 0.0001);

        JsonArray zones = started.getAsJsonArray("zones");
        assertEquals(IConstants.MAX_ZONES, zones.size());

        for (var element : zones) {
            JsonObject zone = element.getAsJsonObject();
            assertNotNull(zone.get("image").getAsString());
            assertFalse(zone.getAsJsonArray("exits").isEmpty());
        }

        JsonObject pack = started.getAsJsonObject("pack");
        assertEquals("jaguar", pack.get("species").getAsString());
        assertEquals(IConstants.MAX_TEAM_SIZE_PLAYER,
                pack.getAsJsonArray("animals").size());
    }

    @Test
    void elResumenFinalTraeEstadisticasDeLosDosJugadores() {
        for (Animal animal : playerOne.getPack().getAnimals()) {
            animal.setCurrentEnergy(0);
        }
        session.update(IConstants.TICK_RATE_MS);

        JsonObject summary = builder.gameOver(session);

        assertEquals(session.getMatchCode(), summary.get("matchCode").getAsString());
        assertEquals("PACK_DEFEATED", summary.get("reason").getAsString());
        assertEquals(playerTwo.getParticipantId(), summary.get("winnerId").getAsString());
        assertEquals(playerTwo.getAccountName(), summary.get("winnerName").getAsString());

        JsonArray players = summary.getAsJsonArray("players");
        assertEquals(2, players.size());

        JsonObject first = players.get(0).getAsJsonObject();
        assertEquals(0, first.get("alive").getAsInt());
        assertEquals(10, first.get("lost").getAsInt());
        assertEquals("jaguar", first.get("species").getAsString());
    }
}
