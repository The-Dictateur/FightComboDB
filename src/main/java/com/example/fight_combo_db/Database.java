package com.example.fight_combo_db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Database {
    public static String updateDatabase() {
        String appData = System.getenv("APPDATA");
        String folderPath = appData + "\\FightComboDB";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        String dbPath = folderPath + "\\fightcombo.db";
        File dbFile = new File(dbPath);

        // Si existe, eliminarlo
        if (dbFile.exists()) dbFile.delete();

        // Copiar el fightcombo.db desde resources
        try (InputStream is = Database.class.getResourceAsStream("/fightcombo.db");
             FileOutputStream fos = new FileOutputStream(dbFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error recreando la base de datos", e);
        }

        return dbPath;
    }
}
