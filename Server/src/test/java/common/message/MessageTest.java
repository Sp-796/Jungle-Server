package common.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void serializaYReconstruyeElSobreCompleto() {
        Message original = new Message(MessageType.MOVE_ANIMALS)
                .put("animalIds", List.of(3, 4, 7))
                .put("targetX", 812.5)
                .put("targetY", 344)
                .put("nickname", "Ana");

        Message restored = Message.fromJson(original.toJson());

        assertEquals(MessageType.MOVE_ANIMALS, restored.getType());
        assertEquals(3, restored.getArray("animalIds").size());
        assertEquals(3, restored.getArray("animalIds").get(0).getAsInt());
        assertEquals(7, restored.getArray("animalIds").get(2).getAsInt());
        assertEquals(812.5, restored.getDouble("targetX", 0), 0.0001);
        assertEquals(344, restored.getInt("targetY", 0));
        assertEquals("Ana", restored.getString("nickname"));
    }

    @Test
    void elFormatoEnElCableIncluyeTypeYPayload() {
        String json = new Message(MessageType.ATTACK)
                .put("targetAnimalId", 9)
                .toJson();

        assertTrue(json.contains("\"type\":\"ATTACK\""));
        assertTrue(json.contains("\"payload\""));
        assertTrue(json.contains("\"targetAnimalId\":9"));
    }

    @Test
    void devuelveValoresPorDefectoSiFaltaElCampo() {
        Message message = new Message(MessageType.VIEW_ZONE);

        assertNull(message.getString("zoneId"));
        assertEquals(42, message.getInt("missing", 42));
        assertEquals(1.5, message.getDouble("missing", 1.5), 0.0001);
        assertEquals(0, message.getArray("missing").size());
    }

    @Test
    void rechazaUnMensajeVacio() {
        assertThrows(IllegalArgumentException.class, () -> Message.fromJson(""));
        assertThrows(IllegalArgumentException.class, () -> Message.fromJson("   "));
        assertThrows(IllegalArgumentException.class, () -> Message.fromJson(null));
    }

    @Test
    void rechazaUnMensajeSinTipo() {
        assertThrows(IllegalArgumentException.class,
                () -> Message.fromJson("{\"payload\":{}}"));
    }

    @Test
    void ignoraCamposDesconocidosParaNoRomperLaCompatibilidad() {
        Message message = Message.fromJson(
                "{\"type\":\"VIEW_ZONE\",\"payload\":{\"zoneId\":\"paramo\"},\"extra\":123}");

        assertEquals(MessageType.VIEW_ZONE, message.getType());
        assertEquals("paramo", message.getString("zoneId"));
    }

    @Test
    void elPayloadNuncaEsNulo() {
        Message message = new Message(MessageType.ERROR, null);

        assertEquals(0, message.getPayload().size());
        message.put("message", "algo fallo");
        assertEquals("algo fallo", message.getString("message"));
    }
}
