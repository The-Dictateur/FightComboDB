package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class FrameDataService {

    private final Map<String, JsonNode> cache = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode obtenerPersonaje(String juego, String personaje) {
        String key = juego + "|" + personaje;
        return cache.computeIfAbsent(key, k -> cargarDesdeJson(juego, personaje));
    }

    private JsonNode cargarDesdeJson(String juego, String personaje) {
        try {
            String slug = personaje.toLowerCase();
            String ruta = "/json/" + juego + "/" + slug + ".json";

            System.out.println("Buscando fichero de personaje en: " + ruta);

            InputStream in = getClass().getResourceAsStream(ruta);

            if (in == null) {
                System.out.println("No se encontró el fichero de personaje en: " + ruta);
                return null;
            }

            return mapper.readTree(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}