package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class NotationService {

    private JsonNode cache;
    private boolean intentadoCargar = false;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode obtenerNotacion() {
        if (!intentadoCargar) {
            cache = cargarDesdeJson();
            intentadoCargar = true;
        }
        return cache;
    }

    private JsonNode cargarDesdeJson() {
        try {
            String ruta = "/json/notation.json";
            InputStream in = getClass().getResourceAsStream(ruta);

            if (in == null) {
                System.out.println("No se encontró el fichero de notación universal en: " + ruta);
                return null;
            }

            return mapper.readTree(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}