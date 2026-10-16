package app;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import analisis.AnalizadorPaquetes;
import io.LectorArchivo;
import io.LocalizadorArchivo;
import modelo.Paquete;
import reporte.Estadisticas;
import reporte.GeneradorReporte;

public class Main {
    private static final String RUTA_DEFECTO = "paquetes.txt";
    private static final String ARCHIVO_REPORTE = "reporte_analisis.txt";

    public static void main(String[] args) {
        String ruta = RUTA_DEFECTO;
        boolean directoAlMenu = false;
        boolean sinMenu = false;
        for (String a : args) {
            if (a.equals("--menu")) {
                directoAlMenu = true;
            } else if (a.equals("--sin-menu")) {
                sinMenu = true;
            } else {
                ruta = a;
            }
        }

        Path archivo = new LocalizadorArchivo(ruta).localizar();
        if (archivo == null) {
            System.out.println("No se encontró el archivo '" + ruta + "'.");
            System.out.println("Se buscó en la carpeta actual, en sus carpetas superiores y en sus subcarpetas.");
            System.out.println("Colócalo junto a la carpeta src o indica la ruta completa como argumento.");
            return;
        }

        List<String> lineas;
        try {
            lineas = new LectorArchivo(archivo.toString()).leerLineas();
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo '" + archivo + "'. Verifica que sea legible.");
            return;
        }

        AnalizadorPaquetes analizador = new AnalizadorPaquetes();
        analizador.analizar(lineas);
        Estadisticas stats = new Estadisticas(analizador.getPaquetes());
        GeneradorReporte reporte = new GeneradorReporte(stats);

        System.out.println("Archivo analizado: " + archivo + "\n");

        if (sinMenu) {
            System.out.println(reporte.reporteCompleto(analizador));
            return;
        }

        Scanner sc = new Scanner(System.in);
        if (directoAlMenu) {
            System.out.println(reporte.encabezado(analizador));
            ejecutarMenu(sc, analizador, stats, reporte);
        } else {
            pantallaInicio(sc, analizador, stats, reporte);
        }
    }

    private static void pantallaInicio(Scanner sc, AnalizadorPaquetes analizador, Estadisticas stats,
                                       GeneradorReporte reporte) {
        System.out.println("=== ANALIZADOR DE TRÁFICO TCP/IP ===");
        System.out.println(reporte.encabezado(analizador));
        boolean yaMostrado = false;
        while (true) {
            System.out.println("1. Mostrar el análisis completo y el resumen");
            System.out.println("2. Interactuar con el menú");
            System.out.println("0. Salir");
            System.out.print("Opción [1]: ");
            String entrada = leer(sc);
            if (entrada == null) {
                if (!yaMostrado) {
                    System.out.println("\n");
                    System.out.println(reporte.reporteCompleto(analizador));
                }
                return;
            }
            if (entrada.isEmpty()) {
                entrada = "1";
            }
            switch (entrada) {
                case "1":
                    System.out.println(reporte.reporteCompleto(analizador));
                    yaMostrado = true;
                    break;
                case "2":
                    ejecutarMenu(sc, analizador, stats, reporte);
                    break;
                case "0":
                    System.out.println("Hasta luego.");
                    return;
                default:
                    System.out.println("Opción inválida.\n");
            }
        }
    }

    private static void ejecutarMenu(Scanner sc, AnalizadorPaquetes analizador, Estadisticas stats,
                                     GeneradorReporte reporte) {
        boolean volver = false;
        while (!volver) {
            mostrarOpciones();
            String entrada = leer(sc);
            if (entrada == null) {
                break;
            }
            switch (entrada) {
                case "1":
                    System.out.println(reporte.detalle(analizador.getPaquetes()));
                    break;
                case "2":
                    verPaquete(sc, stats);
                    break;
                case "3":
                    System.out.println(reporte.resumen());
                    break;
                case "4":
                    System.out.print("IP (origen o destino): ");
                    mostrarLista(stats.buscarPorIp(textoOVacio(leer(sc))), reporte);
                    break;
                case "5":
                    System.out.print("Protocolo de transporte (TCP/UDP/ICMP): ");
                    mostrarLista(stats.filtrarPorTransporte(textoOVacio(leer(sc))), reporte);
                    break;
                case "6":
                    System.out.print("Protocolo de aplicación (HTTP, DNS, ...): ");
                    mostrarLista(stats.filtrarPorApp(textoOVacio(leer(sc))), reporte);
                    break;
                case "7":
                    System.out.println(reporte.incidencias(analizador));
                    break;
                case "8":
                    exportar(analizador, reporte);
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void mostrarOpciones() {
        System.out.println("=== MENÚ ===");
        System.out.println("1. Ver todos los paquetes");
        System.out.println("2. Ver un paquete por número de línea");
        System.out.println("3. Ver resumen estadístico");
        System.out.println("4. Buscar por dirección IP");
        System.out.println("5. Filtrar por protocolo de transporte");
        System.out.println("6. Filtrar por protocolo de aplicación");
        System.out.println("7. Ver incidencias (líneas mal formadas y vacías)");
        System.out.println("8. Exportar análisis a archivo");
        System.out.println("0. Volver");
        System.out.print("Opción: ");
    }

    private static void verPaquete(Scanner sc, Estadisticas stats) {
        System.out.print("Número de línea: ");
        String texto = textoOVacio(leer(sc));
        try {
            Paquete p = stats.buscarPorLinea(Integer.parseInt(texto));
            if (p == null) {
                System.out.println("No hay un paquete válido en esa línea.\n");
            } else {
                System.out.println(p.describir() + "\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Número inválido.\n");
        }
    }

    private static void mostrarLista(List<Paquete> lista, GeneradorReporte reporte) {
        System.out.println(reporte.detalle(lista) + "Coincidencias: " + lista.size() + "\n");
    }

    private static void exportar(AnalizadorPaquetes analizador, GeneradorReporte reporte) {
        try {
            reporte.exportar(ARCHIVO_REPORTE, reporte.reporteCompleto(analizador));
            System.out.println("Análisis exportado a " + ARCHIVO_REPORTE + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo exportar: " + e.getMessage() + "\n");
        }
    }

    private static String leer(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }

    private static String textoOVacio(String s) {
        return s == null ? "" : s;
    }
}
