package analisis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelo.CabeceraAPP;
import modelo.CabeceraEthernet;
import modelo.CabeceraICMP;
import modelo.CabeceraIP;
import modelo.CabeceraTCP;
import modelo.CabeceraTransporte;
import modelo.CabeceraUDP;
import modelo.Paquete;
import modelo.PaqueteInvalidoException;

public class AnalizadorPaquetes {
    private final List<Paquete> paquetes = new ArrayList<Paquete>();
    private final List<String> errores = new ArrayList<String>();
    private final List<Integer> lineasVacias = new ArrayList<Integer>();
    private int lineasLeidas = 0;

    public void analizar(List<String> lineas) {
        lineasLeidas = lineas.size();
        for (int i = 0; i < lineas.size(); i++) {
            int numero = i + 1;
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) {
                lineasVacias.add(numero);
                continue;
            }
            try {
                paquetes.add(analizarLinea(numero, linea));
            } catch (PaqueteInvalidoException e) {
                errores.add("Línea " + numero + ": " + e.getMessage());
            }
        }
    }

    public Paquete analizarLinea(int numero, String linea) throws PaqueteInvalidoException {
        String[] bloques = linea.split("\\|", -1);
        if (bloques.length != 4) {
            throw new PaqueteInvalidoException("se esperaban 4 bloques (capas) y hay " + bloques.length);
        }

        Map<String, String> eth = campos(bloques[0], "ETH");
        Map<String, String> ip = campos(bloques[1], "IP");
        String nombreTransporte = nombre(bloques[2]);
        Map<String, String> transp = campos(bloques[2], nombreTransporte);
        Map<String, String> app = campos(bloques[3], "APP");

        CabeceraEthernet cEth = new CabeceraEthernet(eth);
        CabeceraIP cIp = new CabeceraIP(ip);
        CabeceraTransporte cTransp = crearTransporte(nombreTransporte, transp);
        CabeceraAPP cApp = new CabeceraAPP(app);

        if (!cIp.getProtocolo().equals(nombreTransporte)) {
            throw new PaqueteInvalidoException("IP indica proto=" + cIp.getProtocolo()
                    + " pero la capa de transporte es " + nombreTransporte);
        }
        return new Paquete(numero, cEth, cIp, cTransp, cApp);
    }

    private CabeceraTransporte crearTransporte(String nombre, Map<String, String> campos)
            throws PaqueteInvalidoException {
        if (nombre.equals("TCP")) return new CabeceraTCP(campos);
        if (nombre.equals("UDP")) return new CabeceraUDP(campos);
        if (nombre.equals("ICMP")) return new CabeceraICMP(campos);
        throw new PaqueteInvalidoException("cabecera de transporte desconocida: " + nombre);
    }

    private String nombre(String bloque) throws PaqueteInvalidoException {
        int llave = bloque.indexOf('{');
        if (llave <= 0 || !bloque.endsWith("}")) {
            throw new PaqueteInvalidoException("bloque mal formado: '" + bloque + "'");
        }
        return bloque.substring(0, llave).trim();
    }

    private Map<String, String> campos(String bloque, String esperado) throws PaqueteInvalidoException {
        String nombre = nombre(bloque);
        if (!nombre.equals(esperado)) {
            throw new PaqueteInvalidoException("se esperaba la cabecera " + esperado + " y llegó " + nombre);
        }
        String interior = bloque.substring(bloque.indexOf('{') + 1, bloque.length() - 1);
        Map<String, String> mapa = new LinkedHashMap<String, String>();
        for (String par : interior.split(",", -1)) {
            int igual = par.indexOf('=');
            if (igual <= 0) {
                throw new PaqueteInvalidoException("par clave=valor inválido en " + nombre + ": '" + par + "'");
            }
            mapa.put(par.substring(0, igual).trim(), par.substring(igual + 1).trim());
        }
        return mapa;
    }

    public List<Paquete> getPaquetes()      { return paquetes; }
    public List<String> getErrores()        { return errores; }
    public List<Integer> getLineasVacias()  { return lineasVacias; }
    public int getLineasLeidas()            { return lineasLeidas; }
}
