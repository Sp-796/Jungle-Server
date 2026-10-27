// Paquete de la logica del servidor: contiene el servicio de visibilidad (niebla de guerra).
package server.logic;

// Importa ArrayList para listas.
import java.util.ArrayList;
// Importa List para listas.
import java.util.List;
// Importa Animal del modelo.
import server.model.Animal;
// Importa GameState del modelo.
import server.model.GameState;
// Importa Player del modelo.
import server.model.Player;
// Importa Zone del modelo.
import server.model.Zone;

/**
 * Aplica la niebla de guerra.
 *
 * Regla del enunciado: el jugador ve UNA zona a la vez y solo puede ver una
 * zona con contenido si tiene al menos un animal vivo dentro. El filtrado se
 * hace aqui, en el servidor; nunca se envia el mapa completo para que el
 * cliente lo esconda.
 */
// Clase que aplica la visibilidad (fog of war).
public class VisibilityService {

    // Construye la instantanea que ese jugador tiene permitido ver.
    public GameState buildState(GameSession session, Player player) {
        // Zonas con animales vivos.
        List<Zone> available = zonesWithLivingAnimals(session, player);

        // Zona vista actualmente.
        Zone viewed = session.getMapManager().getZone(player.getViewedZoneId());

        // Si la zona pedida ya no es valida, se cae a la primera disponible.
        if (viewed == null || !available.contains(viewed)) {
            viewed = available.isEmpty() ? null : available.get(0);
        }
        // Actualiza la zona vista.
        if (viewed != null) {
            player.setViewedZoneId(viewed.getZoneId());
        }

        // Crea el estado.
        GameState state = new GameState(session.getMatchCode(), player.getAccountName());
        // Asigna la duracion.
        state.setMatchDurationSeconds(session.getMatchDurationSeconds());
        // Asigna la zona vista.
        state.setViewedZone(viewed);
        // Asigna las zonas disponibles.
        state.setAvailableZones(available);
        // Asigna la manada propia.
        state.setMyPack(player.getPack());
        // Asigna si termino.
        state.setGameOver(session.isFinished());
        // Asigna si gano.
        state.setPlayerWon(player.getParticipantId().equals(session.getWinnerId()));

        // Obtiene el oponente.
        Player opponent = session.getOpponent(player);
        // Asigna el nombre del oponente.
        state.setOpponentName(opponent == null ? "" : opponent.getAccountName());

        // Si hay zona vista, agrega animales y tazas visibles.
        if (viewed != null) {
            state.setVisibleAnimals(new ArrayList<>(viewed.getEntitiesInside()));
            state.setVisibleCups(new ArrayList<>(viewed.getCupsInside()));
        }

        // Retorna el estado.
        return state;
    }

    // Zonas donde el jugador tiene al menos un animal vivo.
    public List<Zone> zonesWithLivingAnimals(GameSession session, Player player) {
        // Lista de zonas.
        List<Zone> zones = new ArrayList<>();

        // Si no tiene manada, retorna vacio.
        if (player.getPack() == null) {
            return zones;
        }

        // Recorre los animales vivos.
        for (Animal animal : player.getPack().getLivingAnimals()) {
            // Obtiene la zona del animal.
            Zone zone = session.getMapManager().getZone(animal.getZoneId());
            // Si existe y no esta ya, la agrega.
            if (zone != null && !zones.contains(zone)) {
                zones.add(zone);
            }
        }

        // Retorna las zonas.
        return zones;
    }
}