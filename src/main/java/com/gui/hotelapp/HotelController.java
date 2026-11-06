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
    @FXML private Button btnBuscar;

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
                lblResultado.getStyleClass().removeAll("estado-ok", "estado-error", "estado-aviso");
                lblResultado.setText("Introduce un número de habitación.");
                txtNumero.requestFocus();
            }
        });
    }

    @FXML
    private void buscar() {
        String texto = txtNumero.getText().trim();
        if (texto.isEmpty()) {
            mostrar("⚠ Escribe un número de habitación.", "estado-aviso");
            return;
        }
        int numero;
        try {
            numero = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            mostrar("✖ «" + texto + "» no es un número válido. Escribe solo cifras (ej. 112).", "estado-error");
            return;
        }

        Habitacion h = habitaciones.get(numero);
        if (h == null) {
            mostrar("✖ La habitación " + numero + " no existe.", "estado-error");
            return;
        }
        switch (h.estado()) {
            case "disponible"    -> mostrar("✔ Habitación " + numero + ": DISPONIBLE\n" + detalle(h), "estado-ok");
            case "ocupada"       -> mostrar("✖ Habitación " + numero + ": OCUPADA\n" + detalle(h), "estado-error");
            case "mantenimiento" -> mostrar("⚠ Habitación " + numero + ": en MANTENIMIENTO\n" + detalle(h), "estado-aviso");
            default              -> mostrar("Habitación " + numero + ": estado desconocido (" + h.estado() + ")", null);
        }
        tablaHabitaciones.getSelectionModel().select(h);
        tablaHabitaciones.scrollTo(h);
    }

    /** Línea de detalle de la habitación (tipo, precio, capacidad y planta). */
    private static String detalle(Habitacion h) {
        double precio = h.precioNoche();
        String importe = precio == Math.floor(precio) ? String.valueOf((long) precio) : String.valueOf(precio);
        return "Tipo: " + h.tipo() + " · Precio: " + importe + " €/noche · Capacidad: "
                + h.capacidad() + (h.capacidad() == 1 ? " persona" : " personas")
                + " · Planta " + h.planta();
    }

    private void mostrar(String texto, String claseEstado) {
        lblResultado.setText(texto);
        lblResultado.getStyleClass().removeAll("estado-ok", "estado-error", "estado-aviso");
        if (claseEstado != null) lblResultado.getStyleClass().add(claseEstado);
        Voz.leer(texto);
    }

    /** Activa o desactiva el botón por defecto (Enter) según la pestaña visible. */
    public void setPantallaActiva(boolean activa) {
        btnBuscar.setDefaultButton(activa);
    }

    /** Lleva el foco al campo de búsqueda. */
    public void enfocar() {
        javafx.application.Platform.runLater(txtNumero::requestFocus);
    }
}