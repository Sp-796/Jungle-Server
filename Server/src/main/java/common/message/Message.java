// Paquete comun de mensajes: contiene el sobre de todos los mensajes del protocolo.
package common.message;

// Importa Gson para serializar y deserializar JSON.
import com.google.gson.Gson;
// Importa JsonArray para manejar listas en el payload.
import com.google.gson.JsonArray;
// Importa JsonElement para leer campos del payload.
import com.google.gson.JsonElement;
// Importa JsonObject para el payload generico.
import com.google.gson.JsonObject;

/**
 * Sobre (envelope) de todos los mensajes del protocolo.
 *
 * Formato en el cable, un objeto JSON por linea:
 *
 *   { "type": "MOVE_ANIMALS", "payload": { "animalIds": [3,4], "targetX": 812, "targetY": 344 } }
 *
 * El payload se maneja como {@link JsonObject} para no tener que crear una
 * clase por cada mensaje y para poder agregar campos sin romper a los clientes
 * viejos.
 */
// Clase que representa un mensaje del protocolo con tipo y payload.
public class Message {

    // Gson compartido para serializar y deserializar (patron: instancia estatica reutilizable).
    private static final Gson GSON = new Gson();

    // Tipo del mensaje (CREATE_MATCH, MOVE_ANIMALS, etc.).
    private String type;
    // Payload generico del mensaje.
    private JsonObject payload;

    // Constructor vacio requerido por Gson.
    public Message() {
        // Inicializa el payload vacio.
        this.payload = new JsonObject();
    }

    // Constructor con solo el tipo de mensaje.
    public Message(String type) {
        // Delega al constructor completo con payload vacio.
        this(type, new JsonObject());
    }

    // Constructor con tipo y payload.
    public Message(String type, JsonObject payload) {
        // Asigna el tipo.
        this.type = type;
        // Asigna el payload, o uno vacio si es nulo.
        this.payload = payload == null ? new JsonObject() : payload;
    }

    // Getter del tipo.
    public String getType() {
        // Retorna el tipo.
        return type;
    }

    // Setter del tipo.
    public void setType(String type) {
        // Actualiza el tipo.
        this.type = type;
    }

    // Getter del payload, crea uno si es nulo.
    public JsonObject getPayload() {
        // Si el payload es nulo, crea uno vacio.
        if (payload == null) {
            payload = new JsonObject();
        }
        // Retorna el payload.
        return payload;
    }

    // Setter del payload.
    public void setPayload(JsonObject payload) {
        // Actualiza el payload.
        this.payload = payload;
    }

    // Agrega un campo al payload y devuelve este mismo mensaje (encadenable).
    public Message put(String key, Object value) {
        // Agrega el valor convertido a JSON.
        getPayload().add(key, GSON.toJsonTree(value));
        // Retorna this para encadenar.
        return this;
    }

    // Lee un String del payload.
    public String getString(String key) {
        // Obtiene el elemento.
        JsonElement element = getPayload().get(key);
        // Si no existe o es nulo, retorna null.
        if (element == null || element.isJsonNull()) {
            return null;
        }
        // Retorna como String.
        return element.getAsString();
    }

    // Lee un int del payload con valor por defecto.
    public int getInt(String key, int defaultValue) {
        // Obtiene el elemento.
        JsonElement element = getPayload().get(key);
        // Si no existe o es nulo, retorna el valor por defecto.
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }
        // Retorna como int.
        return element.getAsInt();
    }

    // Lee un double del payload con valor por defecto.
    public double getDouble(String key, double defaultValue) {
        // Obtiene el elemento.
        JsonElement element = getPayload().get(key);
        // Si no existe o es nulo, retorna el valor por defecto.
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }
        // Retorna como double.
        return element.getAsDouble();
    }

    // Lee un array del payload.
    public JsonArray getArray(String key) {
        // Obtiene el elemento.
        JsonElement element = getPayload().get(key);
        // Si no existe o no es array, retorna array vacio.
        if (element == null || !element.isJsonArray()) {
            return new JsonArray();
        }
        // Retorna como array.
        return element.getAsJsonArray();
    }

    // Serializa el mensaje completo a una linea JSON.
    public String toJson() {
        // Convierte this a JSON.
        return GSON.toJson(this);
    }

    // Reconstruye un mensaje desde una linea JSON.
    public static Message fromJson(String json) {
        // Si es nulo o vacio, lanza excepcion.
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Mensaje vacio");
        }
        // Deserializa con Gson.
        Message message = GSON.fromJson(json, Message.class);
        // Valida que tenga tipo.
        if (message == null || message.getType() == null || message.getType().isBlank()) {
            throw new IllegalArgumentException("El mensaje no tiene campo 'type'");
        }
        // Retorna el mensaje.
        return message;
    }

    // Representacion textual del mensaje.
    @Override
    public String toString() {
        // Retorna "Message{type, payload}".
        return "Message{" + type + ", " + getPayload() + "}";
    }
}