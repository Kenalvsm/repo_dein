package com.dm2.tabla;

import java.net.URL;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import java.text.SimpleDateFormat;

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
    private int contadorId = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Asignamos la lista de personas a la tabla
        tabla.setItems(personas);

        // Decimos a cada columna de qué propiedad de Persona coge el valor
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
    }

    private void anadirPersona() {
        String nombre = tf_nombre.getText().trim();
        String apellido = tf_apellido.getText().trim();

        // Validación básica
        if (nombre.isEmpty() || apellido.isEmpty() || f_nac.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Debes rellenar nombre, apellido y fecha de nacimiento").showAndWait();
            return;
        }

        // Convertir LocalDate (DatePicker) a java.util.Date (el tipo de Persona)
        Date fecha = Date.from(f_nac.getValue()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        Persona p = new Persona(contadorId++, nombre, apellido, fecha);
        personas.add(p); // Al añadir a la lista, la tabla se actualiza sola

        // Limpiamos los campos
        tf_nombre.clear();
        tf_apellido.clear();
        f_nac.setValue(null);
    }

    private void eliminarPersona() {
        Persona seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            eliminadas.add(seleccionada);
            personas.remove(seleccionada);
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecciona una fila para eliminar").showAndWait();
        }
    }

    private void restaurarPersona() {
        if (!eliminadas.isEmpty()) {
            personas.add(eliminadas.remove(eliminadas.size() - 1));
        }
    }
}
