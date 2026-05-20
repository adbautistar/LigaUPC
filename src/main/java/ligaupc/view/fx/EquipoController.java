package ligaupc.view.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ligaupc.MainApp;
import ligaupc.model.Equipo;
import ligaupc.model.Jugador;
import ligaupc.model.Tecnico;
import ligaupc.service.EquipoService;
import ligaupc.service.JugadorService;
import ligaupc.service.TecnicoService;

/**
 * Controlador JavaFX del formulario de Equipos.
 *
 * <h2>Complejidad: 3 Services</h2>
 * <p>Este es el Controller mas complejo de los modulos simples. Necesita:</p>
 * <ul>
 *   <li>{@link EquipoService} — CRUD principal del equipo</li>
 *   <li>{@link TecnicoService} — para poblar el ComboBox de tecnicos</li>
 *   <li>{@link JugadorService} — para poblar la tabla de jugadores disponibles</li>
 * </ul>
 * <p>Los tres se obtienen via {@link MainApp} (ServiceLocator).</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>EquipoController no crea ninguna instancia de Service directamente.
 * Tampoco conoce a otros Controllers. Solo consume el ServiceLocator.</p>
 *
 * <h2>Patron GRASP Expert</h2>
 * <p>Toda la logica de negocio (validar jugador ya en equipo, verificar tecnico,
 * etc.) vive en EquipoService. El Controller solo delega y muestra el resultado.</p>
 */
public class EquipoController {

    // ── Seccion 1: Datos basicos ─────────────────────────────────────────────
    @FXML private TextField txtNombre;
    @FXML private TextField txtSede;

    // ── Seccion 2: Asignar Tecnico ───────────────────────────────────────────
    @FXML private ComboBox<Tecnico> cmbTecnico;

    // ── Seccion 3: Gestion jugadores ─────────────────────────────────────────
    @FXML private TextField txtIdJugador;

    // ── Mensaje ──────────────────────────────────────────────────────────────
    @FXML private Label lblMensaje;
    @FXML private Label lblJugadoresEquipo;

    // ── Tabla principal: equipos ─────────────────────────────────────────────
    @FXML private TableView<Equipo>           tablaEquipos;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colSede;
    @FXML private TableColumn<Equipo, String> colTecnico;

    // ── Tabla secundaria: jugadores del equipo seleccionado ──────────────────
    @FXML private TableView<Jugador>           tablaJugadoresEquipo;
    @FXML private TableColumn<Jugador, String> colJugadorId;
    @FXML private TableColumn<Jugador, String> colJugadorNombre;
    @FXML private TableColumn<Jugador, String> colJugadorPosicion;
    @FXML private TableColumn<Jugador, Number> colJugadorCamiseta;

    // ── Services (via ServiceLocator) ────────────────────────────────────────
    private final EquipoService   equipoService   = MainApp.getEquipoService();
    private final TecnicoService  tecnicoService  = MainApp.getTecnicoService();
    private final JugadorService  jugadorService  = MainApp.getJugadorService();

    /**
     * JavaFX invoca este metodo despues de cargar el FXML.
     * Configura todas las columnas, el ComboBox de tecnicos y los listeners.
     */
    @FXML
    public void initialize() {
        configurarTablaEquipos();
        configurarTablaJugadores();
        configurarComboTecnico();

        // Al seleccionar un equipo: llenar formulario + mostrar sus jugadores
        tablaEquipos.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> {
                if (seleccionado != null) {
                    llenarFormulario(seleccionado);
                    mostrarJugadoresDelEquipo(seleccionado);
                }
            }
        );

        refrescarTablaEquipos();
    }

    // ── Acciones CRUD del equipo ─────────────────────────────────────────────

    /** Registra un nuevo equipo con nombre y sede del formulario. */
    @FXML
    private void registrar() {
        try {
            Equipo equipo = new Equipo(txtNombre.getText().trim(), txtSede.getText().trim());
            equipoService.registrarEquipo(equipo);
            mostrarExito("Equipo registrado correctamente.");
            limpiar();
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Busca un equipo por nombre y llena el formulario. */
    @FXML
    private void buscar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { mostrarError("Ingrese el nombre del equipo a buscar."); return; }
            Equipo equipo = equipoService.buscarEquipo(nombre);
            llenarFormulario(equipo);
            mostrarJugadoresDelEquipo(equipo);
            mostrarExito("Equipo encontrado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Actualiza la sede del equipo. */
    @FXML
    private void actualizar() {
        try {
            Equipo equipo = new Equipo(txtNombre.getText().trim(), txtSede.getText().trim());
            equipoService.actualizarEquipo(equipo);
            mostrarExito("Equipo actualizado correctamente.");
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Elimina el equipo cuyo nombre esta en el formulario. */
    @FXML
    private void eliminar() {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { mostrarError("Ingrese el nombre del equipo a eliminar."); return; }
            equipoService.eliminarEquipo(nombre);
            mostrarExito("Equipo eliminado correctamente.");
            limpiar();
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Limpia el formulario y deselecciona la tabla. */
    @FXML
    private void limpiar() {
        txtNombre.clear();
        txtSede.clear();
        txtIdJugador.clear();
        cmbTecnico.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        lblJugadoresEquipo.setText("Jugadores del equipo");
        tablaEquipos.getSelectionModel().clearSelection();
        tablaJugadoresEquipo.getItems().clear();
    }

    // ── Acciones de gestion de plantel ───────────────────────────────────────

    /**
     * Asigna el tecnico seleccionado en el ComboBox al equipo del formulario.
     * Usa EquipoService.asignarTecnico() que valida ambas entidades antes de asignar.
     */
    @FXML
    private void asignarTecnico() {
        try {
            String nombreEquipo = txtNombre.getText().trim();
            if (nombreEquipo.isEmpty()) { mostrarError("Primero seleccione o busque un equipo."); return; }
            Tecnico tecnico = cmbTecnico.getValue();
            if (tecnico == null) { mostrarError("Seleccione un tecnico del listado."); return; }
            equipoService.asignarTecnico(nombreEquipo, tecnico.getIdentificacion());
            mostrarExito("Tecnico asignado correctamente.");
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Agrega el jugador (por ID) al equipo actual del formulario.
     * EquipoService valida que el jugador exista y no este ya en el equipo.
     */
    @FXML
    private void agregarJugador() {
        try {
            String nombreEquipo = txtNombre.getText().trim();
            String idJugador    = txtIdJugador.getText().trim();
            if (nombreEquipo.isEmpty()) { mostrarError("Primero seleccione o busque un equipo."); return; }
            if (idJugador.isEmpty())    { mostrarError("Ingrese el ID del jugador a agregar."); return; }
            equipoService.agregarJugador(nombreEquipo, idJugador);
            mostrarExito("Jugador agregado al equipo.");
            txtIdJugador.clear();
            // Refresca la tabla de jugadores del equipo
            mostrarJugadoresDelEquipo(equipoService.buscarEquipo(nombreEquipo));
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Quita el jugador (por ID) del equipo actual del formulario.
     * EquipoService valida que el jugador pertenezca al equipo antes de quitarlo.
     */
    @FXML
    private void quitarJugador() {
        try {
            String nombreEquipo = txtNombre.getText().trim();
            String idJugador    = txtIdJugador.getText().trim();
            if (nombreEquipo.isEmpty()) { mostrarError("Primero seleccione o busque un equipo."); return; }
            if (idJugador.isEmpty())    { mostrarError("Ingrese el ID del jugador a quitar."); return; }
            equipoService.quitarJugador(nombreEquipo, idJugador);
            mostrarExito("Jugador retirado del equipo.");
            txtIdJugador.clear();
            mostrarJugadoresDelEquipo(equipoService.buscarEquipo(nombreEquipo));
            refrescarTablaEquipos();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ── Metodos privados de configuracion ────────────────────────────────────

    private void configurarTablaEquipos() {
        colNombre .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSede   .setCellValueFactory(new PropertyValueFactory<>("sede"));
        // Tecnico es un objeto anidado — se accede con lambda
        colTecnico.setCellValueFactory(cellData -> {
            Tecnico t = cellData.getValue().getTecnico();
            return new SimpleStringProperty(t != null ? t.getNombre() : "Sin asignar");
        });
    }

    private void configurarTablaJugadores() {
        colJugadorId      .setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colJugadorNombre  .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colJugadorPosicion.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colJugadorCamiseta.setCellValueFactory(new PropertyValueFactory<>("numeroCamiseta"));
    }

    /**
     * Configura el ComboBox de tecnicos con un StringConverter para mostrar
     * el nombre del tecnico en lugar del toString() del objeto.
     */
    private void configurarComboTecnico() {
        cmbTecnico.setConverter(new StringConverter<Tecnico>() {
            @Override
            public String toString(Tecnico t) {
                return (t != null) ? t.getNombre() + " (" + t.getIdentificacion() + ")" : "";
            }
            @Override
            public Tecnico fromString(String s) { return null; }
        });
        recargarComboTecnico();
    }

    private void recargarComboTecnico() {
        cmbTecnico.setItems(FXCollections.observableArrayList(tecnicoService.listarTecnicos()));
    }

    private void refrescarTablaEquipos() {
        ObservableList<Equipo> datos =
            FXCollections.observableArrayList(equipoService.listarEquipos());
        tablaEquipos.setItems(datos);
        recargarComboTecnico();
    }

    private void mostrarJugadoresDelEquipo(Equipo equipo) {
        lblJugadoresEquipo.setText("Jugadores de: " + equipo.getNombre()
            + "  (" + equipo.getJugadores().size() + ")");
        tablaJugadoresEquipo.setItems(
            FXCollections.observableArrayList(equipo.getJugadores())
        );
    }

    private void llenarFormulario(Equipo equipo) {
        txtNombre.setText(equipo.getNombre());
        txtSede.setText(equipo.getSede());
        if (equipo.getTecnico() != null) cmbTecnico.setValue(equipo.getTecnico());
        lblMensaje.setText("");
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
