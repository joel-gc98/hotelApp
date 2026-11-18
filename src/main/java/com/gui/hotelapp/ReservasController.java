package com.gui.hotelapp;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
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
import java.util.function.Supplier;

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
    private final FilteredList<Reserva> reservasVisibles = new FilteredList<>(reservas);
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
        colEstado.setCellFactory(col -> new CeldaEstado());

        tablaReservas.setItems(reservasVisibles);
        tablaReservas.setPlaceholder(new Label("No hay reservas que mostrar."));
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
        configurarSelectorHabitacion();
        configurarPersonas();
        configurarFechas();
    }

    private void configurarSelectorHabitacion() {
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
        cmbHabitacion.valueProperty().addListener((obs, antes, h) -> {
            ajustarPersonas(h);
            actualizarResumen();
        });
    }

    /** Personas: el máximo depende de la capacidad de la habitación elegida. */
    private void configurarPersonas() {
        spnPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
        spnPersonas.setEditable(true);
        spnPersonas.getEditor().focusedProperty().addListener((obs, tenia, tiene) -> {
            if (!tiene) confirmarPersonas();
        });
    }

    /** Fechas con formato dd/MM/yyyy, sin días pasados. */
    private void configurarFechas() {
        StringConverter<LocalDate> conversor = new ConversorFecha();
        dpEntrada.setConverter(conversor);
        dpSalida.setConverter(conversor);

        dpEntrada.setDayCellFactory(celdasDesde(LocalDate::now));
        dpSalida.setDayCellFactory(celdasDesde(this::minimoSalida));

        dpEntrada.valueProperty().addListener((obs, antes, entrada) -> alCambiarEntrada(entrada));
        dpSalida.valueProperty().addListener((obs, antes, salida) -> actualizarResumen());
    }

    private static Callback<DatePicker, DateCell> celdasDesde(Supplier<LocalDate> minimo) {
        return picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vacio) {
                super.updateItem(dia, vacio);
                setDisable(vacio || dia.isBefore(minimo.get()));
            }
        };
    }

    private LocalDate minimoSalida() {
        LocalDate entrada = dpEntrada.getValue();
        return (entrada != null ? entrada : LocalDate.now()).plusDays(1);
    }

    /** Si la salida ya no es posterior a la nueva entrada, se vuelve a pedir. */
    private void alCambiarEntrada(LocalDate entrada) {
        LocalDate salida = dpSalida.getValue();
        if (entrada != null && salida != null && !salida.isAfter(entrada)) {
            dpSalida.setValue(null);
        }
        actualizarResumen();
    }

    private void configurarValidacion() {
        validarDatosCliente();
        validarEstancia();
    }

    private void validarDatosCliente() {
        validacion.registerValidator(txtNombre, true,
                Validator.<String>createEmptyValidator("El nombre es obligatorio"));
        validacion.registerValidator(txtApellidos, true,
                Validator.<String>createEmptyValidator("Los apellidos son obligatorios"));
        validacion.registerValidator(txtEmail, true, Validator.combine(
                Validator.<String>createEmptyValidator("El correo electrónico es obligatorio"),
                Validator.createRegexValidator("Formato de correo no válido (ej. nombre@dominio.com)",
                        REGEX_EMAIL, Severity.ERROR)));
    }

    private void validarEstancia() {
        validacion.registerValidator(cmbHabitacion, true,
                Validator.<Habitacion>createEmptyValidator("Elige una habitación"));
        validacion.registerValidator(dpEntrada, true, Validator.combine(
                Validator.<LocalDate>createEmptyValidator("Indica la fecha de entrada (dd/mm/aaaa)"),
                Validator.<LocalDate>createPredicateValidator(
                        ReservasController::entradaValida,
                        "La fecha de entrada no puede ser anterior a hoy")));
        validacion.registerValidator(dpSalida, true, Validator.combine(
                Validator.<LocalDate>createEmptyValidator("Indica la fecha de salida (dd/mm/aaaa)"),
                Validator.<LocalDate>createPredicateValidator(
                        this::salidaValida,
                        "La salida debe ser posterior a la entrada")));
    }

    private static boolean entradaValida(LocalDate fecha) {
        return fecha == null || !fecha.isBefore(LocalDate.now());
    }

    private boolean salidaValida(LocalDate fecha) {
        LocalDate entrada = dpEntrada.getValue();
        return fecha == null || entrada == null || fecha.isAfter(entrada);
    }

    // ---------------------------------------------------------------- acciones

    @FXML
    private void reservar() {
        confirmarPersonas();
        validacion.setErrorDecorationEnabled(true);

        if (validacion.isInvalid()) {
            avisarFormularioInvalido();
            return;
        }

        Habitacion habitacion = cmbHabitacion.getValue();
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();

        Reserva conflicto = buscarConflicto(habitacion.numero(), entrada, salida);
        if (conflicto != null) {
            avisarConflicto(habitacion, conflicto);
            return;
        }
        registrarReserva(habitacion, entrada, salida);
    }

    private void avisarFormularioInvalido() {
        ValidationMessage primero = validacion.getValidationResult().getErrors().iterator().next();
        mostrar("✖ Revisa el formulario: " + primero.getText(), "estado-error");
        if (primero.getTarget() != null) primero.getTarget().requestFocus();
    }

    private void avisarConflicto(Habitacion habitacion, Reserva conflicto) {
        mostrar("✖ La habitación " + habitacion.numero() + " ya tiene la reserva " + conflicto.codigo()
                + " del " + FORMATO_FECHA.format(conflicto.entrada()) + " al "
                + FORMATO_FECHA.format(conflicto.salida())
                + ". Elige otras fechas u otra habitación.", "estado-error");
        dpEntrada.requestFocus();
    }

    private void registrarReserva(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        String codigo = siguienteCodigo();
        Reserva nueva = new Reserva(codigo, "pendiente",
                txtNombre.getText().trim(), txtApellidos.getText().trim(), txtEmail.getText().trim(),
                habitacion.numero(), entrada, salida, spnPersonas.getValue(),
                txtObservaciones.getText().trim());
        reservas.add(nueva);

        long noches = ChronoUnit.DAYS.between(entrada, salida);
        String total = IMPORTE.format(noches * habitacion.precioNoche());

        limpiarCampos();
        reservasVisibles.setPredicate(r -> true);   // por si había una búsqueda activa
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

    // ---------------------------------------------------------------- control por voz

    /** Filtra la tabla por nombre del cliente, sin distinguir mayúsculas ni tildes. */
    public void buscarPorVoz(String consulta) {
        String buscado = InterpreteComandos.normalizar(consulta);
        if (buscado.isEmpty()) {
            mostrar("⚠ No he entendido el nombre. Di, por ejemplo: buscar reserva de Juan.", "estado-aviso");
            return;
        }
        List<String> palabras = List.of(buscado.split("\\s+"));
        reservasVisibles.setPredicate(r -> coincide(r, palabras));
        informarBusqueda(consulta);
    }

    /** Quita el filtro y vuelve a mostrar todas las reservas. */
    public void mostrarTodas() {
        reservasVisibles.setPredicate(r -> true);
        mostrar("✔ Se muestran todas las reservas (" + reservas.size() + ").", "estado-ok");
    }

    private static boolean coincide(Reserva r, List<String> palabras) {
        String nombre = InterpreteComandos.normalizar(r.nombreCompleto());
        return palabras.stream().allMatch(nombre::contains);
    }

    private void informarBusqueda(String consulta) {
        if (reservasVisibles.isEmpty()) {
            mostrar("✖ No hay reservas a nombre de «" + consulta + "».", "estado-error");
            return;
        }
        Reserva primera = reservasVisibles.get(0);
        tablaReservas.getSelectionModel().select(primera);
        if (reservasVisibles.size() == 1) {
            mostrar("✔ " + descripcion(primera), "estado-ok");
        } else {
            mostrar("✔ " + reservasVisibles.size() + " reservas encontradas para «" + consulta
                    + "». Se muestran en la tabla.", "estado-ok");
        }
    }

    private static String descripcion(Reserva r) {
        return "Reserva " + r.codigo() + ": " + r.nombreCompleto() + ", habitación " + r.habitacion()
                + ", del " + FORMATO_FECHA.format(r.entrada()) + " al " + FORMATO_FECHA.format(r.salida())
                + ", " + r.estado() + ".";
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

    // ---------------------------------------------------------------- clases internas

    /** El estado se distingue por símbolo + texto + color, nunca solo por color. */
    private static final class CeldaEstado extends TableCell<Reserva, String> {
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
    }

    private static final class ConversorFecha extends StringConverter<LocalDate> {
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
    }
}