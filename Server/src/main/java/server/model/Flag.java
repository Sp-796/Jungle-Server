// Paquete del modelo del servidor: contiene las banderas.
package server.model;

/**
 * Bandera de un jugador.
 *
 * Esta en la zona inicial de su dueno. El jugador rival gana la partida cuando
 * uno de sus animales entra en el radio de la bandera.
 */
// Clase que representa la bandera de un jugador.
public class Flag {

    // Identificador de la bandera.
    private final String flagId;
    // Id del dueno.
    private final String ownerId;
    // Color de la bandera.
    private final String color;
    // Id de la zona donde esta.
    private final String zoneId;
    // Coordenada x.
    private final double x;
    // Coordenada y.
    private final double y;

    // Constructor de la bandera.
    public Flag(String flagId, String ownerId, String color, String zoneId, double x, double y) {
        // Asigna el id.
        this.flagId = flagId;
        // Asigna el dueno.
        this.ownerId = ownerId;
        // Asigna el color.
        this.color = color;
        // Asigna la zona.
        this.zoneId = zoneId;
        // Asigna x.
        this.x = x;
        // Asigna y.
        this.y = y;
    }

    // Getter del id.
    public String getFlagId() {
        // Retorna flagId.
        return flagId;
    }

    // Getter del dueno.
    public String getOwnerId() {
        // Retorna ownerId.
        return ownerId;
    }

    // Getter del color.
    public String getColor() {
        // Retorna color.
        return color;
    }

    // Getter de la zona.
    public String getZoneId() {
        // Retorna zoneId.
        return zoneId;
    }

    // Getter de x.
    public double getX() {
        // Retorna x.
        return x;
    }

    // Getter de y.
    public double getY() {
        // Retorna y.
        return y;
    }
}