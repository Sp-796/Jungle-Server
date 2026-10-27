// Paquete comun de utilidades: contiene la fuente de tiempo inyectable.
package common.util;

/**
 * Fuente de tiempo inyectable.
 *
 * Se usa para poder probar los cooldowns de ataque sin esperar segundos reales:
 * en produccion se usa el reloj del sistema, en las pruebas un reloj falso.
 */
// Interfaz funcional que permite inyectar el tiempo (patron para testabilidad).
@FunctionalInterface
public interface TimeSource {

    // Retorna el tiempo actual en milisegundos.
    long currentTimeMillis();

    // Crea una fuente de tiempo basada en el reloj del sistema.
    static TimeSource system() {
        // Usa el metodo currentTimeMillis de System.
        return System::currentTimeMillis;
    }
}