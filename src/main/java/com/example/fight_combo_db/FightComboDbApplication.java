package com.example.fight_combo_db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

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
		stage.setTitle("FightComboDB");
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

	@Bean
    public DataSource dataSource() {
        try {
            String appData = System.getenv("APPDATA");
            String folderPath = appData + "\\FightComboDB";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // crea carpeta si no existe
            }

            String dbPath = folderPath + "\\fightcombo.db";
            File dbFile = new File(dbPath);

            // Copiar la DB de resources si no existe
            if (!dbFile.exists()) {
                try (InputStream is = getClass().getResourceAsStream("/fightcombo.db");
                     FileOutputStream fos = new FileOutputStream(dbFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
            }

            // Crear DataSource apuntando a la DB en AppData
            return DataSourceBuilder.create()
                    .driverClassName("org.sqlite.JDBC")
                    .url("jdbc:sqlite:" + dbPath)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error creando DataSource", e);
        }
    }
}
