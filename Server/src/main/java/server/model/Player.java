// Paquete del modelo del servidor: contiene los jugadores humanos.
package server.model;

// Importa Species del paquete comun.
import common.model.Species;
// Importa las constantes del juego.
import constants.IConstants;

/**
 * Jugador humano conectado a una partida.
 */
// Clase Player que extiende Participant (herencia) y representa un jugador humano.
public class Player extends Participant {

    // Especie seleccionada por el jugador.
    private String selectedSpecies;
    // Id de la zona que esta mirando.
    private String viewedZoneId;

    // Color de la bandera propia.
    private String flagColor;

    // Constructor del jugador.
    public Player(String participantId, String accountName) {
        // Llama al constructor de Participant.
        super(participantId, accountName);
    }

    // Indica que no es bot.
    @Override
    public boolean isBot() {
        // Retorna false.
        return false;
    }

    // Registra la especie elegida.
    public boolean selectSpecies(String species) {
        // Si la especie no es valida o ya eligio, retorna false.
        if (!Species.isValid(species) || selectedSpecies != null) {
            return false;
        }
        // Guarda el id de la especie.
        this.selectedSpecies = Species.fromId(species).getId();
        // Retorna true.
        return true;
    }

    // Alias que conserva el nombre pedido en la especificacion.
    public String getAccountName() {
        // Retorna el nombre del participante.
        return getName();
    }

    // Getter de la especie seleccionada.
    public String getSelectedSpecies() {
        // Retorna selectedSpecies.
        return selectedSpecies;
    }

    // Indica si ya selecciono especie.
    public boolean hasSelectedSpecies() {
        // Retorna true si no es nula.
        return selectedSpecies != null;
    }

    // Getter de la zona vista.
    public String getViewedZoneId() {
        // Retorna viewedZoneId.
        return viewedZoneId;
    }

    // Setter de la zona vista.
    public void setViewedZoneId(String viewedZoneId) {
        // Asigna viewedZoneId.
        this.viewedZoneId = viewedZoneId;
    }

    // Getter del color de la bandera.
    public String getFlagColor() {
        // Retorna flagColor.
        return flagColor;
    }

    // Setter del color de la bandera.
    public void setFlagColor(String flagColor) {
        // Asigna flagColor.
        this.flagColor = flagColor;
    }

    // Valida que el nickname tenga un largo aceptable.
    public static boolean isValidNickname(String nickname) {
        // Retorna true si no es nulo, no esta en blanco y no supera el maximo.
        return nickname != null
                && !nickname.isBlank()
                && nickname.trim().length() <= IConstants.MAX_NICKNAME_LENGTH;
    }
}