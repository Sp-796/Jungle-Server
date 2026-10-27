// Paquete del modelo del servidor: contiene los animales controlados por el servidor.
package server.model;

// Importa BaseAnimal del paquete comun (herencia).
import common.model.BaseAnimal;
// Importa Point2D del paquete comun.
import common.model.Point2D;
// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para la ruta.
import java.util.ArrayList;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa List para la ruta.
import java.util.List;
// Importa AtomicInteger para ids unicos.
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Animal controlado por el servidor.
 *
 * Extiende {@link BaseAnimal} (datos compartidos con el cliente) y agrega todo
 * lo que solo el servidor necesita: ruta, zona, enfriamiento de ataque y estado
 * de combate.
 */
// Clase que extiende BaseAnimal (herencia) y agrega logica del servidor.
public class Animal extends BaseAnimal {

    // Contador global para que ningun animal de la partida repita id (patron: id autoincremental).
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    // Manada a la que pertenece el animal.
    private final Pack pack;

    // Puntos que el animal debe recorrer en orden: centros de zonas + destino final.
    private final List<Point2D> route = new ArrayList<>();
    // Indice actual dentro de la ruta.
    private int routeIndex;

    // Destino final pedido por el jugador (se envia al cliente para interpolar).
    private double targetX;
    private double targetY;

    // Id de la zona donde esta el animal.
    private String zoneId;

    // Momento del ultimo ataque (para cooldown).
    private long lastAttackTime;
    // Indica si ya ha atacado al menos una vez.
    private boolean hasAttacked;

    // Constructor del animal.
    public Animal(String species, int energy, double x, double y, Pack pack) {
        // Llama al constructor de BaseAnimal con id autoincremental.
        super(NEXT_ID.getAndIncrement(), species, energy, x, y);
        // Asigna la manada.
        this.pack = pack;
    }

    // Resta energia. La muerte se evalua con isDefeated().
    public void takeDamage(int amount) {
        // Si el dano no es positivo, no hace nada.
        if (amount <= 0) {
            return;
        }
        // Resta el dano a la energia actual.
        setCurrentEnergy(getCurrentEnergy() - amount);
    }

    // Cura sin pasar del maximo permitido.
    public void heal(int amount) {
        // Si la cura no es positiva, no hace nada.
        if (amount <= 0) {
            return;
        }
        // Suma la cura sin superar MAX_ENERGY.
        setCurrentEnergy(Math.min(IConstants.MAX_ENERGY, getCurrentEnergy() + amount));
    }

    // Un animal muere cuando su energia llega a cero.
    public boolean isDefeated() {
        // Retorna true si la energia es <= 0.
        return getCurrentEnergy() <= 0;
    }

    // Indica si el enfriamiento de ataque ya termino.
    public boolean canAttack(long currentTimeMillis) {
        // Un animal derrotado no puede atacar.
        if (isDefeated()) {
            return false;
        }
        // Puede atacar si nunca ha atacado o si paso el cooldown.
        return !hasAttacked || currentTimeMillis - lastAttackTime >= IConstants.ATTACK_COOLDOWN_MS;
    }

    // Registra que el animal acaba de atacar (inicia el enfriamiento).
    public void markAttack(long currentTimeMillis) {
        // Marca que ya ataco.
        this.hasAttacked = true;
        // Guarda el momento del ataque.
        this.lastAttackTime = currentTimeMillis;
    }

    // Avanza la posicion a lo largo de la ruta.
    public void movePosition(double deltaSeconds) {
        // Si esta derrotado o el delta no es positivo, no se mueve.
        if (isDefeated() || deltaSeconds <= 0) {
            return;
        }

        // Distancia que puede avanzar en este tick.
        double remaining = IConstants.ANIMAL_SPEED * deltaSeconds;

        // Avanza paso a paso por la ruta.
        while (remaining > 0 && routeIndex < route.size()) {
            // Obtiene el siguiente punto objetivo.
            Point2D target = route.get(routeIndex);
            // Obtiene la posicion actual.
            Point2D current = getPosition();
            // Calcula la distancia al objetivo.
            double distance = current.distanceTo(target);

            // Si ya llego al punto, avanza al siguiente.
            if (distance == 0.0) {
                routeIndex++;
                continue;
            }

            // Si alcanza o supera el objetivo en este tick.
            if (distance <= remaining) {
                // Mueve al objetivo.
                setX(target.getX());
                setY(target.getY());
                // Resta la distancia recorrida.
                remaining -= distance;
                // Avanza al siguiente punto.
                routeIndex++;
            } else {
                // Avanza parcialmente hacia el objetivo.
                Point2D next = current.stepTowards(target, remaining);
                // Actualiza la posicion.
                setX(next.getX());
                setY(next.getY());
                // Ya no queda distancia.
                remaining = 0;
            }
        }
    }

    // Reemplaza la ruta actual. La lista se copia para no depender del llamador.
    public void setRoute(List<Point2D> waypoints, double targetX, double targetY) {
        // Limpia la ruta anterior.
        route.clear();
        // Si hay waypoints, los copia.
        if (waypoints != null) {
            route.addAll(waypoints);
        }
        // Reinicia el indice.
        routeIndex = 0;
        // Guarda el destino final.
        this.targetX = targetX;
        this.targetY = targetY;
    }

    // Limpia la ruta actual.
    public void clearRoute() {
        // Limpia la ruta.
        route.clear();
        // Reinicia el indice.
        routeIndex = 0;
        // El destino pasa a ser la posicion actual.
        targetX = getX();
        targetY = getY();
    }

    // Indica si el animal se esta moviendo.
    public boolean isMoving() {
        // Hay puntos pendientes por recorrer.
        return routeIndex < route.size();
    }

    // Retorna la posicion actual como Point2D.
    public Point2D getPosition() {
        // Crea un nuevo Point2D con la posicion actual.
        return new Point2D(getX(), getY());
    }

    // Retorna la ruta como lista inmutable.
    public List<Point2D> getRoute() {
        // Retorna una vista inmutable.
        return Collections.unmodifiableList(route);
    }

    // Retorna la manada del animal.
    public Pack getPack() {
        // Retorna pack.
        return pack;
    }

    // Retorna el id de la zona actual.
    public String getZoneId() {
        // Retorna zoneId.
        return zoneId;
    }

    // Asigna el id de la zona actual.
    public void setZoneId(String zoneId) {
        // Actualiza zoneId.
        this.zoneId = zoneId;
    }

    // Retorna la coordenada x del destino final.
    public double getTargetX() {
        // Retorna targetX.
        return targetX;
    }

    // Retorna la coordenada y del destino final.
    public double getTargetY() {
        // Retorna targetY.
        return targetY;
    }
}