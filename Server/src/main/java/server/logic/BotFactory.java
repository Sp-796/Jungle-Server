// Paquete de la logica del servidor: contiene las fabricas de participantes.
package server.logic;

// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para la lista de animales.
import java.util.ArrayList;
// Importa List para la lista de animales.
import java.util.List;
// Importa Animal del modelo.
import server.model.Animal;
// Importa Bot del modelo.
import server.model.Bot;
// Importa Pack del modelo.
import server.model.Pack;
// Importa Participant del modelo.
import server.model.Participant;

/**
 * Fabrica concreta de bots: 4 animales de 10 de energia y dano 1.
 */
// Clase que implementa IParticipantFactory para crear bots (patron Factory).
public class BotFactory implements IParticipantFactory {

    // Crea un participante bot.
    @Override
    public Participant createParticipant(String participantId, String name) {
        // Retorna un nuevo Bot.
        return new Bot(participantId, name);
    }

    // Crea un pack de bot.
    @Override
    public Pack createPack(String packId, String species) {
        // Crea un pack marcado como bot.
        return new Pack(packId, species, true);
    }

    // Crea los animales del bot.
    @Override
    public List<Animal> createAnimals(Pack pack, int count) {
        // Lista de animales.
        List<Animal> animals = new ArrayList<>();
        // Crea count animales.
        for (int i = 0; i < count; i++) {
            // Crea un animal bot.
            animals.add(new Animal(
                    pack.getSpecies(),
                    IConstants.INITIAL_ENERGY_BOT,
                    0,
                    0,
                    pack
            ));
        }
        // Retorna la lista.
        return animals;
    }
}