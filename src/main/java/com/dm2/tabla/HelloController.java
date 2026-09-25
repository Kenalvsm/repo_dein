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

public class HelloController implements Initializable {

    @FXML
    private Button bt_add;

    @FXML
    private Button bt_eliminar;

    @FXML
    private Button bt_restaurar;

    @FXML
    private DatePicker f_nac;

    @FXML
    private TableView<Persona> tabla;

    @FXML
    private TableColumn<Persona, Integer> tb_id;

    @FXML
    private TableColumn<Persona, String> tb_nom;

    @FXML
    private TableColumn<Persona, String> tb_ap;

    @FXML
    private TableColumn<Persona, Date> tb_f_nac;

    @FXML
    private TextField tf_apellido;

    @FXML
    private TextField tf_nombre;

    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final List<Persona> eliminadas = new ArrayList<>();

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

    // Lee todas las personas de la BBDD y las muestra en la tabla
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

    private void anadirPersona() {
        String nombre = tf_nombre.getText().trim();
        String apellido = tf_apellido.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || f_nac.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Debes rellenar nombre, apellido y fecha de nacimiento").showAndWait();
            return;
        }

        // LocalDate (DatePicker) → java.util.Date (tipo de Persona)
        Date fecha = Date.from(f_nac.getValue()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        String sql = "INSERT INTO personas (nombre, apellido, f_nac) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setDate(3, new java.sql.Date(fecha.getTime())); // Date → java.sql.Date
            ps.executeUpdate();

            // Recuperamos el id autogenerado por la BBDD
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

    private void restaurarPersona() {
        if (eliminadas.isEmpty()) {
            return;
        }

        Persona p = eliminadas.get(eliminadas.size() - 1);

        // La volvemos a insertar en la BBDD
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

    private void mostrarError(String mensaje, SQLException e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, mensaje + ": " + e.getMessage()).showAndWait();
    }
}