package com.dm2.tabla.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dm2.tabla.assets.I18n;
import com.dm2.tabla.assets.LoggerConfig;
import com.dm2.tabla.assets.Persona;
import com.dm2.tabla.conexiones_DB.ConexionDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador principal de la vista FXML que muestra una tabla de personas.
 * <p>
 * Permite realizar las operaciones básicas de un CRUD (Crear, Leer, Borrar)
 * sobre la tabla {@code personas} de la base de datos, además de una
 * operación de "deshacer" el último borrado mediante el botón restaurar.
 * </p>
 * <p>
 * Toda la actividad relevante (inicio, carga de datos, inserciones, borrados,
 * restauraciones, validaciones y errores) se registra mediante un
 * {@link Logger} que escribe en el archivo {@code aplicacion.log}.
 * </p>
 * <p>
 * Los textos mostrados al usuario (tooltips, alertas) se obtienen del
 * {@link ResourceBundle} mediante {@link I18n}, por lo que se traducen
 * automáticamente según la locale del sistema.
 * </p>
 * <p>
 * La fecha de nacimiento no puede ser posterior al día actual: el
 * {@link DatePicker} deshabilita las celdas futuras.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public class HelloController implements Initializable {

    /** Logger del controlador. */
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getName());

    /** Botón para añadir una nueva persona. */
    @FXML
    private Button bt_add;

    /** Botón para eliminar la persona seleccionada en la tabla. */
    @FXML
    private Button bt_eliminar;

    /** Botón para restaurar la última persona eliminada. */
    @FXML
    private Button bt_restaurar;

    /** Selector de fecha para la fecha de nacimiento de la nueva persona. */
    @FXML
    private DatePicker f_nac;

    /** Tabla que muestra la lista de personas. */
    @FXML
    private TableView<Persona> tabla;

    /** Columna de la tabla correspondiente al identificador. */
    @FXML
    private TableColumn<Persona, Integer> tb_id;

    /** Columna de la tabla correspondiente al nombre. */
    @FXML
    private TableColumn<Persona, String> tb_nom;

    /** Columna de la tabla correspondiente al apellido. */
    @FXML
    private TableColumn<Persona, String> tb_ap;

    /** Columna de la tabla correspondiente a la fecha de nacimiento. */
    @FXML
    private TableColumn<Persona, Date> tb_f_nac;

    /** Campo de texto para introducir el apellido. */
    @FXML
    private TextField tf_apellido;

    /** Campo de texto para introducir el nombre. */
    @FXML
    private TextField tf_nombre;

    /** Lista observable que alimenta la tabla. */
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    /** Pila de personas eliminadas para poder restaurarlas. */
    private final List<Persona> eliminadas = new ArrayList<>();

    /**
     * Inicializa el controlador tras cargar el archivo FXML.
     *
     * @param url            ubicación usada para resolver rutas relativas
     * @param resourceBundle recursos para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Primero configuramos el logger para que todo lo demás quede registrado
        LoggerConfig.configurar();
        LOGGER.info("Inicializando HelloController...");

        tabla.setItems(personas);

        tb_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tb_nom.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tb_ap.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        tb_f_nac.setCellValueFactory(new PropertyValueFactory<>("f_nac"));

        tb_f_nac.setCellFactory(col -> new TableCell<Persona, Date>() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : sdf.format(item));
            }
        });

        // -----------------------------------------------------------
        // RESTRICCIÓN DE FECHA: no permitir fechas posteriores a hoy
        // -----------------------------------------------------------
        f_nac.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        // -----------------------------------------------------------
        // TOOLTIPS en botones y otros controles (traducidos con I18n)
        // -----------------------------------------------------------
        bt_add.setTooltip(new Tooltip(I18n.get("tooltip.add")));
        bt_eliminar.setTooltip(new Tooltip(I18n.get("tooltip.delete")));
        bt_restaurar.setTooltip(new Tooltip(I18n.get("tooltip.restore")));
        f_nac.setTooltip(new Tooltip(I18n.get("tooltip.birthdate")));
        tf_nombre.setTooltip(new Tooltip(I18n.get("tooltip.name")));
        tf_apellido.setTooltip(new Tooltip(I18n.get("tooltip.surname")));
        tabla.setTooltip(new Tooltip(I18n.get("tooltip.table")));

        // Manejadores
        bt_add.setOnAction(e -> anadirPersona());
        bt_eliminar.setOnAction(e -> eliminarPersona());
        bt_restaurar.setOnAction(e -> restaurarPersona());

        cargarPersonas();

        LOGGER.info("HelloController inicializado correctamente.");
    }

    /**
     * Lee todas las personas de la base de datos y las añade a la lista
     * observable {@link #personas}.
     */
    private void cargarPersonas() {
        LOGGER.info("Cargando personas desde la base de datos...");
        String sql = "SELECT id, nombre, apellido, f_nac FROM personas ORDER BY id";
        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int contador = 0;
            while (rs.next()) {
                personas.add(new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("f_nac")
                ));
                contador++;
            }
            LOGGER.info("Carga completada. Personas cargadas: " + contador);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la lista de personas", e);
            mostrarError(I18n.get("alert.error.load"), e);
        }
    }

    /**
     * Añade una nueva persona a la base de datos y a la tabla.
     */
    private void anadirPersona() {
        String nombre = tf_nombre.getText().trim();
        String apellido = tf_apellido.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || f_nac.getValue() == null) {
            LOGGER.warning("Intento de añadir persona con campos vacíos o fecha nula.");
            new Alert(Alert.AlertType.WARNING,
                    I18n.get("alert.missing.fields")).showAndWait();
            return;
        }

        Date fecha = Date.from(f_nac.getValue()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        LOGGER.info("Insertando nueva persona: " + nombre + " " + apellido);

        String sql = "INSERT INTO personas (nombre, apellido, f_nac) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setDate(3, new java.sql.Date(fecha.getTime()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;

            personas.add(new Persona(id, nombre, apellido, fecha));
            LOGGER.info("Persona insertada correctamente. ID asignado: " + id);

            tf_nombre.clear();
            tf_apellido.clear();
            f_nac.setValue(null);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar la persona: " + nombre + " " + apellido, e);
            mostrarError(I18n.get("alert.error.insert"), e);
        }
    }

    /**
     * Elimina de la base de datos y de la tabla la persona seleccionada.
     */
    private void eliminarPersona() {
        Persona seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            LOGGER.warning("Intento de eliminar sin fila seleccionada.");
            new Alert(Alert.AlertType.WARNING,
                    I18n.get("alert.select.row")).showAndWait();
            return;
        }

        LOGGER.info("Eliminando persona con ID: " + seleccionada.getId());

        String sql = "DELETE FROM personas WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seleccionada.getId());
            ps.executeUpdate();

            eliminadas.add(seleccionada);
            personas.remove(seleccionada);
            LOGGER.info("Persona eliminada correctamente. ID: " + seleccionada.getId()
                    + ", pendientes de restaurar: " + eliminadas.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la persona con ID "
                    + seleccionada.getId(), e);
            mostrarError(I18n.get("alert.error.delete"), e);
        }
    }

    /**
     * Restaura la última persona eliminada volviéndola a insertar.
     */
    private void restaurarPersona() {
        if (eliminadas.isEmpty()) {
            LOGGER.warning("Intento de restaurar sin personas eliminadas.");
            return;
        }

        Persona p = eliminadas.get(eliminadas.size() - 1);
        LOGGER.info("Restaurando persona: " + p.getNombre() + " " + p.getApellido());

        String sql = "INSERT INTO personas (nombre, apellido, f_nac) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setDate(3, new java.sql.Date(p.getF_nac().getTime()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                p.setId(keys.getInt(1));
            }

            eliminadas.remove(eliminadas.size() - 1);
            personas.add(p);
            LOGGER.info("Persona restaurada correctamente con nuevo ID: " + p.getId());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al restaurar la persona", e);
            mostrarError(I18n.get("alert.error.restore"), e);
        }
    }

    /**
     * Muestra un diálogo de alerta con un mensaje de error.
     *
     * @param mensaje texto descriptivo del error producido
     * @param e       excepción SQL que se ha producido
     */
    private void mostrarError(String mensaje, SQLException e) {
        LOGGER.log(Level.SEVERE, mensaje, e);
        new Alert(Alert.AlertType.ERROR, mensaje + ": " + e.getMessage()).showAndWait();
    }
}