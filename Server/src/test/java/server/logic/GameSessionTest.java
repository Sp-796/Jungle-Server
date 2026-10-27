package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import common.message.MessageType;
import common.model.Point2D;
import constants.IConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.Bot;
import server.model.CoffeeCup;
import server.model.Pack;
import server.model.Player;
import server.model.Zone;
import server.support.MutableClock;
import server.support.TestFixtures;

class GameSessionTest {

    // ------------------------------------------------------------------
    // Lobby
    // ------------------------------------------------------------------

    @Test
    void laPartidaEmpiezaEnEsperaYPasaASeleccion() {
        GameSession session = new GameSession("ABCD", new Random(1), new MutableClock(0));

        assertEquals(GameSession.Phase.WAITING, session.getPhase());

        session.addPlayer(new Player("P-1", "Ana"));
        assertEquals(GameSession.Phase.WAITING, session.getPhase());

        session.addPlayer(new Player("P-2", "Beto"));
        assertEquals(GameSession.Phase.SELECTING, session.getPhase());
    }

    @Test
    void rechazaUnTercerJugador() {
        GameSession session = TestFixtures.newRunningSession();

        assertThrows(MatchException.class,
                () -> session.addPlayer(new Player("P-3", "Carla")));
    }

    @Test
    void rechazaQueLosDosJugadoresElijanLaMismaEspecie() {
        GameSession session = new GameSession("ABCD", new Random(1), new MutableClock(0));
        Player first = new Player("P-1", "Ana");
        Player second = new Player("P-2", "Beto");
        session.addPlayer(first);
        session.addPlayer(second);

        session.selectSpecies(first, "jaguar");

        assertThrows(MatchException.class,
                () -> session.selectSpecies(second, "JAGUAR"));
    }

    @Test
    void rechazaElegirEspecieDosVeces() {
        GameSession session = new GameSession("ABCD", new Random(1), new MutableClock(0));
        Player first = new Player("P-1", "Ana");
        session.addPlayer(first);
        session.addPlayer(new Player("P-2", "Beto"));

        session.selectSpecies(first, "jaguar");

        assertThrows(MatchException.class,
                () -> session.selectSpecies(first, "mono"));
    }

    @Test
    void rechazaUnaEspecieInexistente() {
        GameSession session = new GameSession("ABCD", new Random(1), new MutableClock(0));
        Player first = new Player("P-1", "Ana");
        session.addPlayer(first);
        session.addPlayer(new Player("P-2", "Beto"));

        assertThrows(MatchException.class,
                () -> session.selectSpecies(first, "dragon"));
    }

    // ------------------------------------------------------------------
    // Inicio
    // ------------------------------------------------------------------

    @Test
    void alIniciarCreaCincoBotsConLasEspeciesLibres() {
        GameSession session = TestFixtures.newRunningSession();

        assertEquals(GameSession.Phase.RUNNING, session.getPhase());
        assertEquals(2, session.getPlayers().size());
        assertEquals(IConstants.BOT_PACK_COUNT, session.getBots().size());

        Set<String> playerSpecies = new HashSet<>();
        for (Player player : session.getPlayers()) {
            playerSpecies.add(player.getSelectedSpecies());
            assertEquals(IConstants.MAX_TEAM_SIZE_PLAYER, player.getPack().getAnimals().size());
            assertEquals(IConstants.INITIAL_DAMAGE_PLAYER, player.getPack().getDamageCapacity());
        }

        Set<String> botSpecies = new HashSet<>();
        for (Bot bot : session.getBots()) {
            assertNotNull(bot.getPack());
            assertEquals(IConstants.MAX_TEAM_SIZE_BOT, bot.getPack().getAnimals().size());
            assertEquals(IConstants.INITIAL_ENERGY_BOT,
                    bot.getPack().getAnimals().get(0).getCurrentEnergy());
            assertEquals(IConstants.INITIAL_DAMAGE_BOT, bot.getPack().getDamageCapacity());

            botSpecies.add(bot.getPack().getSpecies());
            assertFalse(playerSpecies.contains(bot.getPack().getSpecies()));
        }

        assertEquals(IConstants.BOT_PACK_COUNT, botSpecies.size());
    }

    @Test
    void alIniciarColocaBanderasTazasYZonas() {
        GameSession session = TestFixtures.newRunningSession();

        int flags = 0;
        int cups = 0;
        for (Zone zone : session.getMapManager().getZones()) {
            if (zone.getFlag() != null) {
                flags++;
            }
            cups += zone.getCupsInside().size();
        }

        assertEquals(2, flags);
        assertEquals(IConstants.MAX_AMOUNT_COFFEE, cups);

        for (Player player : session.getPlayers()) {
            assertNotNull(player.getCurrentZone());
            assertEquals(player.getCurrentZone().getZoneId(), player.getViewedZoneId());
            assertNotNull(session.getMapManager().findFlagOf(player.getParticipantId()));
        }
    }

    // ------------------------------------------------------------------
    // Movimiento
    // ------------------------------------------------------------------

    @Test
    void mueveSoloLosAnimalesSeleccionados() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);

        Animal first = player.getPack().getLivingAnimals().get(0);
        Animal second = player.getPack().getLivingAnimals().get(1);
        Zone destination = zoneOtherThan(session, first.getZoneId(), player);

        Point2D target = destination.getCenter();

        session.commandMove(player,
                List.of(first.getId(), second.getId()),
                target.getX(), target.getY());

        assertTrue(first.isMoving());
        assertTrue(second.isMoving());
        assertEquals(target.getX(), first.getTargetX(), 0.0001);
        assertEquals(target.getY(), first.getTargetY(), 0.0001);
    }

    @Test
    void rechazaUnDestinoFueraDelMapa() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);
        Animal animal = player.getPack().getLivingAnimals().get(0);

        assertThrows(MatchException.class,
                () -> session.commandMove(player, List.of(animal.getId()), -10, 50));
    }

    @Test
    void rechazaUnaListaDeAnimalesVacia() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);

        assertThrows(MatchException.class,
                () -> session.commandMove(player, List.of(), 100, 100));
    }

    @Test
    void soloSePuedeVerUnaZonaDondeHayAnimalesPropios() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);

        session.viewZone(player, player.getCurrentZone().getZoneId());
        assertEquals(player.getCurrentZone().getZoneId(), player.getViewedZoneId());

        Zone emptyZone = zoneOtherThan(session, player.getCurrentZone().getZoneId(), player);

        assertThrows(MatchException.class,
                () -> session.viewZone(player, emptyZone.getZoneId()));
    }

    // ------------------------------------------------------------------
    // Fin de partida
    // ------------------------------------------------------------------

    @Test
    void tocarLaBanderaRivalTerminaLaPartida() {
        GameSession session = TestFixtures.newRunningSession();
        Player attacker = TestFixtures.playerOne(session);
        Player owner = TestFixtures.playerTwo(session);

        RecordingListener listener = new RecordingListener();
        session.registerListener(attacker.getParticipantId(), listener);
        session.registerListener(owner.getParticipantId(), listener);

        Animal animal = attacker.getPack().getLivingAnimals().get(0);
        var flag = session.getMapManager().findFlagOf(owner.getParticipantId());

        moveAnimalTo(session, animal, flag.getZoneId(), flag.getX(), flag.getY());

        session.update(IConstants.TICK_RATE_MS);

        assertTrue(session.isFinished());
        assertEquals(attacker.getParticipantId(), session.getWinnerId());
        assertEquals("FLAG_TOUCHED", session.getFinishReason());

        // El resumen final llega a los dos jugadores conectados.
        assertEquals(2, listener.count(MessageType.GAME_OVER));
        JsonObject summary = listener.last(MessageType.GAME_OVER);
        assertEquals(attacker.getParticipantId(), summary.get("winnerId").getAsString());
    }

    @Test
    void perderTodoElPackTambienTerminaLaPartida() {
        GameSession session = TestFixtures.newRunningSession();
        Player loser = TestFixtures.playerOne(session);
        Player winner = TestFixtures.playerTwo(session);

        for (Animal animal : loser.getPack().getAnimals()) {
            animal.setCurrentEnergy(0);
        }

        session.update(IConstants.TICK_RATE_MS);

        assertTrue(session.isFinished());
        assertEquals(winner.getParticipantId(), session.getWinnerId());
        assertEquals("PACK_DEFEATED", session.getFinishReason());
    }

    @Test
    void desconectarseDuranteLaPartidaDaLaVictoriaAlRival() {
        GameSession session = TestFixtures.newRunningSession();
        Player leaving = TestFixtures.playerOne(session);
        Player staying = TestFixtures.playerTwo(session);

        RecordingListener listener = new RecordingListener();
        session.registerListener(staying.getParticipantId(), listener);

        session.handleDisconnect(leaving);

        assertFalse(leaving.isConnected());
        assertTrue(session.isFinished());
        assertEquals(staying.getParticipantId(), session.getWinnerId());
        assertEquals("DISCONNECT", session.getFinishReason());
        assertEquals(1, listener.count(MessageType.GAME_OVER));
    }

    @Test
    void siElRivalAbandonaElLobbyElOtroJugadorRecibeUnError() {
        GameSession session = new GameSession("ABCD", new Random(1), new MutableClock(0));
        Player first = new Player("P-1", "Ana");
        Player second = new Player("P-2", "Beto");
        session.addPlayer(first);
        session.addPlayer(second);

        RecordingListener listener = new RecordingListener();
        session.registerListener(second.getParticipantId(), listener);

        session.handleDisconnect(first);

        assertTrue(session.isFinished());
        assertEquals("OPPONENT_LEFT", session.getFinishReason());
        assertEquals(1, listener.count(MessageType.ERROR));
        assertTrue(listener.last(MessageType.ERROR).get("message").getAsString()
                .contains("rival"));
    }

    // ------------------------------------------------------------------
    // Ciclo
    // ------------------------------------------------------------------

    @Test
    void recogerCafeAvisaAlJugadorYSubeElDanoDelPack() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);
        Animal animal = player.getPack().getLivingAnimals().get(0);

        // Se limpian las tazas aleatorias para que la prueba sea determinista.
        for (Zone zone : session.getMapManager().getZones()) {
            for (CoffeeCup cup : new ArrayList<>(zone.getCupsInside())) {
                zone.removeCup(cup);
            }
        }

        Zone zone = session.getMapManager().getZone(animal.getZoneId());
        CoffeeCup cup = new CoffeeCup("CUP-TEST", zone.getZoneId(), animal.getX(), animal.getY());
        zone.addCup(cup);

        RecordingListener listener = new RecordingListener();
        session.registerListener(player.getParticipantId(), listener);

        session.update(IConstants.TICK_RATE_MS);

        assertTrue(cup.isConsumed());
        assertEquals(1, player.getPack().getCoffeeCupsCollected());
        assertEquals(IConstants.INITIAL_DAMAGE_PLAYER + 1,
                player.getPack().getDamageCapacity());
        assertEquals(1, listener.count(MessageType.COFFEE_COLLECTED));
        assertEquals(3, listener.last(MessageType.COFFEE_COLLECTED)
                .get("damageCapacity").getAsInt());
    }

    @Test
    void cadaDosTicksEnviaUnaActualizacionDeEstado() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);

        RecordingListener listener = new RecordingListener();
        session.registerListener(player.getParticipantId(), listener);

        session.update(IConstants.TICK_RATE_MS);
        assertEquals(0, listener.count(MessageType.STATE_UPDATE));

        session.update(IConstants.TICK_RATE_MS);
        assertEquals(1, listener.count(MessageType.STATE_UPDATE));

        session.update(IConstants.TICK_RATE_MS);
        assertEquals(1, listener.count(MessageType.STATE_UPDATE));

        session.update(IConstants.TICK_RATE_MS);
        assertEquals(2, listener.count(MessageType.STATE_UPDATE));
    }

    @Test
    void acumulaLaDuracionRealDeLaPartida() {
        GameSession session = TestFixtures.newRunningSession();

        session.update(1500);
        assertEquals(1, session.getMatchDurationSeconds());

        session.update(500);
        assertEquals(2, session.getMatchDurationSeconds());
    }

    @Test
    void verUnaZonaRespondeConElEstadoActualizado() {
        GameSession session = TestFixtures.newRunningSession();
        Player player = TestFixtures.playerOne(session);

        RecordingListener listener = new RecordingListener();
        session.registerListener(player.getParticipantId(), listener);

        session.viewZone(player, player.getCurrentZone().getZoneId());

        assertEquals(1, listener.count(MessageType.STATE_UPDATE));
    }

    @Test
    void unTickCompletoNoLanzaExcepciones() {
        GameSession session = TestFixtures.newRunningSession();

        for (int i = 0; i < 20; i++) {
            session.update(IConstants.TICK_RATE_MS);
        }

        assertEquals(GameSession.Phase.RUNNING, session.getPhase());
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private Zone zoneOtherThan(GameSession session, String zoneId, Player player) {
        for (Zone zone : session.getMapManager().getZones()) {
            if (!zone.getZoneId().equals(zoneId)) {
                return zone;
            }
        }
        throw new IllegalStateException("No hay otra zona para la prueba");
    }

    private void moveAnimalTo(GameSession session, Animal animal, String zoneId, double x, double y) {
        Zone from = session.getMapManager().getZone(animal.getZoneId());
        Zone to = session.getMapManager().getZone(zoneId);

        session.getMapManager().transferEntity(animal, from, to);
        animal.setX(x);
        animal.setY(y);
        animal.clearRoute();
    }

    /** Doble de prueba del puerto de salida: guarda los mensajes enviados. */
    private static final class RecordingListener implements GameListener {

        private final List<String> types = new ArrayList<>();
        private final List<JsonObject> payloads = new ArrayList<>();

        @Override
        public void onGameMessage(String type, JsonObject payload) {
            types.add(type);
            payloads.add(payload);
        }

        private int count(String type) {
            int total = 0;
            for (String each : types) {
                if (each.equals(type)) {
                    total++;
                }
            }
            return total;
        }

        private JsonObject last(String type) {
            for (int i = types.size() - 1; i >= 0; i--) {
                if (types.get(i).equals(type)) {
                    return payloads.get(i);
                }
            }
            return null;
        }
    }
}
