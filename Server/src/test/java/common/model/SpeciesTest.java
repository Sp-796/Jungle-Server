package common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpeciesTest {

    @Test
    void existenLasSieteEspeciesDelEnunciado() {
        assertEquals(7, Species.values().length);
        assertEquals(7, Species.allIds().size());
    }

    @Test
    void losIdentificadoresSonUnicos() {
        Set<String> ids = new HashSet<>(Species.allIds());

        assertEquals(7, ids.size());
        assertTrue(ids.contains("jaguar"));
        assertTrue(ids.contains("boaconstructora"));
    }

    @Test
    void buscaPorIdSinDistinguirMayusculasNiEspacios() {
        assertEquals(Species.JAGUAR, Species.fromId("jaguar"));
        assertEquals(Species.JAGUAR, Species.fromId("  JaGuAr "));
        assertEquals(Species.BOA_CONSTRICTORA, Species.fromId("BOACONSTRUCTORA"));
    }

    @Test
    void validaIdentificadores() {
        assertTrue(Species.isValid("mono"));
        assertFalse(Species.isValid("dragon"));
        assertFalse(Species.isValid(null));
    }

    @Test
    void rechazaEspeciesDesconocidas() {
        assertThrows(IllegalArgumentException.class, () -> Species.fromId("dragon"));
        assertThrows(IllegalArgumentException.class, () -> Species.fromId(null));
    }

    @Test
    void cadaEspecieTieneNombreParaMostrar() {
        List<Species> species = List.of(Species.values());

        for (Species each : species) {
            assertFalse(each.getDisplayName().isBlank());
            assertFalse(each.getId().isBlank());
        }
    }
}
