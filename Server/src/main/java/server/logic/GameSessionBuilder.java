package server.logic;

import common.model.Species;
import common.util.TimeSource;
import constants.IConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import server.model.Animal;
import server.model.Bot;
import server.model.Pack;
import server.model.Participant;
import server.model.Player;

/**
 * Patron Builder.
 *
 * Arma una partida paso a paso para que GameSession no tenga un constructor
 * gigante ni conozca los detalles de creacion de bots, mapa e items.
 *
 * Orden tipico usado por GameSession.startGame():
 *   reset -> buildMap -> buildPlayerPacks -> buildBots ->
 *   buildCombatManager -> buildStartingZones -> buildItems -> getResult
 */
public class GameSessionBuilder {

    private final Random random;
    private final TimeSource clock;
    private GameSession session;

    public GameSessionBuilder(Random random, TimeSource clock) {
        this.random = random;
        this.clock = clock;
    }

    /** Prepara el builder para completar una sesion que ya existe. */
    public void reset(GameSession session) {
        this.session = session;
    }

    /** Prepara el builder para crear una sesion nueva desde cero. */
    public void reset(String matchCode) {
        this.session = new GameSession(matchCode, random, clock);
    }

    public void buildPlayers(Player player1, Player player2) {
        buildPlayer(player1);
        buildPlayer(player2);
    }

    /** Agrega un solo jugador (usado al crear la sala en espera). */
    public void buildPlayer(Player player) {
        session.addPlayer(player);
    }

    public void buildMap(MapManager mapManager) {
        session.setMapManager(mapManager);
    }

    public void buildCombatManager(CombatManager combatManager) {
        session.setCombatManager(combatManager);
    }

    /**
     * Crea el pack de cada jugador con su especie elegida y sus 10 animales.
     * Debe llamarse despues de que ambos eligieron especie y antes de repartir
     * las zonas iniciales.
     */
    public void buildPlayerPacks(IParticipantFactory playerFactory) {
        int playerNumber = 1;

        for (Player player : session.getPlayers()) {
            Pack pack = playerFactory.createPack(
                    "PACK-P-" + playerNumber,
                    player.getSelectedSpecies()
            );

            for (Animal animal : playerFactory.createAnimals(pack, IConstants.MAX_TEAM_SIZE_PLAYER)) {
                pack.addAnimal(animal);
            }

            player.setPack(pack);
            playerNumber++;
        }
    }

    /**
     * Crea un pack bot por cada especie que no eligio ninguno de los dos
     * jugadores. Con dos especies ocupadas quedan exactamente 5 bots.
     */
    public void buildBots(IParticipantFactory botFactory) {
        List<Species> available = new ArrayList<>(List.of(Species.values()));

        for (Player player : session.getPlayers()) {
            available.removeIf(species -> species.getId().equals(player.getSelectedSpecies()));
        }

        List<Bot> bots = new ArrayList<>();
        int botNumber = 1;

        int amount = Math.min(available.size(), IConstants.BOT_PACK_COUNT);

        for (int i = 0; i < amount; i++) {
            Species species = available.get(i);

            Bot bot = (Bot) botFactory.createParticipant(
                    "BOT-" + botNumber,
                    "Bot " + species.getDisplayName()
            );

            Pack pack = botFactory.createPack("PACK-BOT-" + botNumber, species.getId());

            for (Animal animal : botFactory.createAnimals(pack, IConstants.MAX_TEAM_SIZE_BOT)) {
                pack.addAnimal(animal);
            }

            bot.setPack(pack);
            bots.add(bot);
            botNumber++;
        }

        session.setBots(bots);
    }

    /** Reparte las 7 manadas en 7 zonas distintas. */
    public void buildStartingZones() {
        List<Participant> participants = new ArrayList<>(session.getPlayers());
        participants.addAll(session.getBots());

        session.getMapManager().assignStartingZones(participants, random);
    }

    /** Coloca las 2 banderas y las 10 tazas de cafe. */
    public void buildItems() {
        session.getMapManager().spawnItems(session.getPlayers(), random);
    }

    public GameSession getResult() {
        return session;
    }
}
