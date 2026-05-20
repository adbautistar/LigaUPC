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
import ligaupc.model.Arbitro;
import ligaupc.service.ArbitroService;

/**
 * Controlador JavaFX del formulario de Arbitros.
 *
 * <h2>Patron GRASP High Cohesion</h2>
 * <p>Gestiona unicamente la interaccion de usuario para la entidad Arbitro.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>Accede a {@link ArbitroService} via {@link MainApp#getArbitroService()}.
 * No conoce al DAO ni a ningun otro Controller.</p>
 *
 * <h2>Patron GRASP Expert</h2>
 * <p>Las validaciones de negocio las delega a ArbitroService, que es el
 * experto en las reglas de la entidad Arbitro.</p>
 */
public class ArbitroController {

    // ── Campos del formulario ────────────────────────────────────────────────
    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private ComboBox<String> cmbCertificacion;

    // ── Tabla ────────────────────────────────────────────────────────────────
    @FXML private TableView<Arbitro>           tablaArbitros;
    @FXML private TableColumn<Arbitro, String> colIdentificacion;
    @FXML private TableColumn<Arbitro, String> colNombre;
    @FXML private TableColumn<Arbitro, String> colContacto;
    @FXML private TableColumn<Arbitro, String> colCertificacion;

    // ── Mensaje de estado ────────────────────────────────────────────────────
    @FXML private Label lblMensaje;

    // ── Service (via ServiceLocator) ─────────────────────────────────────────
    private final ArbitroService arbitroService = MainApp.getArbitroService();

    /**
     * JavaFX invoca este metodo despues de cargar el FXML.
     * Configura columnas, categorias de certificacion, listener de seleccion y carga inicial.
     */
    @FXML
    public void initialize() {
        colIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colNombre        .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto      .setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colCertificacion .setCellValueFactory(new PropertyValueFactory<>("categoriaCertificacion"));

        cmbCertificacion.setItems(FXCollections.observableArrayList(
            "FIFA", "CONMEBOL", "Nacional", "Regional", "Local"
        ));

        tablaArbitros.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> {
                if (seleccionado != null) llenarFormulario(seleccionado);
            }
        );

        refrescarTabla();
    }

    // ── Acciones CRUD ────────────────────────────────────────────────────────

    /** Registra un nuevo arbitro con los datos del formulario. */
    @FXML
    private void registrar() {
        try {
            arbitroService.registrarArbitro(construirArbitroDesdeFormulario());
            mostrarExito("Arbitro registrado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Busca un arbitro por identificacion y llena el formulario. */
    @FXML
    private void buscar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese una identificacion para buscar."); return; }
            llenarFormulario(arbitroService.buscarArbitro(id));
            mostrarExito("Arbitro encontrado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Actualiza los datos del arbitro cuya identificacion esta en el formulario. */
    @FXML
    private void actualizar() {
        try {
            arbitroService.actualizarArbitro(construirArbitroDesdeFormulario());
            mostrarExito("Arbitro actualizado correctamente.");
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Elimina el arbitro cuya identificacion esta en el campo correspondiente. */
    @FXML
    private void eliminar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese la identificacion del arbitro a eliminar."); return; }
            arbitroService.eliminarArbitro(id);
            mostrarExito("Arbitro eliminado correctamente.");
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
        cmbCertificacion.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        tablaArbitros.getSelectionModel().clearSelection();
    }

    // ── Metodos privados de apoyo ────────────────────────────────────────────

    private Arbitro construirArbitroDesdeFormulario() {
        return new Arbitro(
            txtNombre.getText().trim(),
            txtIdentificacion.getText().trim(),
            txtContacto.getText().trim(),
            cmbCertificacion.getValue()
        );
    }

    private void llenarFormulario(Arbitro arbitro) {
        txtIdentificacion.setText(arbitro.getIdentificacion());
        txtNombre.setText(arbitro.getNombre());
        txtContacto.setText(arbitro.getContacto());
        cmbCertificacion.setValue(arbitro.getCategoriaCertificacion());
        lblMensaje.setText("");
    }

    private void refrescarTabla() {
        ObservableList<Arbitro> datos =
            FXCollections.observableArrayList(arbitroService.listarArbitros());
        tablaArbitros.setItems(datos);
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
