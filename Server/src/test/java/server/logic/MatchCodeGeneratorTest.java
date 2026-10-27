package server.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constants.IConstants;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MatchCodeGeneratorTest {

    @Test
    void generaCodigosDeCuatroCaracteresDelAlfabetoPermitido() {
        MatchCodeGenerator generator = new MatchCodeGenerator(new Random(7));

        for (int i = 0; i < 50; i++) {
            String code = generator.generate();

            assertEquals(IConstants.MATCH_CODE_LENGTH, code.length());
            for (char character : code.toCharArray()) {
                assertTrue(
                        IConstants.MATCH_CODE_ALPHABET.indexOf(character) >= 0,
                        "Caracter fuera del alfabeto: " + character
                );
            }
        }
    }

    @Test
    void conLaMismaSemillaGeneraLaMismaSecuencia() {
        MatchCodeGenerator first = new MatchCodeGenerator(new Random(123));
        MatchCodeGenerator second = new MatchCodeGenerator(new Random(123));

        assertEquals(first.generate(), second.generate());
        assertEquals(first.generate(), second.generate());
    }

    @Test
    void evitaUnCodigoQueYaEstaEnUso() {
        MatchCodeGenerator generator = new MatchCodeGenerator(new Random(99));
        String used = generator.generate();

        String next = generator.generateUnique(Set.of(used));

        assertNotEquals(used, next);
    }

    @Test
    void puedeGenerarMuchosCodigosSinRepetirseEnLaPractica() {
        MatchCodeGenerator generator = new MatchCodeGenerator(new Random(2026));
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 200; i++) {
            codes.add(generator.generateUnique(codes));
        }

        assertEquals(200, codes.size());
    }
}
