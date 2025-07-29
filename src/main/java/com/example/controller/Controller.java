package com.example.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.Juego;
import com.example.model.Personaje;
import com.example.service.CharService;
import com.example.service.GameService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class Controller {

    @FXML
    private ComboBox<String> combo_game;

    @FXML
    private ComboBox<String> combo_char;

    @FXML
    private StackPane stackPaneGame;

    @FXML
    private StackPane stackPaneChar;

    @FXML
    private Button buttonNewEntry;

    @Autowired
    private GameService gameService;

    @Autowired
    private CharService charService;

    public void initialize() {
        System.out.println("Controller initialized");
        cargarJuegos();
        combo_game.setOnAction(event -> {
            stackPaneGame.getChildren().clear(); // Limpiar logo del juego
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Juego juego = gameService.obtenerTodosLosJuegos().stream()
                .filter(j -> j.getNombre().equals(selectedGame))
                .findFirst()
                .orElse(null);
            mostrarLogoJuego(juego);
            combo_char.getItems().clear(); // Limpiar personajes al cambiar juego
            stackPaneChar.getChildren().clear(); // Limpiar logo del personaje
            cargarPersonajes();
            combo_char.getSelectionModel().clearSelection(); // Limpiar selección de personaje
        });

        combo_char.setOnAction(event -> {
            String selectedChar = combo_char.getSelectionModel().getSelectedItem();
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Personaje personaje = charService.obtenerPersonajePorNombreYJuego(selectedChar, selectedGame);
            mostrarLogoPersonaje(personaje);
        });

        buttonNewEntry.setOnAction(event -> {
            System.out.println("Botón Nueva Entrada presionado");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Note.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("FightComboDB");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
            stage.showAndWait(); // Espera hasta que se cierre
        });
    }

    public void cargarJuegos() {
        combo_game.getItems().clear();
        gameService.obtenerTodosLosJuegos().forEach(juego -> combo_game.getItems().add(juego.getNombre()));
    }

    public void cargarPersonajes() {
        combo_char.getItems().clear();
        String selectedGame = combo_game.getSelectionModel().getSelectedItem();
        charService.obtenerPersonajePorJuego(selectedGame).forEach(personaje -> combo_char.getItems().add(personaje.getNombre()));
    }

    public void mostrarLogoJuego(Juego juego) {
        if (combo_game.getSelectionModel().isEmpty()) return;

        try {
            String ruta = "/logos/" + combo_game.getSelectionModel().getSelectedItem() + "/" + juego.getIcon();
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("No se encontró la imagen: " + ruta);
                return;
            }
            Image image = new Image(url.toExternalForm());

            // Crear un ImageView
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true); // Mantener proporciones
            imageView.setSmooth(true);

            // Enlazar tamaño al StackPane
            imageView.fitWidthProperty().bind(stackPaneGame.widthProperty());
            imageView.fitHeightProperty().bind(stackPaneGame.heightProperty());

            // Limpiar cualquier imagen anterior
            stackPaneGame.getChildren().clear();
            stackPaneGame.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void mostrarLogoPersonaje(Personaje personaje) {
        System.out.println("Mostrando logo del personaje: " + personaje.getNombre());
        if (personaje == null || personaje.getIcon() == null) return;

        try {
            String ruta = "/logos/" + personaje.getJuego() + "/" + personaje.getIcon();
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("No se encontró la imagen: " + ruta);
                return;
            }
            Image image = new Image(url.toExternalForm());

        // Crear un ImageView
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false); // Mantener proporciones
        imageView.setSmooth(true);

        // Enlazar tamaño al StackPane
        imageView.fitWidthProperty().bind(stackPaneChar.widthProperty());
        imageView.fitHeightProperty().bind(stackPaneChar.heightProperty());

        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(20);  // ancho del redondeo
        clip.arcHeightProperty().set(20); // alto del redondeo
        clip.widthProperty().bind(imageView.fitWidthProperty());
        clip.heightProperty().bind(imageView.fitHeightProperty());
        imageView.setClip(clip);

        // Limpiar cualquier imagen anterior
        stackPaneChar.getChildren().clear();
        stackPaneChar.getChildren().add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
