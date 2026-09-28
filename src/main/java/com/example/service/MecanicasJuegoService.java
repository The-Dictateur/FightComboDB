package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MecanicasJuegoService {

    private final Map<String, List<Mecanica>> cache = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Mecanica> obtenerMecanicas(String nombreJuego) {
        return cache.computeIfAbsent(nombreJuego, this::cargarDesdeJson);
    }

    private List<Mecanica> cargarDesdeJson(String nombreJuego) {
        try {
            String ruta = "/json/" + nombreJuego + "/mechanics.json";
            InputStream in = getClass().getResourceAsStream(ruta);

            if (in == null) {
                System.out.println("No se encontró el fichero de mecánicas en: " + ruta);
                return List.of();
            }

            JuegoData data = mapper.readValue(in, JuegoData.class);
            // Aquí el println útil
            System.out.println("=== Mecánicas cargadas para: " + data.getGame() + " ===");
            System.out.println("Total de mecánicas: " + data.getMechanics().size());
            for (Mecanica m : data.getMechanics()) {
                System.out.println("- " + m.getName() + ": " + m.getDescription());
            }
            System.out.println("=== Fin de mecánicas ===");
            return data.getMechanics();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Clases internas para mapear el JSON
    public static class JuegoData {
        private String game;
        private List<Mecanica> mechanics;

        public String getGame() { return game; }
        public void setGame(String game) { this.game = game; }
        public List<Mecanica> getMechanics() { return mechanics; }
        public void setMechanics(List<Mecanica> mechanics) { this.mechanics = mechanics; }
    }

    public static class Mecanica {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}