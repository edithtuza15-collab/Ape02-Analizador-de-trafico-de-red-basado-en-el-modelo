package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivo {
    private final String ruta;

    public LectorArchivo(String ruta) {
        this.ruta = ruta;
    }

    public List<String> leerLineas() throws IOException {
        List<String> lineas = new ArrayList<String>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ruta), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }
}
