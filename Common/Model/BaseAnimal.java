package Common.Model; // Define el paquete al que debe pertenecer

public abstract class BaseAnimal { // Se define como abstract dado que esta funge como plantilla y no puede crear objetos con "new" 
   
    protected int id; // Identificador unico de cada animal, se usa protected para evitar modificaciones en la herencia
    protected String species; // Establece la especie a la que pertenece el animal
    protected int currentEnergy; // Define la energia actual que tiene el animal para ejecutar una accion
    protected double x; // Establece la posicion horizontal del animal en el mapa
    protected double y; // Establece la posicion vertical del animal en el mapa

    public BaseAnimal(int id, String species, int currentEnergy, double x, double y) { // Constructor para inicializar los valores basicos cuando se cree una clase hija
        
        this.id = id; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase
        this.species = species; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase
        this.currentEnergy = currentEnergy; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase
        this.x = x; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase
        this.y = y; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase

    } 

    public int getId() { //Implementa el "getter" para leer y modificar atributos por encapsulamiento

        return id; // Retorna el valor del id del animal

    }

    public String getSpecies() { //Implementa el "getter" para leer y modificar atributos por encapsulamiento

        return species; // Retorna el nombre de la especie del animal

    }

    public int getCurrentEnergy() { //Implementa el "getter" para leer y modificar atributos por encapsulamiento

        return currentEnergy; // Retorna el valor de la energia actual del animal

    }

    public void setCurrentEnergy(int currentEnergy){ // Implementa un "setter" para actualizar la informacion necesaria

        this.currentEnergy = currentEnergy; // Actualiza el valor de la energia actual en caso de recibir un ataque o cierta accion

    }

    public double getX() { // Implementa el "getter" para obtener determinada informacion
 
        return x; // Retorna el valor actual de la posicion horizontal en el mapa

    }

    public void setX(double x){ // Implementa un "setter" para actualizar la informacion necesaria

        this.x = x; // Actualiza la posicion horizontal segun lo deseado

    }

    public double getY() { // Implementa el "getter" para obtener determinada informacion

        return y; // Retorna el valor actual de la posicion vertical en el mapa

    }

    public void setY(double y) { // Implementa un "setter" para actualizar la informacion necesaria

        this.y = y; // Actualiza el valor de la posicion vertical segun lo necesitado

    }
}
