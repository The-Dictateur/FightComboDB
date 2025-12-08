package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.GameService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class ControllerGames {
    
    @FXML
    private VBox VBoxGames;

    @Autowired
    private GameService gameService;

    private Controller controller;

    public void initialize() {
        System.out.println("ControllerGames initialized");
        showGames();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void showGames() {
        System.out.println("Showing games...");

        VBoxGames.getChildren().clear();
        VBoxGames.setAlignment(javafx.geometry.Pos.CENTER);
        VBoxGames.setSpacing(10);

        gameService.obtenerTodosLosJuegos().forEach(juego -> {

            // Crear el botón
            Button botonJuego = new Button();
            botonJuego.setPrefWidth(200);
            botonJuego.setStyle("-fx-background-color: transparent;");

            // Cargar la imagen del juego
            try {
                String ruta = "/logos/" + juego.getNombre() + "/" + juego.getIcon();
                java.net.URL url = getClass().getResource(ruta);

                if (url == null) {
                    System.err.println("No se encontró el logo: " + ruta);
                } else {
                    javafx.scene.image.Image imagen = new javafx.scene.image.Image(url.toExternalForm());
                    javafx.scene.image.ImageView imagenView = new javafx.scene.image.ImageView(imagen);

                    imagenView.setFitWidth(140);   // Ajusta el tamaño según necesites
                    imagenView.setFitHeight(140);
                    imagenView.setPreserveRatio(true);

                    botonJuego.setGraphic(imagenView); // ← Añade la imagen al botón
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Acción del botón
            botonJuego.setOnAction(event -> {
                System.out.println("Has pulsado el juego con ID: " + juego.getId());

                Stage stage = (Stage) botonJuego.getScene().getWindow();

                if (controller != null) {
                    // Forzar selección en el combo
                    controller.combo_game.getSelectionModel().select(juego.getNombre());
                    controller.mostrarLogoJuego(juego);
                }

                stage.close();
            });

            VBoxGames.getChildren().add(botonJuego);
        });
    }
    
}