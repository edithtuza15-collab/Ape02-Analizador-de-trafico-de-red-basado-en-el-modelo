package modelo;

import java.util.Map;

public class CabeceraAPP extends Cabecera {
    public CabeceraAPP(Map<String, String> campos) throws PaqueteInvalidoException {
        super("APP", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("proto", "data");
    }

    public String getProtocolo() { return get("proto"); }
    public String getDatos()     { return get("data"); }

    public boolean esConsulta() {
        String p = getProtocolo();
        String d = getDatos();
        if (p.equals("DNS"))  return d.startsWith("QUERY_");
        if (p.equals("SNMP")) return d.startsWith("GET_");
        if (p.equals("PING")) return d.equals("ECHO_REQUEST");
        if (p.equals("DHCP")) return d.equals("DISCOVER");
        return false;
    }

    public boolean esRespuesta() {
        String p = getProtocolo();
        String d = getDatos();
        if (p.equals("DNS"))  return d.startsWith("RESPONSE_");
        if (p.equals("SNMP")) return d.startsWith("RESPONSE_");
        if (p.equals("PING")) return d.equals("ECHO_REPLY");
        if (p.equals("DHCP")) return d.startsWith("OFFER_");
        return false;
    }

    @Override
    public String describir() {
        return String.format("  [Aplicación]    Protocolo: %s | Datos: %s", getProtocolo(), getDatos());
    }
}
