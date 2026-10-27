package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constants.IConstants;
import java.util.Random;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.Player;

class GameLoopTest {

    @Test
    void actualizaLasPartidasYRetiraLasQueYaTerminaron() {
        MatchManager matchManager = new MatchManager(new Random(3));

        Player first = new Player("P-1", "Ana");
        String code = matchManager.createMatch(first);
        Player second = new Player("P-2", "Beto");
        GameSession session = matchManager.joinMatch(code, second);

        session.selectSpecies(first, "jaguar");
        session.selectSpecies(second, "mono");

        GameLoop loop = new GameLoop(IConstants.TICK_RATE_MS, matchManager);

        loop.updateMatches(IConstants.TICK_RATE_MS);
        assertEquals(1, matchManager.getActiveMatchCount());
        assertTrue(session.isRunning());

        for (Animal animal : first.getPack().getAnimals()) {
            animal.setCurrentEnergy(0);
        }

        loop.updateMatches(IConstants.TICK_RATE_MS);

        assertTrue(session.isFinished());
        assertEquals(0, matchManager.getActiveMatchCount());
    }

    @Test
    void arrancaYSeDetieneSinDejarHilosVivos() {
        MatchManager matchManager = new MatchManager(new Random(3));
        GameLoop loop = new GameLoop(10, matchManager);

        assertFalse(loop.isRunning());

        loop.start();
        assertTrue(loop.isRunning());

        loop.stop();
        assertFalse(loop.isRunning());
    }
}
