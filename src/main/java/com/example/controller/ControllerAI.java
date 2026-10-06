package com.example.controller;

import com.example.AI.OllamaClient;
import com.example.service.FrameDataService;
import com.example.service.MecanicasJuegoService;
import com.example.service.NotationService;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ControllerAI {

    @FXML
    private Button sendAi;

    @FXML
    private TextArea textAi;

    private TextArea targetTextArea;
    private String juego;
    private String personaje;

    @Autowired
    private MecanicasJuegoService mecanicasJuegoService;

    @Autowired
    private FrameDataService frameDataService;

    @Autowired
    private NotationService notationService;

    private volatile boolean cargando = false;

    public void initialize() {
        System.out.println("AI Controller initialized");

        Platform.runLater(() -> {
            Stage stage = (Stage) sendAi.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (cargando) {
                    event.consume();
                    ControllerInfo.showInfo("Espera a que la IA termine de responder antes de cerrar esta ventana.");
                }
            });
        });

        sendAi.setOnAction(event -> {
            String pregunta = textAi.getText();

            if (pregunta == null || pregunta.isBlank()) {
                ControllerInfo.showInfo("The text is blank, please write a question for the AI");
                return;
            }

            sendAi.setDisable(true);
            cargando = true;

            Task<String> tarea = new Task<>() {
                @Override
                protected String call() throws Exception {
                    String systemPrompt = construirSystemPrompt();
                    String prompt = construirPrompt(pregunta);
                    System.out.println("=== SYSTEM PROMPT ===\n" + systemPrompt);
                    System.out.println("=== PROMPT ===\n" + prompt);
                    System.out.println("=== Longitud aproximada del prompt: " + prompt.length() + " caracteres (~" + (prompt.length() / 4) + " tokens) ===");
                    return ollamaClient(systemPrompt, prompt);
                }
            };

            tarea.setOnSucceeded(e -> {
                String respuesta = tarea.getValue();
                if (targetTextArea != null) {
                    targetTextArea.setText(respuesta);
                }
                sendAi.setDisable(false);
                cargando = false;
            });

            tarea.setOnFailed(e -> {
                tarea.getException().printStackTrace();
                ControllerInfo.showInfo("Error al preguntar a la IA: " + tarea.getException().getMessage());
                sendAi.setDisable(false);
                cargando = false;
            });

            new Thread(tarea).start();
        });
    }

    private String construirSystemPrompt() {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a highly experienced professional coach and teacher specialized in fighting games.\n");

        if (juego != null && !juego.isBlank()) {
            sb.append("GAME: ").append(juego).append("\n");
            sb.append("Every answer must be specific to ").append(juego).append(" and its actual mechanics. ");
            sb.append("Never mix in mechanics, terms or numbers from any other fighting game.\n");
        } else {
            sb.append("No specific game has been identified for this note. Ask the user to clarify which game they mean if it's not obvious from their question.\n");
        }

        if (personaje != null && !personaje.isBlank()) {
            sb.append("CHARACTER: ").append(personaje).append("\n");
            sb.append("Every answer must be specific to ").append(personaje).append(" and their actual moveset. ");
            sb.append("Never mix in moves, frame data or combos from any other character.\n");
        }

        sb.append("""
            
            You will be given an input notation reference, the game's mechanics, and the character's data as
            reference material in the user message below. Use that reference material as ground truth. If a
            specific data point isn't in it (exact frame data, exact damage, etc.), say so clearly instead of
            inventing it.
            
            Your job is to help write clear, useful study notes about this specific character,
            explaining strengths, weaknesses, key normals, neutral tools, pressure options, combos
            and matchups when you have enough information for it.
            
            Notation rule: whenever you refer to any move, button, or input, you MUST write it using the
            numpad notation defined in the input notation reference (e.g. 236K, 2P, j.S, 623H, c.S, f.S).
            Never use arrow symbols, words like "quarter circle forward", or any other notation style to
            describe an input — always use the numpad digits and letters exactly as shown in the reference
            and in the character's own movelist.
            
            Answer in the language the user is talking to you in, in a clear and organized way, suitable to be saved directly as a study note.
            """);

        return sb.toString();
    }

    /**
     * Construye el contexto concreto (mecánicas del juego + datos del personaje, en texto legible) + la pregunta.
     */
    private String construirPrompt(String pregunta) {
        StringBuilder sb = new StringBuilder();

        JsonNode notacion = notationService.obtenerNotacion();
        if (notacion != null) {
            sb.append("=== INPUT NOTATION REFERENCE (applies to all fighting games unless the game's own mechanics below say otherwise) ===\n");
            sb.append(jsonATextoLegible(notacion));
            sb.append("\n");
        }

        if (juego != null && !juego.isBlank()) {
            sb.append("=== GAME: ").append(juego).append(" ===\n");

            List<MecanicasJuegoService.Mecanica> mecanicas = mecanicasJuegoService.obtenerMecanicas(juego);
            if (!mecanicas.isEmpty()) {
                sb.append("Mechanics of ").append(juego).append(":\n");
                for (MecanicasJuegoService.Mecanica m : mecanicas) {
                    sb.append("- ").append(m.getName()).append(": ").append(m.getDescription()).append("\n");
                }
            } else {
                sb.append("(No mechanics file found for this game.)\n");
            }
            sb.append("\n");
        }

        if (personaje != null && !personaje.isBlank()) {
            sb.append("=== CHARACTER: ").append(personaje).append(" (from ").append(juego).append(") ===\n");

            JsonNode datosPersonaje = frameDataService.obtenerPersonaje(juego, personaje);
            if (datosPersonaje != null) {
                sb.append(jsonATextoLegible(datosPersonaje));
            } else {
                sb.append("(No character data file found for ").append(personaje).append(". ");
                sb.append("Answer using your general knowledge of this character if you have it, but say clearly when unsure instead of inventing.)\n");
            }
            sb.append("\n");
        }

        sb.append("=== END OF REFERENCE MATERIAL ===\n");
        sb.append("Everything above belongs strictly to ");
        if (juego != null && !juego.isBlank()) sb.append(juego);
        if (personaje != null && !personaje.isBlank()) sb.append(" and the character ").append(personaje);
        sb.append(". Do not reference any other game or character.\n\n");

        sb.append("Pregunta del usuario: ").append(pregunta);
        return sb.toString();
    }

    /**
     * Convierte un JsonNode (el JSON de un personaje) en texto plano legible, campo por campo,
     * en vez de volcar el JSON crudo con llaves/corchetes. Esto reduce muchísimo los tokens
     * y es más fácil de "leer" correctamente para el modelo.
     */
    private String jsonATextoLegible(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        recorrerJsonNode(node, sb, "");
        return sb.toString();
    }

    private void recorrerJsonNode(JsonNode node, StringBuilder sb, String prefijo) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> campos = node.fields();
            while (campos.hasNext()) {
                Map.Entry<String, JsonNode> campo = campos.next();
                JsonNode valor = campo.getValue();

                if (valor.isValueNode()) {
                    sb.append(prefijo).append(campo.getKey()).append(": ").append(valor.asText()).append("\n");
                } else {
                    sb.append(prefijo).append(campo.getKey()).append(":\n");
                    recorrerJsonNode(valor, sb, prefijo + "  ");
                }
            }
        } else if (node.isArray()) {
            int i = 1;
            for (JsonNode item : node) {
                if (item.isValueNode()) {
                    sb.append(prefijo).append("- ").append(item.asText()).append("\n");
                } else {
                    sb.append(prefijo).append("[").append(i).append("]\n");
                    recorrerJsonNode(item, sb, prefijo + "  ");
                }
                i++;
            }
        } else {
            sb.append(prefijo).append(node.asText()).append("\n");
        }
    }

    public String ollamaClient(String systemPrompt, String prompt) throws Exception {
        OllamaClient client = new OllamaClient();
        return client.preguntar("qwen2.5:14b-instruct", systemPrompt, prompt);
    }

    public void setTargetTextArea(TextArea targetTextArea) {
        this.targetTextArea = targetTextArea;
    }

    public void setJuego(String juego) {
        this.juego = juego;
        System.out.println("Juego recibido en ControllerAI: [" + juego + "]");
    }

    public void setPersonaje(String personaje) {
        this.personaje = personaje;
        System.out.println("Personaje recibido en ControllerAI: [" + personaje + "]");
    }
}