package com.gui.hotelapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Ventana principal: navegación por pestañas entre habitaciones, reservas y
 * opciones de accesibilidad.
 */
public class PrincipalController implements Initializable {

    @FXML private TabPane tabs;
    @FXML private Tab tabHabitaciones, tabReservas, tabAccesibilidad;
    @FXML private Label lblAtajos;

    // Inyectados por fx:include (fx:id + "Controller")
    @FXML private HotelController habitacionesController;
    @FXML private ReservasController reservasController;
    @FXML private AccesibilidadController accesibilidadController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabs.getSelectionModel().selectedItemProperty()
                .addListener((obs, antes, ahora) -> actualizarPantallaActiva());
        actualizarPantallaActiva();

        // La barra de atajos solo se ve con la opción "Navegación por teclado"
        lblAtajos.visibleProperty().bind(accesibilidadController.tecladoActivoProperty());
        lblAtajos.managedProperty().bind(accesibilidadController.tecladoActivoProperty());

        tabs.sceneProperty().addListener((obs, antes, scene) -> {
            if (scene == null) return;
            accesibilidadController.iniciar(scene);

            // Ctrl+1, Ctrl+2 y Ctrl+3 cambian de pestaña y llevan el foco al primer control
            atajo(scene, KeyCode.DIGIT1, () -> {
                tabs.getSelectionModel().select(tabHabitaciones);
                habitacionesController.enfocar();
            });
            atajo(scene, KeyCode.DIGIT2, () -> {
                tabs.getSelectionModel().select(tabReservas);
                reservasController.enfocar();
            });
            atajo(scene, KeyCode.DIGIT3, () -> {
                tabs.getSelectionModel().select(tabAccesibilidad);
                accesibilidadController.enfocar();
            });

            // Ctrl + / Ctrl - / Ctrl 0: tamaño de letra desde cualquier pantalla
            for (KeyCode mas : new KeyCode[]{KeyCode.PLUS, KeyCode.EQUALS, KeyCode.ADD}) {
                atajo(scene, mas, accesibilidadController::aumentarTexto);
            }
            for (KeyCode menos : new KeyCode[]{KeyCode.MINUS, KeyCode.SUBTRACT}) {
                atajo(scene, menos, accesibilidadController::reducirTexto);
            }
            atajo(scene, KeyCode.DIGIT0, accesibilidadController::textoNormal);
        });
    }

    private static void atajo(javafx.scene.Scene scene, KeyCode tecla, Runnable accion) {
        scene.getAccelerators().put(new KeyCodeCombination(tecla, KeyCombination.CONTROL_DOWN), accion);
    }

    /**
     * Solo la pantalla visible tiene botón por defecto (Enter); si los dos botones
     * lo tuvieran a la vez, Enter podría pulsar el de la pantalla oculta.
     */
    private void actualizarPantallaActiva() {
        Tab activa = tabs.getSelectionModel().getSelectedItem();
        habitacionesController.setPantallaActiva(activa == tabHabitaciones);
        reservasController.setPantallaActiva(activa == tabReservas);
    }
}
