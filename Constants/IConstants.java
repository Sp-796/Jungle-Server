package Constants;  // Define el paquete al que debe pertenecer este archivo

public interface IConstants {  // Define la interfaz de las constantes a utilizar, son globales pero no pueden ser modificadas en ejecucion
    
    int initialEnergyPlayer = 20;  // Establece la energia inicial con la que arranca cada animal de los jugadores
    int initialEnergyBot = 10; // Establece la energia inicial con la que arranca cada animal del bot
    int maxEnergy = 20; // Define el limite de energia maxima que puede alcanzar un animal del jugador
    
    int initialDamagePlayer = 2; // Define el daño de ataque inicial de cada animal de los jugadores
    int initialDamageBot = 1; // Define el daño de ataque inicial de cada uno de los animales del bot
    int maxDamageCapacityPlayer = 12; // Establece el limite de ataque que puede alcanzar la manada de los jugadores
    int maxDamageCapacityBot = 1; // Establece el limite de ataque maximo que puede alcanzar las manadas del bot
    int attackCoolDown = 1000; // Define el tiempo de espera entre cada uno de los ataques entre jugadores y bots

    int maxTeamSizePlayer = 10; // Establece el tamaño maximo de cada manada de los jugadores
    int maxTeamSizeBot = 4; // Establece el tamaño maximo de cada manada del bot
    int botPackCount = 5; // Define la cantidad total de manadas que podra controlar el bot
    int maxAmountCoffee = 10; // Define la cantidad maxima de tazas de cafe que apareceran en el mapa
    int maxZones = 12; // Establece el total de posibles cantidades del mapa durante el juego

    double animalSpeed = 50.0; // Define la velocidad constante a la cual se moveran los animales dentro del mapa y juego
    double encounterRadius = 15.0; // Define el radio de deteccion de oponentes u objetos en el mapa
    int refreshRate = 30; // Establece la frecuencia en milisegundos en la que se debe actualizar la interfaz grafica
    String defaultServerIp = "localhost"; // Define la IP en la cual se podra probar el juego
    int defaultServerPort = 8080; // Define el puerto de red TCP por donde se comunicara el cliente con el servidor
    
    int windowWidth = 1280; // Establece el tamaño maximo del ancho de la ventana principal del juego
    int windowHeight = 720; // Establece el tamaño maximo del alto de la ventana principal del juego
    String assetsPath = "/assets/images/"; // Define la ruta donde se almacenaran las imagenes SVG de los animales y el mapa
}
