package server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import constants.IConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerMainTest {

    private String previousProperty;

    @BeforeEach
    void setUp() {
        previousProperty = System.getProperty("server.port");
        System.clearProperty("server.port");
    }

    @AfterEach
    void tearDown() {
        if (previousProperty == null) {
            System.clearProperty("server.port");
        } else {
            System.setProperty("server.port", previousProperty);
        }
    }

    @Test
    void usaElPrimerArgumentoSiEsUnNumero() {
        assertEquals(9090, ServerMain.resolvePort(new String[]{"9090"}));
    }

    @Test
    void usaLaPropiedadSiNoHayArgumentos() {
        System.setProperty("server.port", "7070");

        assertEquals(7070, ServerMain.resolvePort(new String[0]));
    }

    @Test
    void elArgumentoGanaSobreLaPropiedad() {
        System.setProperty("server.port", "7070");

        assertEquals(9191, ServerMain.resolvePort(new String[]{"9191"}));
    }

    @Test
    void caeAlValorPorDefectoSiNoHayNadaConfigurado() {
        assertEquals(IConstants.DEFAULT_SERVER_PORT, ServerMain.resolvePort(null));
        assertEquals(IConstants.DEFAULT_SERVER_PORT, ServerMain.resolvePort(new String[0]));
    }

    @Test
    void ignoraValoresInvalidosYUsaElSiguienteCandidato() {
        assertEquals(IConstants.DEFAULT_SERVER_PORT,
                ServerMain.resolvePort(new String[]{"no-es-un-puerto"}));

        System.setProperty("server.port", "tampoco");

        assertEquals(IConstants.DEFAULT_SERVER_PORT,
                ServerMain.resolvePort(new String[]{"abc"}));
    }

    @Test
    void usaLaPropiedadValidaAunqueElArgumentoSeaInvalido() {
        System.setProperty("server.port", "6060");

        assertEquals(6060, ServerMain.resolvePort(new String[]{"nope"}));
    }
}
