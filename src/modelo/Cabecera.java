package modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Cabecera {
    private final String nombre;
    private final Map<String, String> campos;

    protected Cabecera(String nombre, Map<String, String> campos) throws PaqueteInvalidoException {
        this.nombre = nombre;
        this.campos = new LinkedHashMap<String, String>(campos);
        validar();
    }

    protected abstract void validar() throws PaqueteInvalidoException;

    public abstract String describir();

    public String getNombre() {
        return nombre;
    }

    public String get(String clave) {
        return campos.get(clave);
    }

    protected void exigir(String... claves) throws PaqueteInvalidoException {
        for (String c : claves) {
            String v = campos.get(c);
            if (v == null || v.isEmpty()) {
                throw new PaqueteInvalidoException("Cabecera " + nombre + ": falta el campo '" + c + "'");
            }
        }
    }

    protected int entero(String clave, int min, int max) throws PaqueteInvalidoException {
        try {
            int n = Integer.parseInt(campos.get(clave));
            if (n < min || n > max) {
                throw new PaqueteInvalidoException("Cabecera " + nombre + ": '" + clave + "'=" + n
                        + " fuera de rango [" + min + "-" + max + "]");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new PaqueteInvalidoException("Cabecera " + nombre + ": '" + clave + "' no es numérico ("
                    + campos.get(clave) + ")");
        }
    }

    @Override
    public String toString() {
        return nombre + campos;
    }
}
