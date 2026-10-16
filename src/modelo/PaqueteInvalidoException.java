package modelo;

public class PaqueteInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;

    public PaqueteInvalidoException(String mensaje) {
        super(mensaje);
    }
}
