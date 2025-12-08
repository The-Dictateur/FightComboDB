package com.example.controller;

import com.example.service.CharService;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;

@Component
public class ControllerChars {

    private Controller controller;

    @FXML
    private Tab tabChar;

    @FXML
    private Tab tabFav;

    @FXML
    private FlowPane flowPaneChar;

    @FXML
    private FlowPane flowPaneFav;

    @FXML
    private ScrollPane scrollChar;

    @FXML
    private ScrollPane  scrollFav;

    @Autowired
    private CharService charService;

    public void initialize() {
        System.out.println("ControllerChars initialized");
    }

    public void setController(Controller controller) {
            this.controller = controller;
    }

    public void showChars() {
        System.out.println("Showing characters...");

        scrollChar.setFitToHeight(false);
        scrollChar.setFitToWidth(true);

        scrollFav.setFitToHeight(false);
        scrollFav.setFitToWidth(true);

        flowPaneChar.getChildren().clear();
        flowPaneChar.setAlignment(javafx.geometry.Pos.CENTER);
        flowPaneChar.setHgap(10);
        flowPaneChar.setVgap(10);
        
        flowPaneFav.getChildren().clear();
        flowPaneFav.setAlignment(javafx.geometry.Pos.CENTER);
        flowPaneFav.setHgap(10);
        flowPaneFav.setVgap(10);

        System.out.println("Juego selecciondao: " + controller.getCombo_game().getValue());

        charService.obtenerPersonajePorJuego(controller.getCombo_game().getValue()).forEach(character -> {
            // Crear el botón
            Button botonChar = new Button();
            botonChar.setPrefWidth(100);
            botonChar.setStyle("-fx-background-color: transparent;");

            try {
                String ruta = "/logos/" + character.getJuego() + "/" + character.getIcon();
                java.net.URL url = getClass().getResource(ruta);

                if (url == null) {
                    System.err.println("No se encontró el logo: " + ruta);
                } else {
                    javafx.scene.image.Image imagen = new javafx.scene.image.Image(url.toExternalForm());
                    javafx.scene.image.ImageView imagenView = new javafx.scene.image.ImageView(imagen);

                    imagenView.setFitWidth(80);   // Ajusta el tamaño según necesites
                    imagenView.setFitHeight(80);
                    imagenView.setPreserveRatio(true);

                    Rectangle clip = new Rectangle(80, 80);
                    clip.setArcWidth(20);  // curvatura horizontal
                    clip.setArcHeight(20); // curvatura vertical
                    imagenView.setClip(clip);

                    botonChar.setGraphic(imagenView); // ← Añade la imagen al botón
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            botonChar.setOnAction(event -> {
                System.out.println(character.getNombre());
            });
            flowPaneChar.getChildren().add(botonChar);
        });

        charService.obtenerPersonajeFavorito().forEach(character -> {

            Button botonFav = new Button();
            botonFav.setPrefWidth(100);
            botonFav.setStyle("-fx-background-color: transparent;");

            try {
                String ruta = "/logos/" + character.getJuego() + "/" + character.getIcon();
                java.net.URL url = getClass().getResource(ruta);

                if (url == null) {
                    System.err.println("No se encontró el logo: " + ruta);
                } else {
                    javafx.scene.image.Image imagen = new javafx.scene.image.Image(url.toExternalForm());
                    javafx.scene.image.ImageView imagenView = new javafx.scene.image.ImageView(imagen);

                    imagenView.setFitWidth(80);   // Ajusta el tamaño según necesites
                    imagenView.setFitHeight(80);
                    imagenView.setPreserveRatio(true);

                    Rectangle clip = new Rectangle(80, 80);
                    clip.setArcWidth(20);  // curvatura horizontal
                    clip.setArcHeight(20); // curvatura vertical
                    imagenView.setClip(clip);

                    botonFav.setGraphic(imagenView); // ← Añade la imagen al botón
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            botonFav.setOnAction(event -> {
                System.out.println(character.getNombre());
            });
            flowPaneFav.getChildren().add(botonFav);
        });
    }
    
}
