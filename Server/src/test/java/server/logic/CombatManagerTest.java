package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Species;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.Animal;
import server.model.Bot;
import server.model.Pack;
import server.model.Player;
import server.model.Zone;
import server.support.TestFixtures;

class CombatManagerTest {

    private GameSession session;
    private CombatManager combatManager;

    @BeforeEach
    void setUp() {
        session = TestFixtures.newRunningSession();
        combatManager = session.getCombatManager();
    }

    // ------------------------------------------------------------------
    // Resolucion de un ataque
    // ------------------------------------------------------------------

    @Test
    void unAtaqueRestaElDanoDelPackAtacante() {
        Player attacker = TestFixtures.playerOne(session);
        Animal attackerAnimal = attacker.getPack().getLivingAnimals().get(0);
        Animal target = session.getBots().get(0).getPack().getLivingAnimals().get(0);

        int before = target.getCurrentEnergy();

        combatManager.executeAttack(attackerAnimal, target, attacker.getPack());

        assertEquals(before - attacker.getPack().getDamageCapacity(), target.getCurrentEnergy());
    }

    @Test
    void cuandoElObjetivoMuereSeLimpiaSuRuta() {
        Player attacker = TestFixtures.playerOne(session);
        Animal attackerAnimal = attacker.getPack().getLivingAnimals().get(0);
        Animal target = session.getBots().get(0).getPack().getLivingAnimals().get(0);

        target.setCurrentEnergy(1);
        target.setRoute(List.of(target.getPosition()), target.getX(), target.getY());

        combatManager.executeAttack(attackerAnimal, target, attacker.getPack());

        assertTrue(target.isDefeated());
        assertFalse(target.isMoving());
        assertEquals(0, target.getCurrentEnergy());
    }

    @Test
    void matarDosBotsDisparaElBonusDeEnergiaDelPack() {
        Player player = TestFixtures.playerOne(session);
        Pack playerPack = player.getPack();

        for (Animal animal : playerPack.getLivingAnimals()) {
            animal.setCurrentEnergy(15);
        }

        List<Animal> botAnimals = session.getBots().get(0).getPack().getLivingAnimals();
        Animal first = botAnimals.get(0);
        Animal second = botAnimals.get(1);
        first.setCurrentEnergy(1);
        second.setCurrentEnergy(1);

        Animal attacker = playerPack.getLivingAnimals().get(0);

        combatManager.executeAttack(attacker, first, playerPack);
        assertEquals(15, playerPack.getLivingAnimals().get(0).getCurrentEnergy());

        combatManager.executeAttack(attacker, second, playerPack);

        assertEquals(2, playerPack.getBotsKilled());
        for (Animal animal : playerPack.getLivingAnimals()) {
            assertEquals(16, animal.getCurrentEnergy());
        }
    }

    @Test
    void ignorarAtaquesConDatosInvalidos() {
        Player player = TestFixtures.playerOne(session);
        Animal animal = player.getPack().getLivingAnimals().get(0);

        combatManager.executeAttack(null, animal, player.getPack());
        combatManager.executeAttack(animal, null, player.getPack());
        combatManager.executeAttack(animal, animal, null);

        assertEquals(20, animal.getCurrentEnergy());
    }

    // ------------------------------------------------------------------
    // Bots
    // ------------------------------------------------------------------

    @Test
    void unBotAtacaAutomaticamenteAlJugadorQueTieneAlLado() {
        Player player = TestFixtures.playerOne(session);
        Animal victim = player.getPack().getLivingAnimals().get(0);

        Bot bot = session.getBots().get(0);
        Animal hunter = bot.getPack().getLivingAnimals().get(0);

        moveToZone(hunter, victim.getZoneId(), victim.getX() + 1, victim.getY());

        int before = victim.getCurrentEnergy();

        combatManager.processCombat(session);

        assertEquals(before - bot.getPack().getDamageCapacity(), victim.getCurrentEnergy());
    }

    @Test
    void unBotNoAtacaSiElJugadorEstaLejos() {
        Player player = TestFixtures.playerOne(session);
        Animal victim = player.getPack().getLivingAnimals().get(0);

        Bot bot = session.getBots().get(0);
        Animal hunter = bot.getPack().getLivingAnimals().get(0);

        moveToZone(hunter, victim.getZoneId(), victim.getX() + 500, victim.getY());

        int before = victim.getCurrentEnergy();

        combatManager.processCombat(session);

        assertEquals(before, victim.getCurrentEnergy());
    }

    @Test
    void elEnfriamientoImpideDosAtaquesEnElMismoSegundo() {
        Player player = TestFixtures.playerOne(session);
        Animal victim = player.getPack().getLivingAnimals().get(0);

        Bot bot = session.getBots().get(0);
        Animal hunter = bot.getPack().getLivingAnimals().get(0);

        moveToZone(hunter, victim.getZoneId(), victim.getX() + 1, victim.getY());

        combatManager.processCombat(session);
        int afterFirst = victim.getCurrentEnergy();

        combatManager.processCombat(session);

        assertEquals(afterFirst, victim.getCurrentEnergy());
    }

    // ------------------------------------------------------------------
    // Jugadores
    // ------------------------------------------------------------------

    @Test
    void laOrdenDeAtaqueSeEjecutaEnElSiguienteTick() {
        Player player = TestFixtures.playerOne(session);
        Player opponent = TestFixtures.playerTwo(session);

        Animal attacker = player.getPack().getLivingAnimals().get(0);
        Animal target = opponent.getPack().getLivingAnimals().get(0);

        moveToZone(target, attacker.getZoneId(), attacker.getX() + 1, attacker.getY());

        session.commandAttack(player, List.of(attacker.getId()), target.getId());

        int before = target.getCurrentEnergy();

        combatManager.processCombat(session);

        assertEquals(before - player.getPack().getDamageCapacity(), target.getCurrentEnergy());
    }

    @Test
    void noSePuedeAtacarAlPropioPack() {
        Player player = TestFixtures.playerOne(session);
        Animal attacker = player.getPack().getLivingAnimals().get(0);
        Animal mate = player.getPack().getLivingAnimals().get(1);

        assertThrows(MatchException.class,
                () -> session.commandAttack(player, List.of(attacker.getId()), mate.getId()));
    }

    @Test
    void noSePuedeAtacarAUnAnimalMuerto() {
        Player player = TestFixtures.playerOne(session);
        Player opponent = TestFixtures.playerTwo(session);

        Animal attacker = player.getPack().getLivingAnimals().get(0);
        Animal target = opponent.getPack().getLivingAnimals().get(0);
        target.setCurrentEnergy(0);

        assertThrows(MatchException.class,
                () -> session.commandAttack(player, List.of(attacker.getId()), target.getId()));
    }

    @Test
    void laOrdenSeCancelaCuandoElObjetivoMuere() {
        Player player = TestFixtures.playerOne(session);
        Player opponent = TestFixtures.playerTwo(session);

        Animal attacker = player.getPack().getLivingAnimals().get(0);
        Animal target = opponent.getPack().getLivingAnimals().get(0);

        moveToZone(target, attacker.getZoneId(), attacker.getX() + 1, attacker.getY());
        target.setCurrentEnergy(1);

        session.commandAttack(player, List.of(attacker.getId()), target.getId());
        combatManager.processCombat(session);

        assertTrue(target.isDefeated());
        combatManager.processCombat(session);

        // En el segundo tick la orden ya fue descartada.
        assertEquals(null, session.getAttackOrder(player.getParticipantId()));
    }

    @Test
    void laOrdenSeCancelaCuandoElObjetivoSaleDelRadio() {
        Player player = TestFixtures.playerOne(session);
        Player opponent = TestFixtures.playerTwo(session);

        Animal attacker = player.getPack().getLivingAnimals().get(0);
        Animal target = opponent.getPack().getLivingAnimals().get(0);

        moveToZone(target, attacker.getZoneId(), attacker.getX() + 1, attacker.getY());
        session.commandAttack(player, List.of(attacker.getId()), target.getId());

        combatManager.processCombat(session);
        int afterFirstAttack = target.getCurrentEnergy();

        // El objetivo se aleja: ya no esta en el radio de encuentro.
        target.setX(attacker.getX() + 500);
        combatManager.processCombat(session);

        assertEquals(afterFirstAttack, target.getCurrentEnergy());
        assertEquals(null, session.getAttackOrder(player.getParticipantId()));
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private void moveToZone(Animal animal, String zoneId, double x, double y) {
        Zone targetZone = session.getMapManager().getZone(zoneId);
        Zone from = session.getMapManager().getZone(animal.getZoneId());

        session.getMapManager().transferEntity(animal, from, targetZone);
        animal.setX(x);
        animal.setY(y);
        animal.clearRoute();
    }

    /** Comprobacion basica de que las fabricas producen los packs esperados. */
    @Test
    void lasFabricasCreanLosPacksConSusEstadisticas() {
        IParticipantFactory playerFactory = new PlayerFactory();
        IParticipantFactory botFactory = new BotFactory();

        Pack playerPack = playerFactory.createPack("PACK-P", Species.JAGUAR.getId());
        Pack botPack = botFactory.createPack("PACK-B", Species.MONO.getId());

        assertEquals(2, playerPack.getDamageCapacity());
        assertEquals(1, botPack.getDamageCapacity());
        assertEquals(10, playerFactory.createAnimals(playerPack, 10).size());
        assertEquals(4, botFactory.createAnimals(botPack, 4).size());

        assertFalse(playerFactory.createParticipant("P-1", "Ana").isBot());
        assertTrue(botFactory.createParticipant("BOT-1", "Bot").isBot());
    }
}
