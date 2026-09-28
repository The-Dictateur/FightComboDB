package com.example.controller;

import com.example.AI.OllamaClient;
import com.example.service.MecanicasJuegoService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControllerAI {

    @FXML
    private Button sendAi;

    @FXML
    private TextArea textAi;

    private TextArea targetTextArea;
    private String juego;

    @Autowired
    private MecanicasJuegoService mecanicasJuegoService;

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

    /**
     * Construye el "carácter" de la IA, insertando el juego actual como variable.
     * Esto es lo que define cómo se comporta la IA, no lo que sabe en concreto.
     */
    private String construirSystemPrompt() {
        StringBuilder sb = new StringBuilder();

        if (juego != null && !juego.isBlank()) {
            sb.append("You are a highly experienced professional coach and teacher specialized in fighting games.\n");
            sb.append("The game you are currently helping with is: ").append(juego).append(".\n");
            sb.append("Every answer you give must be specific to ").append(juego).append(" and its actual mechanics.\n");

            List<MecanicasJuegoService.Mecanica> mecanicas = mecanicasJuegoService.obtenerMecanicas(juego);
            if (!mecanicas.isEmpty()) {
                sb.append("You have been provided with the official mechanics of ").append(juego).append(" below — use them as ground truth.\n");
            } else {
                sb.append("No mechanics data file was found for ").append(juego).append(". ");
                sb.append("Answer using your general knowledge of this specific game if you have it, ");
                sb.append("but clearly say when you are not certain about a specific detail instead of inventing it.\n");
            }
        } else {
            sb.append("You are a highly experienced professional coach and teacher specialized in fighting games in general.\n");
            sb.append("No specific game has been identified for this note. Ask the user to clarify which game they mean if it's not obvious from their question.\n");
        }

        sb.append("""
            
            Your job is to help write clear, useful study notes about specific characters,
            explaining strengths, weaknesses, key normals, neutral tools, pressure options, combos
            and matchups when you have enough information for it.
            
            Important rules:
            - Never mix mechanics from a different game than the one specified above.
            - If you don't have a specific data point (exact frame data, exact damage, etc.), say so clearly instead of inventing it.
            - Answer in the language the user is talking to you in, in a clear and organized way, suitable to be saved directly as a study note.
            """);

        return sb.toString();
    }

    /**
     * Construye el contexto concreto (mecánicas del juego) + la pregunta del usuario.
     */
    private String construirPrompt(String pregunta) {
        StringBuilder sb = new StringBuilder();

        if (juego != null && !juego.isBlank()) {
            sb.append("Game: ").append(juego).append("\n");

            List<MecanicasJuegoService.Mecanica> mecanicas = mecanicasJuegoService.obtenerMecanicas(juego);
            if (!mecanicas.isEmpty()) {
                sb.append("Game mechanics:\n");
                for (MecanicasJuegoService.Mecanica m : mecanicas) {
                    sb.append("- ").append(m.getName()).append(": ").append(m.getDescription()).append("\n");
                }
            }
            // Refuerzo: lo repetimos justo antes de la pregunta, después de la lista larga
            sb.append("\n(Remember: all of the above mechanics belong specifically to ").append(juego).append(".)\n\n");
        }

        sb.append("Pregunta del usuario: ").append(pregunta);
        return sb.toString();
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
    }
}