package reporte;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import modelo.Paquete;

public class Estadisticas {
    private final List<Paquete> paquetes;

    public Estadisticas(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public int total() {
        return paquetes.size();
    }

    public int contarTransporte(String tipo) {
        int n = 0;
        for (Paquete p : paquetes) {
            if (p.getTipoTransporte().equals(tipo)) n++;
        }
        return n;
    }

    public Map<String, Integer> porProtocoloApp() {
        Map<String, Integer> m = new TreeMap<String, Integer>();
        for (Paquete p : paquetes) acumular(m, p.getProtocoloAplicacion());
        return m;
    }

    public Map<String, Integer> frecuenciaIpOrigen() {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        for (Paquete p : paquetes) acumular(m, p.getIp().getIpOrigen());
        return m;
    }

    public Map<String, Integer> frecuenciaIpDestino() {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        for (Paquete p : paquetes) acumular(m, p.getIp().getIpDestino());
        return m;
    }

    public Map<String, Integer> frecuenciaPuertoDestino() {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        for (Paquete p : paquetes) {
            if (p.getTransporte().usaPuertos()) acumular(m, String.valueOf(p.getPuertoDestino()));
        }
        return m;
    }

    public Map<String, Integer> conteoFlagsTcp() {
        Map<String, Integer> m = new TreeMap<String, Integer>();
        for (Paquete p : paquetes) {
            String flags = p.getTransporte().getFlags();
            if (flags != null) acumular(m, flags);
        }
        return m;
    }

    public Map<String, int[]> resumenPorServicio() {
        Map<String, int[]> m = new TreeMap<String, int[]>();
        for (Paquete p : paquetes) {
            int[] fila = m.get(p.getProtocoloAplicacion());
            if (fila == null) {
                fila = new int[3];
                m.put(p.getProtocoloAplicacion(), fila);
            }
            fila[0]++;
            if (p.getAplicacion().esConsulta()) fila[1]++;
            if (p.getAplicacion().esRespuesta()) fila[2]++;
        }
        return m;
    }

    public List<String> masFrecuentes(Map<String, Integer> m) {
        int max = 0;
        for (int v : m.values()) {
            if (v > max) max = v;
        }
        List<String> r = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : m.entrySet()) {
            if (e.getValue() == max) r.add(e.getKey());
        }
        return r;
    }

    public List<Paquete> buscarPorIp(String ip) {
        List<Paquete> r = new ArrayList<Paquete>();
        for (Paquete p : paquetes) {
            if (p.getIp().getIpOrigen().equals(ip) || p.getIp().getIpDestino().equals(ip)) r.add(p);
        }
        return r;
    }

    public List<Paquete> filtrarPorTransporte(String tipo) {
        List<Paquete> r = new ArrayList<Paquete>();
        for (Paquete p : paquetes) {
            if (p.getTipoTransporte().equalsIgnoreCase(tipo)) r.add(p);
        }
        return r;
    }

    public List<Paquete> filtrarPorApp(String protocolo) {
        List<Paquete> r = new ArrayList<Paquete>();
        for (Paquete p : paquetes) {
            if (p.getProtocoloAplicacion().equalsIgnoreCase(protocolo)) r.add(p);
        }
        return r;
    }

    public Paquete buscarPorLinea(int numeroLinea) {
        for (Paquete p : paquetes) {
            if (p.getNumeroLinea() == numeroLinea) return p;
        }
        return null;
    }

    private void acumular(Map<String, Integer> m, String clave) {
        Integer actual = m.get(clave);
        m.put(clave, actual == null ? 1 : actual + 1);
    }
}
