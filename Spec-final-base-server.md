Spec server - Caso 2 POO

Constants (package constants)

	interface IConstants // Centraliza los valores fijos compartidos por el servidor y el cliente

		+ int INITIAL_ENERGY_PLAYER = 20 // Energia inicial de cada animal perteneciente al pack de un jugador
		+ int INITIAL_ENERGY_BOT = 10 // Energia inicial de cada animal perteneciente a un pack bot
		+ int MAX_ENERGY = 20 // Energia maxima que puede alcanzar un animal del pack de un jugador
		+ int INITIAL_DAMAGE_PLAYER = 2 // Dano inicial compartido por los animales del pack de un jugador
		+ int INITIAL_DAMAGE_BOT = 1 // Dano inicial compartido por los animales de un pack bot
		+ int MAX_DAMAGE_CAPACITY_PLAYER = 12 // Dano maximo teorico del pack de un jugador dano inicial 2 + 10 tazas
		+ int MAX_DAMAGE_CAPACITY_BOT = 1 // Dano del pack bot que no aumenta mediante las tazas de cafe
		+ int ATTACK_COOLDOWN_MS = 1000 // Tiempo minimo en milisegundos entre ataques consecutivos de un animal
		+ int MAX_TEAM_SIZE_PLAYER = 10 // Cantidad de animales que conforman el pack de un jugador
		+ int MAX_TEAM_SIZE_BOT = 4 // Cantidad de animales que conforman cada pack bot
		+ int BOT_PACK_COUNT = 5 // Cantidad de packs bot correspondientes a las especies no elegidas
		+ int MAX_PLAYERS = 2 // Cantidad maxima de jugadores humanos en una partida
		+ String FLAG_COLOR_PLAYER_ONE = "Guaria Morada" // Color de la bandera del primer jugador
		+ String FLAG_COLOR_PLAYER_TWO = "Yiguirro" // Color de la bandera del segundo jugador
		+ int MAX_AMOUNT_COFFEE = 10 // Cantidad total de tazas de cafe disponibles en la partida
		+ int MAX_ZONES = 12 // Cantidad total de zonas que conforman el mapa
		+ int BOTS_PER_ENERGY_BONUS = 2 // Cantidad de bajas de bots necesarias para otorgar energia
		+ int ENERGY_BONUS_AMOUNT = 1 // Energia que gana cada animal vivo por el bonus de bots
		+ int CUP_DAMAGE_BONUS = 1 // Dano que suma al pack cada taza de cafe recogida
		+ double ANIMAL_SPEED = 50.0 // Velocidad constante de los animales en unidades por segundo
		+ double ENCOUNTER_RADIUS = 15.0 // Radio utilizado para determinar cuando dos animales se encuentran
		+ double FLAG_RADIUS = 15.0 // Radio utilizado para determinar cuando un animal toca una bandera
		+ double CUP_RADIUS = 12.0 // Radio utilizado para determinar cuando un animal recoge una taza
		+ double BOT_DETECTION_RADIUS = 120.0 // Radio en el que un bot detecta enemigos para perseguirlos
		+ double FLAG_MARGIN_MULTIPLIER = 1.5 // Multiplicador del margen usado al colocar la bandera lejos del borde
		+ double MILLIS_PER_SECOND = 1000.0 // Milisegundos contenidos en un segundo
		+ long NANOS_PER_MILLI = 1_000_000L // Nanosegundos contenidos en un milisegundo
		+ String FINISH_REASON_FLAG_TOUCHED = "FLAG_TOUCHED" // Motivo de fin al tocar la bandera rival
		+ String FINISH_REASON_PACK_DEFEATED = "PACK_DEFEATED" // Motivo de fin cuando un pack se queda sin animales
		+ String FINISH_REASON_DISCONNECT = "DISCONNECT" // Motivo de fin cuando un jugador se desconecta
		+ String FINISH_REASON_OPPONENT_LEFT = "OPPONENT_LEFT" // Motivo de fin cuando el rival abandona la sala
		+ int TICK_RATE_MS = 50 // Milisegundos entre actualizaciones del motor del servidor
		+ int STATE_BROADCAST_EVERY_TICKS = 2 // Cantidad de ticks entre cada STATE_UPDATE
		+ int DEFAULT_SERVER_PORT = 8080 // Puerto UDP utilizado por defecto para recibir conexiones
		+ int UDP_HANDSHAKE_TIMEOUT_MS = 5000 // Tiempo maximo de espera del HELLO_ACK
		+ int UDP_RETRANSMIT_MS = 150 // Milisegundos antes de reenviar un mensaje sin ACK
		+ int UDP_KEEPALIVE_MS = 2000 // Milisegundos entre PING de mantenimiento del cliente
		+ int UDP_CLIENT_TIMEOUT_MS = 15000 // Milisegundos sin recibir nada antes de considerar caido al cliente
		+ int UDP_WINDOW_SIZE = 256 // Maximo de mensajes pendientes de ACK antes de descartar los nuevos
		+ int UDP_MAX_BUFFERED_MESSAGES = 1024 // Maximo de mensajes adelantados que se guardan esperando al faltante
		+ int UDP_MAX_RETRANSMITS_PER_TICK = 32 // Maximo de retransmisiones por pasada de mantenimiento
		+ int UDP_MAX_DATAGRAM_SIZE = 60000 // Tamano maximo de un datagrama
		+ int UDP_RECEIVE_BUFFER_SIZE = 1_048_576 // Buffer de recepcion del socket UDP
		+ int UDP_HOUSEKEEPING_MS = 50 // Milisegundos entre pasadas de retransmision y deteccion de inactividad
		+ int MATCH_CODE_LENGTH = 4 // Longitud del codigo de partida
		+ String MATCH_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" // Alfabeto permitido para el codigo
		+ double WORLD_WIDTH = 1280.0 // Ancho logico del mundo
		+ double WORLD_HEIGHT = 720.0 // Alto logico del mundo
		+ int ZONE_COLUMNS = 4 // Cantidad de columnas de la cuadricula de zonas
		+ int ZONE_ROWS = 3 // Cantidad de filas de la cuadricula de zonas
		+ double ZONE_WIDTH = WORLD_WIDTH / ZONE_COLUMNS // Ancho de cada zona
		+ double ZONE_HEIGHT = WORLD_HEIGHT / ZONE_ROWS // Alto de cada zona
		+ double ZONE_MARGIN = 40.0 // Margen interno para no generar elementos pegados al borde
		+ int MAX_NICKNAME_LENGTH = 20 // Longitud maxima del nickname
		+ int MAX_MESSAGE_LENGTH = 64 * 1024 // Longitud maxima de una linea JSON recibida

Common (package common.model) // Libreria compartida importada por el Cliente y el Servidor

	abstract class BaseAnimal // Contiene los atributos que tanto cliente como servidor comparten

		+ int id // Identificador unico del animal
		+ String species // Especie del animal
		+ int currentEnergy // Energia actual del animal
		+ double x // Posicion horizontal actual
		+ double y // Posicion vertical actual
		+ int getId() // Retorna el identificador del animal
		+ String getSpecies() // Retorna la especie del animal
		+ int getCurrentEnergy() // Retorna la energia actual
		+ void setCurrentEnergy(int currentEnergy) // Actualiza la energia sin permitir valores negativos
		+ double getX() // Retorna la coordenada horizontal
		+ void setX(double x) // Actualiza la coordenada horizontal
		+ double getY() // Retorna la coordenada vertical
		+ void setY(double y) // Actualiza la coordenada vertical

	abstract class BaseZone // Atributos topologicos basicos compartidos por ambas partes

		+ String zoneId // Identificador unico de la zona del mapa
		+ boolean hasFlag // Indica si existe una bandera en esta zona
		+ String getZoneId() // Retorna el identificador de la zona
		+ boolean hasFlag() // Indica si la zona tiene bandera
		+ void setHasFlag(boolean hasFlag) // Actualiza el estado de la bandera

	class Point2D // Punto inmutable del mundo utilizado para rutas y distancias

		- double x // Coordenada horizontal inmutable
		- double y // Coordenada vertical inmutable
		+ double getX() // Retorna la coordenada horizontal
		+ double getY() // Retorna la coordenada vertical
		+ double distanceTo(Point2D other) // Calcula la distancia euclidiana hacia otro punto
		+ Point2D stepTowards(Point2D target, double maxDistance) // Avanza hacia el objetivo sin pasarse de la distancia maxima

	enum Species // Las 7 especies nativas que pueden participar en la partida

		+ Species PEREZOSO // Especie perezoso
		+ Species MONO // Especie mono
		+ Species TUCAN // Especie tucan
		+ Species RANA_ROJA // Especie rana roja
		+ Species JAGUAR // Especie jaguar
		+ Species DANTA // Especie danta
		+ Species BOA_CONSTRUCTORA // Especie boa constrictora
		+ String getId() // Retorna el identificador usado por el protocolo y las imagenes
		+ String getDisplayName() // Retorna el nombre visible de la especie
		+ static Species fromId(String id) // Busca una especie por su identificador
		+ static boolean isValid(String id) // Indica si un identificador corresponde a una especie valida
		+ static List<String> allIds() // Retorna la lista de identificadores de todas las especies

Common (package common.message) // Sobre y catalogo de mensajes del protocolo

	class Message // Envoltorio de todos los mensajes con tipo y payload JSON

		- String type // Tipo del mensaje del protocolo
		- JsonObject payload // Contenido generico del mensaje
		+ Message(String type) // Construye un mensaje con payload vacio
		+ Message(String type, JsonObject payload) // Construye un mensaje con su contenido
		+ String getType() // Retorna el tipo del mensaje
		+ JsonObject getPayload() // Retorna el payload creandolo si es nulo
		+ Message put(String key, Object value) // Agrega un campo al payload de forma encadenable
		+ String getString(String key) // Lee un texto del payload
		+ int getInt(String key, int defaultValue) // Lee un entero del payload con valor por defecto
		+ double getDouble(String key, double defaultValue) // Lee un decimal del payload con valor por defecto
		+ JsonArray getArray(String key) // Lee un arreglo del payload
		+ String toJson() // Serializa el mensaje completo a JSON
		+ static Message fromJson(String json) // Reconstruye un mensaje desde una linea JSON

	class MessageType // Catalogo de constantes String con los tipos del protocolo

		+ String CREATE_MATCH // Solicitud para crear una partida
		+ String JOIN_MATCH // Solicitud para unirse a una partida existente
		+ String SELECT_ANIMAL // Seleccion de la especie del jugador
		+ String MOVE_ANIMALS // Orden de movimiento para los animales seleccionados
		+ String ATTACK // Orden de ataque contra un animal enemigo
		+ String VIEW_ZONE // Solicitud para cambiar la zona visible
		+ String MATCH_CREATED // Confirmacion de creacion de partida
		+ String MATCH_JOINED // Confirmacion de ingreso a una partida
		+ String ANIMAL_SELECTION // Estado de la seleccion de especies
		+ String GAME_STARTED // Inicio de la partida con el mapa y el pack inicial
		+ String STATE_UPDATE // Instantanea periodica del estado visible
		+ String GAME_OVER // Resumen final de la partida
		+ String COFFEE_COLLECTED // Evento puntual de taza de cafe recogida
		+ String ERROR // Notificacion de error enviada al cliente

Common (package common.util) // Utilidades compartidas por la logica del servidor

	interface TimeSource // Fuente de tiempo inyectable para probar cooldowns sin esperar

		+ long currentTimeMillis() // Retorna el tiempo actual en milisegundos
		+ static TimeSource system() // Crea una fuente basada en el reloj del sistema

Model (package server.model) // Paquete del modelo especifico para la logica del servidor

	class Animal extends common.model.BaseAnimal // Hereda atributos base de la libreria comun

		+ Pack pack // Manada a la que pertenece el animal
		+ List<Point2D> route // Puntos que el animal debe recorrer en orden
		+ int routeIndex // Indice actual dentro de la ruta
		+ double targetX // Destino horizontal final pedido por el jugador
		+ double targetY // Destino vertical final pedido por el jugador
		+ String zoneId // Identificador de la zona donde esta el animal
		+ long lastAttackTime // Momento en milisegundos del ultimo ataque
		+ boolean hasAttacked // Indica si el animal ya ataco al menos una vez
		+ void takeDamage(int amount) // Resta energia al animal
		+ void heal(int amount) // Aumenta la energia sin superar el maximo permitido
		+ boolean isDefeated() // Indica si el animal se quedo sin energia
		+ boolean canAttack(long currentTimeMillis) // Comprueba si el cooldown permite realizar otro ataque
		+ void markAttack(long currentTimeMillis) // Registra el ataque e inicia el enfriamiento
		+ void movePosition(double deltaSeconds) // Avanza la posicion a lo largo de la ruta oficial
		+ void setRoute(List<Point2D> waypoints, double targetX, double targetY) // Reemplaza la ruta actual
		+ void clearRoute() // Limpia la ruta y detiene el movimiento
		+ boolean isMoving() // Indica si quedan puntos pendientes en la ruta
		+ Point2D getPosition() // Retorna la posicion actual como Point2D

	class Pack // Representa una agrupacion de animales con estadisticas compartidas

		+ String packId // Identificador unico de la manada o pack
		+ String species // Especie seleccionada para todos los miembros
		+ boolean botPack // Indica si la manada pertenece a un bot
		+ List<Animal> animals // Lista de animales pertenecientes al pack
		+ int damageCapacity // Dano base compartido por todo el pack
		+ int botsKilled // Cantidad de bots eliminados por el pack
		+ int coffeeCupsCollected // Cantidad de tazas recolectadas por el pack
		+ void addAnimal(Animal animal) // Agrega un animal evitando duplicados
		+ void increaseDamage() // Aumenta el dano del pack al recolectar cafe
		+ void registerCoffeeCup() // Registra una taza y aumenta el dano
		+ boolean registerKill(Pack victimPack) // Registra una baja y aplica el bonus de energia
		+ void applyEnergyBonus() // Aplica bonus de energia a los animales vivos
		+ List<Animal> getLivingAnimals() // Retorna los animales que siguen vivos
		+ int getAliveCount() // Retorna la cantidad de animales vivos
		+ int getLostCount() // Retorna la cantidad de animales perdidos
		+ boolean isDefeated() // Comprueba si todos los miembros del pack han sido aniquilados

	abstract class Participant // Entidad abstracta que engloba a jugadores y bots en la partida

		+ String participantId // Identificador unico del participante
		+ String name // Nombre visible del participante
		+ Pack pack // Referencia al pack de animales asociado
		+ Zone currentZone // Zona actual del participante
		+ boolean connected // Estado de conexion en la partida actual
		+ boolean isBot() // Metodo abstracto que distingue jugadores de bots
		+ Pack getPack() // Retorna el pack asociado
		+ void setPack(Pack pack) // Asigna el pack del participante
		+ Zone getCurrentZone() // Retorna la zona actual
		+ void setCurrentZone(Zone currentZone) // Asigna la zona actual
		+ boolean isConnected() // Indica si el participante sigue conectado

	class Player extends Participant // Representa un jugador humano conectado

		+ String selectedSpecies // Nombre de la especie seleccionada
		+ String viewedZoneId // Identificador de la zona que esta mirando
		+ String flagColor // Color de la bandera propia
		+ boolean isBot() // Indica que el participante no es un bot
		+ boolean selectSpecies(String species) // Registra y valida la especie en el sistema
		+ String getAccountName() // Alias del nombre del jugador
		+ boolean hasSelectedSpecies() // Indica si el jugador ya eligio especie
		+ static boolean isValidNickname(String nickname) // Valida que el nickname tenga un largo aceptable

	class Bot extends Participant // Representa a la inteligencia automatizada sin conexion real

		+ Animal targetCandidate // Animal enemigo detectado como objetivo
		+ boolean isBot() // Indica que el participante es un bot
		+ void updateAI(GameSession session, double deltaSeconds) // Ejecuta un tick de la IA del bot
		+ void searchTargets(GameSession session) // Identifica el enemigo vivo mas cercano
		+ void autoMove(GameSession session) // Calcula y asigna rutas de patrullaje o persecucion

	class Zone extends common.model.BaseZone // Hereda id y bandera de la libreria comun

		+ String displayName // Nombre visible de la zona
		+ String imageName // Nombre del archivo de imagen de la zona
		+ double x // Coordenada horizontal del rectangulo de la zona
		+ double y // Coordenada vertical del rectangulo de la zona
		+ double width // Ancho del rectangulo de la zona
		+ double height // Alto del rectangulo de la zona
		+ List<Zone> adjacentNodes // Conexiones reales a otras zonas para el algoritmo BFS
		+ List<Animal> entitiesInside // Animales que oficialmente estan dentro de la zona
		+ List<CoffeeCup> cupsInside // Tazas de cafe reales generadas en esta zona
		+ Flag flag // Objeto bandera asignado a esta zona si corresponde
		+ void connect(Zone other) // Conecta dos zonas en ambos sentidos
		+ boolean contains(double pointX, double pointY) // Indica si un punto esta dentro de la zona
		+ Point2D getCenter() // Retorna el centro geometrico de la zona
		+ Point2D randomPointInside(Random random, double margin) // Genera un punto aleatorio con margen
		+ void addEntity(Animal animal) // Ingresa oficialmente un animal a la zona logica
		+ void removeEntity(Animal animal) // Remueve un animal que acaba de salir de la zona
		+ void addCup(CoffeeCup cup) // Agrega una taza a la zona
		+ void removeCup(CoffeeCup cup) // Remueve una taza consumida de la zona
		+ void setFlag(Flag flag) // Asigna la bandera y actualiza hasFlag

	class CoffeeCup // Elemento interactivo en el mapa

		+ String cupId // Identificador de la taza
		+ String zoneId // Identificador de la zona donde esta la taza
		+ double x // Coordenada horizontal de la taza
		+ double y // Coordenada vertical de la taza
		+ boolean consumed // Indica si ya fue tomada por un animal
		+ void consume() // Marca la taza como consumida

	class Flag // Objetivo de la partida para los jugadores

		+ String flagId // Identificador de la bandera
		+ String ownerId // Identificador del jugador propietario
		+ String color // Color de la bandera
		+ String zoneId // Identificador de la zona donde esta
		+ double x // Coordenada horizontal de la bandera
		+ double y // Coordenada vertical de la bandera

	class AttackOrder // Orden persistente de ataque de un jugador

		+ List<Animal> attackers // Animales que mantienen la orden de ataque
		+ Animal target // Animal objetivo del ataque
		+ boolean isActive() // Indica si el objetivo sigue vivo
		+ List<Animal> getAttackers() // Retorna los atacantes como lista inmutable
		+ Animal getTarget() // Retorna el objetivo de la orden

	class GameState // Instantanea personalizada que se envia al cliente en STATE_UPDATE

		+ String matchCode // Codigo de la partida
		+ String playerName // Nombre del jugador que recibe el estado
		+ int matchDurationSeconds // Duracion transcurrida en segundos
		+ String opponentName // Nombre del oponente
		+ Zone viewedZone // Zona que el jugador esta mirando
		+ List<Zone> availableZones // Zonas donde el jugador tiene animales vivos
		+ List<Animal> visibleAnimals // Animales visibles en la zona vista
		+ List<CoffeeCup> visibleCups // Tazas visibles en la zona vista
		+ Pack myPack // Manada propia del jugador
		+ boolean gameOver // Indica si la partida termino
		+ boolean playerWon // Indica si el jugador gano
		+ JsonObject toJson() // Serializa el estado completo a JSON

Logic (package server.logic) // Paquete de control y logica principal del servidor

	interface IParticipantFactory // Patron: Abstract Factory. Crea familias de objetos relacionados sin depender de clases concretas

		+ Participant createParticipant(String participantId, String name) // Instancia el tipo de participante adecuado
		+ Pack createPack(String packId, String species) // Crea y configura el pack para el participante
		+ List<Animal> createAnimals(Pack pack, int count) // Genera la cantidad de animales indicada

	class PlayerFactory implements IParticipantFactory // Fabrica concreta encargada de instanciar familias para Jugadores

		+ Participant createParticipant(String participantId, String name) // Crea y retorna un Player
		+ Pack createPack(String packId, String species) // Crea un pack inicializado para el jugador
		+ List<Animal> createAnimals(Pack pack, int count) // Crea 10 animales de 20 de energia para el jugador

	class BotFactory implements IParticipantFactory // Fabrica concreta encargada de instanciar familias para Bots autonomos

		+ Participant createParticipant(String participantId, String name) // Crea y retorna un Bot
		+ Pack createPack(String packId, String species) // Crea un pack con los atributos limitados de un bot
		+ List<Animal> createAnimals(Pack pack, int count) // Crea 4 animales de 10 de energia para el bot

	class GameSessionBuilder // Patron: Builder. Construye el objeto complejo GameSession paso a paso encapsulando la creacion

		- Random random // Generador aleatorio compartido con la sesion
		- TimeSource clock // Reloj inyectable de la sesion
		- GameSession session // Instancia de la sesion en proceso de construccion
		+ void reset(GameSession session) // Prepara el builder para completar una sesion existente
		+ void reset(String matchCode) // Inicializa una nueva sesion vacia con su codigo
		+ void buildPlayers(Player p1, Player p2) // Ensambla los dos jugadores dentro de la sesion
		+ void buildPlayer(Player player) // Agrega un solo jugador al crear la sala en espera
		+ void buildMap(MapManager mapManager) // Configura el manager del mapa en la sesion
		+ void buildCombatManager(CombatManager combatManager) // Configura el manager de combate
		+ void buildPlayerPacks(IParticipantFactory playerFactory) // Crea el pack de cada jugador con su especie
		+ void buildBots(IParticipantFactory botFactory) // Construye los 5 bots de las especies no elegidas
		+ void buildStartingZones() // Reparte las 7 manadas en zonas distintas
		+ void buildItems() // Genera las banderas y las 10 tazas de cafe
		+ GameSession getResult() // Retorna el objeto GameSession completamente ensamblado

	class MatchManager // Patron: Singleton. Garantiza que exista solo una instancia de la clase administradora en todo el servidor

		- static MatchManager instance // Instancia estatica unica
		- ConcurrentHashMap<String, GameSession> activeMatches // Diccionario thread safe de partidas activas
		- MatchCodeGenerator codeGenerator // Generador de codigos de partida
		- Random random // Generador aleatorio compartido
		- MatchManager() // Constructor privado para evitar instanciacion externa
		+ static MatchManager getInstance() // Retorna la instancia global o la crea si no existe
		+ String createMatch(Player player1) // Utiliza el builder para instanciar un room nuevo
		+ GameSession joinMatch(String code, Player player2) // Busca la sesion y agrega al segundo jugador
		+ GameSession getMatch(String code) // Obtiene una partida por codigo normalizado
		+ void removeMatch(String code) // Elimina la sesion de memoria al finalizar
		+ void handleDisconnect(GameSession session, Player player) // Procesa la desconexion y limpia la sala
		+ Collection<GameSession> snapshotMatches() // Retorna una copia segura de las partidas activas
		+ static String normalizeCode(String code) // Normaliza el codigo a mayusculas sin espacios

	class MatchCodeGenerator // Genera los codigos de 4 caracteres que identifican cada partida

		- Random random // Generador aleatorio inyectado
		+ String generate() // Genera un codigo de 4 caracteres
		+ String generateUnique(Set<String> usedCodes) // Genera un codigo que no este en uso

	class MatchException extends RuntimeException // Error de negocio del servidor que se envia como mensaje ERROR

		+ MatchException(String message) // Construye la excepcion con el mensaje para el cliente

	interface GameListener // Puerto de salida de una partida para no depender de los sockets

		+ void onGameMessage(String type, JsonObject payload) // Envia un mensaje del juego a un participante

	class GameSession // Room que encapsula todo el estado de una partida aislada

		enum Phase // Estados posibles de una partida

			+ WAITING // Solo esta el primer jugador
			+ SELECTING // Estan los dos jugadores eligiendo especie
			+ RUNNING // Partida en curso
			+ FINISHED // Partida terminada y resumen enviado

		+ String matchCode // Codigo de la partida
		+ Random random // Generador aleatorio de la sesion
		+ TimeSource clock // Reloj inyectable de la sesion
		+ Phase phase // Fase actual de la partida
		+ List<Player> players // Jugadores humanos de la partida
		+ List<Bot> bots // Participantes autonomos controlados por IA
		+ MapManager mapManager // Delegado para calculos topologicos
		+ CombatManager combatManager // Delegado para procesar combates
		+ VisibilityService visibilityService // Servicio que aplica la niebla de guerra
		+ SnapshotBuilder snapshotBuilder // Constructor de payloads JSON
		+ Map<String, GameListener> listeners // Oyentes de salida por participante
		+ Map<String, AttackOrder> attackOrders // Orden de ataque vigente por jugador
		+ long matchDurationMs // Duracion acumulada de la partida en milisegundos
		+ long tickCounter // Contador de ticks para espaciar los envios de estado
		+ boolean gameOver // Bandera de fin de juego
		+ String winnerId // Identificador del ganador
		+ String finishReason // Razon de finalizacion
		+ void addPlayer(Player player) // Agrega un jugador y controla el limite de dos
		+ void selectSpecies(Player player, String species) // Registra la especie e inicia la partida si ambos eligieron
		+ void startGame() // Ensambla la partida con el Builder y envia GAME_STARTED
		+ void update(int deltaMs) // Bucle central de la sesion que actualiza su estado interno
		+ void commandMove(Player player, List<Integer> animalIds, double targetX, double targetY) // Aplica una orden de movimiento
		+ void commandAttack(Player player, List<Integer> animalIds, int targetAnimalId) // Registra una orden de ataque
		+ void viewZone(Player player, String zoneId) // Cambia la zona que el jugador esta mirando
		+ void handleDisconnect(Player player) // Procesa la desconexion de un jugador
		+ void registerListener(String participantId, GameListener listener) // Registra el oyente de un participante
		+ void removeListener(String participantId) // Remueve el oyente de un participante
		+ void sendToPlayer(String participantId, String type, JsonObject payload) // Envia un mensaje a un jugador
		+ void broadcastAction(String type, JsonObject payload) // Notifica estado a todos los oyentes
		+ AttackOrder getAttackOrder(String participantId) // Retorna la orden de ataque vigente
		+ void clearAttackOrder(String participantId) // Limpia la orden de ataque de un jugador
		+ List<Animal> getAllAnimals() // Retorna todos los animales de jugadores y bots
		+ Player getOpponent(Player player) // Retorna el otro jugador de la partida
		+ boolean isRunning() // Indica si la partida esta en curso
		+ boolean isFinished() // Indica si la partida ya termino
		+ int getMatchDurationSeconds() // Retorna la duracion de la partida en segundos
		- void moveAnimals(double deltaSeconds) // Mueve los animales y actualiza su zona
		- void collectCoffee() // Consume las tazas que toquen los animales de jugadores
		- void checkFlags() // Verifica si algun animal toco la bandera rival
		- void checkPacksDefeated() // Verifica si algun pack se quedo sin animales
		- void finish(String winnerId, String reason) // Termina la partida y envia GAME_OVER
		- void broadcastState() // Envia STATE_UPDATE personalizado a cada jugador

	class MapManager // Modulo delegado para el manejo espacial del juego

		- Map<String, Zone> worldMap // Topologia del mapa del juego
		- void buildWorld() // Construye las 12 zonas y sus caminos
		+ Zone getZone(String zoneId) // Retorna una zona por identificador
		+ Collection<Zone> getZones() // Retorna todas las zonas como vista inmutable
		+ Zone findZoneAt(double x, double y) // Retorna la zona que contiene un punto
		+ List<Zone> calculateRouteBFS(Zone start, Zone destination) // Ejecuta busqueda de ruta mas corta
		+ boolean commandMove(Animal animal, double targetX, double targetY) // Convierte una ruta de zonas en waypoints
		+ void assignStartingZones(List<Participant> participants, Random random) // Distribuye a los packs en las zonas de inicio
		+ void spawnItems(List<Player> players, Random random) // Genera las banderas y las tazas de cafe
		+ Flag findFlagOf(String ownerId) // Busca la bandera de un jugador
		+ void transferEntity(Animal animal, Zone from, Zone to) // Intercambia logicamente al animal de zona

	class CombatManager // Modulo delegado para el calculo de colisiones y batallas

		+ void processCombat(GameSession session) // Procesa los ataques de bots y jugadores
		+ void executeAttack(Animal attacker, Animal target, Pack attackerPack) // Ejecuta el descuento de energia
		+ void validateDeath(Animal target) // Verifica si el animal murio tras el ataque
		- void processBotAttacks(GameSession session, long now) // Resuelve los ataques automaticos de los bots
		- void processPlayerAttacks(GameSession session, long now) // Resuelve las ordenes explicitas de los jugadores
		- boolean inRange(Animal attacker, Animal target) // Indica si dos animales pueden pelear

	class VisibilityService // Aplica la niebla de guerra del lado del servidor

		+ GameState buildState(GameSession session, Player player) // Construye la instantanea que el jugador puede ver
		+ List<Zone> zonesWithLivingAnimals(GameSession session, Player player) // Retorna las zonas con animales vivos del jugador

	class SnapshotBuilder // Construye los payloads JSON que no son una simple instantanea de estado

		+ JsonObject matchJoined(GameSession session, Player viewer) // Payload de MATCH_JOINED para un jugador
		+ JsonObject animalSelection(GameSession session, Player viewer) // Payload de ANIMAL_SELECTION para un jugador
		+ List<String> availableSpecies(GameSession session) // Especies que todavia puede elegir un jugador
		+ JsonObject gameStarted(GameSession session, Player viewer) // Payload de GAME_STARTED con mapa y pack inicial
		+ JsonObject gameOver(GameSession session) // Resumen final de la partida
		- JsonArray zonesArray(GameSession session) // Serializa las zonas del mapa
		- JsonObject packJson(Pack pack) // Serializa el pack del jugador
		- JsonArray speciesArray(List<String> species) // Convierte una lista de especies a JsonArray

	class GameLoop implements Runnable // Hilo maestro de actualizacion autonoma del motor

		- int tickRateMs // Cantidad de milisegundos entre ticks del servidor
		- MatchManager matchManager // Gestor de partidas que se actualiza
		- boolean running // Bandera de estado del hilo maestro
		- Thread thread // Hilo del bucle
		- long lastTickNanos // Momento del ultimo tick en nanosegundos
		+ void start() // Levanta el hilo de procesamiento continuo
		+ void stop() // Detiene el hilo del bucle
		+ void run() // Ejecuta el ciclo continuo del motor
		+ void updateMatches(int deltaMs) // Itera sobre las partidas de MatchManager llamando a su update local
		+ boolean isRunning() // Indica si el bucle esta activo

Network (package network) // Paquete de integracion y conectividad de red

	interface ClientChannel // Abstraccion de la conexion logica con un cliente

		+ String receive() // Espera el siguiente mensaje del cliente
		+ boolean send(String message) // Envia una linea JSON
		+ void close() // Cierra la conexion
		+ String remoteAddress() // Retorna la direccion remota para los logs

	class NetworkServer // Servidor UDP encargado de aceptar y repartir los datagramas

		- MessageRouterServer router // Enrutador de mensajes entrantes
		- ExecutorService clientPool // Pool de hilos para atender cada conexion
		- ScheduledExecutorService housekeeping // Ejecutor periodico de mantenimiento
		- Map<InetSocketAddress, UdpConnection> connections // Conexiones activas por direccion remota
		- DatagramSocket socket // Socket UDP del servidor
		- boolean running // Indica si el servidor esta corriendo
		- int port // Puerto real del servidor
		- Thread receiveThread // Hilo que recibe todos los datagramas
		+ void startServer(int port) // Abre el puerto e inicia recepcion y mantenimiento
		- void receiveLoop() // Recibe datagramas y los reparte entre las conexiones
		- void handleHello(InetSocketAddress from, Packet packet) // Completa el handshake de un cliente nuevo
		- void housekeepingTick() // Retransmite y detecta clientes inactivos
		+ void stop() // Detiene el servidor y cierra todas las conexiones
		+ int getPort() // Retorna el puerto del servidor
		+ boolean isRunning() // Indica si el servidor esta corriendo

	class ClientHandler implements Runnable, GameListener // Hilo asignado exclusivamente para atender a un cliente conectado

		- ClientChannel channel // Canal de comunicacion con el cliente
		- MessageRouterServer router // Enrutador instanciado para redirigir las peticiones parseadas
		- Player player // Jugador asociado a la conexion
		- GameSession session // Partida asociada a la conexion
		- boolean sendFailureLogged // Indica si ya se registro un fallo de envio
		+ void run() // Bucle de lectura de entrada mientras el canal siga vivo
		+ void sendMessage(String type, JsonObject payload) // Formatea en JSON y lo envia por el canal
		+ void sendError(String description) // Envia un mensaje ERROR al cliente
		+ void onGameMessage(String type, JsonObject payload) // Implementa GameListener enviando el mensaje del juego
		+ void close() // Cierra el canal de comunicacion
		+ void attach(Player player, GameSession session) // Asocia el jugador y la partida a la conexion
		+ Player getPlayer() // Retorna el jugador asociado
		+ GameSession getSession() // Retorna la partida asociada
		+ String getRoomCode() // Retorna el codigo de la sala o null

	class MessageRouterServer // Modulo intermedio que decide a que handler derivar la instruccion

		- MessageHandler handler // Referencia al procesador de mensajes
		+ void routeMessage(ClientHandler client, String jsonMessage) // Lee el tipo y envia al metodo adecuado
		+ void onDisconnect(ClientHandler client) // Delega la desconexion al manejador

	class MessageHandler // Ejecutor final de la logica inyectada por el cliente sobre su room

		- MatchManager matchManager // Gestor de partidas activas
		- SnapshotBuilder snapshotBuilder // Constructor de payloads del lobby y del resumen
		+ void handleCreateMatch(ClientHandler client, Message message) // Crea una partida y envia MATCH_CREATED
		+ void handleJoinMatch(ClientHandler client, Message message) // Une al jugador y envia MATCH_JOINED
		+ void handleSelectAnimal(ClientHandler client, Message message) // Registra la especie elegida
		+ void handleMoveAnimals(ClientHandler client, Message message) // Aplica movimiento a los animales indicados
		+ void handleAttack(ClientHandler client, Message message) // Inicia una orden de ataque
		+ void handleViewZone(ClientHandler client, Message message) // Cambia la zona visible del jugador
		+ void handleDisconnect(ClientHandler client) // Procesa la desconexion del cliente
		- GameSession requireSession(ClientHandler client) // Valida que la conexion tenga partida
		- Player requirePlayer(ClientHandler client) // Valida que la conexion tenga jugador
		- String requireNickname(String nickname) // Valida y recorta el nickname recibido
		- List<Integer> readAnimalIds(Message message) // Lee los identificadores de animales del payload

Network UDP (package network.udp) // Transporte confiable construido sobre datagramas UDP

	class UdpProtocol // Formato comun de los datagramas del transporte

		+ int MAGIC = 0x4A41 // Numero magico que identifica el protocolo
		+ byte VERSION = 1 // Version del protocolo UDP
		+ byte DATA // Tipo de datagrama con datos
		+ byte ACK // Tipo de datagrama de confirmacion
		+ byte HELLO // Tipo de datagrama de inicio de handshake
		+ byte HELLO_ACK // Tipo de datagrama de respuesta al handshake
		+ byte BYE // Tipo de datagrama de cierre
		+ byte PING // Tipo de datagrama de mantenimiento
		+ byte PONG // Tipo de datagrama de respuesta al mantenimiento
		+ int HEADER_SIZE = 12 // Tamano del encabezado de cada datagrama
		+ int MAX_DATAGRAM_SIZE = 60000 // Tamano maximo de un datagrama
		+ byte[] encode(byte type, int sequence, int acknowledgement, byte[] payload) // Codifica un datagrama completo
		+ byte[] encodeText(byte type, int sequence, int acknowledgement, String text) // Codifica un datagrama con texto UTF-8
		+ Packet decode(byte[] data, int length) // Decodifica y valida un datagrama
		+ byte[] encodeLong(long value) // Convierte un long a bytes para el handshake
		+ long decodeLong(byte[] payload) // Reconstruye un long desde el payload del handshake

		class Packet // Datagrama decodificado con tipo, secuencia, ACK y payload

			+ byte type // Tipo del datagrama
			+ int sequence // Numero de secuencia del emisor
			+ int acknowledgement // Ultimo numero confirmado por el receptor
			+ byte[] payload // Contenido del datagrama
			+ String payloadAsText() // Convierte el payload a texto UTF-8

	class ReliableUdpChannel // Canal UDP confiable y ordenado, extremo a extremo

		- DatagramSink sink // Funcion encargada de enviar cada datagrama
		- Map<Integer, Pending> pending // Mensajes enviados pendientes de ACK
		- TreeMap<Integer, byte[]> future // Mensajes adelantados esperando al faltante
		- ByteArrayOutputStream assembly // Trozos del mensaje que se esta reensamblando
		- Consumer<String> receiver // Consumidor de los mensajes completos
		- int nextSequence // Siguiente numero de secuencia a enviar
		- int expectedSequence // Numero de secuencia esperado al recibir
		+ boolean send(String text) // Envia un mensaje JSON de forma confiable
		+ void sendControl(byte type) // Envia un datagrama de control
		+ void onPacket(Packet packet, long now) // Procesa un datagrama recibido
		+ void tick(long now) // Reenvia los mensajes que siguen sin ACK
		+ void close() // Cierra el canal y limpia sus buffers
		- void onData(Packet packet) // Procesa un fragmento DATA y confirma la secuencia
		- void onAck(int acknowledgement) // Elimina los mensajes ya confirmados

	class UdpConnection implements ClientChannel // Conexion UDP con un cliente concreto

		- InetSocketAddress remote // Direccion remota del cliente
		- long sessionId // Identificador de sesion del handshake
		- ReliableUdpChannel channel // Canal confiable asociado a la conexion
		- BlockingQueue<String> inbox // Cola de mensajes completos recibidos
		- boolean closed // Indica si la conexion esta cerrada
		- Runnable closeAction // Accion ejecutada al cerrar la conexion
		+ void acceptHandshake() // Envia HELLO_ACK al cliente
		+ void onPacket(Packet packet, long now) // Delega el datagrama al canal confiable
		+ void tick(long now) // Ejecuta el mantenimiento del canal
		+ long lastReceivedNanos() // Retorna el momento del ultimo mensaje recibido
		+ long getSessionId() // Retorna el identificador de sesion
		+ void setCloseAction(Runnable closeAction) // Asigna la accion de cierre
		+ String receive() // Recibe el siguiente mensaje o null al cerrarse
		+ boolean send(String message) // Envia un mensaje si la conexion sigue abierta
		+ void close() // Cierra el canal y ejecuta la accion de cierre
		+ String remoteAddress() // Retorna la direccion remota

Server Entry (package server) // Punto de entrada del servidor

	class ServerMain // Clase final de arranque del servidor

		+ static void main(String[] args) // Ensambla las piezas y arranca el motor y la red
		- static int resolvePort(String[] args) // Resuelve el puerto desde argumentos o propiedad del sistema


Diagrama Mermaid:

```mermaid
classDiagram

    namespace constants {
        class IConstants {
            <<interface>>
            +int INITIAL_ENERGY_PLAYER
            +int INITIAL_ENERGY_BOT
            +int MAX_ENERGY
            +int INITIAL_DAMAGE_PLAYER
            +int INITIAL_DAMAGE_BOT
            +int MAX_DAMAGE_CAPACITY_PLAYER
            +int MAX_DAMAGE_CAPACITY_BOT
            +int ATTACK_COOLDOWN_MS
            +int MAX_TEAM_SIZE_PLAYER
            +int MAX_TEAM_SIZE_BOT
            +int BOT_PACK_COUNT
            +int MAX_PLAYERS
            +String FLAG_COLOR_PLAYER_ONE
            +String FLAG_COLOR_PLAYER_TWO
            +int MAX_AMOUNT_COFFEE
            +int MAX_ZONES
            +int BOTS_PER_ENERGY_BONUS
            +int ENERGY_BONUS_AMOUNT
            +int CUP_DAMAGE_BONUS
            +double ANIMAL_SPEED
            +double ENCOUNTER_RADIUS
            +double FLAG_RADIUS
            +double CUP_RADIUS
            +double BOT_DETECTION_RADIUS
            +double FLAG_MARGIN_MULTIPLIER
            +double MILLIS_PER_SECOND
            +long NANOS_PER_MILLI
            +String FINISH_REASON_FLAG_TOUCHED
            +String FINISH_REASON_PACK_DEFEATED
            +String FINISH_REASON_DISCONNECT
            +String FINISH_REASON_OPPONENT_LEFT
            +int TICK_RATE_MS
            +int STATE_BROADCAST_EVERY_TICKS
            +int DEFAULT_SERVER_PORT
            +int UDP_HANDSHAKE_TIMEOUT_MS
            +int UDP_RETRANSMIT_MS
            +int UDP_KEEPALIVE_MS
            +int UDP_CLIENT_TIMEOUT_MS
            +int UDP_WINDOW_SIZE
            +int UDP_MAX_BUFFERED_MESSAGES
            +int UDP_MAX_RETRANSMITS_PER_TICK
            +int UDP_MAX_DATAGRAM_SIZE
            +int UDP_RECEIVE_BUFFER_SIZE
            +int UDP_HOUSEKEEPING_MS
            +int MATCH_CODE_LENGTH
            +String MATCH_CODE_ALPHABET
            +double WORLD_WIDTH
            +double WORLD_HEIGHT
            +int ZONE_COLUMNS
            +int ZONE_ROWS
            +double ZONE_WIDTH
            +double ZONE_HEIGHT
            +double ZONE_MARGIN
            +int MAX_NICKNAME_LENGTH
            +int MAX_MESSAGE_LENGTH
        }
    }

    namespace common_model {
        class BaseAnimal {
            <<abstract>>
            +int id
            +String species
            +int currentEnergy
            +double x
            +double y
            +getId() int
            +getSpecies() String
            +getCurrentEnergy() int
            +setCurrentEnergy(int currentEnergy) void
            +getX() double
            +setX(double x) void
            +getY() double
            +setY(double y) void
        }
        class BaseZone {
            <<abstract>>
            +String zoneId
            +boolean hasFlag
            +getZoneId() String
            +hasFlag() boolean
            +setHasFlag(boolean hasFlag) void
        }
        class Point2D {
            -double x
            -double y
            +getX() double
            +getY() double
            +distanceTo(Point2D other) double
            +stepTowards(Point2D target, double maxDistance) Point2D
        }
        class Species {
            <<enumeration>>
            +Species PEREZOSO
            +Species MONO
            +Species TUCAN
            +Species RANA_ROJA
            +Species JAGUAR
            +Species DANTA
            +Species BOA_CONSTRUCTORA
            +getId() String
            +getDisplayName() String
            +fromId(String id)$ Species
            +isValid(String id)$ boolean
            +allIds()$ List~String~
        }
    }

    namespace common_message {
        class Message {
            -String type
            -JsonObject payload
            +Message(String type)
            +Message(String type, JsonObject payload)
            +getType() String
            +getPayload() JsonObject
            +put(String key, Object value) Message
            +getString(String key) String
            +getInt(String key, int defaultValue) int
            +getDouble(String key, double defaultValue) double
            +getArray(String key) JsonArray
            +toJson() String
            +fromJson(String json)$ Message
        }
        class MessageType {
            +String CREATE_MATCH
            +String JOIN_MATCH
            +String SELECT_ANIMAL
            +String MOVE_ANIMALS
            +String ATTACK
            +String VIEW_ZONE
            +String MATCH_CREATED
            +String MATCH_JOINED
            +String ANIMAL_SELECTION
            +String GAME_STARTED
            +String STATE_UPDATE
            +String GAME_OVER
            +String COFFEE_COLLECTED
            +String ERROR
        }
    }

    namespace common_util {
        class TimeSource {
            <<interface>>
            +currentTimeMillis() long
            +system()$ TimeSource
        }
    }

    namespace server_model {
        class Animal {
            +Pack pack
            +List~Point2D~ route
            +int routeIndex
            +double targetX
            +double targetY
            +String zoneId
            +long lastAttackTime
            +boolean hasAttacked
            +takeDamage(int amount) void
            +heal(int amount) void
            +isDefeated() boolean
            +canAttack(long currentTimeMillis) boolean
            +markAttack(long currentTimeMillis) void
            +movePosition(double deltaSeconds) void
            +setRoute(List~Point2D~ waypoints, double targetX, double targetY) void
            +clearRoute() void
            +isMoving() boolean
            +getPosition() Point2D
        }
        class Pack {
            +String packId
            +String species
            +boolean botPack
            +List~Animal~ animals
            +int damageCapacity
            +int botsKilled
            +int coffeeCupsCollected
            +addAnimal(Animal animal) void
            +increaseDamage() void
            +registerCoffeeCup() void
            +registerKill(Pack victimPack) boolean
            +applyEnergyBonus() void
            +getLivingAnimals() List~Animal~
            +getAliveCount() int
            +getLostCount() int
            +isDefeated() boolean
        }
        class Participant {
            <<abstract>>
            +String participantId
            +String name
            +Pack pack
            +Zone currentZone
            +boolean connected
            +isBot() boolean
            +getPack() Pack
            +setPack(Pack pack) void
            +getCurrentZone() Zone
            +setCurrentZone(Zone currentZone) void
            +isConnected() boolean
        }
        class Player {
            +String selectedSpecies
            +String viewedZoneId
            +String flagColor
            +isBot() boolean
            +selectSpecies(String species) boolean
            +getAccountName() String
            +hasSelectedSpecies() boolean
            +isValidNickname(String nickname)$ boolean
        }
        class Bot {
            +Animal targetCandidate
            +isBot() boolean
            +updateAI(GameSession session, double deltaSeconds) void
            +searchTargets(GameSession session) void
            +autoMove(GameSession session) void
        }
        class Zone {
            +String displayName
            +String imageName
            +double x
            +double y
            +double width
            +double height
            +List~Zone~ adjacentNodes
            +List~Animal~ entitiesInside
            +List~CoffeeCup~ cupsInside
            +Flag flag
            +connect(Zone other) void
            +contains(double pointX, double pointY) boolean
            +getCenter() Point2D
            +randomPointInside(Random random, double margin) Point2D
            +addEntity(Animal animal) void
            +removeEntity(Animal animal) void
            +addCup(CoffeeCup cup) void
            +removeCup(CoffeeCup cup) void
            +setFlag(Flag flag) void
        }
        class CoffeeCup {
            +String cupId
            +String zoneId
            +double x
            +double y
            +boolean consumed
            +consume() void
        }
        class Flag {
            +String flagId
            +String ownerId
            +String color
            +String zoneId
            +double x
            +double y
        }
        class AttackOrder {
            +List~Animal~ attackers
            +Animal target
            +isActive() boolean
            +getAttackers() List~Animal~
            +getTarget() Animal
        }
        class GameState {
            +String matchCode
            +String playerName
            +int matchDurationSeconds
            +String opponentName
            +Zone viewedZone
            +List~Zone~ availableZones
            +List~Animal~ visibleAnimals
            +List~CoffeeCup~ visibleCups
            +Pack myPack
            +boolean gameOver
            +boolean playerWon
            +toJson() JsonObject
        }
    }

    namespace logic {
        class IParticipantFactory {
            <<interface>>
            +createParticipant(String participantId, String name) Participant
            +createPack(String packId, String species) Pack
            +createAnimals(Pack pack, int count) List~Animal~
        }
        class PlayerFactory {
            +createParticipant(String participantId, String name) Participant
            +createPack(String packId, String species) Pack
            +createAnimals(Pack pack, int count) List~Animal~
        }
        class BotFactory {
            +createParticipant(String participantId, String name) Participant
            +createPack(String packId, String species) Pack
            +createAnimals(Pack pack, int count) List~Animal~
        }
        class GameSessionBuilder {
            -Random random
            -TimeSource clock
            -GameSession session
            +reset(GameSession session) void
            +reset(String matchCode) void
            +buildPlayers(Player p1, Player p2) void
            +buildPlayer(Player player) void
            +buildMap(MapManager mapManager) void
            +buildCombatManager(CombatManager combatManager) void
            +buildPlayerPacks(IParticipantFactory playerFactory) void
            +buildBots(IParticipantFactory botFactory) void
            +buildStartingZones() void
            +buildItems() void
            +getResult() GameSession
        }
        class MatchManager {
            -MatchManager instance$
            -ConcurrentHashMap~String, GameSession~ activeMatches
            -MatchCodeGenerator codeGenerator
            -Random random
            -MatchManager()
            +getInstance()$ MatchManager
            +createMatch(Player player1) String
            +joinMatch(String code, Player player2) GameSession
            +getMatch(String code) GameSession
            +removeMatch(String code) void
            +handleDisconnect(GameSession session, Player player) void
            +snapshotMatches() Collection~GameSession~
            +normalizeCode(String code)$ String
        }
        class MatchCodeGenerator {
            -Random random
            +generate() String
            +generateUnique(Set~String~ usedCodes) String
        }
        class MatchException {
            +MatchException(String message)
        }
        class GameListener {
            <<interface>>
            +onGameMessage(String type, JsonObject payload) void
        }
        class GameSession {
            -String matchCode
            -Random random
            -TimeSource clock
            -Phase phase
            -List~Player~ players
            -List~Bot~ bots
            -MapManager mapManager
            -CombatManager combatManager
            -VisibilityService visibilityService
            -SnapshotBuilder snapshotBuilder
            -Map~String, GameListener~ listeners
            -Map~String, AttackOrder~ attackOrders
            -long matchDurationMs
            -long tickCounter
            -boolean gameOver
            -String winnerId
            -String finishReason
            +addPlayer(Player player) void
            +selectSpecies(Player player, String species) void
            +startGame() void
            +update(int deltaMs) void
            +commandMove(Player player, List~Integer~ animalIds, double targetX, double targetY) void
            +commandAttack(Player player, List~Integer~ animalIds, int targetAnimalId) void
            +viewZone(Player player, String zoneId) void
            +handleDisconnect(Player player) void
            +registerListener(String participantId, GameListener listener) void
            +removeListener(String participantId) void
            +sendToPlayer(String participantId, String type, JsonObject payload) void
            +broadcastAction(String type, JsonObject payload) void
            +getAttackOrder(String participantId) AttackOrder
            +clearAttackOrder(String participantId) void
            +getAllAnimals() List~Animal~
            +getOpponent(Player player) Player
            +isRunning() boolean
            +isFinished() boolean
            +getMatchDurationSeconds() int
            -moveAnimals(double deltaSeconds) void
            -collectCoffee() void
            -checkFlags() void
            -checkPacksDefeated() void
            -finish(String winnerId, String reason) void
            -broadcastState() void
        }
        class Phase {
            <<enumeration>>
            +Phase WAITING
            +Phase SELECTING
            +Phase RUNNING
            +Phase FINISHED
        }
        class MapManager {
            -Map~String, Zone~ worldMap
            -buildWorld() void
            +getZone(String zoneId) Zone
            +getZones() Collection~Zone~
            +findZoneAt(double x, double y) Zone
            +calculateRouteBFS(Zone start, Zone destination) List~Zone~
            +commandMove(Animal animal, double targetX, double targetY) boolean
            +assignStartingZones(List~Participant~ participants, Random random) void
            +spawnItems(List~Player~ players, Random random) void
            +findFlagOf(String ownerId) Flag
            +transferEntity(Animal animal, Zone from, Zone to) void
        }
        class CombatManager {
            +processCombat(GameSession session) void
            +executeAttack(Animal attacker, Animal target, Pack attackerPack) void
            +validateDeath(Animal target) void
            -processBotAttacks(GameSession session, long now) void
            -processPlayerAttacks(GameSession session, long now) void
            -inRange(Animal attacker, Animal target) boolean
        }
        class VisibilityService {
            +buildState(GameSession session, Player player) GameState
            +zonesWithLivingAnimals(GameSession session, Player player) List~Zone~
        }
        class SnapshotBuilder {
            +matchJoined(GameSession session, Player viewer) JsonObject
            +animalSelection(GameSession session, Player viewer) JsonObject
            +availableSpecies(GameSession session) List~String~
            +gameStarted(GameSession session, Player viewer) JsonObject
            +gameOver(GameSession session) JsonObject
            -zonesArray(GameSession session) JsonArray
            -packJson(Pack pack) JsonObject
            -speciesArray(List~String~ species) JsonArray
        }
        class GameLoop {
            -int tickRateMs
            -MatchManager matchManager
            -boolean running
            -Thread thread
            -long lastTickNanos
            +start() void
            +stop() void
            +run() void
            +updateMatches(int deltaMs) void
            +isRunning() boolean
        }
    }

    namespace network {
        class ClientChannel {
            <<interface>>
            +receive() String
            +send(String message) boolean
            +close() void
            +remoteAddress() String
        }
        class NetworkServer {
            -MessageRouterServer router
            -ExecutorService clientPool
            -ScheduledExecutorService housekeeping
            -Map~InetSocketAddress, UdpConnection~ connections
            -DatagramSocket socket
            -boolean running
            -int port
            -Thread receiveThread
            +startServer(int port) void
            -receiveLoop() void
            -handleHello(InetSocketAddress from, Packet packet) void
            -housekeepingTick() void
            +stop() void
            +getPort() int
            +isRunning() boolean
        }
        class ClientHandler {
            -ClientChannel channel
            -MessageRouterServer router
            -Player player
            -GameSession session
            -boolean sendFailureLogged
            +run() void
            +sendMessage(String type, JsonObject payload) void
            +sendError(String description) void
            +onGameMessage(String type, JsonObject payload) void
            +close() void
            +attach(Player player, GameSession session) void
            +getPlayer() Player
            +getSession() GameSession
            +getRoomCode() String
        }
        class MessageRouterServer {
            -MessageHandler handler
            +routeMessage(ClientHandler client, String jsonMessage) void
            +onDisconnect(ClientHandler client) void
        }
        class MessageHandler {
            -MatchManager matchManager
            -SnapshotBuilder snapshotBuilder
            +handleCreateMatch(ClientHandler client, Message message) void
            +handleJoinMatch(ClientHandler client, Message message) void
            +handleSelectAnimal(ClientHandler client, Message message) void
            +handleMoveAnimals(ClientHandler client, Message message) void
            +handleAttack(ClientHandler client, Message message) void
            +handleViewZone(ClientHandler client, Message message) void
            +handleDisconnect(ClientHandler client) void
            -requireSession(ClientHandler client) GameSession
            -requirePlayer(ClientHandler client) Player
            -requireNickname(String nickname) String
            -readAnimalIds(Message message) List~Integer~
        }
    }

    namespace network_udp {
        class UdpProtocol {
            +int MAGIC
            +byte VERSION
            +byte DATA
            +byte ACK
            +byte HELLO
            +byte HELLO_ACK
            +byte BYE
            +byte PING
            +byte PONG
            +int HEADER_SIZE
            +int MAX_DATAGRAM_SIZE
            +encode(byte type, int sequence, int acknowledgement, byte[] payload)$ byte[]
            +encodeText(byte type, int sequence, int acknowledgement, String text)$ byte[]
            +decode(byte[] data, int length)$ Packet
            +encodeLong(long value)$ byte[]
            +decodeLong(byte[] payload)$ long
        }
        class Packet {
            +byte type
            +int sequence
            +int acknowledgement
            +byte[] payload
            +payloadAsText() String
        }
        class ReliableUdpChannel {
            -DatagramSink sink
            -Map~Integer, Pending~ pending
            -TreeMap~Integer, byte[]~ future
            -ByteArrayOutputStream assembly
            -Consumer~String~ receiver
            -int nextSequence
            -int expectedSequence
            +send(String text) boolean
            +sendControl(byte type) void
            +onPacket(Packet packet, long now) void
            +tick(long now) void
            +close() void
            -onData(Packet packet) void
            -onAck(int acknowledgement) void
        }
        class UdpConnection {
            -InetSocketAddress remote
            -long sessionId
            -ReliableUdpChannel channel
            -BlockingQueue~String~ inbox
            -boolean closed
            -Runnable closeAction
            +acceptHandshake() void
            +onPacket(Packet packet, long now) void
            +tick(long now) void
            +lastReceivedNanos() long
            +getSessionId() long
            +setCloseAction(Runnable closeAction) void
            +receive() String
            +send(String message) boolean
            +close() void
            +remoteAddress() String
        }
    }

    namespace server {
        class ServerMain {
            +main(String[] args)$ void
            -resolvePort(String[] args)$ int
        }
    }

    Animal --|> BaseAnimal
    Zone --|> BaseZone
    Player --|> Participant
    Bot --|> Participant
    Participant --> Pack : pack
    Pack --> Animal : animals
    Zone --> Animal
    Zone --> CoffeeCup
    Zone --> Flag
    AttackOrder --> Animal
    GameSession --> Player
    GameSession --> Bot
    GameSession --> MapManager
    GameSession --> CombatManager
    GameSession --> VisibilityService
    GameSession --> SnapshotBuilder
    GameSession --> AttackOrder
    GameSession --> GameListener
    GameSession *-- Phase
    MapManager --> Zone
    CombatManager --> GameSession
    VisibilityService --> GameState
    SnapshotBuilder --> GameState
    GameLoop --> MatchManager
    PlayerFactory ..|> IParticipantFactory : implements
    BotFactory ..|> IParticipantFactory : implements
    GameSessionBuilder --> GameSession : builds
    MatchManager --> GameSessionBuilder : uses to create session
    MatchManager --> MatchCodeGenerator
    MatchManager --> GameSession
    NetworkServer --> UdpConnection
    NetworkServer --> ClientHandler
    NetworkServer --> MessageRouterServer
    UdpConnection ..|> ClientChannel : implements
    UdpConnection *-- ReliableUdpChannel
    ReliableUdpChannel ..> UdpProtocol : usa
    ClientHandler ..|> GameListener : implements
    ClientHandler --> MessageRouterServer
    ClientHandler --> GameSession
    ClientHandler --> Player
    MessageRouterServer --> MessageHandler
    MessageHandler --> MatchManager
    MessageHandler --> SnapshotBuilder
    MessageHandler --> Player
    ServerMain --> MatchManager
    ServerMain --> MessageHandler
    ServerMain --> MessageRouterServer
    ServerMain --> NetworkServer
    ServerMain --> GameLoop
```
