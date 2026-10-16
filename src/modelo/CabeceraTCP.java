package modelo;

import java.util.Map;

public class CabeceraTCP extends CabeceraTransporte {
    public CabeceraTCP(Map<String, String> campos) throws PaqueteInvalidoException {
        super("TCP", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("sport", "dport", "seq", "ack", "flags");
        entero("sport", 0, 65535);
        entero("dport", 0, 65535);
    }

    @Override
    public boolean usaPuertos()   { return true; }

    @Override
    public int getPuertoOrigen()  { return Integer.parseInt(get("sport")); }

    @Override
    public int getPuertoDestino() { return Integer.parseInt(get("dport")); }

    @Override
    public String getFlags()      { return get("flags"); }

    @Override
    public String describir() {
        return String.format("  [Transporte]    TCP | puerto origen: %d | puerto destino: %d | seq: %s | ack: %s | flags: %s",
                getPuertoOrigen(), getPuertoDestino(), get("seq"), get("ack"), getFlags());
    }
}
