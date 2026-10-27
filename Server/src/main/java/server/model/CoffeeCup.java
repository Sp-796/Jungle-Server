// Paquete del modelo del servidor: contiene las tazas de cafe.
package server.model;

/**
 * Taza de cafe colocada en una zona.
 *
 * La consume el primer animal de un jugador que la toque. Una vez consumida
 * desaparece del mapa y no puede volver a usarse.
 */
// Clase que representa una taza de cafe.
public class CoffeeCup {

    // Identificador de la taza.
    private final String cupId;
    // Id de la zona donde esta la taza.
    private final String zoneId;
    // Coordenada x de la taza.
    private final double x;
    // Coordenada y de la taza.
    private final double y;

    // Indica si ya fue consumida.
    private boolean consumed;

    // Constructor de la taza.
    public CoffeeCup(String cupId, String zoneId, double x, double y) {
        // Asigna el id.
        this.cupId = cupId;
        // Asigna la zona.
        this.zoneId = zoneId;
        // Asigna x.
        this.x = x;
        // Asigna y.
        this.y = y;
    }

    // Marca la taza como consumida.
    public void consume() {
        // Pone consumed en true.
        this.consumed = true;
    }

    // Getter del id.
    public String getCupId() {
        // Retorna cupId.
        return cupId;
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

    // Indica si esta consumida.
    public boolean isConsumed() {
        // Retorna consumed.
        return consumed;
    }
}