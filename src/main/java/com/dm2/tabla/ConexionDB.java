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

/**
 * Clase utilitaria encargada de gestionar la conexión con la base de datos
 * MariaDB/MySQL utilizada por la aplicación.
 * <p>
 * La configuración de la conexión (host, puerto, base de datos, usuario y
 * contraseña) se lee desde el archivo {@code db.properties} ubicado en
 * {@code /com/dm2/tabla/}. Si alguna clave no se encuentra en dicho archivo
 * se utilizan valores por defecto.
 * </p>
 * <p>
 * Esta clase no puede instanciarse; todos sus métodos son estáticos.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public class ConexionDB {

    /**
     * Ruta del archivo de propiedades con la configuración de la BBDD.
     */
    private static final String FICH_CONFIG = "/com/dm2/tabla/db.properties";

    /**
     * Mapa que almacena los pares clave-valor leídos del archivo de
     * configuración.
     */
    private static final Map<String, String> config = new HashMap<>();

    /**
     * URL JDBC construida a partir de la configuración. Tiene el formato
     * {@code jdbc:mariadb://host:puerto/baseDeDatos}.
     */
    private static final String URL;

    /*
     * Bloque estático de inicialización: carga la configuración y construye
     * la URL de conexión.
     */
    static {
        cargarConfig();
        String host = config.getOrDefault("DB_HOST", "localhost");
        String port = config.getOrDefault("DB_PORT", "3306");
        String db   = config.getOrDefault("MARIADB_DATABASE", "TableViewDB");
        URL = "jdbc:mariadb://" + host + ":" + port + "/" + db;
    }

    /**
     * Constructor privado para evitar que la clase sea instanciada.
     */
    private ConexionDB() { }

    /**
     * Lee el archivo de configuración {@code db.properties} y almacena sus
     * pares clave-valor en el mapa {@link #config}.
     * <p>
     * Se ignoran las líneas vacías y aquellas que comienzan por {@code #}
     * (comentarios). Si ocurre un error de E/S se imprime un mensaje por
     * la salida de error estándar.
     * </p>
     */
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

    /**
     * Obtiene una nueva conexión a la base de datos utilizando los datos
     * de configuración cargados.
     * <p>
     * El usuario y la contraseña se obtienen de las claves
     * {@code MARIADB_USER} y {@code MARIADB_PASSWORD} del archivo de
     * configuración. Si no existen, se usan {@code "root"} y {@code "root"}
     * respectivamente.
     * </p>
     *
     * @return una {@link Connection} activa contra la base de datos
     * @throws SQLException si no se puede establecer la conexión
     */
    public static Connection getConnection() throws SQLException {
        String user = config.getOrDefault("MARIADB_USER", "root");
        String pass = config.getOrDefault("MARIADB_PASSWORD", "root");
        return DriverManager.getConnection(URL, user, pass);
    }
}