package server.logic;

import server.model.Pack;
import server.model.Participant;
import server.model.Animal;
import java.util.List;

/**
 * Patron Abstract Factory.
 *
 * Permite crear familias completas de objetos (participante + pack + animales)
 * sin que el resto del codigo sepa si esta creando un jugador o un bot. Cambiar
 * las estadisticas de un bot solo requiere tocar {@link BotFactory}.
 */
public interface IParticipantFactory {

    Participant createParticipant(String participantId, String name);

    Pack createPack(String packId, String species);

    List<Animal> createAnimals(Pack pack, int count);
}
