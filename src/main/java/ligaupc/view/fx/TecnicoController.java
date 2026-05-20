package ligaupc.view.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ligaupc.MainApp;
import ligaupc.model.Tecnico;
import ligaupc.service.TecnicoService;

/**
 * Controlador JavaFX del formulario de Tecnicos.
 *
 * <h2>Patron GRASP High Cohesion</h2>
 * <p>Gestiona unicamente la interaccion de usuario para la entidad Tecnico.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>Accede a {@link TecnicoService} via {@link MainApp#getTecnicoService()}.
 * No conoce al DAO ni a ningun otro Controller.</p>
 *
 * <h2>Patron GRASP Expert</h2>
 * <p>Las validaciones de negocio las delega a TecnicoService, que es el
 * experto en las reglas de la entidad Tecnico.</p>
 */
public class TecnicoController {

    // ── Campos del formulario ────────────────────────────────────────────────
    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private ComboBox<String> cmbEspecialidad;

    // ── Tabla ────────────────────────────────────────────────────────────────
    @FXML private TableView<Tecnico>           tablaTecnicos;
    @FXML private TableColumn<Tecnico, String> colIdentificacion;
    @FXML private TableColumn<Tecnico, String> colNombre;
    @FXML private TableColumn<Tecnico, String> colContacto;
    @FXML private TableColumn<Tecnico, String> colEspecialidad;

    // ── Mensaje de estado ────────────────────────────────────────────────────
    @FXML private Label lblMensaje;

    // ── Service (via ServiceLocator) ─────────────────────────────────────────
    private final TecnicoService tecnicoService = MainApp.getTecnicoService();

    /**
     * JavaFX invoca este metodo despues de cargar el FXML.
     * Configura columnas, opciones del ComboBox, listener de seleccion y carga inicial.
     */
    @FXML
    public void initialize() {
        colIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colNombre        .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto      .setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colEspecialidad  .setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        cmbEspecialidad.setItems(FXCollections.observableArrayList(
            "Ofensiva", "Defensiva", "Tactica General",
            "Formacion de Juveniles", "Preparacion Fisica",
            "Porteros", "Analitica de Datos"
        ));

        tablaTecnicos.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> {
                if (seleccionado != null) llenarFormulario(seleccionado);
            }
        );

        refrescarTabla();
    }

    // ── Acciones CRUD ────────────────────────────────────────────────────────

    /** Registra un nuevo tecnico con los datos del formulario. */
    @FXML
    private void registrar() {
        try {
            tecnicoService.registrarTecnico(construirTecnicoDesdeFormulario());
            mostrarExito("Tecnico registrado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Busca un tecnico por identificacion y llena el formulario. */
    @FXML
    private void buscar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese una identificacion para buscar."); return; }
            llenarFormulario(tecnicoService.buscarTecnico(id));
            mostrarExito("Tecnico encontrado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Actualiza los datos del tecnico cuya identificacion esta en el formulario. */
    @FXML
    private void actualizar() {
        try {
            tecnicoService.actualizarTecnico(construirTecnicoDesdeFormulario());
            mostrarExito("Tecnico actualizado correctamente.");
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Elimina el tecnico cuya identificacion esta en el campo correspondiente. */
    @FXML
    private void eliminar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese la identificacion del tecnico a eliminar."); return; }
            tecnicoService.eliminarTecnico(id);
            mostrarExito("Tecnico eliminado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Limpia todos los campos del formulario. */
    @FXML
    private void limpiar() {
        txtIdentificacion.clear();
        txtNombre.clear();
        txtContacto.clear();
        cmbEspecialidad.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        tablaTecnicos.getSelectionModel().clearSelection();
    }

    // ── Metodos privados de apoyo ────────────────────────────────────────────

    private Tecnico construirTecnicoDesdeFormulario() {
        return new Tecnico(
            txtNombre.getText().trim(),
            txtIdentificacion.getText().trim(),
            txtContacto.getText().trim(),
            cmbEspecialidad.getValue()
        );
    }

    private void llenarFormulario(Tecnico tecnico) {
        txtIdentificacion.setText(tecnico.getIdentificacion());
        txtNombre.setText(tecnico.getNombre());
        txtContacto.setText(tecnico.getContacto());
        cmbEspecialidad.setValue(tecnico.getEspecialidad());
        lblMensaje.setText("");
    }

    private void refrescarTabla() {
        ObservableList<Tecnico> datos =
            FXCollections.observableArrayList(tecnicoService.listarTecnicos());
        tablaTecnicos.setItems(datos);
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
