package com.example.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @FXML
    private Label loadLabel;

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

    private File extractResourceToTemp(String resourcePath) throws IOException {
        // resourcePath ejemplo: "/lib/yt-dlp.exe"
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) throw new IOException("No se encontró el recurso: " + resourcePath);

            File tempFile = File.createTempFile(resourcePath.replaceAll("/", "_"), null);
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Hacer ejecutable en sistemas tipo Unix/Mac
            tempFile.setExecutable(true);

            return tempFile;
        }
    }

    private File extractFolderToTemp(String resourceFolder) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "ffmpeg_" + System.currentTimeMillis());
        tempDir.mkdirs();

        String[] resources = {"ffmpeg.exe", "ffprobe.exe"};
        for (String res : resources) {
            try (InputStream in = getClass().getResourceAsStream(resourceFolder + "/" + res)) {
                if (in == null) throw new IOException("No se encontró el recurso: " + res);
                File tempFile = new File(tempDir, res);
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
                tempFile.setExecutable(true);
            }
        }
        return tempDir;
    }

    private File downloadMP4(String youtubeUrl) {
        try {
            File ytDlpExe = extractResourceToTemp("/lib/yt-dlp.exe");
            File ffmpegDir = extractFolderToTemp("/lib/ffmpeg/bin");

            File outputFile = new File(System.getProperty("java.io.tmpdir"),
                    "youtube_video_" + System.currentTimeMillis() + ".mp4");

            ProcessBuilder pb = new ProcessBuilder(
                ytDlpExe.getAbsolutePath(),
                "-f", "bestvideo[height<=720][vcodec^=avc1]+bestaudio[ext=m4a]/best[height<=720][vcodec^=avc1]",
                "--merge-output-format", "mp4",
                "--ffmpeg-location", ffmpegDir.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                youtubeUrl
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String message = line;
                    System.out.println(message);
                    
                    Platform.runLater(() -> {
                        String currentText = loadLabel.getText();
                        loadLabel.setText(currentText + message + "\n");
                    });
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

        // Vaciar el label antes de empezar
        Platform.runLater(() -> loadLabel.setText("Descargando video...\n"));

        containerNote.getChildren().removeIf(node -> node instanceof MediaView);

        new Thread(() -> {
            File videoFile = downloadMP4(youtubeUrl); // siempre nuevo
            if (videoFile == null) {
                System.out.println("No se pudo descargar el video.");
                Platform.runLater(() -> loadLabel.setText("Error descargando el video."));
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

                    //Crear nuevo MediaPlayer
                    mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);
                    mediaView.fitWidthProperty().bind(containerNote.widthProperty());
                    mediaView.fitHeightProperty().bind(containerNote.heightProperty().multiply(0.8)); // 80% del alto del VBox
                    mediaView.setPreserveRatio(true);

                    //Crear Slider MediaPlayer
                    javafx.scene.control.Slider videoSlider = new javafx.scene.control.Slider();
                    videoSlider.prefWidthProperty().bind(containerNote.widthProperty());
                    videoSlider.setMin(0);
                    videoSlider.setValue(0);
                    videoSlider.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                    videoSlider.getStyleClass().add("video-slider");

                    containerNote.getChildren().add(mediaView);
                    containerNote.getChildren().add(videoSlider);

                    mediaPlayer.setOnReady(() -> {
                        mediaPlayer.play();
                        videoSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                    });

                    mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                        if (!videoSlider.isValueChanging()) {
                            videoSlider.setValue(newTime.toSeconds());
                        }
                    });

                    // Permite arrastrar o hacer clic en el slider para adelantar o retroceder
                    videoSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                        if (!isChanging) {
                            mediaPlayer.seek(javafx.util.Duration.seconds(videoSlider.getValue()));
                        }
                    });
                    videoSlider.setOnMousePressed(event -> mediaPlayer.pause());
                    videoSlider.setOnMouseReleased(event -> {
                        mediaPlayer.seek(javafx.util.Duration.seconds(videoSlider.getValue()));
                        mediaPlayer.play();
                    });

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
        containerNote.getChildren().setAll(originalNodes);

        // Limpiamos el Label cuando se quita el link
        loadLabel.setText("");

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
