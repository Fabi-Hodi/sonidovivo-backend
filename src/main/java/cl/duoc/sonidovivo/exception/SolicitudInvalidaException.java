package cl.duoc.sonidovivo.exception;

/** HTTP 400  datos que cumplen el formato pero no la regla del negocio. */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
