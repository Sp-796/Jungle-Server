package constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IConstantsTest {

    @Test
    void lasEstadisticasDelEnunciadoEstanCentralizadas() {
        assertEquals(20, IConstants.INITIAL_ENERGY_PLAYER);
        assertEquals(10, IConstants.INITIAL_ENERGY_BOT);
        assertEquals(20, IConstants.MAX_ENERGY);
        assertEquals(2, IConstants.INITIAL_DAMAGE_PLAYER);
        assertEquals(1, IConstants.INITIAL_DAMAGE_BOT);
        assertEquals(12, IConstants.MAX_DAMAGE_CAPACITY_PLAYER);
        assertEquals(1000, IConstants.ATTACK_COOLDOWN_MS);
        assertEquals(10, IConstants.MAX_TEAM_SIZE_PLAYER);
        assertEquals(4, IConstants.MAX_TEAM_SIZE_BOT);
        assertEquals(5, IConstants.BOT_PACK_COUNT);
        assertEquals(12, IConstants.MAX_ZONES);
        assertEquals(10, IConstants.MAX_AMOUNT_COFFEE);
    }

    @Test
    void elCodigoDePartidaTieneCuatroCaracteresAlfanumericos() {
        assertEquals(4, IConstants.MATCH_CODE_LENGTH);
        assertEquals(36, IConstants.MATCH_CODE_ALPHABET.length());
        assertTrue(IConstants.MATCH_CODE_ALPHABET.contains("A"));
        assertTrue(IConstants.MATCH_CODE_ALPHABET.contains("Z"));
        assertTrue(IConstants.MATCH_CODE_ALPHABET.contains("0"));
        assertTrue(IConstants.MATCH_CODE_ALPHABET.contains("9"));
    }

    @Test
    void laGeometriaDelMundoEsCoherente() {
        assertEquals(IConstants.WORLD_WIDTH / IConstants.ZONE_COLUMNS,
                IConstants.ZONE_WIDTH, 0.0001);
        assertEquals(IConstants.WORLD_HEIGHT / IConstants.ZONE_ROWS,
                IConstants.ZONE_HEIGHT, 0.0001);
        assertEquals(12, IConstants.ZONE_COLUMNS * IConstants.ZONE_ROWS);
    }

    @Test
    void laRedYLosTiemposTienenValoresValidos() {
        assertEquals(8080, IConstants.DEFAULT_SERVER_PORT);
        assertEquals(2, IConstants.MAX_PLAYERS);
        assertTrue(IConstants.TICK_RATE_MS > 0);
        assertTrue(IConstants.STATE_BROADCAST_EVERY_TICKS > 0);
        assertEquals(1000.0, IConstants.MILLIS_PER_SECOND, 0.0001);
        assertEquals(1_000_000L, IConstants.NANOS_PER_MILLI);
    }
}
