package com.example.controller;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

@Component
public class Controller {

    @FXML
    private ComboBox<String> combo_game;

    @FXML
    private ComboBox<String> combo_char;

    public void initialize() {
        // Initialize the combo boxes with some example data
        combo_game.getItems().addAll("Street Fighter 6", "Garou: Mark of the Wolves", "Guilty Gear Xrd");

        combo_game.setOnAction(event -> {
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            actualizarPersonajes(selectedGame);
        });
    }

    public void actualizarPersonajes(String game) {
        combo_char.getItems().clear();

        switch (game) {
            case "Street Fighter 6":
                combo_char.getItems().addAll("Ryu", "Chun-Li", "Luke");
                break;
            case "Garou: Mark of the Wolves":
                combo_char.getItems().addAll("Terry Bogard", "Rock Howard", "Gato");
                break;
            case "Guilty Gear Xrd":
                combo_char.getItems().addAll("Sol Badguy", "Ky Kiske", "May");
                break;
            default:
                combo_char.getItems().add("Choose a game first");
        }
    }
}
