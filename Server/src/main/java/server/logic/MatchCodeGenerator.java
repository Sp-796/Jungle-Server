// Paquete de la logica del servidor: contiene el generador de codigos de partida.
package server.logic;

// Importa las constantes del juego.
import constants.IConstants;
// Importa Random para la aleatoriedad.
import java.util.Random;
// Importa Set para los codigos usados.
import java.util.Set;

/**
 * Genera los codigos de 4 caracteres que identifican cada partida.
 *
 * Recibe el {@link Random} por constructor para poder probarlo con una semilla
 * fija.
 */
// Clase que genera codigos de partida (patron: inyeccion de Random para testabilidad).
public class MatchCodeGenerator {

    // Generador aleatorio.
    private final Random random;

    // Constructor que recibe el random.
    public MatchCodeGenerator(Random random) {
        // Asigna el random.
        this.random = random;
    }

    // Genera un codigo de 4 caracteres.
    public String generate() {
        // StringBuilder para construir el codigo.
        StringBuilder code = new StringBuilder(IConstants.MATCH_CODE_LENGTH);
        // Genera cada caracter.
        for (int i = 0; i < IConstants.MATCH_CODE_LENGTH; i++) {
            // Indice aleatorio.
            int index = random.nextInt(IConstants.MATCH_CODE_ALPHABET.length());
            // Agrega el caracter.
            code.append(IConstants.MATCH_CODE_ALPHABET.charAt(index));
        }
        // Retorna el codigo.
        return code.toString();
    }

    // Genera un codigo que no este en uso.
    public String generateUnique(Set<String> usedCodes) {
        // Codigo generado.
        String code;
        // Repite hasta que no este en uso.
        do {
            code = generate();
        } while (usedCodes.contains(code));
        // Retorna el codigo unico.
        return code;
    }
}