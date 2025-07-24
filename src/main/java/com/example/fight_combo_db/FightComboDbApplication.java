package com.example.fight_combo_db;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@EntityScan(basePackages = "com.example.model")

@SpringBootApplication(scanBasePackages = "com.example")
public class FightComboDbApplication extends Application {

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
