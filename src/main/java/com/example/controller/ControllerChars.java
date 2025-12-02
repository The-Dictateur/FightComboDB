package com.example.controller;

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

    public void initialize() {
        System.out.println("ControllerChars initialized");
    }

    public void setController(Controller controller) {
            this.controller = controller;
    }

    public void showChars() {
        System.out.println("Showing characters...");

        flowPaneChar.getChildren().clear();
        flowPaneChar.setAlignment(javafx.geometry.Pos.CENTER);
        flowPaneChar.setHgap(10);
        flowPaneChar.setVgap(10);
        
        flowPaneFav.getChildren().clear();
        flowPaneFav.setAlignment(javafx.geometry.Pos.CENTER);
        flowPaneFav.setHgap(10);
        flowPaneFav.setVgap(10);

        System.out.println("Juego selecciondao: " + controller.getCombo_game().getValue());
    }
    
}
