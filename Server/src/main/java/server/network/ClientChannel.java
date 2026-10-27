// Paquete de red del servidor: contiene la interfaz de canal con el cliente.
package server.network;

/**
 * Conexion logica con un cliente, independiente del transporte.
 *
 * {@link ClientHandler} solo necesita leer lineas JSON y escribir respuestas;
 * asi la logica del juego no sabe si por debajo hay UDP (o cualquier otro
 * transporte) y se puede probar con un canal en memoria.
 */
// Interfaz que define el canal de comunicacion con un cliente (patron: abstraccion de transporte).
public interface ClientChannel {

    // Espera el siguiente mensaje del cliente.
    String receive();

    // Envia una linea JSON.
    boolean send(String message);

    // Cierra la conexion.
    void close();

    // Direccion remota (solo para logs).
    String remoteAddress();
}