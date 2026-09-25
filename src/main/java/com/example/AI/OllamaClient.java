package com.example.AI;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class OllamaClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Envía un prompt a un modelo local de Ollama y devuelve la respuesta como texto.
     *
     * @param modelo nombre del modelo, p.ej. "qwen2.5:14b-instruct"
     * @param prompt el texto completo que se le manda al modelo
     * @return la respuesta generada por la IA
     */
    public String preguntar(String modelo, String prompt) throws Exception {
        Map<String, Object> body = Map.of(
                "model", modelo,
                "prompt", prompt,
                "stream", false
        );

        String jsonBody = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ollama respondió con código " + response.statusCode() + ": " + response.body());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> respuestaJson = mapper.readValue(response.body(), Map.class);

        Object respuesta = respuestaJson.get("response");
        if (respuesta == null) {
            throw new RuntimeException("Respuesta de Ollama sin campo 'response': " + response.body());
        }

        return respuesta.toString();
    }
}