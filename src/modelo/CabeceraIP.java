package modelo;

import java.util.Map;

public class CabeceraIP extends Cabecera {
    private static final String OCTETO = "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)";
    private static final String IPV4 = OCTETO + "(\\." + OCTETO + "){3}";

    public CabeceraIP(Map<String, String> campos) throws PaqueteInvalidoException {
        super("IP", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("src", "dst", "ttl", "proto");
        if (!getIpOrigen().matches(IPV4) || !getIpDestino().matches(IPV4)) {
            throw new PaqueteInvalidoException("Cabecera IP: dirección IPv4 inválida ("
                    + getIpOrigen() + " -> " + getIpDestino() + ")");
        }
        entero("ttl", 0, 255);
    }

    public String getIpOrigen()  { return get("src"); }
    public String getIpDestino() { return get("dst"); }
    public int getTtl()          { return Integer.parseInt(get("ttl")); }
    public String getProtocolo() { return get("proto"); }

    @Override
    public String describir() {
        return String.format("  [Internet]      IP origen: %s | IP destino: %s | TTL: %d | Protocolo: %s",
                getIpOrigen(), getIpDestino(), getTtl(), getProtocolo());
    }
}
