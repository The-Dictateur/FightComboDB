package com.example.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.NoteService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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


    private Long personajeId;

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
            ControllerError.showError("Title and content cannot be empty.");
            return;
        } else if (personajeId == null) {
            ControllerError.showError("No character associated.");
            return;
        }

        noteService.saveNote(title, content, personajeId);
    }

    public void setPersonajeId(Long personajeId) {
        this.personajeId = personajeId;
        System.out.println("ID del personaje recibido: " + personajeId);
    }
}
