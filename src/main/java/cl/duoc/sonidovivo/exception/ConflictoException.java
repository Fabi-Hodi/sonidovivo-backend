package cl.duoc.sonidovivo.exception;

/** HTTP 409  conflicto con el estado de los datos o una regla del negocio. */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
