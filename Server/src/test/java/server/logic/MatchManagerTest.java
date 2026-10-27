package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Player;

class MatchManagerTest {

    private MatchManager matchManager;

    @BeforeEach
    void setUp() {
        // Se usa el constructor de paquete para tener una instancia aislada
        // (el singleton real se probaria con getInstance()).
        matchManager = new MatchManager(new Random(5));
        matchManager.resetForTesting();
    }

    @Test
    void creaUnaPartidaEnEsperaConCodigoDeCuatroCaracteres() {
        String code = matchManager.createMatch(new Player("P-1", "Ana"));

        assertEquals(4, code.length());
        assertEquals(1, matchManager.getActiveMatchCount());

        GameSession session = matchManager.getMatch(code);
        assertNotNull(session);
        assertEquals(GameSession.Phase.WAITING, session.getPhase());
    }

    @Test
    void uneAlSegundoJugadorSinImportarMayusculas() {
        String code = matchManager.createMatch(new Player("P-1", "Ana"));
        Player second = new Player("P-2", "Beto");

        GameSession session = matchManager.joinMatch(code.toLowerCase(), second);

        assertEquals(GameSession.Phase.SELECTING, session.getPhase());
        assertEquals(2, session.getPlayers().size());
        assertTrue(session.getPlayers().contains(second));
    }

    @Test
    void rechazaUnCodigoInexistente() {
        MatchException error = assertThrows(MatchException.class,
                () -> matchManager.joinMatch("ZZZZ", new Player("P-2", "Beto")));

        assertTrue(error.getMessage().contains("ZZZZ"));
    }

    @Test
    void rechazaUnTercerJugador() {
        String code = matchManager.createMatch(new Player("P-1", "Ana"));
        matchManager.joinMatch(code, new Player("P-2", "Beto"));

        assertThrows(MatchException.class,
                () -> matchManager.joinMatch(code, new Player("P-3", "Carla")));
    }

    @Test
    void eliminaUnaSalaSiElJugadorSeVaDelLobby() {
        String code = matchManager.createMatch(new Player("P-1", "Ana"));
        GameSession session = matchManager.getMatch(code);

        matchManager.handleDisconnect(session, session.getPlayers().get(0));

        assertEquals(0, matchManager.getActiveMatchCount());
        assertNull(matchManager.getMatch(code));
    }

    @Test
    void mantenerLaSalaSiLaPartidaSigueEnCursoPorDesconexion() {
        String code = matchManager.createMatch(new Player("P-1", "Ana"));
        Player second = new Player("P-2", "Beto");
        GameSession session = matchManager.joinMatch(code, second);

        Player first = session.getPlayers().get(0);
        session.selectSpecies(first, "jaguar");
        session.selectSpecies(second, "mono");

        matchManager.handleDisconnect(session, first);

        // La partida termino (el rival gano), por eso el GameLoop la puede
        // retirar en la siguiente vuelta; el manager ya la considera inactiva.
        assertTrue(session.isFinished());
        assertEquals(second.getParticipantId(), session.getWinnerId());
    }

    @Test
    void normalizaElCodigoRecortandoEspacios() {
        assertEquals("ABCD", MatchManager.normalizeCode("  abcd "));
        assertNull(MatchManager.normalizeCode(null));
        assertNull(MatchManager.normalizeCode("   "));
    }

    @Test
    void devuelveUnaCopiaDeLasPartidasActivas() {
        matchManager.createMatch(new Player("P-1", "Ana"));
        matchManager.createMatch(new Player("P-2", "Beto"));

        assertEquals(2, matchManager.snapshotMatches().size());
        assertEquals(2, matchManager.getActiveMatches().size());
    }
}
