package com.gui.hotelapp;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Opciones de accesibilidad: tamaño de letra, tema (claro, oscuro y alto contraste),
 * foco reforzado con atajos visibles y lectura en voz alta.
 */
public class AccesibilidadController implements Initializable {

    private enum Tema { CLARO, OSCURO, ALTO_CONTRASTE }

    private static final int[] TAMANOS = {12, 14, 16, 18, 20, 22};
    private static final int INDICE_NORMAL = 1;

    @FXML private Button btnMenos, btnNormal, btnMas;
    @FXML private Label lblTamano;
    @FXML private ToggleGroup grupoTema;
    @FXML private RadioButton rbClaro, rbOscuro, rbContraste;
    @FXML private ToggleSwitch swTeclado, swVoz;

    private Scene escena;
    private int indice = INDICE_NORMAL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rbClaro.setUserData(Tema.CLARO);
        rbOscuro.setUserData(Tema.OSCURO);
        rbContraste.setUserData(Tema.ALTO_CONTRASTE);

        grupoTema.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            if (ahora == null) {
                // Un grupo de radios no debe quedarse sin selección
                antes.setSelected(true);
                return;
            }
            aplicarTema();
            Voz.leer("Tema " + ((RadioButton) ahora).getText());
        });
        swTeclado.selectedProperty().addListener((obs, antes, ahora) -> aplicarFoco());
        swVoz.selectedProperty().addListener((obs, antes, ahora) -> {
            Voz.setActiva(ahora);
            if (ahora) Voz.leer("Lectura en voz alta activada");
        });
        actualizarTamano();
    }

    /** La ventana principal entrega la escena cuando ya existe y se aplican los valores iniciales. */
    public void iniciar(Scene scene) {
        this.escena = scene;
        aplicarTema();
        aplicarTamano();
        aplicarFoco();
    }

    /** Permite enlazar otros elementos (p. ej. la barra de atajos) con el interruptor de teclado. */
    public BooleanProperty tecladoActivoProperty() {
        return swTeclado.selectedProperty();
    }

    public void enfocar() {
        javafx.application.Platform.runLater(btnNormal::requestFocus);
    }

    // ------------------------------------------------------------ tamaño de letra

    @FXML
    public void reducirTexto() {
        cambiarTamano(indice - 1);
    }

    @FXML
    public void textoNormal() {
        cambiarTamano(INDICE_NORMAL);
    }

    @FXML
    public void aumentarTexto() {
        cambiarTamano(indice + 1);
    }

    private void cambiarTamano(int nuevoIndice) {
        indice = Math.max(0, Math.min(TAMANOS.length - 1, nuevoIndice));
        aplicarTamano();
        actualizarTamano();
        Voz.leer("Tamaño de letra " + TAMANOS[indice] + " píxeles");
    }

    private void aplicarTamano() {
        if (escena == null) return;
        // Todos los tamaños del CSS están en em, así que cambian a la vez
        escena.getRoot().setStyle("-fx-font-size: " + TAMANOS[indice] + "px;");
    }

    private void actualizarTamano() {
        int px = TAMANOS[indice];
        int porcentaje = Math.round(px * 100f / TAMANOS[INDICE_NORMAL]);
        lblTamano.setText("Tamaño actual: " + px + " px (" + porcentaje + " %)");
        btnMenos.setDisable(indice == 0);
        btnMas.setDisable(indice == TAMANOS.length - 1);
    }

    // ------------------------------------------------------------ tema y foco

    private Tema temaSeleccionado() {
        return (Tema) grupoTema.getSelectedToggle().getUserData();
    }

    private void aplicarTema() {
        if (escena == null) return;
        Tema tema = temaSeleccionado();

        String hoja = tema == Tema.CLARO
                ? new PrimerLight().getUserAgentStylesheet()
                : new PrimerDark().getUserAgentStylesheet();
        Application.setUserAgentStylesheet(hoja);

        var clases = escena.getRoot().getStyleClass();
        clases.removeAll("tema-oscuro", "tema-alto-contraste");
        if (tema == Tema.OSCURO) clases.add("tema-oscuro");
        if (tema == Tema.ALTO_CONTRASTE) clases.add("tema-alto-contraste");
    }

    private void aplicarFoco() {
        if (escena == null) return;
        var clases = escena.getRoot().getStyleClass();
        clases.remove("foco-reforzado");
        if (swTeclado.isSelected()) clases.add("foco-reforzado");
    }
}
