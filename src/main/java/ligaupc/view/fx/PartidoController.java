package ligaupc.view.fx;

import java.time.ZoneId;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ligaupc.MainApp;
import ligaupc.model.Arbitro;
import ligaupc.model.Equipo;
import ligaupc.model.Partido;
import ligaupc.service.ArbitroService;
import ligaupc.service.EquipoService;
import ligaupc.service.PartidoService;

/**
 * Controlador JavaFX del formulario de Partidos.
 *
 * <h2>Dos flujos de trabajo</h2>
 * <ol>
 *   <li><b>Programar partido:</b> fecha + equipos + arbitro → estado "Programado"</li>
 *   <li><b>Registrar resultado:</b> idPartido + goles → estado "Finalizado"
 *       + actualiza estadisticas de ambos equipos via PartidoService</li>
 * </ol>
 *
 * <h2>Complejidad: 3 Services</h2>
 * <ul>
 *   <li>{@link PartidoService} — flujo principal: programar, buscar, eliminar, registrar resultado</li>
 *   <li>{@link EquipoService} — poblar los dos ComboBox de equipos</li>
 *   <li>{@link ArbitroService} — poblar el ComboBox de arbitros</li>
 * </ul>
 *
 * <h2>Patron GRASP Controller</h2>
 * <p>PartidoController solo captura los datos del usuario y los pasa al service.
 * La logica compleja (actualizar estadisticas de ambos equipos) reside en PartidoService.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>Los 3 services se obtienen via {@link MainApp} (ServiceLocator).
 * No hay dependencia directa entre Controllers.</p>
 *
 * <h2>StringConverter para ComboBox de dominio</h2>
 * <p>Equipo y Arbitro son objetos de dominio. Sin StringConverter JavaFX mostraria
 * el toString() del objeto. Se configura un converter legible para cada ComboBox.</p>
 */
public class PartidoController {

    // ── Seccion 1: Programar partido ─────────────────────────────────────────
    @FXML private TextField  txtIdPartido;
    @FXML private DatePicker dateFecha;
    @FXML private ComboBox<Equipo>  cmbEquipoLocal;
    @FXML private ComboBox<Equipo>  cmbEquipoVisitante;
    @FXML private ComboBox<Arbitro> cmbArbitro;

    // ── Seccion 2: Registrar resultado ───────────────────────────────────────
    @FXML private TextField txtIdResultado;
    @FXML private TextField txtGolesLocal;
    @FXML private TextField txtGolesVisitante;

    // ── Mensaje ──────────────────────────────────────────────────────────────
    @FXML private Label lblMensaje;

    // ── Tabla de partidos ────────────────────────────────────────────────────
    @FXML private TableView<Partido>           tablaPartidos;
    @FXML private TableColumn<Partido, String> colId;
    @FXML private TableColumn<Partido, String> colFecha;
    @FXML private TableColumn<Partido, String> colLocal;
    @FXML private TableColumn<Partido, String> colVisitante;
    @FXML private TableColumn<Partido, String> colGoles;
    @FXML private TableColumn<Partido, String> colArbitro;
    @FXML private TableColumn<Partido, String> colEstado;

    // ── Services (via ServiceLocator) ────────────────────────────────────────
    private final PartidoService  partidoService  = MainApp.getPartidoService();
    private final EquipoService   equipoService   = MainApp.getEquipoService();
    private final ArbitroService  arbitroService  = MainApp.getArbitroService();

    /**
     * JavaFX invoca este metodo despues de cargar el FXML.
     * Configura columnas con lambdas (objetos anidados), los tres ComboBox
     * y carga la tabla inicial.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        configurarComboEquipos();
        configurarComboArbitro();
        refrescarTabla();
    }

    // ── Acciones Seccion 1: Programar ────────────────────────────────────────

    /**
     * Crea y programa un nuevo partido con los datos del formulario.
     * Convierte el LocalDate del DatePicker a java.util.Date.
     */
    @FXML
    private void programar() {
        try {
            int     id        = Integer.parseInt(txtIdPartido.getText().trim());
            Equipo  local     = cmbEquipoLocal.getValue();
            Equipo  visitante = cmbEquipoVisitante.getValue();
            Arbitro arbitro   = cmbArbitro.getValue();

            if (dateFecha.getValue() == null) {
                mostrarError("Seleccione una fecha para el partido."); return;
            }
            if (local == null)     { mostrarError("Seleccione el equipo local."); return; }
            if (visitante == null) { mostrarError("Seleccione el equipo visitante."); return; }
            if (arbitro == null)   { mostrarError("Seleccione el arbitro."); return; }

            // LocalDate → java.util.Date
            Date fecha = Date.from(
                dateFecha.getValue()
                         .atStartOfDay(ZoneId.systemDefault())
                         .toInstant()
            );

            Partido partido = new Partido(id, fecha, local, visitante, arbitro);
            partidoService.registrarPartido(partido);
            mostrarExito("Partido programado correctamente.");
            limpiar();
            refrescarTabla();

        } catch (NumberFormatException e) {
            mostrarError("El ID del partido debe ser un numero entero.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Busca un partido por ID y muestra sus datos.
     * Selecciona la fila correspondiente en la tabla.
     */
    @FXML
    private void buscar() {
        try {
            String idTexto = txtIdPartido.getText().trim();
            if (idTexto.isEmpty()) { mostrarError("Ingrese el ID del partido a buscar."); return; }
            int id = Integer.parseInt(idTexto);
            Partido partido = partidoService.buscarPartido(id);
            // Seleccionar en tabla
            tablaPartidos.getSelectionModel().select(partido);
            tablaPartidos.scrollTo(partido);
            mostrarExito("Partido encontrado.");
        } catch (NumberFormatException e) {
            mostrarError("El ID debe ser un numero entero.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el partido cuyo ID esta en el formulario.
     * Solo se puede eliminar un partido en estado "Programado".
     */
    @FXML
    private void eliminar() {
        try {
            String idTexto = txtIdPartido.getText().trim();
            if (idTexto.isEmpty()) { mostrarError("Ingrese el ID del partido a eliminar."); return; }
            int id = Integer.parseInt(idTexto);
            partidoService.eliminarPartido(id);
            mostrarExito("Partido eliminado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (NumberFormatException e) {
            mostrarError("El ID debe ser un numero entero.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Limpia todos los campos del formulario. */
    @FXML
    private void limpiar() {
        txtIdPartido.clear();
        dateFecha.setValue(null);
        cmbEquipoLocal.getSelectionModel().clearSelection();
        cmbEquipoVisitante.getSelectionModel().clearSelection();
        cmbArbitro.getSelectionModel().clearSelection();
        txtIdResultado.clear();
        txtGolesLocal.clear();
        txtGolesVisitante.clear();
        lblMensaje.setText("");
        tablaPartidos.getSelectionModel().clearSelection();
    }

    // ── Acciones Seccion 2: Registrar resultado ──────────────────────────────

    /**
     * Registra el resultado de un partido en estado "Programado".
     * PartidoService actualiza automaticamente las estadisticas de ambos equipos
     * (GRASP Controller: coordinacion del flujo mas complejo del sistema).
     */
    @FXML
    private void registrarResultado() {
        try {
            String idTexto = txtIdResultado.getText().trim();
            if (idTexto.isEmpty()) { mostrarError("Ingrese el ID del partido."); return; }

            int id           = Integer.parseInt(idTexto);
            int golesLocal   = Integer.parseInt(txtGolesLocal.getText().trim());
            int golesVisitante = Integer.parseInt(txtGolesVisitante.getText().trim());

            partidoService.registrarResultado(id, golesLocal, golesVisitante);
            mostrarExito("Resultado registrado. Estadisticas actualizadas.");
            txtIdResultado.clear();
            txtGolesLocal.clear();
            txtGolesVisitante.clear();
            refrescarTabla();

        } catch (NumberFormatException e) {
            mostrarError("El ID y los goles deben ser numeros enteros.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ── Metodos privados de configuracion ────────────────────────────────────

    /**
     * Configura las cell factories de la tabla con lambdas.
     *
     * <p>Se usan lambdas porque todos los campos de Partido son objetos anidados
     * o necesitan formateo especial (Resultado = "X - Y", Fecha = toString).</p>
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getIdPartido()))
        );
        colFecha.setCellValueFactory(c -> {
            Date fecha = c.getValue().getFecha();
            return new SimpleStringProperty(fecha != null ? fecha.toString().substring(0, 10) : "—");
        });
        colLocal.setCellValueFactory(c -> {
            Equipo e = c.getValue().getEquipoLocal();
            return new SimpleStringProperty(e != null ? e.getNombre() : "—");
        });
        colVisitante.setCellValueFactory(c -> {
            Equipo e = c.getValue().getEquipoVisitante();
            return new SimpleStringProperty(e != null ? e.getNombre() : "—");
        });
        // Resultado: "X - Y" para partidos finalizados; "—" para programados
        colGoles.setCellValueFactory(c -> {
            Partido p = c.getValue();
            if ("Finalizado".equals(p.getEstado())) {
                return new SimpleStringProperty(p.getGolesLocal() + " - " + p.getGolesVisitante());
            }
            return new SimpleStringProperty("—");
        });
        colArbitro.setCellValueFactory(c -> {
            Arbitro a = c.getValue().getArbitro();
            return new SimpleStringProperty(a != null ? a.getNombre() : "—");
        });
        colEstado.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getEstado())
        );
    }

    /**
     * Configura los dos ComboBox de equipos con un StringConverter que muestra
     * el nombre del equipo.
     */
    private void configurarComboEquipos() {
        StringConverter<Equipo> converterEquipo = new StringConverter<Equipo>() {
            @Override
            public String toString(Equipo e) {
                return (e != null) ? e.getNombre() : "";
            }
            @Override
            public Equipo fromString(String s) { return null; }
        };
        cmbEquipoLocal.setConverter(converterEquipo);
        cmbEquipoVisitante.setConverter(converterEquipo);
        recargarCombosEquipos();
    }

    /**
     * Configura el ComboBox de arbitros con StringConverter que muestra
     * nombre + identificacion.
     */
    private void configurarComboArbitro() {
        cmbArbitro.setConverter(new StringConverter<Arbitro>() {
            @Override
            public String toString(Arbitro a) {
                return (a != null) ? a.getNombre() + " (" + a.getIdentificacion() + ")" : "";
            }
            @Override
            public Arbitro fromString(String s) { return null; }
        });
        recargarComboArbitro();
    }

    private void recargarCombosEquipos() {
        ObservableList<Equipo> equipos =
            FXCollections.observableArrayList(equipoService.listarEquipos());
        cmbEquipoLocal.setItems(equipos);
        cmbEquipoVisitante.setItems(equipos);
    }

    private void recargarComboArbitro() {
        cmbArbitro.setItems(
            FXCollections.observableArrayList(arbitroService.listarArbitros())
        );
    }

    private void refrescarTabla() {
        tablaPartidos.setItems(
            FXCollections.observableArrayList(partidoService.listarPartidos())
        );
        recargarCombosEquipos();
        recargarComboArbitro();
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #c62828;");
    }
}
