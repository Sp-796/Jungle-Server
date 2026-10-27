package common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Point2DTest {

    @Test
    void calculaLaDistanciaEntreDosPuntos() {
        Point2D origin = new Point2D(0, 0);
        Point2D other = new Point2D(3, 4);

        assertEquals(5.0, origin.distanceTo(other), 0.0001);
    }

    @Test
    void avanzaParcialmenteHaciaElDestino() {
        Point2D start = new Point2D(0, 0);
        Point2D target = new Point2D(100, 0);

        Point2D next = start.stepTowards(target, 25);

        assertEquals(25.0, next.getX(), 0.0001);
        assertEquals(0.0, next.getY(), 0.0001);
    }

    @Test
    void llegaAlDestinoSiLaDistanciaEsMenorQueElPaso() {
        Point2D start = new Point2D(0, 0);
        Point2D target = new Point2D(10, 0);

        Point2D next = start.stepTowards(target, 50);

        assertEquals(target, next);
    }

    @Test
    void noAvanzaSiYaEstaEnElDestino() {
        Point2D point = new Point2D(5, 5);

        assertEquals(point, point.stepTowards(point, 10));
        assertTrue(point.distanceTo(point) == 0.0);
    }
}
