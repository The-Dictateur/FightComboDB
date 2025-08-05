package com.example.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.Personaje;
import com.example.service.NoteService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class ControllerNote {
        
    @FXML
    private Button buttonSave;

    @FXML
    private TextArea textNote;

    @FXML
    private TextField noteTitle;

    @Autowired
    private NoteService noteService;

    private Personaje personaje;

    public void initialize() {
        System.out.println("Note Controller initialized");

        buttonSave.setOnAction(event -> {
            try {
                saveNote();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveNote() throws IOException {
        String title = noteTitle.getText();
        String content = textNote.getText();

        if (title.isEmpty() || content.isEmpty()) {
            String error = "Title and content cannot be empty.";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NoteError.fxml"));
            Parent root = loader.load();

            ControllerError controllerError = loader.getController();
            Stage errorStage = new Stage();
            controllerError.setStage(errorStage);
            controllerError.setError(error);
            
            errorStage.setTitle("Error");
            errorStage.setScene(new Scene(root));
            errorStage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
            errorStage.showAndWait(); // Espera hasta que se cierre
        } else if (personaje == null) {
        // Por seguridad, si no se ha seteado el personaje
        String error = "No character associated.";
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NoteError.fxml"));
        Parent root = loader.load();

        ControllerError controllerError = loader.getController();
        Stage errorStage = new Stage();
        controllerError.setStage(errorStage);
        controllerError.setError(error);

        errorStage.setTitle("Error");
        errorStage.setScene(new Scene(root));
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.showAndWait();
        }
    }
}
