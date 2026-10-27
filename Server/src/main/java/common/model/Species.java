// Paquete comun del modelo: contiene las 7 especies nativas de Costa Rica.
package common.model;

// Importa ArrayList para construir listas.
import java.util.ArrayList;
// Importa List para retornar listas.
import java.util.List;

/**
 * Las 7 especies nativas que pueden participar en la partida.
 *
 * El id coincide con el nombre del archivo SVG (ver carpeta imagenes/), asi el
 * cliente puede cargar la imagen sin ninguna tabla de traduccion.
 */
// Enumeracion de las 7 especies (polimorfismo por constantes).
public enum Species {

    // Perezoso.
    PEREZOSO("perezoso", "Perezoso"),
    // Mono.
    MONO("mono", "Mono"),
    // Tucan.
    TUCAN("tucan", "Tucan"),
    // Rana roja.
    RANA_ROJA("ranaroja", "Rana Roja"),
    // Jaguar.
    JAGUAR("jaguar", "Jaguar"),
    // Danta.
    DANTA("danta", "Danta"),
    // Boa constrictora.
    BOA_CONSTRICTORA("boaconstructora", "Boa Constrictora");

    // Identificador del archivo/imagen de la especie.
    private final String id;
    // Nombre visible de la especie.
    private final String displayName;

    // Constructor privado del enum.
    Species(String id, String displayName) {
        // Asigna el id.
        this.id = id;
        // Asigna el nombre visible.
        this.displayName = displayName;
    }

    // Getter del id.
    public String getId() {
        // Retorna el id.
        return id;
    }

    // Getter del nombre visible.
    public String getDisplayName() {
        // Retorna el nombre visible.
        return displayName;
    }

    // Busca una especie por su id sin distinguir mayusculas.
    public static Species fromId(String id) {
        // Si el id es nulo, lanza excepcion.
        if (id == null) {
            throw new IllegalArgumentException("La especie no puede ser nula");
        }
        // Recorre todas las especies.
        for (Species species : values()) {
            // Compara sin distinguir mayusculas.
            if (species.id.equalsIgnoreCase(id.trim())) {
                // Retorna la especie encontrada.
                return species;
            }
        }
        // Si no se encontro, lanza excepcion.
        throw new IllegalArgumentException("Especie desconocida: " + id);
    }

    // Indica si un id corresponde a una especie valida.
    public static boolean isValid(String id) {
        // Si el id es nulo, no es valido.
        if (id == null) {
            return false;
        }
        // Recorre todas las especies.
        for (Species species : values()) {
            // Compara sin distinguir mayusculas.
            if (species.id.equalsIgnoreCase(id.trim())) {
                // Es valido.
                return true;
            }
        }
        // No se encontro.
        return false;
    }

    // Retorna la lista de ids de todas las especies.
    public static List<String> allIds() {
        // Crea la lista de ids.
        List<String> ids = new ArrayList<>();
        // Recorre todas las especies.
        for (Species species : values()) {
            // Agrega el id.
            ids.add(species.id);
        }
        // Retorna la lista.
        return ids;
    }
}