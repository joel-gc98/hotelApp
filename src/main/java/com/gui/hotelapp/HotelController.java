package com.gui.hotelapp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.*;

public class HotelController implements Initializable {

    @FXML private TableView<Habitacion> tablaHabitaciones;
    @FXML private TableColumn<Habitacion, Number> colNumero, colCapacidad;
    @FXML private TableColumn<Habitacion, String> colTipo, colEstado, colPrecio;
    @FXML private TextField txtNumero;
    @FXML private Label lblResultado;

    private final Map<Integer, Habitacion> habitaciones = new HashMap<>();

    private final ValidationSupport validacion = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNumero.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().numero()));
        colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().tipo()));
        colCapacidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().capacidad()));
        colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().precioNoche() + " €"));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().estado()));

        try {
            List<Habitacion> lista = HotelRepository.cargarHabitaciones();
            for (Habitacion h : lista) habitaciones.put(h.numero(), h);
            tablaHabitaciones.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            lblResultado.setText("Error al leer reservas.xml: " + e.getMessage());
        }

        validacion.registerValidator(txtNumero, true,
                Validator.createRegexValidator("Escribe solo números (ej. 112)", "\\d{1,4}", Severity.ERROR));

        // Ctrl+F lleva el foco al campo (se registra cuando la escena ya existe)
        txtNumero.sceneProperty().addListener((obs, antes, scene) -> {
            if (scene != null) {
                scene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
                        txtNumero::requestFocus);
            }
        });

        // ESCAPE PARA LIMPIAR LA BUSQUEDA
        txtNumero.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                txtNumero.clear();
                lblResultado.setText("Introduce un número de habitación.");
                txtNumero.requestFocus();
            }
        });
    }

    @FXML
    private void buscar() {
        String texto = txtNumero.getText().trim();
        if (texto.isEmpty()) {
            lblResultado.setText("Escribe un número de habitación.");
            return;
        }
        int numero;
        try {
            numero = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            lblResultado.setText("«" + texto + "» no es un número válido.");
            return;
        }

        Habitacion h = habitaciones.get(numero);
        if (h == null) {
            lblResultado.setText("La habitación " + numero + " no existe.");
            return;
        }
        switch (h.estado()) {
            case "disponible"    -> mostrar("✔ Habitación " + numero + ": DISPONIBLE (" + h.tipo() + ", " + h.precioNoche() + " €/noche)", "estado-ok");
            case "ocupada"       -> mostrar("✖ Habitación " + numero + ": OCUPADA", "estado-error");
            case "mantenimiento" -> mostrar("⚠ Habitación " + numero + ": en MANTENIMIENTO", "estado-aviso");
            default              -> lblResultado.setText("Habitación " + numero + ": estado desconocido (" + h.estado() + ")");
        }
        tablaHabitaciones.getSelectionModel().select(h);
        tablaHabitaciones.scrollTo(h);
    }

    private void mostrar(String texto, String claseEstado) {
        lblResultado.setText(texto);
        lblResultado.getStyleClass().removeAll("estado-ok", "estado-error", "estado-aviso");
        lblResultado.getStyleClass().add(claseEstado);
    }
}