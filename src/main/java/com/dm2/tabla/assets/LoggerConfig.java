package com.dm2.tabla.assets;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Clase de utilidad para configurar el sistema de logging de la aplicación.
 * <p>
 * Configura un {@link FileHandler} que escribe todos los mensajes en el
 * archivo {@code aplicacion.log} situado en el directorio de trabajo, con un
 * formato legible y sin enviarlos también a la consola (evita duplicados).
 * </p>
 * <p>
 * El método {@link #configurar()} es idempotente: solo aplica la configuración
 * la primera vez que se invoca, de modo que puede llamarse desde cualquier
 * clase sin riesgo de duplicar handlers.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public final class LoggerConfig {

    /** Nombre del archivo donde se guardan los registros. */
    private static final String ARCHIVO_LOG = "aplicacion.log";

    /** Logger raíz de la aplicación (paquete base). */
    private static final Logger LOGGER_RAIZ = Logger.getLogger("com.dm2.tabla");

    /** Bandera para no reconfigurar el logger más de una vez. */
    private static boolean configurado = false;

    /** Constructor privado para evitar instanciación. */
    private LoggerConfig() { }

    /**
     * Configura el logger raíz para que escriba en el archivo de log.
     * <p>
     * Solo se ejecuta la primera vez que se llama; en llamadas posteriores
     * no hace nada.
     * </p>
     */
    public static synchronized void configurar() {
        if (configurado) {
            return;
        }
        try {
            // true = añadir al final del archivo (append) en lugar de sobrescribir
            FileHandler fh = new FileHandler(ARCHIVO_LOG, true);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);

            LOGGER_RAIZ.addHandler(fh);
            LOGGER_RAIZ.setLevel(Level.ALL);
            LOGGER_RAIZ.setUseParentHandlers(false); // evita duplicar en consola

            configurado = true;
            LOGGER_RAIZ.info("Sistema de logging inicializado. Archivo: " + ARCHIVO_LOG);

        } catch (IOException e) {
            // Último recurso: si no se puede crear el archivo, avisamos por consola
            System.err.println("No se pudo inicializar el logger: " + e.getMessage());
        }
    }

    /**
     * Devuelve el logger raíz de la aplicación.
     *
     * @return logger configurado
     */
    public static Logger getLogger() {
        return LOGGER_RAIZ;
    }
}