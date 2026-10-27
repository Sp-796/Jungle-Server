Spec client - Caso 2 POO

Constants (package constants)

	interface IConstants // Centraliza los valores fijos y parametros configurables del cliente

		+ int initialEnergyPlayer = 20 // Energia inicial de cada animal perteneciente al pack de un jugador
		+ int initialEnergyBot = 10 // Energia inicial de cada animal perteneciente a un pack bot
		+ int maxEnergy = 20 // Energia maxima que puede alcanzar un animal del pack de un jugador
		+ int initialDamagePlayer = 2 // Dano inicial compartido por los animales del pack de un jugador
		+ int initialDamageBot = 1 // Dano inicial compartido por los animales de un pack bot
		+ int maxDamageCapacityPlayer = 12 // Dano maximo teorico del pack de un jugador dano inicial 2 + 10 tazas
		+ int maxDamageCapacityBot = 1 // Dano del pack bot que no aumenta mediante las tazas de cafe
		+ int attackCooldown = 1000 // Tiempo minimo en milisegundos entre ataques consecutivos de un animal
		+ int maxTeamSizePlayer = 10 // Cantidad de animales que conforman el pack de un jugador
		+ int maxTeamSizeBot = 4 // Cantidad de animales que conforman cada pack bot
		+ int botPackCount = 5 // Cantidad de packs bot presentes en la partida
		+ int maxAmountCoffee = 10 // Cantidad total de tazas de cafe disponibles en la partida
		+ int maxZones = 12 // Cantidad total de zonas que conforman el mapa
		+ double animalSpeed = 50.0 // Velocidad constante utilizada para representar el movimiento de los animales
		+ double encounterRadius = 15.0 // Radio utilizado para determinar cuando dos animales se encuentran
		+ int refreshRate = 30 // Frecuencia de actualizacion visual del cliente
		+ String defaultServerIP = "localhost" // Direccion IP utilizada por defecto para conectarse al servidor
		+ int defaultServerPort = 8080 // Puerto utilizado por defecto para establecer la conexion con el servidor
		+ int windowWidth = 1280 // Ancho de la ventana del juego en pixeles
		+ int windowHeight = 720 // Alto de la ventana del juego en pixeles
		+ String assetsPath = "/assets/images/" // Ruta base donde se encuentran los recursos graficos del juego
		+ int worldWidth = 1280 // Ancho logico del mundo recibido en GAME_STARTED
		+ int worldHeight = 720 // Alto logico del mundo recibido en GAME_STARTED
		+ int zoneColumns = 4 // Cantidad de columnas de la cuadricula de zonas
		+ int zoneRows = 3 // Cantidad de filas de la cuadricula de zonas
		+ double animalSpriteSize = 40.0 // Tamano en unidades del mundo con el que se dibuja un animal
		+ double coffeeSpriteSize = 24.0 // Tamano en unidades del mundo con el que se dibuja una taza de cafe
		+ double clickRadius = 22.0 // Distancia para considerar que el clic selecciono un animal
		+ int mapPadding = 4 // Margen del mundo que se deja libre al dibujar
		+ int connectionTimeoutMs = 5000 // Tiempo maximo de espera durante el handshake UDP
		+ int maxMessageLength = 64 * 1024 // Longitud maxima de un mensaje JSON recibido
		+ int maxNicknameLength = 20 // Longitud maxima permitida para el nickname
		+ String defaultNickname = "Jugador" // Nickname sugerido en el formulario del lobby
		+ int udpRetransmitMs = 150 // Milisegundos antes de reenviar un mensaje sin ACK
		+ int udpKeepAliveMs = 2000 // Milisegundos entre PING de mantenimiento
		+ int udpTimeoutMs = 15000 // Milisegundos sin recibir nada antes de dar la conexion por perdida
		+ int udpWindowSize = 256 // Maximo de mensajes pendientes de ACK antes de descartar los nuevos
		+ int udpMaxBufferedMessages = 1024 // Maximo de mensajes adelantados que se guardan esperando al faltante
		+ int udpMaxRetransmitsPerTick = 32 // Maximo de retransmisiones por pasada de mantenimiento
		+ int udpHandshakePollMs = 200 // Milisegundos entre intentos de HELLO durante el handshake
		+ int udpMaxDatagramSize = 60000 // Tamano maximo de un datagrama
		+ int udpReceiveBufferSize = 1_048_576 // Buffer de recepcion del socket UDP
		+ int udpHousekeepingMs = 50 // Milisegundos entre pasadas de retransmision y deteccion de timeout
		+ String flagColorPlayerOne = "Guaria Morada" // Color de la bandera del primer jugador
		+ String flagColorPlayerTwo = "Yiguirro" // Color de la bandera del segundo jugador
		+ String finishReasonFlagTouched = "FLAG_TOUCHED" // Motivo de fin al tocar la bandera rival
		+ String finishReasonPackDefeated = "PACK_DEFEATED" // Motivo de fin cuando un pack se queda sin animales
		+ String finishReasonDisconnect = "DISCONNECT" // Motivo de fin cuando un jugador se desconecta
		+ String finishReasonOpponentLeft = "OPPONENT_LEFT" // Motivo de fin cuando el rival abandona la sala

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
		+ void setCurrentEnergy(int currentEnergy) // Actualiza la energia actual
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

	class Point2D // Punto inmutable del mundo utilizado por la interfaz y los modelos

		- double x // Coordenada horizontal inmutable
		- double y // Coordenada vertical inmutable
		+ double getX() // Retorna la coordenada horizontal
		+ double getY() // Retorna la coordenada vertical
		+ double distanceTo(Point2D other) // Calcula la distancia euclidiana hacia otro punto
		+ Point2D stepTowards(Point2D target, double maxDistance) // Avanza hacia el objetivo sin pasarse de la distancia maxima

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
		+ String VIEW_ZONE // Solicitud para cambiar la zona que se desea visualizar
		+ String MATCH_CREATED // Confirmacion de creacion de partida
		+ String MATCH_JOINED // Confirmacion de ingreso a una partida
		+ String ANIMAL_SELECTION // Estado de la seleccion de especies
		+ String GAME_STARTED // Inicio de la partida con el mapa y el pack inicial
		+ String STATE_UPDATE // Instantanea periodica del estado visible
		+ String GAME_OVER // Resumen final de la partida
		+ String COFFEE_COLLECTED // Evento puntual de taza de cafe recogida
		+ String ERROR // Notificacion de error enviada por el servidor

Model (package client.model) // Paquete del modelo especifico para el cliente

	class Animal extends common.model.BaseAnimal // Hereda id, species, currentEnergy, x, y del common

		+ double targetX // Destino horizontal para interpolar el movimiento localmente
		+ double targetY // Destino vertical para interpolar el movimiento localmente
		+ Zone currentZone // Referencia visual de la zona en la que se encuentra el animal
		+ boolean isAlive // Indica si el animal continua vivo segun el estado recibido del servidor
		+ String zoneId // Identificador de la zona asignada por el servidor
		+ String owner // Dueno del animal: ME, BOT u OPPONENT
		+ boolean moving // Indica si el servidor reporta movimiento en curso
		+ void updateLocalPosition(int deltaTime) // Interpola la posicion hacia el target de forma autonoma
		+ void applyServerState(String species, int energy, double serverX, double serverY, boolean alive, String zoneId, String owner, double targetX, double targetY, boolean moving) // Aplica una instantanea recibida del servidor
		+ boolean isMine() // Indica si el animal pertenece al jugador local
		+ boolean isBot() // Indica si el animal pertenece a un pack bot
		+ void setTarget(double targetX, double targetY) // Actualiza el destino local de interpolacion
		+ void setCurrentZone(Zone currentZone) // Asocia la zona visual del animal

	class Zone extends common.model.BaseZone // Hereda zoneId y hasFlag del common

		+ String displayName // Nombre visible de la zona
		+ String imageName // Nombre de la imagen utilizada para dibujar la zona
		+ double x // Coordenada horizontal del rectangulo de la zona
		+ double y // Coordenada vertical del rectangulo de la zona
		+ double width // Ancho del rectangulo de la zona
		+ double height // Alto del rectangulo de la zona
		+ List<Path> connectedPaths // Caminos visuales que conectan esta zona con otras
		+ List<Animal> presentAnimals // Animales actualmente contenidos dentro de esta zona visualmente
		+ List<CoffeeCup> presentCoffeeCups // Tazas de cafe contenidas dentro de esta zona visualmente
		+ boolean isVisible // Indica si la zona puede ser mostrada actualmente al jugador
		+ String flagOwnerId // Identificador del dueno de la bandera
		+ String flagColor // Color de la bandera de la zona
		+ double flagX // Coordenada horizontal de la bandera
		+ double flagY // Coordenada vertical de la bandera
		+ void addPath(Path path) // Agrega un camino visual evitando duplicados
		+ void addPresentAnimal(Animal animal) // Agrega un animal a la zona visual
		+ void addCoffeeCup(CoffeeCup cup) // Agrega una taza a la zona visual
		+ boolean contains(double pointX, double pointY) // Indica si un punto esta dentro de la zona
		+ Point2D getCenter() // Retorna el centro geometrico de la zona
		+ void setFlag(String ownerId, String color, double flagX, double flagY) // Asigna los datos visuales de la bandera
		+ void clearFlag() // Limpia la bandera de la zona

	class Path // Camino visual entre dos zonas

		+ String fromZoneId // Identificador de la zona de origen
		+ String toZoneId // Identificador de la zona de destino
		+ double x1 // Coordenada horizontal del origen
		+ double y1 // Coordenada vertical del origen
		+ double x2 // Coordenada horizontal del destino
		+ double y2 // Coordenada vertical del destino
		+ String getKey() // Clave estable para dibujar una sola vez cada arista

	class CoffeeCup // Taza de cafe que el servidor permite ver en la zona actual

		+ String cupId // Identificador de la taza
		+ double x // Coordenada horizontal de la taza
		+ double y // Coordenada vertical de la taza

	class GameState // Patron: Sujeto en Observer. Representa el estado global de la partida

		+ Map<String, Zone> mapZones // Contiene las zonas conocidas del mapa organizadas jerarquicamente
		+ List<Animal> visibleAnimals // Animales que el servidor permite visualizar al jugador
		+ List<Animal> myAnimals // Animales propios enviados en la instantanea
		+ List<CoffeeCup> visibleCoffeeCups // Tazas de cafe visibles en la zona actual
		+ List<String> availableZones // Zonas donde el jugador tiene animales vivos
		+ int aliveAnimalsPlayer // Cantidad de animales vivos pertenecientes al jugador
		+ int deadAnimalsPlayer // Cantidad de animales eliminados del pack del jugador
		+ int botsKilled // Cantidad de animales pertenecientes a packs bot eliminados por el jugador
		+ int coffeeCupsCollected // Cantidad de tazas de cafe consumidas por el jugador
		+ int damageCapacity // Dano actual compartido por el pack del jugador
		+ String playerName // Nickname del jugador
		+ String opponentName // Nickname del rival
		+ String matchCode // Codigo de la partida
		+ String mySpecies // Especie elegida por el jugador
		+ String currentZoneId // Identificador de la zona actualmente seleccionada para visualizar
		+ boolean isGameOver // Indica si la partida ha terminado
		+ boolean playerWon // Indica si el jugador obtuvo la victoria cuando la partida finalizo
		+ int matchDuration // Duracion de la partida recibida durante el juego
		+ Zone viewedZone // Zona actualmente mostrada en el mapa
		+ void addObserver(Observer observer) // Registra una vista interesada en el estado
		+ void removeObserver(Observer observer) // Remueve una vista registrada
		+ void notifyObservers() // Notifica el estado a todas las vistas registradas
		+ Zone getZone(String zoneId) // Busca una zona por su identificador
		+ List<Path> getAllPaths() // Retorna todos los caminos sin repetir

	class MatchResult // Resumen que envia el servidor al terminar la partida

		+ String matchCode // Codigo de la partida finalizada
		+ String reason // Motivo de finalizacion
		+ int durationSeconds // Duracion total en segundos
		+ String winnerId // Identificador del ganador
		+ String winnerName // Nombre del ganador
		+ List<PlayerResult> players // Estadisticas de cada participante
		+ boolean isWinner(String playerId) // Indica si el jugador indicado gano
		+ String describe() // Genera una descripcion textual del resultado
		+ static String translateReason(String reason) // Traduce el motivo a texto legible

		class PlayerResult // Estadisticas de un participante dentro del resumen

			+ String playerId // Identificador del jugador
			+ String name // Nombre del jugador
			+ String species // Especie utilizada
			+ int alive // Animales vivos al finalizar
			+ int lost // Animales perdidos durante la partida
			+ int botsKilled // Bots eliminados por el jugador
			+ int coffeeCups // Tazas de cafe recolectadas
			+ int damageCapacity // Dano final del pack

UI Layer (package ui) // Paquete encargado de la interfaz de usuario del cliente

	interface Observer // Patron: Observer. Interfaz para recibir actualizaciones del modelo

		+ void update(GameState gameState) // Recibe una actualizacion del estado del juego para refrescar la interfaz

	class MainWindow implements Observer, ClientEventListener // Ventana principal y coordinador de la interfaz

		+ MapPanel mapPanel // Panel encargado de representar visualmente el mapa y sus elementos
		+ HUDPanel hudPanel // Panel encargado de mostrar informacion de la partida
		- LobbyPanel lobbyPanel // Pantalla de conexion, lobby y seleccion de especie
		- NetworkClient networkClient // Cliente de red utilizado por la ventana
		- InputController inputController // Controlador de entrada del mapa
		- ClientGameLoop gameLoop // Bucle local de interpolacion visual
		- GameState currentState // Ultimo estado recibido del servidor
		- void initializeWindow() // Configura la ventana utilizando las constantes de tamano definidas
		- void wireActions() // Conecta los callbacks del lobby, el HUD y el mapa
		+ void showWindow() // Hace visible la ventana principal
		+ void update(GameState gameState) // Cambia a la pantalla de juego y actualiza la entrada
		+ void onConnected() // Ejecuta la accion pendiente despues de conectar
		+ void onDisconnected(String reason) // Vuelve al lobby cuando se pierde la conexion
		+ void onMatchCreated(String code, String playerId, String playerName) // Muestra la espera del rival
		+ void onMatchJoined(String code, String playerId, String playerName, int playerNumber, String opponentName, List<String> availableSpecies) // Muestra la seleccion de especie
		+ void onAnimalSelection(String yourSpecies, String opponentSpecies, List<String> availableSpecies) // Refresca el estado de la seleccion
		+ void onGameStarted(GameState gameState) // Cambia a la pantalla de juego e inicia el bucle local
		+ void onGameOver(MatchResult result) // Detiene el juego y muestra el resultado final
		+ void onError(String message) // Presenta un error en el lobby o en el HUD
		+ void onCoffeeCollected(String cupId, int damageCapacity) // Informa la recoleccion de una taza
		- void returnToLobby() // Restablece la ventana para una nueva partida

	class MapPanel implements Observer // Panel que dibuja el mapa y actualiza su vista

		- SpriteLoader sprites // Cargador y cache de imagenes del juego
		- Set<Integer> selectedAnimalIds // Identificadores de los animales seleccionados
		- GameState currentGameState // Estado utilizado para dibujar el mapa
		+ void update(GameState gameState) // Actualiza la representacion visual utilizando el estado recibido
		+ void setSelectedAnimalIds(Collection<Integer> ids) // Actualiza el resaltado de la seleccion
		+ Point2D toWorld(int screenX, int screenY) // Convierte coordenadas de pantalla a coordenadas del mundo
		+ Animal getAnimalAt(double worldX, double worldY) // Busca el animal que ocupa una posicion del mundo
		- void paintComponent(Graphics g) // Dibuja zonas, caminos, animales, tazas y banderas
		- void drawAnimal(Graphics2D g2d, Animal animal) // Dibuja un animal con su barra de energia y seleccion

	class HUDPanel implements Observer // Panel que muestra las estadisticas y el estado del jugador

		- JLabel playerLabel // Nombre del jugador
		- JLabel speciesLabel // Especie elegida
		- JLabel matchLabel // Codigo de la partida
		- JLabel opponentLabel // Nombre del rival
		- JLabel durationLabel // Duracion transcurrida
		- JLabel aliveLabel // Animales vivos
		- JLabel deadLabel // Animales perdidos
		- JLabel botsLabel // Bots eliminados
		- JLabel coffeeLabel // Tazas recolectadas
		- JLabel damageLabel // Dano actual del pack
		- JComboBox<ZoneItem> zoneSelector // Selector de la zona que se desea ver
		- void drawScoreboard(int aliveAnimalsPlayer, int botsKilled) // Muestra informacion relacionada con los animales
		- void drawCoffeeCounter(int cups) // Muestra la cantidad de tazas de cafe recolectadas
		- void showGameOverScreen(boolean isGameOver, boolean playerWon) // Muestra el resultado de la partida
		- void updateZoneSelector(GameState gameState) // Refresca las zonas disponibles y la zona actual
		+ void update(GameState gameState) // Actualiza la informacion del HUD en base al nuevo estado
		+ void setZoneSelectionListener(Consumer<String> listener) // Registra la accion de cambio de zona
		+ void setSelectAllAction(Runnable action) // Registra la accion del boton Todos
		+ void setMessage(String message) // Muestra un aviso temporal
		+ void setError(String message) // Muestra un error temporal
		+ static String speciesName(String speciesId) // Traduce el id de la especie a un nombre legible

	class LobbyPanel // Pantalla de conexion, lobby y eleccion de especie

		- JTextField nicknameField // Nickname ingresado por el jugador
		- JTextField hostField // Direccion del servidor
		- JTextField portField // Puerto del servidor
		- JTextField codeField // Codigo de la partida para unirse
		- JButton createButton // Boton para crear una partida
		- JButton joinButton // Boton para unirse a una partida
		- JComboBox<SpeciesItem> speciesSelector // Especies disponibles para elegir
		- JButton confirmSpeciesButton // Boton para confirmar la especie
		- Consumer<CreateRequest> onCreate // Callback de creacion de partida
		- Consumer<JoinRequest> onJoin // Callback de ingreso a partida
		- Consumer<String> onSpecies // Callback de seleccion de especie
		- boolean busy // Indica que hay una solicitud en curso
		- boolean locked // Indica que el formulario esta bloqueado
		- boolean speciesConfirmed // Indica que el jugador ya confirmo especie
		- void buildHeader() // Construye el encabezado visual del lobby
		- void buildConnectionForm() // Construye los campos de conexion y los botones
		- void handleCreate() // Valida los datos y notifica la intencion de crear
		- void handleJoin() // Valida los datos y notifica la intencion de unirse
		- void handleSpecies() // Notifica la especie seleccionada
		- Integer parsePort() // Valida y convierte el puerto ingresado
		+ void showWaiting(String matchCode) // Muestra la espera del rival con el codigo de partida
		+ void showSelection(String matchCode, List<String> availableSpecies, String yourSpecies, String opponentSpecies) // Muestra la seleccion de especie
		+ void setBusy(boolean value) // Deshabilita temporalmente los botones mientras se conecta
		+ void setStatus(String message, boolean error) // Actualiza el mensaje de estado del lobby
		+ void allowSpeciesRetry() // Permite reintentar la eleccion si el servidor la rechazo
		+ void resetForNewMatch() // Vuelve al estado inicial para crear o unirse a otra partida
		+ void refreshEnabled() // Recalcula que controles deben estar habilitados

		class CreateRequest // DTO con los datos necesarios para crear una partida

			+ String nickname // Nickname elegido
			+ String host // Direccion del servidor
			+ int port // Puerto del servidor

		class JoinRequest // DTO con los datos necesarios para unirse a una partida

			+ String nickname // Nickname elegido
			+ String host // Direccion del servidor
			+ int port // Puerto del servidor
			+ String code // Codigo de la partida

		class SpeciesItem // Elemento del combo de especies con id y nombre visible

			+ String id // Identificador usado en el protocolo
			+ String displayName // Nombre mostrado al jugador

	class SpriteLoader // Carga y cachea las imagenes del juego

		- Map<String, Image> cache // Imagenes ya cargadas por nombre base
		- Set<String> missing // Recursos que no pudieron cargarse
		+ Image load(String assetName) // Carga un recurso por nombre y lo guarda en cache
		+ Image zone(String imageName) // Carga la imagen de una zona
		+ Image animal(String species) // Carga la imagen de una especie
		+ Image coffee() // Carga la imagen de la taza de cafe

Control Layer (package control) // Paquete encargado del control y la comunicacion del cliente

	interface ClientEventListener // Patron: Observer. Eventos del cliente que no son una actualizacion de estado

		+ void onConnected() // Evento de conexion establecida
		+ void onDisconnected(String reason) // Evento de desconexion con su motivo
		+ void onMatchCreated(String matchCode, String playerId, String playerName) // Evento de partida creada
		+ void onMatchJoined(String matchCode, String playerId, String playerName, int playerNumber, String opponentName, List<String> availableSpecies) // Evento de ingreso a partida
		+ void onAnimalSelection(String yourSpecies, String opponentSpecies, List<String> availableSpecies) // Evento de estado de seleccion
		+ void onGameStarted(GameState gameState) // Evento de inicio de partida
		+ void onGameOver(MatchResult result) // Evento de fin de partida
		+ void onError(String message) // Evento de error
		+ void onCoffeeCollected(String cupId, int damageCapacity) // Evento de taza recogida

	class JsonAdapter // PATRON: Adapter. Hace que la interfaz incompatible de JSON funcione con objetos Java

		- Map<String, Zone> zones // Zonas construidas a partir de GAME_STARTED
		- Map<Integer, Animal> animalsById // Animales reutilizados entre instantaneas
		- String matchCode // Codigo de la partida en curso
		- String playerName // Nombre del jugador
		- String opponentName // Nombre del rival
		- String mySpecies // Especie propia
		+ String adaptActionToJson(String actionType, Object payload) // Convierte una accion y su contenido a formato JSON
		+ GameState adaptJsonToGameState(String jsonString) // Convierte un string JSON en un objeto GameState utilizable
		+ void reset() // Limpia el estado acumulado para una partida nueva
		+ GameState adaptGameStarted(JsonObject payload) // Adapta el payload de GAME_STARTED
		+ GameState adaptStateUpdate(JsonObject payload) // Adapta el payload de STATE_UPDATE
		+ MatchResult adaptGameOver(JsonObject payload) // Adapta el payload de GAME_OVER
		- Zone parseZone(JsonObject json) // Crea o actualiza una zona desde JSON
		- Animal parseAnimal(JsonObject json, String fallbackSpecies) // Crea o actualiza un animal desde JSON

	class NetworkClient // Encargado de la conexion UDP con el servidor

		- JsonAdapter jsonAdapter // Se apoya en el adapter para procesar y enviar los mensajes
		- List<Observer> observers // Observadores que reciben las actualizaciones del estado del juego
		- List<ClientEventListener> listeners // Oyentes de eventos puntuales del cliente
		- UdpClientTransport transport // Transporte UDP confiable
		- Thread listenerThread // Hilo encargado de recibir mensajes
		- boolean connected // Indica si la conexion esta activa
		+ void addObserver(Observer observer) // Registra una vista que recibira los estados
		+ void removeObserver(Observer observer) // Remueve una vista registrada
		+ void addEventListener(ClientEventListener listener) // Registra un oyente de eventos
		+ boolean isConnected() // Indica si el cliente esta conectado
		+ void connectToServer(String ip, int port) // Abre el transporte UDP y completa el handshake
		- void listenForUpdate() // Inicia el hilo encargado de recibir mensajes desde el servidor
		- void readLoop() // Lee mensajes, los deserializa y los enruta al EDT
		+ void disconnect() // Cierra la conexion y el transporte
		+ void sendAction(String actionType, Object payload) // Utiliza el adapter para construir y enviar el mensaje al server
		+ void sendCreateMatch(String nickname) // Envia CREATE_MATCH
		+ void sendJoinMatch(String nickname, String code) // Envia JOIN_MATCH
		+ void sendSelectSpecies(String species) // Envia SELECT_ANIMAL
		+ void sendMoveAnimals(List<Integer> animalIds, double targetX, double targetY) // Envia MOVE_ANIMALS
		+ void sendAttack(List<Integer> animalIds, int targetAnimalId) // Envia ATTACK
		+ void sendViewZone(String zoneId) // Envia VIEW_ZONE
		- void route(Message message) // Enruta un mensaje del servidor segun su tipo

	class InputController extends MouseAdapter // Interpreta los eventos del usuario y los convierte en acciones

		+ NetworkClient networkClient // Instancia utilizada para enviar las intenciones del jugador al servidor
		- MapPanel mapPanel // Panel del mapa para convertir coordenadas
		- Set<Integer> selectedAnimalIds // Animales propios seleccionados
		- GameState gameState // Ultimo estado recibido
		- Consumer<String> messageSink // Destino de los mensajes para la interfaz
		+ void setGameState(GameState gameState) // Actualiza el estado y depura la seleccion
		+ void reset() // Prepara el controlador para una partida nueva
		+ void onMouseClick(int x, int y) // Detecta la posicion donde el jugador realizo un clic
		- void handleMoveIntent(List<Integer> selectedAnimalIds, double targetX, double targetY) // Envia intencion de movimiento
		- void handleAttackIntent(List<Integer> selectedAnimalIds, int targetAnimalId) // Envia intencion de ataque
		+ void handleZoneViewIntent(String zoneId) // Solicita cambiar la zona que se desea visualizar en pantalla
		+ void selectAll() // Selecciona todos los animales vivos propios
		+ void clearSelection() // Limpia la seleccion actual
		+ List<Integer> getSelectedAnimalIds() // Retorna los identificadores seleccionados

	class ClientGameLoop // Bucle de juego local para interpolar animaciones

		- Supplier<GameState> stateSupplier // Proveedor del estado actual
		- Runnable onFrame // Accion ejecutada en cada frame
		- int fps // Frecuencia de refresco local para animaciones fluidas
		- Timer timer // Temporizador de Swing que impulsa el bucle
		- boolean running // Estado de ejecucion del bucle local
		+ void start() // Inicia el bucle de interpolacion visual
		+ void stop() // Detiene el bucle local
		+ boolean isRunning() // Indica si el bucle esta activo
		+ void updateAnimations(int deltaTime) // Interpola el movimiento local de los animales hacia su targetX targetY

UDP Transport (package control.udp) // Transporte confiable construido sobre datagramas UDP

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

		class Packet // Datagrama decodificado con tipo, secuencia, ACK y payload

			+ byte type // Tipo del datagrama
			+ int sequence // Numero de secuencia del emisor
			+ int acknowledgement // Ultimo numero confirmado por el receptor
			+ byte[] payload // Contenido del datagrama
			+ String payloadAsText() // Convierte el payload a texto UTF-8

	class ReliableUdpChannel // Canal UDP confiable y ordenado del lado del cliente

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

	class UdpClientTransport implements AutoCloseable // Transporte UDP del cliente con handshake y mantenimiento

		- DatagramSocket socket // Socket UDP conectado al servidor
		- ReliableUdpChannel channel // Canal confiable asociado al socket
		- BlockingQueue<String> inbox // Cola de mensajes completos recibidos
		- boolean open // Indica si el transporte sigue abierto
		- Thread receiverThread // Hilo que recibe datagramas
		- Thread housekeepingThread // Hilo que retransmite y envia PING
		+ static UdpClientTransport connect(String host, int port, int timeoutMs) // Abre el socket y completa el handshake
		+ boolean send(String message) // Envia una linea JSON al servidor
		+ String receive() // Recibe el siguiente mensaje o null al cerrarse
		+ boolean isOpen() // Indica si el transporte esta abierto
		+ void close() // Envia BYE y cierra el transporte
		- void receiveLoop() // Recibe y decodifica datagramas continuamente
		- void housekeepingLoop() // Retransmite, envia PING y detecta timeout

Client Entry (package client) // Punto de entrada de la aplicacion

	class ClientMain // Clase final de arranque del cliente

		+ static void main(String[] args) // Construye la interfaz y abre la ventana principal
		- static void installLookAndFeel() // Instala el look and feel del sistema operativo


Diagrama Mermaid:

```mermaid
classDiagram

    namespace constants {
        class IConstants {
            <<interface>>
            +int initialEnergyPlayer
            +int initialEnergyBot
            +int maxEnergy
            +int initialDamagePlayer
            +int initialDamageBot
            +int maxDamageCapacityPlayer
            +int maxDamageCapacityBot
            +int attackCooldown
            +int maxTeamSizePlayer
            +int maxTeamSizeBot
            +int botPackCount
            +int maxAmountCoffee
            +int maxZones
            +double animalSpeed
            +double encounterRadius
            +int refreshRate
            +String defaultServerIP
            +int defaultServerPort
            +int windowWidth
            +int windowHeight
            +String assetsPath
            +int worldWidth
            +int worldHeight
            +int zoneColumns
            +int zoneRows
            +double animalSpriteSize
            +double coffeeSpriteSize
            +double clickRadius
            +int mapPadding
            +int connectionTimeoutMs
            +int maxMessageLength
            +int maxNicknameLength
            +String defaultNickname
            +int udpRetransmitMs
            +int udpKeepAliveMs
            +int udpTimeoutMs
            +int udpWindowSize
            +int udpMaxBufferedMessages
            +int udpMaxRetransmitsPerTick
            +int udpHandshakePollMs
            +int udpMaxDatagramSize
            +int udpReceiveBufferSize
            +int udpHousekeepingMs
            +String flagColorPlayerOne
            +String flagColorPlayerTwo
            +String finishReasonFlagTouched
            +String finishReasonPackDefeated
            +String finishReasonDisconnect
            +String finishReasonOpponentLeft
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

    namespace client_model {
        class Animal {
            +double targetX
            +double targetY
            +Zone currentZone
            +boolean isAlive
            +String zoneId
            +String owner
            +boolean moving
            +updateLocalPosition(int deltaTime) void
            +applyServerState(String species, int energy, double serverX, double serverY, boolean alive, String zoneId, String owner, double targetX, double targetY, boolean moving) void
            +isMine() boolean
            +isBot() boolean
            +setTarget(double targetX, double targetY) void
            +setCurrentZone(Zone currentZone) void
        }
        class Zone {
            +String displayName
            +String imageName
            +double x
            +double y
            +double width
            +double height
            +List~Path~ connectedPaths
            +List~Animal~ presentAnimals
            +List~CoffeeCup~ presentCoffeeCups
            +boolean isVisible
            +String flagOwnerId
            +String flagColor
            +double flagX
            +double flagY
            +addPath(Path path) void
            +addPresentAnimal(Animal animal) void
            +addCoffeeCup(CoffeeCup cup) void
            +contains(double pointX, double pointY) boolean
            +getCenter() Point2D
            +setFlag(String ownerId, String color, double flagX, double flagY) void
            +clearFlag() void
        }
        class Path {
            +String fromZoneId
            +String toZoneId
            +double x1
            +double y1
            +double x2
            +double y2
            +getKey() String
        }
        class CoffeeCup {
            +String cupId
            +double x
            +double y
        }
        class GameState {
            +Map~String, Zone~ mapZones
            +List~Animal~ visibleAnimals
            +List~Animal~ myAnimals
            +List~CoffeeCup~ visibleCoffeeCups
            +List~String~ availableZones
            +int aliveAnimalsPlayer
            +int deadAnimalsPlayer
            +int botsKilled
            +int coffeeCupsCollected
            +int damageCapacity
            +String playerName
            +String opponentName
            +String matchCode
            +String mySpecies
            +String currentZoneId
            +boolean isGameOver
            +boolean playerWon
            +int matchDuration
            +Zone viewedZone
            +addObserver(Observer observer) void
            +removeObserver(Observer observer) void
            +notifyObservers() void
            +getZone(String zoneId) Zone
            +getAllPaths() List~Path~
        }
        class MatchResult {
            +String matchCode
            +String reason
            +int durationSeconds
            +String winnerId
            +String winnerName
            +List~PlayerResult~ players
            +isWinner(String playerId) boolean
            +describe() String
            +translateReason(String reason)$ String
        }
        class PlayerResult {
            +String playerId
            +String name
            +String species
            +int alive
            +int lost
            +int botsKilled
            +int coffeeCups
            +int damageCapacity
        }
    }

    namespace ui {
        class Observer {
            <<interface>>
            +update(GameState gameState) void
        }
        class MainWindow {
            +MapPanel mapPanel
            +HUDPanel hudPanel
            -LobbyPanel lobbyPanel
            -NetworkClient networkClient
            -InputController inputController
            -ClientGameLoop gameLoop
            -GameState currentState
            -initializeWindow() void
            -wireActions() void
            +showWindow() void
            +update(GameState gameState) void
            +onConnected() void
            +onDisconnected(String reason) void
            +onMatchCreated(String code, String playerId, String playerName) void
            +onMatchJoined(String code, String playerId, String playerName, int playerNumber, String opponentName, List~String~ availableSpecies) void
            +onAnimalSelection(String yourSpecies, String opponentSpecies, List~String~ availableSpecies) void
            +onGameStarted(GameState gameState) void
            +onGameOver(MatchResult result) void
            +onError(String message) void
            +onCoffeeCollected(String cupId, int damageCapacity) void
            -returnToLobby() void
        }
        class MapPanel {
            -SpriteLoader sprites
            -Set~Integer~ selectedAnimalIds
            -GameState currentGameState
            +update(GameState gameState) void
            +setSelectedAnimalIds(Collection~Integer~ ids) void
            +toWorld(int screenX, int screenY) Point2D
            +getAnimalAt(double worldX, double worldY) Animal
            -paintComponent(Graphics g) void
            -drawAnimal(Graphics2D g2d, Animal animal) void
        }
        class HUDPanel {
            -JLabel playerLabel
            -JLabel speciesLabel
            -JLabel matchLabel
            -JLabel opponentLabel
            -JLabel durationLabel
            -JLabel aliveLabel
            -JLabel deadLabel
            -JLabel botsLabel
            -JLabel coffeeLabel
            -JLabel damageLabel
            -JComboBox~ZoneItem~ zoneSelector
            -drawScoreboard(int aliveAnimalsPlayer, int botsKilled) void
            -drawCoffeeCounter(int cups) void
            -showGameOverScreen(boolean isGameOver, boolean playerWon) void
            -updateZoneSelector(GameState gameState) void
            +update(GameState gameState) void
            +setZoneSelectionListener(Consumer~String~ listener) void
            +setSelectAllAction(Runnable action) void
            +setMessage(String message) void
            +setError(String message) void
            +speciesName(String speciesId)$ String
        }
        class LobbyPanel {
            -JTextField nicknameField
            -JTextField hostField
            -JTextField portField
            -JTextField codeField
            -JButton createButton
            -JButton joinButton
            -JComboBox~SpeciesItem~ speciesSelector
            -JButton confirmSpeciesButton
            -Consumer~CreateRequest~ onCreate
            -Consumer~JoinRequest~ onJoin
            -Consumer~String~ onSpecies
            -boolean busy
            -boolean locked
            -boolean speciesConfirmed
            -buildHeader() JPanel
            -buildConnectionForm() JPanel
            -handleCreate() void
            -handleJoin() void
            -handleSpecies() void
            -parsePort() Integer
            +showWaiting(String matchCode) void
            +showSelection(String matchCode, List~String~ availableSpecies, String yourSpecies, String opponentSpecies) void
            +setBusy(boolean value) void
            +setStatus(String message, boolean error) void
            +allowSpeciesRetry() void
            +resetForNewMatch() void
            +refreshEnabled() void
        }
        class CreateRequest {
            +String nickname
            +String host
            +int port
        }
        class JoinRequest {
            +String nickname
            +String host
            +int port
            +String code
        }
        class SpeciesItem {
            +String id
            +String displayName
        }
        class SpriteLoader {
            -Map~String, Image~ cache
            -Set~String~ missing
            +load(String assetName) Image
            +zone(String imageName) Image
            +animal(String species) Image
            +coffee() Image
        }
    }

    namespace control {
        class ClientEventListener {
            <<interface>>
            +onConnected() void
            +onDisconnected(String reason) void
            +onMatchCreated(String matchCode, String playerId, String playerName) void
            +onMatchJoined(String matchCode, String playerId, String playerName, int playerNumber, String opponentName, List~String~ availableSpecies) void
            +onAnimalSelection(String yourSpecies, String opponentSpecies, List~String~ availableSpecies) void
            +onGameStarted(GameState gameState) void
            +onGameOver(MatchResult result) void
            +onError(String message) void
            +onCoffeeCollected(String cupId, int damageCapacity) void
        }
        class JsonAdapter {
            -Map~String, Zone~ zones
            -Map~Integer, Animal~ animalsById
            -String matchCode
            -String playerName
            -String opponentName
            -String mySpecies
            +adaptActionToJson(String actionType, Object payload) String
            +adaptJsonToGameState(String jsonString) GameState
            +reset() void
            +adaptGameStarted(JsonObject payload) GameState
            +adaptStateUpdate(JsonObject payload) GameState
            +adaptGameOver(JsonObject payload) MatchResult
            -parseZone(JsonObject json) Zone
            -parseAnimal(JsonObject json, String fallbackSpecies) Animal
        }
        class NetworkClient {
            -JsonAdapter jsonAdapter
            -List~Observer~ observers
            -List~ClientEventListener~ listeners
            -UdpClientTransport transport
            -Thread listenerThread
            -boolean connected
            +addObserver(Observer observer) void
            +removeObserver(Observer observer) void
            +addEventListener(ClientEventListener listener) void
            +isConnected() boolean
            +connectToServer(String ip, int port) void
            -listenForUpdate() void
            -readLoop() void
            +disconnect() void
            +sendAction(String actionType, Object payload) void
            +sendCreateMatch(String nickname) void
            +sendJoinMatch(String nickname, String code) void
            +sendSelectSpecies(String species) void
            +sendMoveAnimals(List~Integer~ animalIds, double targetX, double targetY) void
            +sendAttack(List~Integer~ animalIds, int targetAnimalId) void
            +sendViewZone(String zoneId) void
            -route(Message message) void
        }
        class InputController {
            +NetworkClient networkClient
            -MapPanel mapPanel
            -Set~Integer~ selectedAnimalIds
            -GameState gameState
            -Consumer~String~ messageSink
            +setGameState(GameState gameState) void
            +reset() void
            +onMouseClick(int x, int y) void
            -handleMoveIntent(List~Integer~ selectedAnimalIds, double targetX, double targetY) void
            -handleAttackIntent(List~Integer~ selectedAnimalIds, int targetAnimalId) void
            +handleZoneViewIntent(String zoneId) void
            +selectAll() void
            +clearSelection() void
            +getSelectedAnimalIds() List~Integer~
        }
        class ClientGameLoop {
            -Supplier~GameState~ stateSupplier
            -Runnable onFrame
            -int fps
            -Timer timer
            -boolean running
            +start() void
            +stop() void
            +isRunning() boolean
            +updateAnimations(int deltaTime) void
        }
    }

    namespace control_udp {
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
        class UdpClientTransport {
            -DatagramSocket socket
            -ReliableUdpChannel channel
            -BlockingQueue~String~ inbox
            -boolean open
            -Thread receiverThread
            -Thread housekeepingThread
            +connect(String host, int port, int timeoutMs)$ UdpClientTransport
            +send(String message) boolean
            +receive() String
            +isOpen() boolean
            +close() void
            -receiveLoop() void
            -housekeepingLoop() void
        }
    }

    namespace client {
        class ClientMain {
            +main(String[] args)$ void
            -installLookAndFeel()$ void
        }
    }

    Animal --|> BaseAnimal
    Zone --|> BaseZone
    MapPanel ..|> Observer : implements
    HUDPanel ..|> Observer : implements
    MainWindow ..|> Observer : implements
    MainWindow ..|> ClientEventListener : implements
    MainWindow --> MapPanel
    MainWindow --> HUDPanel
    MainWindow --> LobbyPanel
    MainWindow --> InputController
    MainWindow --> ClientGameLoop
    MainWindow --> NetworkClient
    MainWindow --> GameState
    Zone --> Path
    Zone --> Animal
    Zone --> CoffeeCup
    GameState --> Zone
    GameState --> Animal
    GameState --> CoffeeCup
    GameState --> Observer
    MatchResult *-- PlayerResult
    InputController --> NetworkClient
    InputController --> MapPanel
    NetworkClient --> JsonAdapter : usa
    NetworkClient --> UdpClientTransport
    NetworkClient --> Observer
    NetworkClient --> ClientEventListener
    ClientGameLoop --> Animal
    JsonAdapter --> GameState
    JsonAdapter --> MatchResult
    UdpClientTransport *-- ReliableUdpChannel
    ReliableUdpChannel ..> UdpProtocol : usa
    ClientMain --> MainWindow : crea
```
