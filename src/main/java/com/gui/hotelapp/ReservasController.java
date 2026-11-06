package com.gui.hotelapp;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter LECTURA_FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final NumberFormat IMPORTE = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-ES"));
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";
    private static final List<String> CLASES_ESTADO = List.of("estado-ok", "estado-error", "estado-aviso");

    static {
        IMPORTE.setMaximumFractionDigits(2);
    }

    // Formulario
    @FXML private TextField txtNombre, txtApellidos, txtEmail, txtObservaciones;
    @FXML private ComboBox<Habitacion> cmbHabitacion;
    @FXML private DatePicker dpEntrada, dpSalida;
    @FXML private Spinner<Integer> spnPersonas;
    @FXML private Label lblNombre, lblApellidos, lblEmail, lblHabitacion,
            lblEntrada, lblSalida, lblPersonas, lblObservaciones;
    @FXML private Label lblResumen, lblResultado;
    @FXML private Button btnReservar;

    // Tabla
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colCodigo, colCliente, colEntrada, colSalida, colEstado;
    @FXML private TableColumn<Reserva, Number> colHabitacion, colPersonas;

    private final ObservableList<Reserva> reservas = FXCollections.observableArrayList();
    private final ValidationSupport validacion = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Las decoraciones de error no se muestran hasta el primer intento de reservar,
        // para no recibir al usuario con un formulario lleno de avisos rojos.
        validacion.setErrorDecorationEnabled(false);

        asociarEtiquetas();
        configurarTabla();
        cargarDatos();
        configurarFormulario();
        configurarValidacion();
        actualizarResumen();
    }

    // ---------------------------------------------------------------- configuración

    private void asociarEtiquetas() {
        lblNombre.setLabelFor(txtNombre);
        lblApellidos.setLabelFor(txtApellidos);
        lblEmail.setLabelFor(txtEmail);
        lblHabitacion.setLabelFor(cmbHabitacion);
        lblEntrada.setLabelFor(dpEntrada);
        lblSalida.setLabelFor(dpSalida);
        lblPersonas.setLabelFor(spnPersonas);
        lblObservaciones.setLabelFor(txtObservaciones);
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().codigo()));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombreCompleto()));
        colHabitacion.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().habitacion()));
        colEntrada.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_FECHA.format(c.getValue().entrada())));
        colSalida.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_FECHA.format(c.getValue().salida())));
        colPersonas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().personas()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().estado()));

        // El estado se distingue por símbolo + texto + color, nunca solo por color.
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacio) {
                super.updateItem(estado, vacio);
                getStyleClass().removeAll("celda-ok", "celda-aviso", "celda-error", "celda-neutro");
                if (vacio || estado == null) {
                    setText(null);
                    return;
                }
                setText(etiquetaEstado(estado));
                getStyleClass().add(claseCelda(estado));
            }
        });

        tablaReservas.setItems(reservas);
        tablaReservas.setPlaceholder(new Label("No hay reservas registradas."));
    }

    private void cargarDatos() {
        try {
            List<Habitacion> disponibles = HotelRepository.cargarHabitaciones().stream()
                    .filter(h -> "disponible".equals(h.estado()))
                    .toList();
            cmbHabitacion.setItems(FXCollections.observableArrayList(disponibles));
            reservas.setAll(HotelRepository.cargarReservas());
        } catch (Exception e) {
            mostrar("✖ Error al leer reservas.xml: " + e.getMessage(), "estado-error");
            btnReservar.setDisable(true);
        }
    }

    private void configurarFormulario() {
        cmbHabitacion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Habitacion h) {
                if (h == null) return "";
                return h.numero() + " · " + h.tipo() + " · " + IMPORTE.format(h.precioNoche())
                        + " €/noche · hasta " + h.capacidad() + " pers.";
            }

            @Override
            public Habitacion fromString(String texto) {
                return null;
            }
        });

        // Personas: el máximo depende de la capacidad de la habitación elegida
        spnPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
        spnPersonas.setEditable(true);
        spnPersonas.getEditor().focusedProperty().addListener((obs, tenia, tiene) -> {
            if (!tiene) confirmarPersonas();
        });

        cmbHabitacion.valueProperty().addListener((obs, antes, h) -> {
            ajustarPersonas(h);
            actualizarResumen();
        });

        // Fechas con formato dd/MM/yyyy, sin días pasados
        StringConverter<LocalDate> conversor = new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : FORMATO_FECHA.format(fecha);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) return null;
                try {
                    return LocalDate.parse(texto.trim(), LECTURA_FECHA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
        dpEntrada.setConverter(conversor);
        dpSalida.setConverter(conversor);

        dpEntrada.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vacio) {
                super.updateItem(dia, vacio);
                setDisable(vacio || dia.isBefore(LocalDate.now()));
            }
        });
        dpSalida.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vacio) {
                super.updateItem(dia, vacio);
                LocalDate minimo = dpEntrada.getValue() != null
                        ? dpEntrada.getValue().plusDays(1)
                        : LocalDate.now().plusDays(1);
                setDisable(vacio || dia.isBefore(minimo));
            }
        });

        dpEntrada.valueProperty().addListener((obs, antes, entrada) -> {
            // Si la salida ya no es posterior a la nueva entrada, se vuelve a pedir
            LocalDate salida = dpSalida.getValue();
            if (entrada != null && salida != null && !salida.isAfter(entrada)) {
                dpSalida.setValue(null);
            }
            actualizarResumen();
        });
        dpSalida.valueProperty().addListener((obs, antes, salida) -> actualizarResumen());
    }

    private void configurarValidacion() {
        validacion.registerValidator(txtNombre, true,
                Validator.<String>createEmptyValidator("El nombre es obligatorio"));
        validacion.registerValidator(txtApellidos, true,
                Validator.<String>createEmptyValidator("Los apellidos son obligatorios"));
        validacion.registerValidator(txtEmail, true, Validator.combine(
                Validator.<String>createEmptyValidator("El correo electrónico es obligatorio"),
                Validator.createRegexValidator("Formato de correo no válido (ej. nombre@dominio.com)",
                        REGEX_EMAIL, Severity.ERROR)));
        validacion.registerValidator(cmbHabitacion, true,
                Validator.<Habitacion>createEmptyValidator("Elige una habitación"));
        validacion.registerValidator(dpEntrada, true, Validator.combine(
                Validator.<LocalDate>createEmptyValidator("Indica la fecha de entrada (dd/mm/aaaa)"),
                Validator.<LocalDate>createPredicateValidator(
                        fecha -> fecha == null || !fecha.isBefore(LocalDate.now()),
                        "La fecha de entrada no puede ser anterior a hoy")));
        validacion.registerValidator(dpSalida, true, Validator.combine(
                Validator.<LocalDate>createEmptyValidator("Indica la fecha de salida (dd/mm/aaaa)"),
                Validator.<LocalDate>createPredicateValidator(
                        fecha -> fecha == null || dpEntrada.getValue() == null
                                || fecha.isAfter(dpEntrada.getValue()),
                        "La salida debe ser posterior a la entrada")));
    }

    // ---------------------------------------------------------------- acciones

    @FXML
    private void reservar() {
        confirmarPersonas();
        validacion.setErrorDecorationEnabled(true);

        if (validacion.isInvalid()) {
            ValidationMessage primero = validacion.getValidationResult().getErrors().iterator().next();
            mostrar("✖ Revisa el formulario: " + primero.getText(), "estado-error");
            if (primero.getTarget() != null) primero.getTarget().requestFocus();
            return;
        }

        Habitacion habitacion = cmbHabitacion.getValue();
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();

        Reserva conflicto = buscarConflicto(habitacion.numero(), entrada, salida);
        if (conflicto != null) {
            mostrar("✖ La habitación " + habitacion.numero() + " ya tiene la reserva " + conflicto.codigo()
                    + " del " + FORMATO_FECHA.format(conflicto.entrada()) + " al "
                    + FORMATO_FECHA.format(conflicto.salida())
                    + ". Elige otras fechas u otra habitación.", "estado-error");
            dpEntrada.requestFocus();
            return;
        }

        String codigo = siguienteCodigo();
        Reserva nueva = new Reserva(codigo, "pendiente",
                txtNombre.getText().trim(), txtApellidos.getText().trim(), txtEmail.getText().trim(),
                habitacion.numero(), entrada, salida, spnPersonas.getValue(),
                txtObservaciones.getText().trim());
        reservas.add(nueva);

        long noches = ChronoUnit.DAYS.between(entrada, salida);
        String total = IMPORTE.format(noches * habitacion.precioNoche());

        limpiarCampos();
        tablaReservas.getSelectionModel().select(nueva);
        tablaReservas.scrollTo(nueva);
        mostrar("✔ Reserva " + codigo + " registrada (pendiente de confirmación): habitación "
                + habitacion.numero() + ", " + noches + (noches == 1 ? " noche, " : " noches, ")
                + total + " €.", "estado-ok");
        txtNombre.requestFocus();
    }

    @FXML
    private void limpiar() {
        limpiarCampos();
        mostrar("Formulario limpio. Completa los datos para crear una reserva.", null);
        txtNombre.requestFocus();
    }

    // ---------------------------------------------------------------- utilidades

    /** Activa o desactiva el botón por defecto (Enter) según la pestaña visible. */
    public void setPantallaActiva(boolean activa) {
        btnReservar.setDefaultButton(activa);
    }

    /** Lleva el foco al primer campo del formulario. */
    public void enfocar() {
        Platform.runLater(txtNombre::requestFocus);
    }

    private void limpiarCampos() {
        validacion.setErrorDecorationEnabled(false);
        txtNombre.clear();
        txtApellidos.clear();
        txtEmail.clear();
        txtObservaciones.clear();
        cmbHabitacion.setValue(null);
        dpEntrada.setValue(null);
        dpSalida.setValue(null);
        spnPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
        actualizarResumen();
    }

    private void ajustarPersonas(Habitacion h) {
        int capacidad = h == null ? 1 : h.capacidad();
        Integer actual = spnPersonas.getValue();
        int valor = actual == null ? 1 : Math.min(actual, capacidad);
        spnPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, capacidad, valor));
    }

    /** Confirma el número escrito a mano en el spinner (si no, solo se guarda al pulsar Enter). */
    private void confirmarPersonas() {
        SpinnerValueFactory.IntegerSpinnerValueFactory factoria =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) spnPersonas.getValueFactory();
        try {
            int escrito = Integer.parseInt(spnPersonas.getEditor().getText().trim());
            factoria.setValue(Math.max(factoria.getMin(), Math.min(factoria.getMax(), escrito)));
        } catch (NumberFormatException e) {
            // Texto no numérico: se restaura el último valor válido
        }
        spnPersonas.getEditor().setText(String.valueOf(factoria.getValue()));
    }

    private void actualizarResumen() {
        Habitacion h = cmbHabitacion.getValue();
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        if (h == null || entrada == null || salida == null || !salida.isAfter(entrada)) {
            lblResumen.setText("Elige habitación y fechas para ver el importe estimado.");
            return;
        }
        long noches = ChronoUnit.DAYS.between(entrada, salida);
        lblResumen.setText("Estancia: " + noches + (noches == 1 ? " noche × " : " noches × ")
                + IMPORTE.format(h.precioNoche()) + " € = "
                + IMPORTE.format(noches * h.precioNoche()) + " €");
    }

    /** Una reserva confirmada o pendiente de la misma habitación que se solape con las fechas. */
    private Reserva buscarConflicto(int habitacion, LocalDate entrada, LocalDate salida) {
        return reservas.stream()
                .filter(r -> r.habitacion() == habitacion)
                .filter(r -> "confirmada".equals(r.estado()) || "pendiente".equals(r.estado()))
                .filter(r -> entrada.isBefore(r.salida()) && salida.isAfter(r.entrada()))
                .findFirst()
                .orElse(null);
    }

    private String siguienteCodigo() {
        int maximo = reservas.stream()
                .map(Reserva::codigo)
                .filter(c -> c.matches("R\\d+"))
                .mapToInt(c -> Integer.parseInt(c.substring(1)))
                .max()
                .orElse(0);
        return String.format("R%03d", maximo + 1);
    }

    private void mostrar(String texto, String claseEstado) {
        lblResultado.setText(texto);
        lblResultado.getStyleClass().removeAll(CLASES_ESTADO);
        if (claseEstado != null) lblResultado.getStyleClass().add(claseEstado);
        Voz.leer(texto);
    }

    private static String etiquetaEstado(String estado) {
        return switch (estado) {
            case "confirmada" -> "✔ Confirmada";
            case "pendiente" -> "⚠ Pendiente";
            case "cancelada" -> "✖ Cancelada";
            case "finalizada" -> "● Finalizada";
            default -> estado;
        };
    }

    private static String claseCelda(String estado) {
        return switch (estado) {
            case "confirmada" -> "celda-ok";
            case "pendiente" -> "celda-aviso";
            case "cancelada" -> "celda-error";
            default -> "celda-neutro";
        };
    }
}
