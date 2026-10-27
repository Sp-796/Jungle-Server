// Paquete del modelo del servidor: contiene los participantes.
package server.model;

/**
 * Participante de una partida: puede ser un jugador humano o un bot.
 *
 * La clase es abstracta y obliga a las hijas a decidir {@link #isBot()}.
 * Toda la logica que necesite distinguir ambos casos usa polimorfismo, no
 * comparaciones de tipo.
 */
// Clase abstracta que sirve de plantilla para Player y Bot (herencia y polimorfismo).
public abstract class Participant {

    // Identificador del participante.
    private final String participantId;
    // Nombre del participante.
    private final String name;
    // Manada del participante.
    private Pack pack;
    // Zona actual del participante.
    private Zone currentZone;
    // Indica si sigue conectado.
    private boolean connected = true;

    // Constructor protegido.
    protected Participant(String participantId, String name) {
        // Asigna el id.
        this.participantId = participantId;
        // Asigna el nombre.
        this.name = name;
    }

    // Metodo abstracto: cada hija decide si es bot.
    public abstract boolean isBot();

    // Getter del id.
    public String getParticipantId() {
        // Retorna participantId.
        return participantId;
    }

    // Getter del nombre.
    public String getName() {
        // Retorna name.
        return name;
    }

    // Getter de la manada.
    public Pack getPack() {
        // Retorna pack.
        return pack;
    }

    // Setter de la manada.
    public void setPack(Pack pack) {
        // Asigna pack.
        this.pack = pack;
    }

    // Getter de la zona actual.
    public Zone getCurrentZone() {
        // Retorna currentZone.
        return currentZone;
    }

    // Setter de la zona actual.
    public void setCurrentZone(Zone currentZone) {
        // Asigna currentZone.
        this.currentZone = currentZone;
    }

    // Indica si esta conectado.
    public boolean isConnected() {
        // Retorna connected.
        return connected;
    }

    // Setter de conexion.
    public void setConnected(boolean connected) {
        // Asigna connected.
        this.connected = connected;
    }
}