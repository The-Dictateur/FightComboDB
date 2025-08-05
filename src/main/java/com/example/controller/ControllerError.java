package com.example.controller;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@Component
public class ControllerError {

    @FXML
    private Button buttonOK;

    @FXML
    private Label errorLabel;

    private Stage stage;

    public void initialize() {
        System.out.println("Error Controller initialized");

        buttonOK.setOnAction(event -> {
            System.out.println("OK button clicked");
            Stage stage = (Stage) buttonOK.getScene().getWindow();
            stage.close(); // Cierra la ventana de error
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setError(String message) {
        errorLabel.setText(message);

        Platform.runLater(() -> {
            errorLabel.applyCss();
            errorLabel.layout();

            // Medimos el VBox completo, no solo el label
            Stage currentStage = this.stage;
            if (currentStage != null && currentStage.getScene() != null) {
                double prefHeight = currentStage.getScene().getRoot().prefHeight(-1);
                double prefWidth = currentStage.getScene().getRoot().prefWidth(-1);

                currentStage.setHeight(prefHeight + 40); // margen adicional
                currentStage.setWidth(prefWidth);   // margen adicional
            }
        });
    }
}
