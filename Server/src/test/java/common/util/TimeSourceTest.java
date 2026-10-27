package common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimeSourceTest {

    @Test
    void elRelojDelSistemaDevuelveElTiempoActual() {
        long before = System.currentTimeMillis();
        long current = TimeSource.system().currentTimeMillis();
        long after = System.currentTimeMillis();

        assertTrue(current >= before);
        assertTrue(current <= after);
    }

    @Test
    void sePuedeSustituirPorUnRelojDePrueba() {
        long[] now = {500};

        TimeSource fakeClock = () -> now[0];

        assertEquals(500, fakeClock.currentTimeMillis());

        now[0] += 250;

        assertEquals(750, fakeClock.currentTimeMillis());
    }
}
