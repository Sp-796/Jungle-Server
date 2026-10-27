package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constants.IConstants;
import org.junit.jupiter.api.Test;

class PackTest {

    private Pack playerPack() {
        return new Pack("PACK-P", "jaguar", false);
    }

    private Pack botPack() {
        return new Pack("PACK-B", "mono", true);
    }

    private Animal animalWithEnergy(Pack pack, int energy) {
        Animal animal = new Animal(pack.getSpecies(), energy, 0, 0, pack);
        pack.addAnimal(animal);
        return animal;
    }

    @Test
    void elDanoInicialDependeDelTipoDePack() {
        assertEquals(IConstants.INITIAL_DAMAGE_PLAYER, playerPack().getDamageCapacity());
        assertEquals(IConstants.INITIAL_DAMAGE_BOT, botPack().getDamageCapacity());
    }

    @Test
    void elCafeAumentaElDanoDelPackDeJugador() {
        Pack pack = playerPack();

        pack.registerCoffeeCup();

        assertEquals(IConstants.INITIAL_DAMAGE_PLAYER + 1, pack.getDamageCapacity());
        assertEquals(1, pack.getCoffeeCupsCollected());
    }

    @Test
    void elDanoTieneTopeEnElMaximoTeorico() {
        Pack pack = playerPack();

        for (int i = 0; i < 20; i++) {
            pack.registerCoffeeCup();
        }

        assertEquals(IConstants.MAX_DAMAGE_CAPACITY_PLAYER, pack.getDamageCapacity());
    }

    @Test
    void losBotsNoAumentanSuDanoConElCafe() {
        Pack pack = botPack();

        pack.registerCoffeeCup();

        assertEquals(IConstants.INITIAL_DAMAGE_BOT, pack.getDamageCapacity());
    }

    @Test
    void cadaDosBotsEliminadosElPackRecuperaEnergia() {
        Pack attacker = playerPack();
        Animal own = animalWithEnergy(attacker, 15);

        Pack victims = botPack();
        animalWithEnergy(victims, 1);
        animalWithEnergy(victims, 1);

        assertFalse(attacker.registerKill(victims));
        assertEquals(15, own.getCurrentEnergy());

        assertTrue(attacker.registerKill(victims));
        assertEquals(16, own.getCurrentEnergy());
        assertEquals(2, attacker.getBotsKilled());
    }

    @Test
    void elBonusDeEnergiaNoSuperaElMaximo() {
        Pack attacker = playerPack();
        Animal own = animalWithEnergy(attacker, IConstants.MAX_ENERGY);

        Pack victims = botPack();
        animalWithEnergy(victims, 1);
        animalWithEnergy(victims, 1);

        attacker.registerKill(victims);
        attacker.registerKill(victims);

        assertEquals(IConstants.MAX_ENERGY, own.getCurrentEnergy());
    }

    @Test
    void matarAnimalesDeOtroJugadorNoCuentaComoBot() {
        Pack attacker = playerPack();
        Pack otherPlayer = new Pack("PACK-2", "mono", false);

        boolean bonus = attacker.registerKill(otherPlayer);

        assertFalse(bonus);
        assertEquals(0, attacker.getBotsKilled());
    }

    @Test
    void unPackEstaDerrotadoCuandoTodosSusAnimalesMueren() {
        Pack pack = playerPack();
        Animal first = animalWithEnergy(pack, 2);
        Animal second = animalWithEnergy(pack, 2);

        assertFalse(pack.isDefeated());
        assertEquals(2, pack.getAliveCount());

        first.takeDamage(2);
        second.takeDamage(2);

        assertTrue(pack.isDefeated());
        assertEquals(0, pack.getAliveCount());
        assertEquals(2, pack.getLostCount());
    }
}
