package com.dm2.tabla;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Aplicación principal JavaFX.
 * <p>
 * Carga la vista FXML inyectando el {@link ResourceBundle} de mensajes para
 * que todos los textos sean traducibles automáticamente según la locale del
 * sistema.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public class HelloApplication extends Application {

    /** Ruta base del bundle de mensajes. */
    private static final String BUNDLE_BASE = "com.dm2.tabla.i18n.messages";

    @Override
    public void start(Stage stage) throws IOException {
        // Cargamos el bundle según la locale del sistema
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE);

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("hello-view.fxml"));
        fxmlLoader.setResources(bundle); // 👈 clave para que FXML resuelva %clave

        Scene scene = new Scene(fxmlLoader.load());

        // Título de la ventana traducido
        stage.setTitle(bundle.getString("app.title"));
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(400);

        stage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("img/icono.png"))
        ));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}