package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Point2D;
import constants.IConstants;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnimalTest {

    private Pack pack;
    private Animal animal;

    @BeforeEach
    void setUp() {
        pack = new Pack("PACK-1", "jaguar", false);
        animal = new Animal("jaguar", IConstants.INITIAL_ENERGY_PLAYER, 0, 0, pack);
        pack.addAnimal(animal);
    }

    @Test
    void restaEnergiaYMuereCuandoLlegaACero() {
        animal.takeDamage(5);
        assertEquals(15, animal.getCurrentEnergy());
        assertFalse(animal.isDefeated());

        animal.takeDamage(20);
        assertEquals(0, animal.getCurrentEnergy());
        assertTrue(animal.isDefeated());
    }

    @Test
    void laCuracionNoSuperaLaEnergiaMaxima() {
        animal.setCurrentEnergy(18);

        animal.heal(5);

        assertEquals(IConstants.MAX_ENERGY, animal.getCurrentEnergy());
    }

    @Test
    void respetaElEnfriamientoDeUnSegundo() {
        long start = 1_000L;

        assertTrue(animal.canAttack(start));

        animal.markAttack(start);

        assertFalse(animal.canAttack(start + 500));
        assertFalse(animal.canAttack(start + IConstants.ATTACK_COOLDOWN_MS - 1));
        assertTrue(animal.canAttack(start + IConstants.ATTACK_COOLDOWN_MS));
    }

    @Test
    void seMueveALaVelocidadConstante() {
        animal.setRoute(List.of(new Point2D(100, 0)), 100, 0);

        // 0.5 segundos a 50 unidades por segundo = 25 unidades.
        animal.movePosition(0.5);

        assertEquals(25.0, animal.getX(), 0.0001);
        assertEquals(0.0, animal.getY(), 0.0001);
        assertTrue(animal.isMoving());
    }

    @Test
    void recorreVariosPuntosDeLaRuta() {
        animal.setRoute(
                List.of(new Point2D(10, 0), new Point2D(10, 10), new Point2D(20, 10)),
                20,
                10
        );

        animal.movePosition(1.0); // 50 unidades: alcanza los tres puntos.

        assertEquals(20.0, animal.getX(), 0.0001);
        assertEquals(10.0, animal.getY(), 0.0001);
        assertFalse(animal.isMoving());
    }

    @Test
    void unAnimalMuertoYaNoCamina() {
        animal.takeDamage(IConstants.INITIAL_ENERGY_PLAYER);
        animal.setRoute(List.of(new Point2D(100, 0)), 100, 0);

        animal.movePosition(1.0);

        assertEquals(0.0, animal.getX(), 0.0001);
    }
}
