package modelo;

import java.util.Map;

public class CabeceraUDP extends CabeceraTransporte {
    public CabeceraUDP(Map<String, String> campos) throws PaqueteInvalidoException {
        super("UDP", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("sport", "dport", "length");
        entero("sport", 0, 65535);
        entero("dport", 0, 65535);
        entero("length", 8, 65535);
    }

    @Override
    public boolean usaPuertos()   { return true; }

    @Override
    public int getPuertoOrigen()  { return Integer.parseInt(get("sport")); }

    @Override
    public int getPuertoDestino() { return Integer.parseInt(get("dport")); }

    @Override
    public String describir() {
        return String.format("  [Transporte]    UDP | puerto origen: %d | puerto destino: %d | longitud: %s",
                getPuertoOrigen(), getPuertoDestino(), get("length"));
    }
}
