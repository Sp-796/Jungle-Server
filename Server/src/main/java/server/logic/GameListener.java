// Paquete de la logica del servidor: contiene la interfaz de salida de la partida.
package server.logic;

// Importa JsonObject para el payload.
import com.google.gson.JsonObject;

/**
 * Puerto de salida de una partida.
 *
 * GameSession no conoce sockets: solo sabe que alguien quiere recibir mensajes.
 * En produccion lo implementa ClientHandler; en las pruebas, un doble.
 * Esto mantiene separadas la logica del juego y la red.
 */
// Interfaz funcional que define como enviar mensajes de la partida (patron Observer/puerto).
@FunctionalInterface
public interface GameListener {

    // Envia un mensaje del juego.
    void onGameMessage(String type, JsonObject payload);
}