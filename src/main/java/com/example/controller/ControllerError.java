package com.example.controller;

import org.springframework.stereotype.Component;

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

        // Ajuste automático del tamaño de la ventana según el texto
        errorLabel.applyCss();
        errorLabel.layout();
        double height = errorLabel.getHeight() + 130;
        stage.setHeight(height);
    }
}
