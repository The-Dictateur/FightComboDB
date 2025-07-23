package com.example.fight_combo_db;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class FightComboDbApplication extends javafx.application.Application {

	private static ConfigurableApplicationContext springContext;

	@Override
	public void init() throws Exception {
		springContext = new SpringApplicationBuilder(FightComboDbApplication.class).run();
	}

	@Override
	public void start(Stage stage) throws Exception {

		FXMLLoader fxmlLoader = new FXMLLoader(FightComboDbApplication.class.getResource("/Main_window.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);

		Parent root = fxmlLoader.load();

		stage.setTitle("Ventana JavaFX con Spring Boot");
        stage.setScene(new Scene(root));
        stage.show();

	}

	@Override
	public void stop() throws Exception {
		springContext.close();
	}
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
