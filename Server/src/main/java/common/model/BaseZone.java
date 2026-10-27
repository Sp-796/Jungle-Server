// Paquete comun del modelo: contiene los datos topologicos minimos de una zona compartidos entre cliente y servidor.
package common.model;

/**
 * Datos topologicos minimos de una zona que comparten cliente y servidor.
 */
// Clase abstracta que sirve de plantilla para las zonas del cliente y del servidor (herencia).
public abstract class BaseZone {

    // Identificador unico de la zona.
    protected final String zoneId;
    // Indica si la zona contiene la bandera.
    protected boolean hasFlag;

    // Constructor protegido usado por las clases hijas para inicializar los datos basicos.
    protected BaseZone(String zoneId, boolean hasFlag) {
        // Asigna el identificador de la zona.
        this.zoneId = zoneId;
        // Asigna si la zona tiene bandera.
        this.hasFlag = hasFlag;
    }

    // Getter del identificador de la zona.
    public String getZoneId() {
        // Retorna el id de la zona.
        return zoneId;
    }

    // Indica si la zona tiene bandera.
    public boolean hasFlag() {
        // Retorna el estado de la bandera.
        return hasFlag;
    }

    // Setter del estado de la bandera.
    public void setHasFlag(boolean hasFlag) {
        // Actualiza el estado de la bandera.
        this.hasFlag = hasFlag;
    }
}