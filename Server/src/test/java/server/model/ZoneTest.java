package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Point2D;
import constants.IConstants;
import org.junit.jupiter.api.Test;

class ZoneTest {

    private Zone zone() {
        return new Zone(
                "zona",
                "Zona de prueba",
                "zona.svg",
                100,
                200,
                300,
                150
        );
    }

    @Test
    void calcularCentroYContenerPuntos() {
        Zone zone = zone();

        assertEquals(new Point2D(250, 275), zone.getCenter());
        assertTrue(zone.contains(100, 200));
        assertTrue(zone.contains(250, 275));
        assertFalse(zone.contains(99, 275));
        assertFalse(zone.contains(250, 351));
    }

    @Test
    void losCaminosSiempreSonBidireccionales() {
        Zone first = zone();
        Zone second = new Zone("otra", "Otra", "otra.svg", 500, 200, 100, 100);

        first.connect(second);

        assertTrue(first.getAdjacentNodes().contains(second));
        assertTrue(second.getAdjacentNodes().contains(first));

        // Conectar dos veces no duplica la arista.
        first.connect(second);
        assertEquals(1, first.getAdjacentNodes().size());
    }

    @Test
    void unPuntoAleatorioCaeDentroDeLaZonaRespetandoElMargen() {
        Zone zone = zone();

        for (int i = 0; i < 100; i++) {
            Point2D point = zone.randomPointInside(new java.util.Random(i), IConstants.ZONE_MARGIN);

            assertTrue(point.getX() >= 100 + IConstants.ZONE_MARGIN);
            assertTrue(point.getX() <= 100 + 300 - IConstants.ZONE_MARGIN);
            assertTrue(point.getY() >= 200 + IConstants.ZONE_MARGIN);
            assertTrue(point.getY() <= 200 + 150 - IConstants.ZONE_MARGIN);
        }
    }

    @Test
    void laBanderaActualizaElIndicadorDeLaZona() {
        Zone zone = zone();
        Flag flag = new Flag("FLAG-1", "P-1", "Guaria Morada", "zona", 200, 250);

        zone.setFlag(flag);

        assertTrue(zone.hasFlag());
        assertEquals(flag, zone.getFlag());

        zone.setFlag(null);

        assertFalse(zone.hasFlag());
    }
}
