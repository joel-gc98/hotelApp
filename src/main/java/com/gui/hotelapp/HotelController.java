package com.gui.hotelapp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.*;

public class HotelController implements Initializable {

    @FXML private TableView<Habitacion> tablaHabitaciones;
    @FXML private TableColumn<Habitacion, Number> colNumero, colCapacidad;
    @FXML private TableColumn<Habitacion, String> colTipo, colEstado, colPrecio;
    @FXML private TextField txtNumero;
    @FXML private Label lblResultado;

    private final Map<Integer, Habitacion> habitaciones = new HashMap<>();

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
            case "disponible"    -> lblResultado.setText("Habitación " + numero + ": DISPONIBLE (" + h.tipo() + ", " + h.precioNoche() + " €/noche)");
            case "ocupada"       -> lblResultado.setText("Habitación " + numero + ": OCUPADA");
            case "mantenimiento" -> lblResultado.setText("Habitación " + numero + ": en MANTENIMIENTO");
            default              -> lblResultado.setText("Habitación " + numero + ": estado desconocido (" + h.estado() + ")");
        }
        tablaHabitaciones.getSelectionModel().select(h);
        tablaHabitaciones.scrollTo(h);
    }
}