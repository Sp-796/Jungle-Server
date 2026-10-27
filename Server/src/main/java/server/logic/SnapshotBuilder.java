// Paquete de la logica del servidor: contiene el constructor de payloads JSON.
package server.logic;

// Importa JsonArray para arrays JSON.
import com.google.gson.JsonArray;
// Importa JsonObject para objetos JSON.
import com.google.gson.JsonObject;
// Importa Species para la lista de especies.
import common.model.Species;
// Importa las constantes del juego.
import constants.IConstants;
// Importa ArrayList para listas.
import java.util.ArrayList;
// Importa List para listas.
import java.util.List;
// Importa Animal del modelo.
import server.model.Animal;
// Importa Flag del modelo.
import server.model.Flag;
// Importa Pack del modelo.
import server.model.Pack;
// Importa Player del modelo.
import server.model.Player;
// Importa Zone del modelo.
import server.model.Zone;

/**
 * Construye los payloads JSON que no son una simple instantanea de estado:
 * inicio de partida, respuestas de lobby y resumen final.
 *
 * Tenerlo aparte evita que GameSession se llene de codigo de serializacion.
 */
// Clase que construye payloads JSON para el protocolo.
public class SnapshotBuilder {

    // Respuesta para ambos jugadores cuando el segundo se une.
    public JsonObject matchJoined(GameSession session, Player viewer) {
        // Crea el JSON.
        JsonObject json = new JsonObject();
        // Agrega el codigo de la partida.
        json.addProperty("matchCode", session.getMatchCode());
        // Agrega el id del jugador.
        json.addProperty("playerId", viewer.getParticipantId());
        // Agrega el nombre del jugador.
        json.addProperty("playerName", viewer.getAccountName());
        // Agrega el numero de jugador.
        json.addProperty("playerNumber", session.getPlayers().indexOf(viewer) + 1);

        // Obtiene el oponente.
        Player opponent = session.getOpponent(viewer);
        // Agrega el nombre del oponente.
        json.addProperty("opponentName", opponent == null ? "" : opponent.getAccountName());

        // Agrega las especies disponibles.
        json.add("availableSpecies", speciesArray(availableSpecies(session)));
        // Retorna el JSON.
        return json;
    }

    // Estado de la seleccion de especie para un jugador.
    public JsonObject animalSelection(GameSession session, Player viewer) {
        // Crea el JSON.
        JsonObject json = new JsonObject();
        // Agrega el codigo.
        json.addProperty("matchCode", session.getMatchCode());
        // Agrega la especie propia.
        json.addProperty("yourSpecies", viewer.getSelectedSpecies());
        // Agrega las especies disponibles.
        json.add("availableSpecies", speciesArray(availableSpecies(session)));

        // Obtiene el oponente.
        Player opponent = session.getOpponent(viewer);
        // Agrega la especie del oponente.
        json.addProperty("opponentSpecies",
                opponent == null ? null : opponent.getSelectedSpecies());

        // Retorna el JSON.
        return json;
    }

    // Especies que todavia puede elegir un jugador.
    public List<String> availableSpecies(GameSession session) {
        // Lista con todas las especies.
        List<String> available = new ArrayList<>(Species.allIds());

        // Remueve las ya elegidas.
        for (Player player : session.getPlayers()) {
            if (player.getSelectedSpecies() != null) {
                available.remove(player.getSelectedSpecies());
            }
        }

        // Retorna la lista.
        return available;
    }

    // Payload de GAME_STARTED para un jugador.
    public JsonObject gameStarted(GameSession session, Player viewer) {
        // Crea el JSON.
        JsonObject json = new JsonObject();
        // Agrega el codigo.
        json.addProperty("matchCode", session.getMatchCode());
        // Agrega el id del jugador.
        json.addProperty("playerId", viewer.getParticipantId());
        // Agrega el nombre.
        json.addProperty("playerName", viewer.getAccountName());
        // Agrega la especie.
        json.addProperty("species", viewer.getSelectedSpecies());
        // Agrega el color de la bandera.
        json.addProperty("flagColor", viewer.getFlagColor());

        // Obtiene el oponente.
        Player opponent = session.getOpponent(viewer);
        // Agrega el nombre del oponente.
        json.addProperty("opponentName", opponent == null ? "" : opponent.getAccountName());
        // Agrega la especie del oponente.
        json.addProperty("opponentSpecies",
                opponent == null ? null : opponent.getSelectedSpecies());

        // Zona inicial.
        Zone startZone = viewer.getCurrentZone();
        // Agrega el id de la zona inicial.
        json.addProperty("yourZoneId", startZone == null ? null : startZone.getZoneId());

        // Dimensiones del mundo.
        json.addProperty("worldWidth", IConstants.WORLD_WIDTH);
        json.addProperty("worldHeight", IConstants.WORLD_HEIGHT);
        // Velocidad del animal.
        json.addProperty("animalSpeed", IConstants.ANIMAL_SPEED);
        // Tick rate.
        json.addProperty("tickRateMs", IConstants.TICK_RATE_MS);

        // Zonas del mapa.
        json.add("zones", zonesArray(session));
        // Pack del jugador.
        json.add("pack", packJson(viewer.getPack()));

        // Retorna el JSON.
        return json;
    }

    // Serializa las zonas del mapa.
    private JsonArray zonesArray(GameSession session) {
        // Array de zonas.
        JsonArray zones = new JsonArray();

        // Recorre las zonas.
        for (Zone zone : session.getMapManager().getZones()) {
            // Crea el JSON de la zona.
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

            // Salidas.
            JsonArray exits = new JsonArray();
            // Recorre las vecinas.
            for (Zone neighbour : zone.getAdjacentNodes()) {
                // Agrega el id de la vecina.
                exits.add(neighbour.getZoneId());
            }
            // Agrega las salidas.
            json.add("exits", exits);

            // Agrega la zona al array.
            zones.add(json);
        }

        // Retorna el array.
        return zones;
    }

    // Serializa el pack del jugador.
    private JsonObject packJson(Pack pack) {
        // Crea el JSON.
        JsonObject json = new JsonObject();
        // Si es nulo, retorna vacio.
        if (pack == null) {
            return json;
        }

        // Agrega la especie.
        json.addProperty("species", pack.getSpecies());
        // Agrega la capacidad de dano.
        json.addProperty("damageCapacity", pack.getDamageCapacity());
        // Agrega los vivos.
        json.addProperty("alive", pack.getAliveCount());
        // Agrega el total.
        json.addProperty("total", pack.getAnimals().size());

        // Array de animales.
        JsonArray animals = new JsonArray();
        // Recorre los animales.
        for (Animal animal : pack.getAnimals()) {
            // Crea el JSON del animal.
            JsonObject animalJson = new JsonObject();
            // Agrega el id.
            animalJson.addProperty("id", animal.getId());
            // Agrega la energia.
            animalJson.addProperty("energy", animal.getCurrentEnergy());
            // Agrega x.
            animalJson.addProperty("x", animal.getX());
            // Agrega y.
            animalJson.addProperty("y", animal.getY());
            // Agrega la zona.
            animalJson.addProperty("zoneId", animal.getZoneId());
            // Agrega al array.
            animals.add(animalJson);
        }
        // Agrega los animales.
        json.add("animals", animals);

        // Retorna el JSON.
        return json;
    }

    // Resumen final de la partida.
    public JsonObject gameOver(GameSession session) {
        // Crea el JSON.
        JsonObject json = new JsonObject();
        // Agrega el codigo.
        json.addProperty("matchCode", session.getMatchCode());
        // Agrega la razon.
        json.addProperty("reason", session.getFinishReason());
        // Agrega la duracion.
        json.addProperty("durationSeconds", session.getMatchDurationSeconds());
        // Agrega el id del ganador.
        json.addProperty("winnerId", session.getWinnerId());

        // Busca el ganador.
        Player winner = null;
        for (Player player : session.getPlayers()) {
            if (player.getParticipantId().equals(session.getWinnerId())) {
                winner = player;
            }
        }
        // Agrega el nombre del ganador.
        json.addProperty("winnerName", winner == null ? "" : winner.getAccountName());

        // Array de jugadores.
        JsonArray players = new JsonArray();
        // Recorre los jugadores.
        for (Player player : session.getPlayers()) {
            // Crea el JSON del jugador.
            JsonObject jsonPlayer = new JsonObject();
            // Agrega el id.
            jsonPlayer.addProperty("playerId", player.getParticipantId());
            // Agrega el nombre.
            jsonPlayer.addProperty("name", player.getAccountName());
            // Agrega la especie.
            jsonPlayer.addProperty("species", player.getSelectedSpecies());

            // Si tiene manada, agrega estadisticas.
            if (player.getPack() != null) {
                // Agrega vivos.
                jsonPlayer.addProperty("alive", player.getPack().getAliveCount());
                // Agrega perdidos.
                jsonPlayer.addProperty("lost", player.getPack().getLostCount());
                // Agrega bots eliminados.
                jsonPlayer.addProperty("botsKilled", player.getPack().getBotsKilled());
                // Agrega tazas.
                jsonPlayer.addProperty("coffeeCups", player.getPack().getCoffeeCupsCollected());
                // Agrega capacidad de dano.
                jsonPlayer.addProperty("damageCapacity", player.getPack().getDamageCapacity());
            }

            // Agrega al array.
            players.add(jsonPlayer);
        }
        // Agrega los jugadores.
        json.add("players", players);

        // Retorna el JSON.
        return json;
    }

    // Convierte una lista de especies a JsonArray.
    private JsonArray speciesArray(List<String> species) {
        // Crea el array.
        JsonArray array = new JsonArray();
        // Recorre las especies.
        for (String id : species) {
            // Agrega el id.
            array.add(id);
        }
        // Retorna el array.
        return array;
    }
}