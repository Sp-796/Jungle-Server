package server.logic;

/**
 * Error de negocio del servidor.
 *
 * Se lanza cuando una accion del cliente no cumple las reglas (codigo
 * inexistente, especie repetida, partida llena, etc.). El mensaje se envia al
 * cliente dentro de un mensaje ERROR.
 */
public class MatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MatchException(String message) {
        super(message);
    }
}
