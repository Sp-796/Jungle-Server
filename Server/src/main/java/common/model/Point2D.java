// Paquete comun del modelo: contiene el punto inmutable del mundo compartido entre cliente y servidor.
package common.model;

// Importa la clase Objects para hashCode.
import java.util.Objects;

/**
 * Punto inmutable del mundo (x, y).
 *
 * Se usa para los destinos y para los puntos intermedios de una ruta.
 * Al ser inmutable es seguro compartirlo entre hilos.
 */
// Clase final inmutable que representa un punto (x, y) del mundo.
public final class Point2D {

    // Coordenada horizontal inmutable.
    private final double x;
    // Coordenada vertical inmutable.
    private final double y;

    // Constructor que inicializa las coordenadas del punto.
    public Point2D(double x, double y) {
        // Asigna la coordenada x.
        this.x = x;
        // Asigna la coordenada y.
        this.y = y;
    }

    // Getter de la coordenada x.
    public double getX() {
        // Retorna x.
        return x;
    }

    // Getter de la coordenada y.
    public double getY() {
        // Retorna y.
        return y;
    }

    // Calcula la distancia euclidiana entre este punto y otro.
    public double distanceTo(Point2D other) {
        // Retorna la distancia usando Math.hypot.
        return Math.hypot(this.x - other.x, this.y - other.y);
    }

    // Devuelve un punto que avanza hacia el objetivo sin pasarse de maxDistance.
    public Point2D stepTowards(Point2D target, double maxDistance) {
        // Calcula la distancia total al objetivo.
        double distance = distanceTo(target);
        // Si el objetivo esta cerca o es el mismo punto, devuelve el objetivo.
        if (distance <= maxDistance || distance == 0.0) {
            return target;
        }
        // Calcula la fraccion a avanzar.
        double ratio = maxDistance / distance;
        // Devuelve el nuevo punto avanzado.
        return new Point2D(
                this.x + (target.x - this.x) * ratio,
                this.y + (target.y - this.y) * ratio
        );
    }

    // Compara dos puntos por sus coordenadas.
    @Override
    public boolean equals(Object other) {
        // Si es el mismo objeto, son iguales.
        if (this == other) {
            return true;
        }
        // Si no es Point2D, no son iguales.
        if (!(other instanceof Point2D point)) {
            return false;
        }
        // Compara las coordenadas con tolerancia de punto flotante.
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    // Genera el hashCode del punto.
    @Override
    public int hashCode() {
        // Usa Objects.hash sobre x e y.
        return Objects.hash(x, y);
    }

    // Representacion textual del punto.
    @Override
    public String toString() {
        // Retorna "Point2D(x, y)".
        return "Point2D(" + x + ", " + y + ")";
    }
}