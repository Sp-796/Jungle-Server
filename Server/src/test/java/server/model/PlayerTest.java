package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("P-1", "Ana");
    }

    @Test
    void unJugadorNoEsBot() {
        assertFalse(player.isBot());
        assertEquals("P-1", player.getParticipantId());
        assertEquals("Ana", player.getAccountName());
    }

    @Test
    void eligeUnaEspecieValidaUnaSolaVez() {
        assertFalse(player.hasSelectedSpecies());

        assertTrue(player.selectSpecies("JAGUAR"));
        assertTrue(player.hasSelectedSpecies());
        assertEquals("jaguar", player.getSelectedSpecies());

        assertFalse(player.selectSpecies("mono"));
        assertEquals("jaguar", player.getSelectedSpecies());
    }

    @Test
    void rechazaUnaEspecieInexistente() {
        assertFalse(player.selectSpecies("dragon"));
        assertFalse(player.hasSelectedSpecies());
    }

    @Test
    void validaElNickname() {
        assertTrue(Player.isValidNickname("Ana"));
        assertTrue(Player.isValidNickname("  Ana  "));

        assertFalse(Player.isValidNickname(null));
        assertFalse(Player.isValidNickname("   "));
        assertFalse(Player.isValidNickname("a".repeat(21)));
    }
}
