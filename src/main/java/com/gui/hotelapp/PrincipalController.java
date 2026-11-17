package com.gui.hotelapp;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
            atajo(scene, KeyCode.M, this::escucharComando);

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

    @FXML private Button btnVoz;
    @FXML private Label lblVoz;

    @FXML
    private void escucharComando() {
        if (btnVoz.isDisabled()) return;          // evita pulsaciones dobles (Ctrl+M repetido)
        btnVoz.setDisable(true);
        mostrarVoz("● Escuchando… di una orden", "estado-aviso");

        Task<String> tarea = new Task<>() {
            @Override protected String call() throws Exception {
                return ReconocedorVoz.escuchar(5);
            }
        };
        tarea.setOnSucceeded(e -> {
            btnVoz.setDisable(false);
            ejecutar(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            btnVoz.setDisable(false);
            mostrarVoz("✖ Error de voz: " + tarea.getException().getMessage(), "estado-error");
        });
        Thread hilo = new Thread(tarea, "reconocimiento-voz");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void ejecutar(String texto) {
        var cmd = InterpreteComandos.interpretar(texto);
        switch (cmd.tipo()) {
            case IR_HABITACIONES -> {
                tabs.getSelectionModel().select(tabHabitaciones);
                habitacionesController.enfocar();
                confirmar("Mostrando habitaciones");
            }
            case IR_RESERVAS -> {
                tabs.getSelectionModel().select(tabReservas);
                reservasController.enfocar();
                confirmar("Mostrando reservas");
            }
            case IR_ACCESIBILIDAD -> {
                tabs.getSelectionModel().select(tabAccesibilidad);
                accesibilidadController.enfocar();
                confirmar("Mostrando accesibilidad");
            }
            case AUMENTAR_TEXTO -> { accesibilidadController.aumentarTexto(); confirmar("Letra aumentada"); }
            case REDUCIR_TEXTO  -> { accesibilidadController.reducirTexto();  confirmar("Letra reducida"); }
            case TEXTO_NORMAL   -> { accesibilidadController.textoNormal();   confirmar("Letra normal"); }

            // Pendientes: dependen de HotelController y ReservasController
            case BUSCAR_HABITACION -> {
                tabs.getSelectionModel().select(tabHabitaciones);
                // habitacionesController.buscarPorVoz(cmd.argumento());
                confirmar("Buscar habitación " + cmd.argumento());
            }
            case HABITACIONES_DISPONIBLES -> {
                tabs.getSelectionModel().select(tabHabitaciones);
                // habitacionesController.mostrarDisponibles();
                confirmar("Habitaciones disponibles");
            }
            case BUSCAR_RESERVA -> {
                tabs.getSelectionModel().select(tabReservas);
                // reservasController.buscarPorVoz(cmd.argumento());
                confirmar("Buscar reserva de " + cmd.argumento());
            }
            default -> {
                mostrarVoz("⚠ No entendí la orden: «" + texto + "»", "estado-aviso");
                Voz.leer("No entendí la orden");
            }
        }
    }

    private void confirmar(String mensaje) {
        mostrarVoz("✔ " + mensaje, "estado-ok");
        Voz.leer(mensaje);   // solo suena si la opción de lectura en voz alta está activada
    }

    private void mostrarVoz(String texto, String estado) {
        lblVoz.setText(texto);
        lblVoz.getStyleClass().removeAll("estado-ok", "estado-error", "estado-aviso");
        lblVoz.getStyleClass().add(estado);
    }

}
