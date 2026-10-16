package modelo;

import java.util.Map;

public class CabeceraEthernet extends Cabecera {
    private static final String MAC = "([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}";

    public CabeceraEthernet(Map<String, String> campos) throws PaqueteInvalidoException {
        super("ETH", campos);
    }

    @Override
    protected void validar() throws PaqueteInvalidoException {
        exigir("src", "dst", "type");
        if (!getMacOrigen().matches(MAC) || !getMacDestino().matches(MAC)) {
            throw new PaqueteInvalidoException("Cabecera ETH: dirección MAC con formato inválido");
        }
    }

    public String getMacOrigen()  { return get("src"); }
    public String getMacDestino() { return get("dst"); }
    public String getTipoTrama()  { return get("type"); }

    @Override
    public String describir() {
        return String.format("  [Acceso a red]  MAC origen: %s | MAC destino: %s | Tipo de trama: %s",
                getMacOrigen(), getMacDestino(), getTipoTrama());
    }
}
