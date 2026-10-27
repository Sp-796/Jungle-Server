// Paquete del modelo del servidor: contiene las ordenes de ataque.
package server.model;

// Importa ArrayList para copiar la lista.
import java.util.ArrayList;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa List para los atacantes.
import java.util.List;

/**
 * Orden de ataque de un jugador: "estos animales atacan a este objetivo".
 *
 * El jugador no ataca una vez, sino que mantiene la orden. El servidor la
 * resuelve en cada tick: cada atacante pega cuando su enfriamiento lo permite,
 * mientras siga vivo, en la misma zona y dentro del radio de encuentro.
 */
// Clase que representa una orden de ataque persistente.
public class AttackOrder {

    // Lista de animales atacantes.
    private final List<Animal> attackers;
    // Animal objetivo del ataque.
    private final Animal target;

    // Constructor de la orden de ataque.
    public AttackOrder(List<Animal> attackers, Animal target) {
        // Copia la lista de atacantes para no depender del llamador.
        this.attackers = new ArrayList<>(attackers);
        // Asigna el objetivo.
        this.target = target;
    }

    // La orden sigue vigente mientras el objetivo este vivo.
    public boolean isActive() {
        // Retorna true si el objetivo no es nulo y no esta derrotado.
        return target != null && !target.isDefeated();
    }

    // Retorna los atacantes como lista inmutable.
    public List<Animal> getAttackers() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(attackers);
    }

    // Retorna el objetivo del ataque.
    public Animal getTarget() {
        // Retorna target.
        return target;
    }
}