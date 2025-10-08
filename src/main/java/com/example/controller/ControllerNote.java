package com.example.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.NoteService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@Component
public class ControllerNote {

    private WebView webView;

    @FXML
    private Button buttonSave;

    @FXML
    private TextArea textNote;

    @FXML
    private TextField noteTitle;

    @Autowired
    private NoteService noteService;

    @FXML
    private ToggleButton toggleNote;

    @FXML
    private Label labelDesc;

    @FXML
    private VBox containerNote;

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

        // Acción para el ToggleButton: cambia el texto al pulsar
        toggleNote.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                toggleNote.setText("Link");  // Texto cuando está presionado
                labelDesc.setText("Link:");
                showWebView();
            } else {
                toggleNote.setText("Note"); // Texto cuando se desactiva
                labelDesc.setText("Description:");
                hideWebView();
            }
        });
        // Texto inicial del ToggleButton
        toggleNote.setText("Note");
    }

    private boolean isValidUrl(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.startsWith("http://") || text.startsWith("https://");
    }

    private void showWebView() {
        String url = textNote.getText().trim();

        if (!isValidUrl(url)) return;

        if (webView == null) {
            webView = new WebView();
            webView.setPrefHeight(630);
        }

        // Si es un enlace de YouTube, se puede embeber directamente
        if (url.contains("youtube.com/watch")) {
            url = url.replace("watch?v=", "embed/");
        }

        webView.getEngine().load(url);

        if (!containerNote.getChildren().contains(webView)) {
            containerNote.getChildren().add(webView);
        }
    }

    private void hideWebView() {
    if (webView != null && containerNote.getChildren().contains(webView)) {
        containerNote.getChildren().remove(webView);
    }
}

    public void saveNote() throws IOException {
        String title = noteTitle.getText();
        String content = textNote.getText();

        if (title.isEmpty() || content.isEmpty()) {
            ControllerInfo.showInfo("Title and content cannot be empty.");
            return;
        } else if (personajeId == null) {
            ControllerInfo.showInfo("No character associated.");
            return;
        }

        noteService.saveNote(title, content, personajeId);
        try {
            // Cerrar la ventana después de guardar
            Stage stage = (Stage) buttonSave.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPersonajeId(Long personajeId) {
        this.personajeId = personajeId;
        System.out.println("ID del personaje recibido: " + personajeId);
    }

    public void setNote(String title, String content, Long idNote) throws IOException {
        noteTitle.setText(title);
        textNote.setText(content);
        System.out.println("Nota cargada: " + title + " - " + content + " - ID: " + idNote);
        buttonSave.setOnAction(event -> {
            String title2 = noteTitle.getText();
            String content2 = textNote.getText();
            noteService.updateNote(idNote, title2, content2);
            try {
                // Cerrar la ventana después de guardar
                Stage stage = (Stage) buttonSave.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
