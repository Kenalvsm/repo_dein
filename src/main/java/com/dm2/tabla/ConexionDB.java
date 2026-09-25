package com.dm2.tabla;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ConexionDB {

    private static final String FICH_CONFIG = ".env";

    private static final Map<String, String> config = new HashMap<>();
    private static final String URL;

    static {
        cargarConfig();
        String host = config.getOrDefault("DB_HOST", "localhost");
        String port = config.getOrDefault("DB_PORT", "3306");
        String db   = config.getOrDefault("MARIADB_DATABASE", "TableViewDB");
        URL = "jdbc:mariadb://" + host + ":" + port + "/" + db;
    }

    private ConexionDB() { } // no instanciable

    private static void cargarConfig() {
        try (BufferedReader br = new BufferedReader(new FileReader(FICH_CONFIG))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                int igual = linea.indexOf('=');
                if (igual == -1) continue;
                String clave = linea.substring(0, igual).trim();
                String valor = linea.substring(igual + 1).trim();
                config.put(clave, valor);
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo " + FICH_CONFIG + ": " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String user = config.getOrDefault("MARIADB_USER", "root");
        String pass = config.getOrDefault("MARIADB_PASSWORD", "root");
        return DriverManager.getConnection(URL, user, pass);
    }
}