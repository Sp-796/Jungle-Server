// Paquete de la logica del servidor: contiene el gestor de combate.
package server.logic;

// Importa las constantes del juego.
import constants.IConstants;
// Importa Animal del modelo.
import server.model.Animal;
// Importa AttackOrder del modelo.
import server.model.AttackOrder;
// Importa Bot del modelo.
import server.model.Bot;
// Importa Pack del modelo.
import server.model.Pack;
// Importa Player del modelo.
import server.model.Player;

/**
 * Modulo delegado del combate.
 *
 * Reglas:
 *  - Los bots atacan automaticamente a cualquier animal de un jugador que
 *    entre en su radio. Cada bot pega con su propio enfriamiento.
 *  - Los jugadores atacan solo cuando dan una orden explicita (ATTACK).
 *    La orden se mantiene hasta que el objetivo muere o sale del radio.
 *  - Los bots nunca se atacan entre si.
 */
// Clase que gestiona el combate entre animales.
public class CombatManager {

    // Procesa el combate de la partida.
    public void processCombat(GameSession session) {
        // Obtiene el tiempo actual.
        long now = session.now();
        // Procesa ataques de bots.
        processBotAttacks(session, now);
        // Procesa ataques de jugadores.
        processPlayerAttacks(session, now);
    }

    // Procesa los ataques automaticos de los bots.
    private void processBotAttacks(GameSession session, long now) {
        // Recorre los bots.
        for (Bot bot : session.getBots()) {
            // Si el bot no tiene manada o esta derrotado, salta.
            if (bot.getPack() == null || bot.getPack().isDefeated()) {
                continue;
            }

            // Recorre los jugadores.
            for (Player player : session.getPlayers()) {
                // Si el jugador no tiene manada, salta.
                if (player.getPack() == null) {
                    continue;
                }

                // Recorre los animales vivos del jugador.
                for (Animal target : player.getPack().getLivingAnimals()) {
                    // Recorre los animales vivos del bot.
                    for (Animal attacker : bot.getPack().getLivingAnimals()) {
                        // Si no esta en rango, salta.
                        if (!inRange(attacker, target)) {
                            continue;
                        }
                        // Si no puede atacar por cooldown, salta.
                        if (!attacker.canAttack(now)) {
                            continue;
                        }

                        // Ejecuta el ataque.
                        executeAttack(attacker, target, bot.getPack());
                        // Marca el ataque.
                        attacker.markAttack(now);
                    }
                }
            }
        }
    }

    // Procesa los ataques explicitos de los jugadores.
    private void processPlayerAttacks(GameSession session, long now) {
        // Recorre los jugadores.
        for (Player player : session.getPlayers()) {
            // Obtiene la orden de ataque.
            AttackOrder order = session.getAttackOrder(player.getParticipantId());

            // Si no hay orden, salta.
            if (order == null) {
                continue;
            }
            // Si la orden ya no esta activa, la limpia.
            if (!order.isActive()) {
                session.clearAttackOrder(player.getParticipantId());
                continue;
            }

            // Obtiene el objetivo.
            Animal target = order.getTarget();

            // Indica si algun atacante esta en rango.
            boolean anyInRange = false;
            // Recorre los atacantes.
            for (Animal attacker : order.getAttackers()) {
                // Si esta en rango, marca y rompe.
                if (inRange(attacker, target)) {
                    anyInRange = true;
                    break;
                }
            }

            // Si el objetivo se alejo, descarta la orden.
            if (!anyInRange) {
                session.clearAttackOrder(player.getParticipantId());
                continue;
            }

            // Recorre los atacantes.
            for (Animal attacker : order.getAttackers()) {
                // Si no esta en rango, salta.
                if (!inRange(attacker, target)) {
                    continue;
                }
                // Si no puede atacar por cooldown, salta.
                if (!attacker.canAttack(now)) {
                    continue;
                }

                // Ejecuta el ataque.
                executeAttack(attacker, target, player.getPack());
                // Marca el ataque.
                attacker.markAttack(now);
            }
        }
    }

    // Aplica el dano del pack atacante.
    public void executeAttack(Animal attacker, Animal target, Pack attackerPack) {
        // Valida nulos.
        if (attacker == null || target == null || attackerPack == null) {
            return;
        }
        // Si alguno esta derrotado, no ataca.
        if (attacker.isDefeated() || target.isDefeated()) {
            return;
        }

        // Aplica el dano.
        target.takeDamage(attackerPack.getDamageCapacity());
        // Valida la muerte.
        validateDeath(target);

        // Si murio, registra la baja.
        if (target.isDefeated()) {
            attackerPack.registerKill(target.getPack());
        }
    }

    // Un animal muerto no puede seguir caminando.
    public void validateDeath(Animal target) {
        // Si esta derrotado, limpia la ruta.
        if (target != null && target.isDefeated()) {
            target.clearRoute();
        }
    }

    // Dos animales solo pelean si estan en la misma zona y dentro del radio.
    private boolean inRange(Animal attacker, Animal target) {
        // Si alguno esta derrotado, no.
        if (attacker.isDefeated() || target.isDefeated()) {
            return false;
        }
        // Si no estan en la misma zona, no.
        if (attacker.getZoneId() == null || !attacker.getZoneId().equals(target.getZoneId())) {
            return false;
        }
        // Calcula la distancia.
        double distance = attacker.getPosition().distanceTo(target.getPosition());
        // Retorna si esta dentro del radio.
        return distance <= IConstants.ENCOUNTER_RADIUS;
    }
}