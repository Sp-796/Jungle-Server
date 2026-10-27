package server.support;

import java.util.Random;
import server.logic.GameSession;
import server.model.Player;

/**
 * Fabrica de partidas en curso para las pruebas.
 *
 * Evita repetir en cada test la secuencia de crear jugadores y elegir especie.
 */
public final class TestFixtures {

    public static final String PLAYER_ONE_SPECIES = "jaguar";
    public static final String PLAYER_TWO_SPECIES = "mono";

    private TestFixtures() {
    }

    public static GameSession newRunningSession() {
        return newRunningSession(new Random(42), new MutableClock(1_000_000L));
    }

    public static GameSession newRunningSession(Random random, MutableClock clock) {
        GameSession session = new GameSession("TEST", random, clock);

        Player playerOne = new Player("P-1", "Ana");
        Player playerTwo = new Player("P-2", "Beto");

        session.addPlayer(playerOne);
        session.addPlayer(playerTwo);

        // Al elegir el segundo jugador, la sesion arranca automaticamente.
        session.selectSpecies(playerOne, PLAYER_ONE_SPECIES);
        session.selectSpecies(playerTwo, PLAYER_TWO_SPECIES);

        return session;
    }

    public static Player playerOne(GameSession session) {
        return session.getPlayers().get(0);
    }

    public static Player playerTwo(GameSession session) {
        return session.getPlayers().get(1);
    }
}
