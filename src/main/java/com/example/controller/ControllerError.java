package com.example.controller;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

@Component
public class ControllerError {

    @FXML
    private Button buttonOK;

    public void initialize() {
        System.out.println("Error Controller initialized");

        buttonOK.setOnAction(event -> {
            System.out.println("OK button clicked");
            Stage stage = (Stage) buttonOK.getScene().getWindow();
            stage.close(); // Cierra la ventana de error
        });
    }
}
