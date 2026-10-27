// Paquete comun del modelo: contiene los datos minimos de un animal compartidos entre cliente y servidor.
package common.model;

/**
 * Datos minimos de un animal que comparten cliente y servidor.
 *
 * Es abstracta porque por si sola no representa nada: el servidor necesita
 * ruta, cooldown y estado de combate; el cliente necesita interpolacion.
 */
// Clase abstracta que sirve de plantilla para los animales del cliente y del servidor (herencia).
public abstract class BaseAnimal {

    // Identificador unico del animal, compartido por cliente y servidor.
    protected final int id;
    // Especie del animal (mono, tucan, etc.).
    protected final String species;
    // Energia actual del animal, se comparte para que el cliente la muestre.
    protected int currentEnergy;
    // Posicion horizontal del animal en el mundo.
    protected double x;
    // Posicion vertical del animal en el mundo.
    protected double y;

    // Constructor protegido usado por las clases hijas para inicializar los datos basicos.
    protected BaseAnimal(int id, String species, int currentEnergy, double x, double y) {
        // Asigna el identificador recibido.
        this.id = id;
        // Asigna la especie recibida.
        this.species = species;
        // Asigna la energia inicial recibida.
        this.currentEnergy = currentEnergy;
        // Asigna la posicion horizontal inicial.
        this.x = x;
        // Asigna la posicion vertical inicial.
        this.y = y;
    }

    // Getter del identificador del animal.
    public int getId() {
        // Retorna el id del animal.
        return id;
    }

    // Getter de la especie del animal.
    public String getSpecies() {
        // Retorna la especie del animal.
        return species;
    }

    // Getter de la energia actual del animal.
    public int getCurrentEnergy() {
        // Retorna la energia actual.
        return currentEnergy;
    }

    // Setter de la energia actual, evita valores negativos.
    public void setCurrentEnergy(int currentEnergy) {
        // Guarda la energia sin permitir valores menores que cero.
        this.currentEnergy = Math.max(0, currentEnergy);
    }

    // Getter de la posicion horizontal.
    public double getX() {
        // Retorna la coordenada x.
        return x;
    }

    // Setter de la posicion horizontal.
    public void setX(double x) {
        // Actualiza la coordenada x.
        this.x = x;
    }

    // Getter de la posicion vertical.
    public double getY() {
        // Retorna la coordenada y.
        return y;
    }

    // Setter de la posicion vertical.
    public void setY(double y) {
        // Actualiza la coordenada y.
        this.y = y;
    }
}