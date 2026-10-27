// Paquete del modelo del servidor: contiene las zonas del mapa.
package server.model;

// Importa BaseZone del paquete comun (herencia).
import common.model.BaseZone;
// Importa Point2D del paquete comun.
import common.model.Point2D;
// Importa ArrayList para listas internas.
import java.util.ArrayList;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa List para listas.
import java.util.List;
// Importa Random para puntos aleatorios.
import java.util.Random;

/**
 * Zona del mapa.
 *
 * El mundo se representa como un grafo: cada zona es un nodo y cada camino es
 * una arista. Ademas de la topologia, la zona guarda su rectangulo en el mundo
 * para poder dibujarla y para saber que animal esta dentro de ella.
 */
// Clase Zone que extiende BaseZone (herencia) y representa una zona del mapa.
public class Zone extends BaseZone {

    // Nombre visible de la zona.
    private final String displayName;

    // Nombre del archivo SVG que el cliente debe dibujar.
    private final String imageName;

    // Coordenada x del rectangulo de la zona.
    private final double x;
    // Coordenada y del rectangulo.
    private final double y;
    // Ancho del rectangulo.
    private final double width;
    // Alto del rectangulo.
    private final double height;

    // Zonas conectadas por un camino (aristas del grafo).
    private final List<Zone> adjacentNodes = new ArrayList<>();

    // Animales que estan fisicamente dentro del rectangulo.
    private final List<Animal> entitiesInside = new ArrayList<>();

    // Tazas de cafe dentro de la zona.
    private final List<CoffeeCup> cupsInside = new ArrayList<>();

    // Bandera de la zona.
    private Flag flag;

    // Constructor de la zona.
    public Zone(String zoneId,
                String displayName,
                String imageName,
                double x,
                double y,
                double width,
                double height) {
        // Llama al constructor de BaseZone.
        super(zoneId, false);
        // Asigna el nombre visible.
        this.displayName = displayName;
        // Asigna la imagen.
        this.imageName = imageName;
        // Asigna x.
        this.x = x;
        // Asigna y.
        this.y = y;
        // Asigna ancho.
        this.width = width;
        // Asigna alto.
        this.height = height;
    }

    // Conecta dos zonas en ambos sentidos.
    public void connect(Zone other) {
        // Si es nulo o es la misma zona, no hace nada.
        if (other == null || other == this) {
            return;
        }
        // Agrega la conexion si no existe.
        if (!adjacentNodes.contains(other)) {
            adjacentNodes.add(other);
        }
        // Agrega la conexion inversa.
        if (!other.adjacentNodes.contains(this)) {
            other.adjacentNodes.add(this);
        }
    }

    // Indica si un punto esta dentro de la zona.
    public boolean contains(double pointX, double pointY) {
        // Comprueba los limites del rectangulo.
        return pointX >= x && pointX <= x + width
                && pointY >= y && pointY <= y + height;
    }

    // Retorna el centro de la zona.
    public Point2D getCenter() {
        // Calcula el centro.
        return new Point2D(x + width / 2.0, y + height / 2.0);
    }

    // Punto aleatorio dentro de la zona con margen.
    public Point2D randomPointInside(Random random, double margin) {
        // Margen seguro.
        double safeMargin = Math.min(margin, Math.min(width, height) / 2.0);
        // Limites minimos y maximos.
        double minX = x + safeMargin;
        double maxX = x + width - safeMargin;
        double minY = y + safeMargin;
        double maxY = y + height - safeMargin;

        // Coordenadas aleatorias.
        double randomX = minX + random.nextDouble() * (maxX - minX);
        double randomY = minY + random.nextDouble() * (maxY - minY);
        // Retorna el punto.
        return new Point2D(randomX, randomY);
    }

    // Agrega un animal a la zona.
    public void addEntity(Animal animal) {
        // Valida y evita duplicados.
        if (animal != null && !entitiesInside.contains(animal)) {
            entitiesInside.add(animal);
        }
    }

    // Quita un animal de la zona.
    public void removeEntity(Animal animal) {
        // Remueve el animal.
        entitiesInside.remove(animal);
    }

    // Agrega una taza a la zona.
    public void addCup(CoffeeCup cup) {
        // Valida y evita duplicados.
        if (cup != null && !cupsInside.contains(cup)) {
            cupsInside.add(cup);
        }
    }

    // Quita una taza de la zona.
    public void removeCup(CoffeeCup cup) {
        // Remueve la taza.
        cupsInside.remove(cup);
    }

    // Getter del nombre visible.
    public String getDisplayName() {
        // Retorna displayName.
        return displayName;
    }

    // Getter del nombre de imagen.
    public String getImageName() {
        // Retorna imageName.
        return imageName;
    }

    // Getter de x.
    public double getX() {
        // Retorna x.
        return x;
    }

    // Getter de y.
    public double getY() {
        // Retorna y.
        return y;
    }

    // Getter del ancho.
    public double getWidth() {
        // Retorna width.
        return width;
    }

    // Getter del alto.
    public double getHeight() {
        // Retorna height.
        return height;
    }

    // Getter de las zonas vecinas (inmutable).
    public List<Zone> getAdjacentNodes() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(adjacentNodes);
    }

    // Getter de las entidades dentro (inmutable).
    public List<Animal> getEntitiesInside() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(entitiesInside);
    }

    // Getter de las tazas dentro (inmutable).
    public List<CoffeeCup> getCupsInside() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(cupsInside);
    }

    // Getter de la bandera.
    public Flag getFlag() {
        // Retorna flag.
        return flag;
    }

    // Setter de la bandera.
    public void setFlag(Flag flag) {
        // Asigna la bandera.
        this.flag = flag;
        // Actualiza hasFlag segun si hay bandera.
        setHasFlag(flag != null);
    }
}