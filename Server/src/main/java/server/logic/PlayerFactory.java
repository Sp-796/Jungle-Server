// Paquete de la logica del servidor: contiene la fabrica de jugadores.
package server.logic;

// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para la lista de animales.
import java.util.ArrayList;
// Importa List para la lista de animales.
import java.util.List;
// Importa Animal del modelo.
import server.model.Animal;
// Importa Pack del modelo.
import server.model.Pack;
// Importa Participant del modelo.
import server.model.Participant;
// Importa Player del modelo.
import server.model.Player;

/**
 * Fabrica concreta de jugadores: 10 animales de 20 de energia y dano 2.
 */
// Clase que implementa IParticipantFactory para crear jugadores (patron Factory).
public class PlayerFactory implements IParticipantFactory {

    // Crea un participante jugador.
    @Override
    public Participant createParticipant(String participantId, String name) {
        // Retorna un nuevo Player.
        return new Player(participantId, name);
    }

    // Crea un pack de jugador.
    @Override
    public Pack createPack(String packId, String species) {
        // Crea un pack marcado como jugador.
        return new Pack(packId, species, false);
    }

    // Crea los animales del jugador.
    @Override
    public List<Animal> createAnimals(Pack pack, int count) {
        // Lista de animales.
        List<Animal> animals = new ArrayList<>();
        // Crea count animales.
        for (int i = 0; i < count; i++) {
            // Crea un animal jugador.
            animals.add(new Animal(
                    pack.getSpecies(),
                    IConstants.INITIAL_ENERGY_PLAYER,
                    0,
                    0,
                    pack
            ));
        }
        // Retorna la lista.
        return animals;
    }
}