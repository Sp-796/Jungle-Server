// Paquete de la logica del servidor: contiene una partida completa.
package server.logic;

// Importa JsonObject para mensajes.
import com.google.gson.JsonObject;
// Importa MessageType para los tipos de mensaje.
import common.message.MessageType;
// Importa Point2D para posiciones.
import common.model.Point2D;
// Importa Species para validar especies.
import common.model.Species;
// Importa TimeSource para el reloj.
import common.util.TimeSource;
// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para listas internas.
import java.util.ArrayList;
// Importa Collections para listas inmutables.
import java.util.Collections;
// Importa HashMap para mapas.
import java.util.HashMap;
// Importa List para listas.
import java.util.List;
// Importa Map para mapas.
import java.util.Map;
// Importa Random para aleatoriedad.
import java.util.Random;
// Importa ConcurrentHashMap para mapas concurrentes.
import java.util.concurrent.ConcurrentHashMap;
// Importa Animal del modelo.
import server.model.Animal;
// Importa AttackOrder del modelo.
import server.model.AttackOrder;
// Importa Bot del modelo.
import server.model.Bot;
// Importa CoffeeCup del modelo.
import server.model.CoffeeCup;
// Importa Flag del modelo.
import server.model.Flag;
// Importa GameState del modelo.
import server.model.GameState;
// Importa Participant del modelo.
import server.model.Participant;
// Importa Player del modelo.
import server.model.Player;
// Importa Zone del modelo.
import server.model.Zone;

/**
 * Una partida completa y aislada.
 *
 * Es el nucleo del servidor: guarda el estado, valida las ordenes de los
 * jugadores, corre la simulacion y envia las actualizaciones. Todos los metodos
 * publicos que tocan estado estan sincronizados: el hilo del GameLoop y los
 * hilos de los ClientHandler comparten la misma sesion.
 */
// Clase que representa una partida (contexto compartido).
public class GameSession {

    // Estados posibles de una partida.
    public enum Phase {
        // Solo esta el primer jugador.
        WAITING,
        // Estan los dos eligiendo especie.
        SELECTING,
        // Partida en curso.
        RUNNING,
        // Termino, se envio el resumen.
        FINISHED
    }

    // Codigo de la partida.
    private final String matchCode;
    // Generador aleatorio.
    private final Random random;
    // Reloj inyectable.
    private final TimeSource clock;

    // Fase actual de la partida.
    private Phase phase = Phase.WAITING;

    // Lista de jugadores.
    private final List<Player> players = new ArrayList<>();
    // Lista de bots.
    private final List<Bot> bots = new ArrayList<>();

    // Gestor del mapa.
    private MapManager mapManager;
    // Gestor de combate.
    private CombatManager combatManager;
    // Servicio de visibilidad.
    private final VisibilityService visibilityService = new VisibilityService();
    // Constructor de snapshots.
    private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();

    // Oyentes por participante.
    private final Map<String, GameListener> listeners = new ConcurrentHashMap<>();

    // Orden de ataque vigente de cada jugador.
    private final Map<String, AttackOrder> attackOrders = new HashMap<>();

    // Duracion de la partida en ms.
    private long matchDurationMs;
    // Contador de ticks.
    private long tickCounter;

    // Indica si la partida termino.
    private boolean gameOver;
    // Id del ganador.
    private String winnerId;
    // Razon de finalizacion.
    private String finishReason;

    // Constructor con reloj del sistema.
    public GameSession(String matchCode, Random random) {
        // Delega al constructor completo.
        this(matchCode, random, TimeSource.system());
    }

    // Constructor completo.
    public GameSession(String matchCode, Random random, TimeSource clock) {
        // Asigna el codigo.
        this.matchCode = matchCode;
        // Asigna el random.
        this.random = random;
        // Asigna el reloj.
        this.clock = clock;
    }

    // Agrega un jugador. Solo se permiten dos.
    public synchronized void addPlayer(Player player) {
        // Si ya inicio o termino, lanza excepcion.
        if (phase == Phase.RUNNING || phase == Phase.FINISHED) {
            throw new MatchException("La partida ya inicio");
        }
        // Si ya hay dos jugadores, lanza excepcion.
        if (players.size() >= IConstants.MAX_PLAYERS) {
            throw new MatchException("La partida ya tiene dos jugadores");
        }
        // Si ya esta en la lista, no hace nada.
        if (players.contains(player)) {
            return;
        }

        // Asigna el color de la bandera.
        player.setFlagColor(players.isEmpty()
                ? IConstants.FLAG_COLOR_PLAYER_ONE
                : IConstants.FLAG_COLOR_PLAYER_TWO);
        // Agrega el jugador.
        players.add(player);

        // Si ya hay dos, pasa a seleccion.
        if (players.size() == IConstants.MAX_PLAYERS) {
            phase = Phase.SELECTING;
        }
    }

    // Registra la especie elegida por un jugador.
    public synchronized void selectSpecies(Player player, String species) {
        // Si no esta en seleccion, lanza excepcion.
        if (phase != Phase.SELECTING) {
            throw new MatchException("La partida no esta en seleccion de especie");
        }
        // Si el jugador no pertenece, lanza excepcion.
        if (player == null || !players.contains(player)) {
            throw new MatchException("El jugador no pertenece a esta partida");
        }
        // Si la especie no es valida, lanza excepcion.
        if (!Species.isValid(species)) {
            throw new MatchException("Especie invalida: " + species);
        }

        // Normaliza la especie.
        String normalized = Species.fromId(species).getId();

        // Verifica que el otro jugador no haya elegido la misma.
        for (Player other : players) {
            if (other != player && normalized.equals(other.getSelectedSpecies())) {
                throw new MatchException("Esa especie ya fue elegida por el otro jugador");
            }
        }

        // Registra la seleccion.
        if (!player.selectSpecies(normalized)) {
            throw new MatchException("Ya elegiste una especie");
        }

        // Verifica si todos eligieron.
        boolean everyoneChose = players.size() == IConstants.MAX_PLAYERS
                && players.stream().allMatch(Player::hasSelectedSpecies);

        // Si todos eligieron, inicia la partida.
        if (everyoneChose) {
            startGame();
        }
    }

    // Ensambla la partida con el Builder.
    public synchronized void startGame() {
        // Si no esta en seleccion, lanza excepcion.
        if (phase != Phase.SELECTING) {
            throw new MatchException("La partida no esta lista para iniciar");
        }
        // Si no hay dos jugadores, lanza excepcion.
        if (players.size() != IConstants.MAX_PLAYERS) {
            throw new MatchException("Se necesitan dos jugadores para iniciar");
        }

        // Crea el builder.
        GameSessionBuilder builder = new GameSessionBuilder(random, clock);
        // Reinicia el builder con esta sesion.
        builder.reset(this);
        // Construye el mapa.
        builder.buildMap(new MapManager());
        // Construye los packs de jugadores.
        builder.buildPlayerPacks(new PlayerFactory());
        // Construye los bots.
        builder.buildBots(new BotFactory());
        // Construye el gestor de combate.
        builder.buildCombatManager(new CombatManager());
        // Construye las zonas iniciales.
        builder.buildStartingZones();
        // Construye los items.
        builder.buildItems();

        // Asigna la zona vista inicial.
        for (Player player : players) {
            player.setViewedZoneId(player.getCurrentZone().getZoneId());
        }

        // Cambia la fase.
        phase = Phase.RUNNING;

        // Envia GAME_STARTED a cada jugador.
        for (Player player : players) {
            sendToPlayer(player.getParticipantId(), MessageType.GAME_STARTED,
                    snapshotBuilder.gameStarted(this, player));
        }
    }

    // Un tick del servidor.
    public synchronized void update(int deltaMs) {
        // Si no esta en curso o delta no positivo, no hace nada.
        if (phase != Phase.RUNNING || deltaMs <= 0) {
            return;
        }

        // Delta en segundos.
        double deltaSeconds = deltaMs / IConstants.MILLIS_PER_SECOND;
        // Acumula la duracion.
        matchDurationMs += deltaMs;

        // Mueve los animales.
        moveAnimals(deltaSeconds);
        // Recoge cafe.
        collectCoffee();
        // Procesa combate.
        combatManager.processCombat(this);
        // Verifica banderas.
        checkFlags();
        // Verifica packs derrotados.
        checkPacksDefeated();

        // Si ya no esta en curso, sale.
        if (phase != Phase.RUNNING) {
            return;
        }

        // Actualiza la IA de los bots.
        for (Bot bot : bots) {
            bot.updateAI(this, deltaSeconds);
        }

        // Incrementa el contador de ticks.
        tickCounter++;
        // Cada N ticks, envia estado.
        if (tickCounter % IConstants.STATE_BROADCAST_EVERY_TICKS == 0) {
            broadcastState();
        }
    }

    // Mueve todos los animales.
    private void moveAnimals(double deltaSeconds) {
        // Recorre todos los animales.
        for (Animal animal : getAllAnimals()) {
            // Si esta derrotado, salta.
            if (animal.isDefeated()) {
                continue;
            }

            // Mueve el animal.
            animal.movePosition(deltaSeconds);

            // Verifica si cambio de zona.
            Zone currentZone = mapManager.findZoneAt(animal.getX(), animal.getY());
            if (currentZone != null && !currentZone.getZoneId().equals(animal.getZoneId())) {
                // Transfiere la entidad a la nueva zona.
                mapManager.transferEntity(
                        animal,
                        mapManager.getZone(animal.getZoneId()),
                        currentZone
                );
            }
        }
    }

    // Consume las tazas que toque cualquier animal vivo de un jugador.
    private void collectCoffee() {
        // Recorre los jugadores.
        for (Player player : players) {
            // Si no tiene manada, salta.
            if (player.getPack() == null) {
                continue;
            }

            // Recorre los animales vivos.
            for (Animal animal : player.getPack().getLivingAnimals()) {
                // Obtiene la zona del animal.
                Zone zone = mapManager.getZone(animal.getZoneId());
                // Si no existe, salta.
                if (zone == null) {
                    continue;
                }

                // Lista de tazas consumidas.
                List<CoffeeCup> consumed = new ArrayList<>();

                // Recorre las tazas de la zona.
                for (CoffeeCup cup : zone.getCupsInside()) {
                    // Si ya esta consumida, salta.
                    if (cup.isConsumed()) {
                        continue;
                    }

                    // Posicion de la taza.
                    Point2D cupPosition = new Point2D(cup.getX(), cup.getY());
                    // Si el animal la toca.
                    if (animal.getPosition().distanceTo(cupPosition) <= IConstants.CUP_RADIUS) {
                        // Consume la taza.
                        cup.consume();
                        // Registra en la manada.
                        player.getPack().registerCoffeeCup();
                        // Agrega a consumidas.
                        consumed.add(cup);
                        // Notifica al jugador.
                        notifyCoffeeCollected(player, cup);
                    }
                }

                // Remueve las tazas consumidas.
                for (CoffeeCup cup : consumed) {
                    zone.removeCup(cup);
                }
            }
        }
    }

    // Notifica al jugador que recogio una taza.
    private void notifyCoffeeCollected(Player player, CoffeeCup cup) {
        // Crea el payload.
        JsonObject payload = new JsonObject();
        // Agrega el id de la taza.
        payload.addProperty("cupId", cup.getCupId());
        // Agrega x.
        payload.addProperty("x", cup.getX());
        // Agrega y.
        payload.addProperty("y", cup.getY());
        // Agrega la capacidad de dano.
        payload.addProperty("damageCapacity", player.getPack().getDamageCapacity());
        // Envia el mensaje.
        sendToPlayer(player.getParticipantId(), MessageType.COFFEE_COLLECTED, payload);
    }

    // Gana quien toque la bandera del rival.
    private void checkFlags() {
        // Recorre los jugadores.
        for (Player attacker : players) {
            // Obtiene el oponente.
            Player owner = getOpponent(attacker);
            // Si no hay oponente o no tiene manada, salta.
            if (owner == null || attacker.getPack() == null) {
                continue;
            }

            // Busca la bandera del oponente.
            Flag flag = mapManager.findFlagOf(owner.getParticipantId());
            // Si no existe, salta.
            if (flag == null) {
                continue;
            }

            // Posicion de la bandera.
            Point2D flagPosition = new Point2D(flag.getX(), flag.getY());

            // Recorre los animales del atacante.
            for (Animal animal : attacker.getPack().getLivingAnimals()) {
                // Si toca la bandera, termina la partida.
                if (animal.getPosition().distanceTo(flagPosition) <= IConstants.FLAG_RADIUS) {
                    finish(attacker.getParticipantId(), IConstants.FINISH_REASON_FLAG_TOUCHED);
                    return;
                }
            }
        }
    }

    // Si un jugador se queda sin animales, pierde.
    private void checkPacksDefeated() {
        // Recorre los jugadores.
        for (Player player : players) {
            // Si tiene manada y esta derrotada.
            if (player.getPack() != null && player.getPack().isDefeated()) {
                // El ganador es el oponente.
                Player winner = getOpponent(player);
                // Termina la partida.
                finish(winner == null ? null : winner.getParticipantId(),
                        IConstants.FINISH_REASON_PACK_DEFEATED);
                return;
            }
        }
    }

    // Termina la partida.
    private void finish(String winnerId, String reason) {
        // Si ya termino, no hace nada.
        if (gameOver) {
            return;
        }

        // Marca como terminada.
        gameOver = true;
        phase = Phase.FINISHED;
        this.winnerId = winnerId;
        this.finishReason = reason;

        // Construye el resumen.
        JsonObject summary = snapshotBuilder.gameOver(this);

        // Envia GAME_OVER a cada jugador conectado.
        for (Player player : players) {
            if (player.isConnected()) {
                sendToPlayer(player.getParticipantId(),
                        MessageType.GAME_OVER, summary);
            }
        }
    }

    // Mueve los animales indicados hacia un punto del mundo.
    public synchronized void commandMove(Player player, List<Integer> animalIds,
                                         double targetX, double targetY) {
        // Valida que la partida este en curso.
        requireRunning();
        // Valida que el jugador pertenezca.
        requireOwnPlayer(player);

        // Si el destino esta fuera del mapa, lanza excepcion.
        if (mapManager.findZoneAt(targetX, targetY) == null) {
            throw new MatchException("El destino esta fuera del mapa");
        }
        // Si no se seleccionaron animales, lanza excepcion.
        if (animalIds == null || animalIds.isEmpty()) {
            throw new MatchException("No se seleccionaron animales");
        }

        // Mueve cada animal seleccionado.
        for (Integer id : animalIds) {
            Animal animal = findOwnAnimal(player, id);
            if (animal != null && !animal.isDefeated()) {
                mapManager.commandMove(animal, targetX, targetY);
            }
        }
    }

    // Da la orden de ataque.
    public synchronized void commandAttack(Player player, List<Integer> animalIds, int targetAnimalId) {
        // Valida que la partida este en curso.
        requireRunning();
        // Valida que el jugador pertenezca.
        requireOwnPlayer(player);

        // Busca el objetivo.
        Animal target = findAnyAnimal(targetAnimalId);
        // Si no existe o esta derrotado, lanza excepcion.
        if (target == null || target.isDefeated()) {
            throw new MatchException("El objetivo no existe o ya esta eliminado");
        }
        // No se puede atacar a los propios.
        if (target.getPack() == player.getPack()) {
            throw new MatchException("No puedes atacar a tus propios animales");
        }
        // Si no se seleccionaron animales, lanza excepcion.
        if (animalIds == null || animalIds.isEmpty()) {
            throw new MatchException("No se seleccionaron animales");
        }

        // Lista de atacantes validos.
        List<Animal> attackers = new ArrayList<>();
        // Valida cada animal.
        for (Integer id : animalIds) {
            Animal animal = findOwnAnimal(player, id);
            if (animal != null && !animal.isDefeated()) {
                attackers.add(animal);
            }
        }

        // Si no hay atacantes validos, lanza excepcion.
        if (attackers.isEmpty()) {
            throw new MatchException("Ninguno de los animales seleccionados puede atacar");
        }

        // Guarda la orden de ataque.
        attackOrders.put(player.getParticipantId(), new AttackOrder(attackers, target));
    }

    // Cambia la zona que el jugador esta mirando.
    public synchronized void viewZone(Player player, String zoneId) {
        // Valida que la partida este en curso.
        requireRunning();
        // Valida que el jugador pertenezca.
        requireOwnPlayer(player);

        // Obtiene la zona.
        Zone zone = mapManager.getZone(zoneId);
        // Si no existe, lanza excepcion.
        if (zone == null) {
            throw new MatchException("Zona invalida: " + zoneId);
        }
        // Si no tiene animales vivos en esa zona, lanza excepcion.
        if (!visibilityService.zonesWithLivingAnimals(this, player).contains(zone)) {
            throw new MatchException("No tienes animales vivos en esa zona");
        }

        // Asigna la zona vista.
        player.setViewedZoneId(zone.getZoneId());
        // Envia el estado actualizado.
        sendToPlayer(player.getParticipantId(), MessageType.STATE_UPDATE,
                visibilityService.buildState(this, player).toJson());
    }

    // Maneja la desconexion de un jugador.
    public synchronized void handleDisconnect(Player player) {
        // Si es nulo, no hace nada.
        if (player == null) {
            return;
        }

        // Marca como desconectado.
        player.setConnected(false);
        // Remueve el oyente.
        removeListener(player.getParticipantId());

        // Si esta en curso, el rival gana.
        if (phase == Phase.RUNNING) {
            Player opponent = getOpponent(player);
            finish(opponent == null ? null : opponent.getParticipantId(),
                    IConstants.FINISH_REASON_DISCONNECT);
            return;
        }

        // Si esta en lobby/seleccion, avisa al otro.
        if (phase == Phase.WAITING || phase == Phase.SELECTING) {
            // Payload del mensaje.
            JsonObject payload = new JsonObject();
            payload.addProperty("message", "El rival abandono la sala");

            // Envia a los otros jugadores.
            for (Player other : players) {
                if (other != player && other.isConnected()) {
                    sendToPlayer(other.getParticipantId(), MessageType.ERROR, payload);
                }
            }

            // Marca como terminada.
            gameOver = true;
            phase = Phase.FINISHED;
            finishReason = IConstants.FINISH_REASON_OPPONENT_LEFT;
        }
    }

    // Registra un oyente.
    public void registerListener(String participantId, GameListener listener) {
        // Agrega al mapa.
        listeners.put(participantId, listener);
    }

    // Remueve un oyente.
    public void removeListener(String participantId) {
        // Remueve del mapa.
        listeners.remove(participantId);
    }

    // Envia un mensaje a un jugador.
    public void sendToPlayer(String participantId, String type, JsonObject payload) {
        // Obtiene el oyente.
        GameListener listener = listeners.get(participantId);
        // Si existe, envia.
        if (listener != null) {
            listener.onGameMessage(type, payload);
        }
    }

    // Envia a todos los oyentes.
    public void broadcastAction(String type, JsonObject payload) {
        // Recorre los oyentes.
        for (String participantId : listeners.keySet()) {
            // Envia a cada uno.
            sendToPlayer(participantId, type, payload);
        }
    }

    // Envia el estado a todos los jugadores.
    private void broadcastState() {
        // Recorre los jugadores.
        for (Player player : players) {
            // Si no esta conectado, salta.
            if (!player.isConnected()) {
                continue;
            }
            // Construye el estado.
            GameState state = visibilityService.buildState(this, player);
            // Envia el estado.
            sendToPlayer(player.getParticipantId(), MessageType.STATE_UPDATE, state.toJson());
        }
    }

    // Obtiene la orden de ataque.
    public synchronized AttackOrder getAttackOrder(String participantId) {
        // Retorna la orden.
        return attackOrders.get(participantId);
    }

    // Limpia la orden de ataque.
    public synchronized void clearAttackOrder(String participantId) {
        // Remueve la orden.
        attackOrders.remove(participantId);
    }

    // Retorna todos los animales.
    public List<Animal> getAllAnimals() {
        // Lista de animales.
        List<Animal> animals = new ArrayList<>();
        // Recorre los jugadores.
        for (Player player : players) {
            // Si tiene manada, agrega sus animales.
            if (player.getPack() != null) {
                animals.addAll(player.getPack().getAnimals());
            }
        }
        // Recorre los bots.
        for (Bot bot : bots) {
            // Si tiene manada, agrega sus animales.
            if (bot.getPack() != null) {
                animals.addAll(bot.getPack().getAnimals());
            }
        }
        // Retorna la lista.
        return animals;
    }

    // Obtiene el oponente de un jugador.
    public Player getOpponent(Player player) {
        // Recorre los jugadores.
        for (Player other : players) {
            // Si no es el mismo, lo retorna.
            if (!other.getParticipantId().equals(player.getParticipantId())) {
                return other;
            }
        }
        // Si no hay oponente.
        return null;
    }

    // Busca un animal propio.
    private Animal findOwnAnimal(Player player, Integer animalId) {
        // Si el id o la manada es nulo, retorna null.
        if (animalId == null || player.getPack() == null) {
            return null;
        }
        // Recorre los animales de la manada.
        for (Animal animal : player.getPack().getAnimals()) {
            // Si el id coincide, retorna.
            if (animal.getId() == animalId) {
                return animal;
            }
        }
        // No encontrado.
        return null;
    }

    // Busca cualquier animal por id.
    private Animal findAnyAnimal(int animalId) {
        // Recorre todos los animales.
        for (Animal animal : getAllAnimals()) {
            // Si el id coincide, retorna.
            if (animal.getId() == animalId) {
                return animal;
            }
        }
        // No encontrado.
        return null;
    }

    // Valida que la partida este en curso.
    private void requireRunning() {
        // Si no esta en curso, lanza excepcion.
        if (phase != Phase.RUNNING) {
            throw new MatchException("La partida no ha iniciado");
        }
    }

    // Valida que el jugador pertenezca.
    private void requireOwnPlayer(Player player) {
        // Si es nulo o no pertenece, lanza excepcion.
        if (player == null || !players.contains(player)) {
            throw new MatchException("El jugador no pertenece a esta partida");
        }
    }

    // Getter del codigo.
    public String getMatchCode() {
        // Retorna matchCode.
        return matchCode;
    }

    // Getter de la fase.
    public Phase getPhase() {
        // Retorna phase.
        return phase;
    }

    // Indica si esta en curso.
    public boolean isRunning() {
        // Retorna true si esta en RUNNING.
        return phase == Phase.RUNNING;
    }

    // Indica si termino.
    public boolean isFinished() {
        // Retorna true si esta en FINISHED.
        return phase == Phase.FINISHED;
    }

    // Getter de los jugadores (inmutable).
    public List<Player> getPlayers() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(players);
    }

    // Getter de los bots (inmutable).
    public List<Bot> getBots() {
        // Retorna vista inmutable.
        return Collections.unmodifiableList(bots);
    }

    // Getter del gestor del mapa.
    public MapManager getMapManager() {
        // Retorna mapManager.
        return mapManager;
    }

    // Getter del gestor de combate.
    public CombatManager getCombatManager() {
        // Retorna combatManager.
        return combatManager;
    }

    // Getter del random.
    public Random getRandom() {
        // Retorna random.
        return random;
    }

    // Getter del tiempo actual.
    public long now() {
        // Retorna el tiempo del reloj.
        return clock.currentTimeMillis();
    }

    // Getter de la duracion en segundos.
    public int getMatchDurationSeconds() {
        // Convierte ms a segundos.
        return (int) (matchDurationMs / 1000);
    }

    // Getter del ganador.
    public String getWinnerId() {
        // Retorna winnerId.
        return winnerId;
    }

    // Getter de la razon de finalizacion.
    public String getFinishReason() {
        // Retorna finishReason.
        return finishReason;
    }

    // Setter del gestor del mapa (usado por el builder).
    void setMapManager(MapManager mapManager) {
        // Asigna mapManager.
        this.mapManager = mapManager;
    }

    // Setter de los bots (usado por el builder).
    void setBots(List<Bot> botList) {
        // Limpia la lista.
        bots.clear();
        // Si no es nula, copia.
        if (botList != null) {
            bots.addAll(botList);
        }
    }

    // Setter del gestor de combate (usado por el builder).
    void setCombatManager(CombatManager combatManager) {
        // Asigna combatManager.
        this.combatManager = combatManager;
    }
}