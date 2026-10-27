// Paquete del modelo del servidor: contiene las manadas.
package server.model;

// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para la lista de animales.
import java.util.ArrayList;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa List para la lista de animales.
import java.util.List;

/**
 * Manada: agrupa a todos los animales de una especie y guarda las estadisticas
 * que se comparten entre ellos (dano) y las que necesita el resumen final.
 */
// Clase que representa una manada de animales de una especie.
public class Pack {

    // Identificador de la manada.
    private final String packId;
    // Especie de la manada.
    private final String species;
    // Indica si es manada de bot.
    private final boolean botPack;
    // Lista de animales de la manada.
    private final List<Animal> animals = new ArrayList<>();

    // Dano compartido: cada ataque de cualquier miembro resta esto al objetivo.
    private int damageCapacity;

    // Bots eliminados por este pack (solo cuenta si el pack es de un jugador).
    private int botsKilled;

    // Tazas de cafe consumidas por este pack.
    private int coffeeCupsCollected;

    // Constructor de la manada.
    public Pack(String packId, String species, boolean botPack) {
        // Asigna el id.
        this.packId = packId;
        // Asigna la especie.
        this.species = species;
        // Asigna si es bot.
        this.botPack = botPack;
        // Asigna el dano inicial segun el tipo.
        this.damageCapacity = botPack
                ? IConstants.INITIAL_DAMAGE_BOT
                : IConstants.INITIAL_DAMAGE_PLAYER;
    }

    // Agrega un animal a la manada si no esta ya.
    public void addAnimal(Animal animal) {
        // Valida que no sea nulo ni este repetido.
        if (animal != null && !animals.contains(animal)) {
            // Agrega el animal.
            animals.add(animal);
        }
    }

    // El cafe solo beneficia a los packs de jugadores y respeta el tope.
    public void increaseDamage() {
        // Los bots no aumentan dano.
        if (botPack) {
            return;
        }
        // Aumenta el dano sin pasar del maximo.
        damageCapacity = Math.min(
                IConstants.MAX_DAMAGE_CAPACITY_PLAYER,
                damageCapacity + IConstants.CUP_DAMAGE_BONUS
        );
    }

    // Registra una taza recogida por un animal de este pack.
    public void registerCoffeeCup() {
        // Aumenta el contador de tazas.
        coffeeCupsCollected++;
        // Aumenta el dano.
        increaseDamage();
    }

    // Registra la muerte de un animal enemigo.
    public boolean registerKill(Pack victimPack) {
        // Solo cuenta si este pack es de jugador y la victima es bot.
        if (botPack || victimPack == null || !victimPack.isBotPack()) {
            return false;
        }

        // Aumenta el contador de bots eliminados.
        botsKilled++;
        // Si llega a multiplo de 2, aplica bonus de energia.
        if (botsKilled % IConstants.BOTS_PER_ENERGY_BONUS == 0) {
            // Aplica el bonus.
            applyEnergyBonus();
            // Retorna true.
            return true;
        }
        // No hubo bonus.
        return false;
    }

    // Suma energia a todos los animales vivos, sin pasar del maximo.
    public void applyEnergyBonus() {
        // Recorre los animales vivos.
        for (Animal animal : getLivingAnimals()) {
            // Cura 1 punto de energia.
            animal.heal(IConstants.ENERGY_BONUS_AMOUNT);
        }
    }

    // Retorna la lista de animales vivos.
    public List<Animal> getLivingAnimals() {
        // Lista de vivos.
        List<Animal> living = new ArrayList<>();
        // Recorre los animales.
        for (Animal animal : animals) {
            // Si no esta derrotado, lo agrega.
            if (!animal.isDefeated()) {
                living.add(animal);
            }
        }
        // Retorna la lista.
        return living;
    }

    // Retorna la cantidad de animales vivos.
    public int getAliveCount() {
        // Retorna el tamano de la lista de vivos.
        return getLivingAnimals().size();
    }

    // Animales que ya no estan vivos (para el resumen final).
    public int getLostCount() {
        // Total menos vivos.
        return animals.size() - getAliveCount();
    }

    // Indica si la manada fue derrotada.
    public boolean isDefeated() {
        // Si no hay animales o ninguno vivo.
        return animals.isEmpty() || getAliveCount() == 0;
    }

    // Getter del id.
    public String getPackId() {
        // Retorna packId.
        return packId;
    }

    // Getter de la especie.
    public String getSpecies() {
        // Retorna species.
        return species;
    }

    // Indica si es manada de bot.
    public boolean isBotPack() {
        // Retorna botPack.
        return botPack;
    }

    // Retorna los animales como lista inmutable.
    public List<Animal> getAnimals() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(animals);
    }

    // Getter de la capacidad de dano.
    public int getDamageCapacity() {
        // Retorna damageCapacity.
        return damageCapacity;
    }

    // Getter de bots eliminados.
    public int getBotsKilled() {
        // Retorna botsKilled.
        return botsKilled;
    }

    // Getter de tazas consumidas.
    public int getCoffeeCupsCollected() {
        // Retorna coffeeCupsCollected.
        return coffeeCupsCollected;
    }
}