// Paquete de red UDP del servidor: contiene el formato de los datagramas.
package server.network.udp;

// Importa las constantes del juego.
import constants.IConstants;
// Importa ByteBuffer para buffers.
import java.nio.ByteBuffer;
// Importa StandardCharsets para UTF-8.
import java.nio.charset.StandardCharsets;

/**
 * Formato de los datagramas del transporte UDP.
 *
 * <pre>
 * +--------+---------+------+---------+---------+------------------+
 * | magic  | version | tipo | seq (4) | ack (4) | payload (JSON)   |
 * | 2 byte | 1 byte  | 1 b  |         |         |                  |
 * +--------+---------+------+---------+---------+------------------+
 * </pre>
 *
 * El payload de los mensajes DATA es exactamente una linea JSON del protocolo
 * del juego. Los mensajes de control (HELLO, ACK, PING...) no llevan payload
 * salvo HELLO, que transporta el id de sesion del cliente.
 */
// Clase final con utilidades de protocolo UDP.
public final class UdpProtocol {

    // Magic number "JA".
    public static final int MAGIC = 0x4A41;

    // Version del protocolo.
    public static final byte VERSION = 1;

    // Tipo DATA.
    public static final byte DATA = 1;
    // Tipo ACK.
    public static final byte ACK = 2;
    // Tipo HELLO.
    public static final byte HELLO = 3;
    // Tipo HELLO_ACK.
    public static final byte HELLO_ACK = 4;
    // Tipo BYE.
    public static final byte BYE = 5;
    // Tipo PING.
    public static final byte PING = 6;
    // Tipo PONG.
    public static final byte PONG = 7;

    // Tamano del encabezado.
    public static final int HEADER_SIZE = 12;

    // Tamano maximo del datagrama.
    public static final int MAX_DATAGRAM_SIZE = IConstants.UDP_MAX_DATAGRAM_SIZE;

    // Datagrama decodificado.
    public record Packet(byte type, int sequence, int acknowledgement, byte[] payload) {

        // Convierte el payload a texto.
        public String payloadAsText() {
            // Retorna el texto UTF-8.
            return new String(payload, StandardCharsets.UTF_8);
        }
    }

    // Constructor privado: clase de utilidades.
    private UdpProtocol() {
        // Clase de utilidades.
    }

    // Codifica un datagrama.
    public static byte[] encode(byte type, int sequence, int acknowledgement, byte[] payload) {
        // Payload o vacio.
        byte[] body = payload == null ? new byte[0] : payload;
        // Buffer del datagrama.
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + body.length);
        // Escribe el magic.
        buffer.putShort((short) MAGIC);
        // Escribe la version.
        buffer.put(VERSION);
        // Escribe el tipo.
        buffer.put(type);
        // Escribe la secuencia.
        buffer.putInt(sequence);
        // Escribe el ACK.
        buffer.putInt(acknowledgement);
        // Escribe el payload.
        buffer.put(body);
        // Retorna el array.
        return buffer.array();
    }

    // Codifica un datagrama con texto.
    public static byte[] encodeText(byte type, int sequence, int acknowledgement, String text) {
        // Convierte el texto a bytes.
        byte[] body = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        // Delega a encode.
        return encode(type, sequence, acknowledgement, body);
    }

    // Decodifica un datagrama.
    public static Packet decode(byte[] data, int length) {
        // Si es nulo o muy corto, retorna null.
        if (data == null || length < HEADER_SIZE) {
            return null;
        }

        // Buffer envuelto.
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        // Lee el magic.
        int magic = buffer.getShort() & 0xFFFF;
        // Lee la version.
        byte version = buffer.get();
        // Lee el tipo.
        byte type = buffer.get();
        // Lee la secuencia.
        int sequence = buffer.getInt();
        // Lee el ACK.
        int acknowledgement = buffer.getInt();

        // Valida magic y version.
        if (magic != MAGIC || version != VERSION) {
            return null;
        }

        // Lee el payload.
        byte[] payload = new byte[length - HEADER_SIZE];
        buffer.get(payload);
        // Retorna el paquete.
        return new Packet(type, sequence, acknowledgement, payload);
    }

    // Codifica un long.
    public static byte[] encodeLong(long value) {
        // Retorna los bytes del long.
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    // Decodifica un long.
    public static long decodeLong(byte[] payload) {
        // Si es nulo o muy corto, retorna 0.
        if (payload == null || payload.length < Long.BYTES) {
            return 0L;
        }
        // Retorna el long.
        return ByteBuffer.wrap(payload).getLong();
    }
}