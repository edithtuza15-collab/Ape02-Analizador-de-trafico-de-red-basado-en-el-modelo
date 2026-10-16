package io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalizadorArchivo {
    private static final int NIVELES_SUPERIORES = 3;
    private static final int NIVELES_INFERIORES = 2;

    private final String nombre;

    public LocalizadorArchivo(String nombre) {
        this.nombre = nombre;
    }

    public Path localizar() {
        Path indicado = Paths.get(nombre);
        if (Files.isRegularFile(indicado)) {
            return indicado.toAbsolutePath().normalize();
        }
        if (indicado.isAbsolute()) {
            return null;
        }

        Path actual = Paths.get("").toAbsolutePath();
        Path directo = actual.resolve(nombre);
        if (Files.isRegularFile(directo)) {
            return directo.normalize();
        }

        Path abajo = buscarEnSubcarpetas(actual, NIVELES_INFERIORES);
        if (abajo != null) {
            return abajo;
        }

        List<Path> bases = new ArrayList<Path>();
        agregarConSuperiores(bases, actual);
        Path codigo = ubicacionDelCodigo();
        if (codigo != null) {
            agregarConSuperiores(bases, codigo);
        }
        for (Path base : bases) {
            Path candidato = base.resolve(nombre);
            if (Files.isRegularFile(candidato)) {
                return candidato.normalize();
            }
        }
        return null;
    }

    private void agregarConSuperiores(List<Path> bases, Path inicio) {
        Path p = inicio;
        for (int i = 0; i <= NIVELES_SUPERIORES && p != null; i++) {
            if (!bases.contains(p)) {
                bases.add(p);
            }
            p = p.getParent();
        }
    }

    private Path ubicacionDelCodigo() {
        try {
            Path ruta = Paths.get(LocalizadorArchivo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            return Files.isDirectory(ruta) ? ruta : ruta.getParent();
        } catch (URISyntaxException | RuntimeException e) {
            return null;
        }
    }

    private Path buscarEnSubcarpetas(Path base, int niveles) {
        if (niveles == 0) {
            return null;
        }
        List<Path> subcarpetas = new ArrayList<Path>();
        try (DirectoryStream<Path> flujo = Files.newDirectoryStream(base)) {
            for (Path hijo : flujo) {
                String n = hijo.getFileName().toString();
                if (Files.isDirectory(hijo) && !n.startsWith(".")) {
                    subcarpetas.add(hijo);
                }
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
        for (Path hijo : subcarpetas) {
            Path candidato = hijo.resolve(nombre);
            if (Files.isRegularFile(candidato)) {
                return candidato.normalize();
            }
        }
        for (Path hijo : subcarpetas) {
            Path hallado = buscarEnSubcarpetas(hijo, niveles - 1);
            if (hallado != null) {
                return hallado;
            }
        }
        return null;
    }
}
