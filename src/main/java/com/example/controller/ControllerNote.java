package com.example.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.NoteService;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

@Component
public class ControllerNote {

    private MediaPlayer mediaPlayer;
    private File downloadedVideoFile;
    private java.util.List<javafx.scene.Node> originalNodes;

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
        originalNodes = new java.util.ArrayList<>(containerNote.getChildren());
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
                showVideo();
            } else {
                toggleNote.setText("Note"); // Texto cuando se desactiva
                labelDesc.setText("Description:");
                hideVideo();
            }
        });
        // Texto inicial del ToggleButton
        toggleNote.setText("Note");

        Platform.runLater(() -> {
            Stage stage = (Stage) containerNote.getScene().getWindow();
            stage.setOnCloseRequest(event -> hideVideo());
        });
    }

    private boolean isValidUrl(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.startsWith("http://") || text.startsWith("https://");
    }

    private File downloadMP4(String youtubeUrl) {
        try {
            // Solo generamos una ruta temporal, no creamos el archivo
            File outputFile = new File(System.getProperty("java.io.tmpdir"),
                                    "youtube_video_" + System.currentTimeMillis() + ".mp4");

            ProcessBuilder pb = new ProcessBuilder(
                "lib/yt-dlp.exe",
                "-f", "bestvideo[height<=720][vcodec^=avc1]+bestaudio[ext=m4a]/best[height<=720][vcodec^=avc1]",
                "--merge-output-format", "mp4",
                "--ffmpeg-location", "lib/ffmpeg/bin",
                "-o", outputFile.getAbsolutePath(),
                youtubeUrl
            );

            System.out.println("Executing command: " + String.join(" ", pb.command()));

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            process.waitFor();

            if (outputFile.exists() && outputFile.length() > 0) {
                return outputFile;
            } else {
                System.out.println("El archivo no se generó correctamente.");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showVideo() {

        String youtubeUrl = textNote.getText().trim();
        if (!isValidUrl(youtubeUrl)) {
            System.out.println("URL inválida");
            return;
        }

        // Eliminamos cualquier MediaView existente
        containerNote.getChildren().removeIf(node -> node instanceof MediaView);

        new Thread(() -> {
            File videoFile = downloadMP4(youtubeUrl); // siempre nuevo
            if (videoFile == null) {
                System.out.println("No se pudo descargar el video.");
                return;
            }
            downloadedVideoFile = videoFile;

            Platform.runLater(() -> {
                try {
                    Media media = new Media(videoFile.toURI().toString());
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }

                    mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.setFitWidth(800);
                    mediaView.setFitHeight(450);
                    mediaView.setPreserveRatio(true);

                    containerNote.getChildren().add(mediaView);

                    mediaPlayer.setOnReady(() -> mediaPlayer.play());
                    mediaView.setOnMouseClicked(e -> {
                        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.play();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private void hideVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        containerNote.getChildren().setAll(originalNodes); // restauramos layout inicial

        // Eliminamos el archivo de video si existe
        if (downloadedVideoFile != null && downloadedVideoFile.exists()) {
            boolean deleted = downloadedVideoFile.delete();
            System.out.println("Archivo de video eliminado: " + deleted);
            downloadedVideoFile = null;
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
