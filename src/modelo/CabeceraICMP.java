package modelo;

import java.util.Map;

public class CabeceraICMP extends CabeceraTransporte {
    public CabeceraICMP(Map<String, String> campos) throws PaqueteInvalidoException {
        super("ICMP", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("type", "code", "id", "seq");
        entero("type", 0, 255);
        entero("code", 0, 255);
    }

    public int getTipo() { return Integer.parseInt(get("type")); }

    public String getNombreTipo() {
        switch (getTipo()) {
            case 8:  return "Echo Request";
            case 0:  return "Echo Reply";
            default: return "Otro";
        }
    }

    @Override
    public String describir() {
        return String.format("  [Transporte]    ICMP | tipo: %d (%s) | código: %s | id: %s | seq: %s",
                getTipo(), getNombreTipo(), get("code"), get("id"), get("seq"));
    }
}
