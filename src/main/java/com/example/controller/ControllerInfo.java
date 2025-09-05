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
public class ControllerInfo {

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

    public void setInfo(String message) {
        errorLabel.setText(message);

        Platform.runLater(() -> {
            errorLabel.applyCss();
            errorLabel.layout();
            errorLabel.setWrapText(true);

            // Medimos el VBox completo, no solo el label
            Stage currentStage = this.stage;
            if (currentStage != null && currentStage.getScene() != null) {
                double prefHeight = currentStage.getScene().getRoot().prefHeight(-1);
                double prefWidth = currentStage.getScene().getRoot().prefWidth(-1);

                currentStage.setHeight(prefHeight + 40); // margen adicional
                currentStage.setWidth(prefWidth + 15);   // margen adicional
                currentStage.setResizable(false);
            }
        });
    }

    public static void showInfo(String info) {
        try {
            FXMLLoader loader = new FXMLLoader(ControllerInfo.class.getResource("/Info.fxml"));
            Parent root = loader.load();

            ControllerInfo controllerInfo = loader.getController();
            Stage infoStage = new Stage();
            controllerInfo.setStage(infoStage);
            controllerInfo.setInfo(info);

            infoStage.setTitle("Info");
            infoStage.setScene(new Scene(root));
            infoStage.initModality(Modality.APPLICATION_MODAL);
            infoStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
