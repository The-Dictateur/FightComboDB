package com.example.controller;

import com.example.AI.OllamaClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class ControllerAI {

    @FXML
    private Button sendAi;

    @FXML
    private TextArea textAi;

    private TextArea targetTextArea;

    private volatile boolean cargando = false; // indica si hay una pregunta en curso

    public void initialize() {
        System.out.println("AI Controller initialized");

        // Interceptamos el cierre de la ventana en cuanto la escena esté disponible
        Platform.runLater(() -> {
            Stage stage = (Stage) sendAi.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (cargando) {
                    event.consume(); // cancela el cierre
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
                    return ollamaClient(pregunta);
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
                ControllerInfo.showInfo("Error al preguntar a la IA: " + tarea.getException().getMessage());
                sendAi.setDisable(false);
                cargando = false;
            });

            new Thread(tarea).start();
        });
    }

    public String ollamaClient(String pregunta) throws Exception {
        OllamaClient client = new OllamaClient();
        return client.preguntar("qwen2.5:14b-instruct", pregunta);
    }

    public void setTargetTextArea(TextArea targetTextArea) {
        this.targetTextArea = targetTextArea;
    }
}