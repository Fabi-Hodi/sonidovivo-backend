package cl.duoc.sonidovivo.exception;

/** HTTP 429  demasiados intentos de inicio de sesion. */
public class DemasiadosIntentosException extends RuntimeException {
    public DemasiadosIntentosException(String mensaje) {
        super(mensaje);
    }
}
