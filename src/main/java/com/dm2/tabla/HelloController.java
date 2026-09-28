package com.dm2.tabla;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador principal de la vista FXML que muestra una tabla de personas.
 * <p>
 * Permite realizar las operaciones básicas de un CRUD (Crear, Leer, Borrar)
 * sobre la tabla {@code personas} de la base de datos, además de una
 * operación de "deshacer" el último borrado mediante el botón restaurar.
 * </p>
 * <p>
 * La tabla se rellena al inicializar el controlador consultando la BBDD y
 * se actualiza en memoria a medida que se añaden o eliminan registros.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public class HelloController implements Initializable {

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

    /**
     * Lista observable que alimenta la tabla y se mantiene sincronizada con
     * la vista.
     */
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    /**
     * Pila (implementada como {@link List}) que almacena las personas
     * eliminadas para poder restaurarlas posteriormente.
     */
    private final List<Persona> eliminadas = new ArrayList<>();

    /**
     * Inicializa el controlador tras cargar el archivo FXML.
     * <p>
     * Configura las columnas de la tabla, el formato de fecha, los manejadores
     * de los botones y carga las personas desde la base de datos.
     * </p>
     *
     * @param url            ubicación usada para resolver rutas relativas
     * @param resourceBundle recursos para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        bt_add.setOnAction(e -> anadirPersona());
        bt_eliminar.setOnAction(e -> eliminarPersona());
        bt_restaurar.setOnAction(e -> restaurarPersona());

        cargarPersonas();
    }

    /**
     * Lee todas las personas de la base de datos y las añade a la lista
     * observable {@link #personas} para que se muestren en la tabla.
     * <p>
     * En caso de error SQL se muestra un diálogo de alerta al usuario.
     * </p>
     */
    private void cargarPersonas() {
        String sql = "SELECT id, nombre, apellido, f_nac FROM personas ORDER BY id";
        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                personas.add(new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("f_nac")   // java.sql.Date hereda de java.util.Date
                ));
            }
        } catch (SQLException e) {
            mostrarError("No se pudo cargar la lista de personas", e);
        }
    }

    /**
     * Añade una nueva persona a la base de datos y a la tabla.
     * <p>
     * Valida que los campos nombre, apellido y fecha de nacimiento estén
     * rellenos. Convierte la fecha del {@link DatePicker} a {@link Date},
     * inserta el registro en la BBDD, recupera el id autogenerado y limpia
     * los campos del formulario.
     * </p>
     */
    private void anadirPersona() {
        String nombre = tf_nombre.getText().trim();
        String apellido = tf_apellido.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || f_nac.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Debes rellenar nombre, apellido y fecha de nacimiento").showAndWait();
            return;
        }

        Date fecha = Date.from(f_nac.getValue()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

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

            tf_nombre.clear();
            tf_apellido.clear();
            f_nac.setValue(null);

        } catch (SQLException e) {
            mostrarError("No se pudo insertar la persona", e);
        }
    }

    /**
     * Elimina de la base de datos y de la tabla la persona seleccionada
     * actualmente en la vista.
     * <p>
     * Antes de eliminarla definitivamente, la persona se guarda en la lista
     * {@link #eliminadas} para poder restaurarla después.
     * </p>
     */
    private void eliminarPersona() {
        Persona seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una fila para eliminar").showAndWait();
            return;
        }

        String sql = "DELETE FROM personas WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seleccionada.getId());
            ps.executeUpdate();

            eliminadas.add(seleccionada);  // la guardamos para poder restaurarla
            personas.remove(seleccionada);

        } catch (SQLException e) {
            mostrarError("No se pudo eliminar la persona", e);
        }
    }

    /**
     * Restaura la última persona eliminada volviéndola a insertar en la
     * base de datos y añadiéndola de nuevo a la tabla.
     * <p>
     * Como la BBDD genera un nuevo id autoincremental, el id original se
     * pierde y se actualiza en el objeto {@link Persona} con el nuevo valor.
     * Si la lista de eliminadas está vacía, el método no hace nada.
     * </p>
     */
    private void restaurarPersona() {
        if (eliminadas.isEmpty()) {
            return;
        }

        Persona p = eliminadas.get(eliminadas.size() - 1);

        String sql = "INSERT INTO personas (nombre, apellido, f_nac) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setDate(3, new java.sql.Date(p.getF_nac().getTime()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                p.setId(keys.getInt(1)); // la BBDD le asigna un nuevo id
            }

            eliminadas.remove(eliminadas.size() - 1);
            personas.add(p);

        } catch (SQLException e) {
            mostrarError("No se pudo restaurar la persona", e);
        }
    }

    /**
     * Muestra un diálogo de alerta con un mensaje de error y la traza de la
     * excepción SQL asociada.
     *
     * @param mensaje texto descriptivo del error producido
     * @param e       excepción SQL que se ha producido
     */
    private void mostrarError(String mensaje, SQLException e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, mensaje + ": " + e.getMessage()).showAndWait();
    }
}