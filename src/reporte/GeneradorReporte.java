package reporte;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import analisis.AnalizadorPaquetes;
import modelo.Paquete;

public class GeneradorReporte {
    private final Estadisticas stats;

    public GeneradorReporte(Estadisticas stats) {
        this.stats = stats;
    }

    public String detalle(List<Paquete> lista) {
        StringBuilder sb = new StringBuilder();
        for (Paquete p : lista) {
            sb.append(p.describir()).append("\n\n");
        }
        return sb.toString();
    }

    public String encabezado(AnalizadorPaquetes an) {
        return "Líneas leídas: " + an.getLineasLeidas()
                + " | Paquetes válidos: " + an.getPaquetes().size()
                + " | Líneas mal formadas: " + an.getErrores().size()
                + " | Líneas vacías: " + an.getLineasVacias().size() + "\n";
    }

    public String incidencias(AnalizadorPaquetes an) {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------- INCIDENCIAS ----------------------\n");
        if (an.getErrores().isEmpty() && an.getLineasVacias().isEmpty()) {
            sb.append("Sin incidencias.\n");
            return sb.toString();
        }
        for (String e : an.getErrores()) {
            sb.append("Mal formada  -> ").append(e).append('\n');
        }
        for (int n : an.getLineasVacias()) {
            sb.append("Vacía (omitida) -> Línea ").append(n).append('\n');
        }
        return sb.toString();
    }

    public String resumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================== RESUMEN ESTADÍSTICO ====================\n");
        sb.append("Total de paquetes procesados : ").append(stats.total()).append('\n');
        sb.append("Paquetes TCP                 : ").append(stats.contarTransporte("TCP")).append('\n');
        sb.append("Paquetes UDP                 : ").append(stats.contarTransporte("UDP")).append('\n');
        sb.append("Paquetes ICMP                : ").append(stats.contarTransporte("ICMP")).append('\n');

        sb.append("\nPaquetes por protocolo de aplicación:\n");
        for (Map.Entry<String, Integer> e : stats.porProtocoloApp().entrySet()) {
            sb.append(String.format("  %-8s : %d%n", e.getKey(), e.getValue()));
        }

        sb.append('\n');
        sb.append("IP origen más frecuente      : ").append(masFrecuente(stats.frecuenciaIpOrigen())).append('\n');
        sb.append("IP destino más frecuente     : ").append(masFrecuente(stats.frecuenciaIpDestino())).append('\n');
        sb.append("Puerto destino más frecuente : ").append(masFrecuente(stats.frecuenciaPuertoDestino())).append('\n');

        sb.append("\nConteo de flags TCP:\n");
        for (Map.Entry<String, Integer> e : stats.conteoFlagsTcp().entrySet()) {
            sb.append(String.format("  %-8s : %d%n", e.getKey(), e.getValue()));
        }

        sb.append("\nResumen por servicio (total / consultas / respuestas):\n");
        for (Map.Entry<String, int[]> e : stats.resumenPorServicio().entrySet()) {
            int[] f = e.getValue();
            sb.append(String.format("  %-8s : %d / %d / %d%n", e.getKey(), f[0], f[1], f[2]));
        }
        return sb.toString();
    }

    public String reporteCompleto(AnalizadorPaquetes an) {
        return encabezado(an) + "\n" + detalle(an.getPaquetes()) + incidencias(an) + "\n" + resumen();
    }

    public void exportar(String ruta, String contenido) throws IOException {
        try (PrintWriter pw = new PrintWriter(ruta, "UTF-8")) {
            pw.print(contenido);
        }
    }

    private String masFrecuente(Map<String, Integer> m) {
        if (m.isEmpty()) return "(sin datos)";
        List<String> top = stats.masFrecuentes(m);
        return String.join(", ", top) + "  -> " + m.get(top.get(0)) + " paquetes";
    }
}
