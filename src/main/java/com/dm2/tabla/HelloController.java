package com.dm2.tabla;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private Button bt_add;

    @FXML
    private Button bt_eliminar;

    @FXML
    private Button bt_restaurar;

    @FXML
    private DatePicker f_nac;

    @FXML
    private TableView<?> tabla;

    @FXML
    private TableColumn<?, ?> tb_ap;

    @FXML
    private TableColumn<?, ?> tb_f_nac;

    @FXML
    private TableColumn<?, ?> tb_id;

    @FXML
    private TableColumn<?, ?> tb_nom;

    @FXML
    private TextField tf_apellido;

    @FXML
    private TextField tf_nombre;



}
