package com.example.controller;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class ControllerWarning {
    public static boolean response;

    @FXML
    private Label messageLabel;

    @FXML
    private Button buttonYes;

    @FXML
    private Button buttonNo;

    private Stage stage;

    public void initialize() {
        System.out.println("Warning Controller initialized");

        buttonYes.setOnAction(event -> {
            response = true;
            stage.close();
            System.out.println("Yes button clicked");
        });

        buttonNo.setOnAction(event -> {
            response = false;
            stage.close();
            System.out.println("No button clicked");
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setWarning(String warning) {
        messageLabel.setText(warning);

        Platform.runLater(() -> {
            messageLabel.applyCss();
            messageLabel.layout();
            messageLabel.setWrapText(true);

            // Medimos el VBox completo, no solo el label
            Stage currentStage = this.stage;
            if (currentStage != null && currentStage.getScene() != null) {
                double prefHeight = currentStage.getScene().getRoot().prefHeight(-1);
                double prefWidth = currentStage.getScene().getRoot().prefWidth(-1);

                currentStage.setHeight(prefHeight + 40); // margen adicional
                currentStage.setWidth(prefWidth);   // margen adicional
                currentStage.setResizable(false);
            }
        });
    }

    public static void showWarning(String warning) {
        try {
            FXMLLoader loader = new FXMLLoader(ControllerWarning.class.getResource("/Warning.fxml"));
            Parent root = loader.load();

            ControllerWarning controllerWarning = loader.getController();
            Stage warningStage = new Stage();
            controllerWarning.setStage(warningStage);
            controllerWarning.setWarning(warning);

            warningStage.setTitle("Warning");
            warningStage.setScene(new Scene(root));
            warningStage.initModality(Modality.APPLICATION_MODAL);
            warningStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}