package modelo;

import java.util.Map;

public abstract class CabeceraTransporte extends Cabecera {
    protected CabeceraTransporte(String nombre, Map<String, String> campos) throws PaqueteInvalidoException {
        super(nombre, campos);
    }

    public boolean usaPuertos() {
        return false;
    }

    public int getPuertoOrigen() {
        return -1;
    }

    public int getPuertoDestino() {
        return -1;
    }

    public String getFlags() {
        return null;
    }
}
