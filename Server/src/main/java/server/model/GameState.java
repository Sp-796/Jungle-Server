// Paquete del modelo del servidor: contiene el snapshot de estado de la partida.
package server.model;

// Importa JsonArray para serializar listas.
import com.google.gson.JsonArray;
// Importa JsonObject para serializar.
import com.google.gson.JsonObject;
// Importa ArrayList para listas internas.
import java.util.ArrayList;
// Importa List para listas.
import java.util.List;

/**
 * Instantanea (snapshot) personalizada para un jugador.
 *
 * Es la respuesta a STATE_UPDATE. El servidor la construye con
 * {@link server.logic.VisibilityService}, que solo agrega lo que ese jugador
 * tiene permitido ver. El cliente no recibe el mapa completo.
 */
// Clase que representa el estado de la partida para un jugador.
public class GameState {

    // Codigo de la partida.
    private final String matchCode;
    // Nombre del jugador.
    private final String playerName;

    // Duracion de la partida en segundos.
    private int matchDurationSeconds;
    // Nombre del oponente.
    private String opponentName = "";

    // Zona que se esta mirando.
    private Zone viewedZone;
    // Zonas disponibles para ver.
    private List<Zone> availableZones = new ArrayList<>();
    // Animales visibles.
    private List<Animal> visibleAnimals = new ArrayList<>();
    // Tazas visibles.
    private List<CoffeeCup> visibleCups = new ArrayList<>();
    // Manada propia.
    private Pack myPack;

    // Indica si la partida termino.
    private boolean gameOver;
    // Indica si el jugador gano.
    private boolean playerWon;

    // Constructor del estado.
    public GameState(String matchCode, String playerName) {
        // Asigna el codigo.
        this.matchCode = matchCode;
        // Asigna el nombre del jugador.
        this.playerName = playerName;
    }

    // Serializa el estado a JSON.
    public JsonObject toJson() {
        // Crea el objeto JSON.
        JsonObject json = new JsonObject();
        // Agrega el codigo.
        json.addProperty("matchCode", matchCode);
        // Agrega el nombre del jugador.
        json.addProperty("playerName", playerName);
        // Agrega la duracion.
        json.addProperty("matchDuration", matchDurationSeconds);
        // Agrega el nombre del oponente.
        json.addProperty("opponentName", opponentName);
        // Agrega si termino.
        json.addProperty("gameOver", gameOver);
        // Agrega si gano.
        json.addProperty("playerWon", playerWon);

        // Zonas a las que el jugador puede cambiar la vista.
        JsonArray zones = new JsonArray();
        // Recorre las zonas disponibles.
        for (Zone zone : availableZones) {
            // Agrega el id de la zona.
            zones.add(zone.getZoneId());
        }
        // Agrega al JSON.
        json.add("availableZones", zones);

        // Contenido de la zona que se esta mirando.
        if (viewedZone != null) {
            // Agrega la zona vista.
            json.add("viewedZone", zoneJson(viewedZone));
        }

        // Animales visibles.
        JsonArray animals = new JsonArray();
        // Recorre los animales visibles.
        for (Animal animal : visibleAnimals) {
            // Agrega el JSON del animal.
            animals.add(animalJson(animal));
        }
        // Agrega al JSON.
        json.add("animals", animals);

        // Tazas visibles.
        JsonArray cups = new JsonArray();
        // Recorre las tazas visibles.
        for (CoffeeCup cup : visibleCups) {
            // Crea el JSON de la taza.
            JsonObject cupJson = new JsonObject();
            // Agrega el id.
            cupJson.addProperty("id", cup.getCupId());
            // Agrega x.
            cupJson.addProperty("x", cup.getX());
            // Agrega y.
            cupJson.addProperty("y", cup.getY());
            // Agrega la taza al array.
            cups.add(cupJson);
        }
        // Agrega al JSON.
        json.add("coffeeCups", cups);

        // Manada propia.
        if (myPack != null) {
            // Agrega el resumen de la manada.
            json.add("myPack", packSummaryJson(myPack));

            // Animales propios.
            JsonArray mine = new JsonArray();
            // Recorre los animales de la manada.
            for (Animal animal : myPack.getAnimals()) {
                // Agrega el JSON del animal.
                mine.add(animalJson(animal));
            }
            // Agrega al JSON.
            json.add("myAnimals", mine);
        }

        // Retorna el JSON.
        return json;
    }

    // Serializa una zona a JSON.
    private JsonObject zoneJson(Zone zone) {
        // Crea el objeto JSON.
        JsonObject json = new JsonObject();
        // Agrega el id.
        json.addProperty("id", zone.getZoneId());
        // Agrega el nombre.
        json.addProperty("name", zone.getDisplayName());
        // Agrega la imagen.
        json.addProperty("image", zone.getImageName());
        // Agrega x.
        json.addProperty("x", zone.getX());
        // Agrega y.
        json.addProperty("y", zone.getY());
        // Agrega ancho.
        json.addProperty("width", zone.getWidth());
        // Agrega alto.
        json.addProperty("height", zone.getHeight());
        // Agrega si tiene bandera.
        json.addProperty("hasFlag", zone.hasFlag());

        // Bandera de la zona.
        Flag flag = zone.getFlag();
        // Si hay bandera, la agrega.
        if (flag != null) {
            // Agrega el dueno.
            json.addProperty("flagOwnerId", flag.getOwnerId());
            // Agrega el color.
            json.addProperty("flagColor", flag.getColor());
            // Agrega x.
            json.addProperty("flagX", flag.getX());
            // Agrega y.
            json.addProperty("flagY", flag.getY());
        }

        // Salidas de la zona.
        JsonArray exits = new JsonArray();
        // Recorre las zonas vecinas.
        for (Zone neighbour : zone.getAdjacentNodes()) {
            // Agrega el id de la vecina.
            exits.add(neighbour.getZoneId());
        }
        // Agrega las salidas.
        json.add("exits", exits);

        // Retorna el JSON.
        return json;
    }

    // Serializa un animal a JSON.
    private JsonObject animalJson(Animal animal) {
        // Crea el objeto JSON.
        JsonObject json = new JsonObject();
        // Agrega el id.
        json.addProperty("id", animal.getId());
        // Agrega la especie.
        json.addProperty("species", animal.getSpecies());
        // Agrega la energia.
        json.addProperty("energy", animal.getCurrentEnergy());
        // Agrega x.
        json.addProperty("x", animal.getX());
        // Agrega y.
        json.addProperty("y", animal.getY());
        // Agrega si esta vivo.
        json.addProperty("alive", !animal.isDefeated());
        // Agrega la zona.
        json.addProperty("zoneId", animal.getZoneId());
        // Agrega el dueno.
        json.addProperty("owner", ownerOf(animal));

        // El destino permite que el cliente interpole sin pedir mas mensajes.
        // Agrega targetX.
        json.addProperty("targetX", animal.getTargetX());
        // Agrega targetY.
        json.addProperty("targetY", animal.getTargetY());
        // Agrega si se mueve.
        json.addProperty("moving", animal.isMoving());

        // Retorna el JSON.
        return json;
    }

    // Determina el dueno de un animal.
    private String ownerOf(Animal animal) {
        // Si es de la manada propia.
        if (myPack != null && animal.getPack() == myPack) {
            // Es propio.
            return "ME";
        }
        // Si es de una manada bot.
        if (animal.getPack() != null && animal.getPack().isBotPack()) {
            // Es bot.
            return "BOT";
        }
        // Es del oponente.
        return "OPPONENT";
    }

    // Serializa el resumen de la manada.
    private JsonObject packSummaryJson(Pack pack) {
        // Crea el objeto JSON.
        JsonObject json = new JsonObject();
        // Agrega la especie.
        json.addProperty("species", pack.getSpecies());
        // Agrega vivos.
        json.addProperty("alive", pack.getAliveCount());
        // Agrega perdidos.
        json.addProperty("lost", pack.getLostCount());
        // Agrega total.
        json.addProperty("total", pack.getAnimals().size());
        // Agrega bots eliminados.
        json.addProperty("botsKilled", pack.getBotsKilled());
        // Agrega tazas consumidas.
        json.addProperty("coffeeCups", pack.getCoffeeCupsCollected());
        // Agrega capacidad de dano.
        json.addProperty("damageCapacity", pack.getDamageCapacity());
        // Retorna el JSON.
        return json;
    }

    // Setter de la duracion.
    public void setMatchDurationSeconds(int matchDurationSeconds) {
        // Asigna la duracion.
        this.matchDurationSeconds = matchDurationSeconds;
    }

    // Setter del nombre del oponente.
    public void setOpponentName(String opponentName) {
        // Asigna el nombre o vacio si es nulo.
        this.opponentName = opponentName == null ? "" : opponentName;
    }

    // Setter de la zona vista.
    public void setViewedZone(Zone viewedZone) {
        // Asigna la zona.
        this.viewedZone = viewedZone;
    }

    // Setter de las zonas disponibles.
    public void setAvailableZones(List<Zone> availableZones) {
        // Asigna la lista o una vacia si es nula.
        this.availableZones = availableZones == null ? new ArrayList<>() : availableZones;
    }

    // Setter de los animales visibles.
    public void setVisibleAnimals(List<Animal> visibleAnimals) {
        // Asigna la lista o una vacia si es nula.
        this.visibleAnimals = visibleAnimals == null ? new ArrayList<>() : visibleAnimals;
    }

    // Setter de las tazas visibles.
    public void setVisibleCups(List<CoffeeCup> visibleCups) {
        // Asigna la lista o una vacia si es nula.
        this.visibleCups = visibleCups == null ? new ArrayList<>() : visibleCups;
    }

    // Setter de la manada propia.
    public void setMyPack(Pack myPack) {
        // Asigna la manada.
        this.myPack = myPack;
    }

    // Setter de si la partida termino.
    public void setGameOver(boolean gameOver) {
        // Asigna el valor.
        this.gameOver = gameOver;
    }

    // Setter de si el jugador gano.
    public void setPlayerWon(boolean playerWon) {
        // Asigna el valor.
        this.playerWon = playerWon;
    }
}