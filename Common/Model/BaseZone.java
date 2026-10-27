package Common.Model; // Define el paquete al que debe pertenecer

public abstract class BaseZone { // Se define como abstract dado que esta funge como plantilla y no puede crear objetos con "new" 

    protected String zoneId; // Define el identificador unico de la zona del mapa en modo protegido para evitar modificaciones
    protected boolean hasFlag; // Determina la existencia de la bandera en un punto dado del mapa en modo protegido para evitar modificaciones

    public BaseZone(String zoneId, boolean hasFlag) { // Constructor que permite que cada zona tenga los datos requeridos

        this.zoneId = zoneId; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase
        this.hasFlag = hasFlag; // Toma el valor que entra desde afuera y lo guarda dentro del atrbiuto de esta clase     

    }

    public String getZoneId() { // Implementa "getter" para obtener el id de la zona del mapa preservando su valor

        return zoneId; // Retorna el valor actual del id de la zona del mapa correspondiente

    }

    public void setZoneId(String zoneId) { // Uso del "setter" para permitir modificaciones del id de la zona en caso de ser necesario

        this.zoneId = zoneId; // Actualiza el valor actual al nuevo valor del id del mapa

    }

    public boolean hasFlag() { // Permite obtener un booleano en caso de poseer una bandera en dicha zona del mapa 

        return hasFlag; // Retorna "true" en caso de haber bandera y "false" caso contrario

    }

    public void setHasFlag(boolean hasFlag) { // Uso del "setter" para modificar o actualizar el estado actual de la bandera en la zona

        this.hasFlag = hasFlag; // Actualiza el estado actual en el que se encuentra la bandera, actualizando su booleano

    }
    
}
