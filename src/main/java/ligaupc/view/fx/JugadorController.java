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
import ligaupc.model.Jugador;
import ligaupc.service.JugadorService;

/**
 * Controlador JavaFX del formulario de Jugadores.
 *
 * <h2>Patron GRASP High Cohesion</h2>
 * <p>Este Controller gestiona unicamente la interaccion de usuario
 * para la entidad Jugador. Nada mas.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>Accede a {@link JugadorService} via {@link MainApp#getJugadorService()}.
 * No conoce al DAO ni a ningun otro Controller.</p>
 *
 * <h2>Patron SOLID SRP</h2>
 * <p>Si cambia el formulario de jugadores, solo cambia este Controller.</p>
 */
public class JugadorController {

    // ── Campos del formulario ────────────────────────────────────────────────
    @FXML private TextField  txtIdentificacion;
    @FXML private TextField  txtNombre;
    @FXML private TextField  txtContacto;
    @FXML private ComboBox<String> cmbPosicion;
    @FXML private TextField  txtNumeroCamiseta;

    // ── Tabla ────────────────────────────────────────────────────────────────
    @FXML private TableView<Jugador>       tablaJugadores;
    @FXML private TableColumn<Jugador, String>  colIdentificacion;
    @FXML private TableColumn<Jugador, String>  colNombre;
    @FXML private TableColumn<Jugador, String>  colContacto;
    @FXML private TableColumn<Jugador, String>  colPosicion;
    @FXML private TableColumn<Jugador, Integer> colCamiseta;

    // ── Mensaje de estado ────────────────────────────────────────────────────
    @FXML private Label lblMensaje;

    // ── Service (via ServiceLocator) ─────────────────────────────────────────
    private final JugadorService jugadorService = MainApp.getJugadorService();

    /**
     * JavaFX llama a este metodo automaticamente despues de cargar el FXML.
     * Configura las columnas de la tabla, carga los datos iniciales y
     * registra el listener de seleccion de filas.
     */
    @FXML
    public void initialize() {
        // Vincular columnas a los atributos del modelo (via getters por reflexion)
        colIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colNombre        .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto      .setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colPosicion      .setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colCamiseta      .setCellValueFactory(new PropertyValueFactory<>("numeroCamiseta"));

        // Opciones del ComboBox de posiciones
        cmbPosicion.setItems(FXCollections.observableArrayList(
            "Portero", "Defensa Central", "Lateral Derecho", "Lateral Izquierdo",
            "Mediocampista", "Extremo Derecho", "Extremo Izquierdo",
            "Delantero Centro", "Mediapunta"
        ));

        // Al seleccionar una fila en la tabla, llenar el formulario automaticamente
        tablaJugadores.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> {
                if (seleccionado != null) llenarFormulario(seleccionado);
            }
        );

        refrescarTabla();
    }

    // ── Acciones CRUD ────────────────────────────────────────────────────────

    /**
     * Registra un nuevo jugador con los datos del formulario.
     * Aplica GRASP Expert: la validacion de negocio la hace JugadorService.
     */
    @FXML
    private void registrar() {
        try {
            Jugador jugador = construirJugadorDesdeFormulario();
            jugadorService.registrarJugador(jugador);
            mostrarExito("Jugador registrado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Busca un jugador por identificacion y llena el formulario con sus datos.
     */
    @FXML
    private void buscar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese una identificacion para buscar."); return; }
            Jugador jugador = jugadorService.buscarJugador(id);
            llenarFormulario(jugador);
            mostrarExito("Jugador encontrado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Actualiza los datos del jugador cuya identificacion esta en el formulario.
     */
    @FXML
    private void actualizar() {
        try {
            Jugador jugador = construirJugadorDesdeFormulario();
            jugadorService.actualizarJugador(jugador);
            mostrarExito("Jugador actualizado correctamente.");
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina el jugador cuya identificacion esta en el campo correspondiente.
     */
    @FXML
    private void eliminar() {
        try {
            String id = txtIdentificacion.getText().trim();
            if (id.isEmpty()) { mostrarError("Ingrese la identificacion del jugador a eliminar."); return; }
            jugadorService.eliminarJugador(id);
            mostrarExito("Jugador eliminado correctamente.");
            limpiar();
            refrescarTabla();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Limpia todos los campos del formulario y el mensaje de estado.
     */
    @FXML
    private void limpiar() {
        txtIdentificacion.clear();
        txtNombre.clear();
        txtContacto.clear();
        cmbPosicion.getSelectionModel().clearSelection();
        txtNumeroCamiseta.clear();
        lblMensaje.setText("");
        tablaJugadores.getSelectionModel().clearSelection();
    }

    // ── Metodos privados de apoyo ────────────────────────────────────────────

    /**
     * Construye un objeto Jugador con los valores actuales del formulario.
     *
     * @return Jugador con los datos del formulario
     * @throws NumberFormatException si el numero de camiseta no es entero valido
     */
    private Jugador construirJugadorDesdeFormulario() {
        String nombre     = txtNombre.getText().trim();
        String id         = txtIdentificacion.getText().trim();
        String contacto   = txtContacto.getText().trim();
        String posicion   = cmbPosicion.getValue();
        String camisetaTxt = txtNumeroCamiseta.getText().trim();

        if (camisetaTxt.isEmpty()) throw new IllegalArgumentException("El numero de camiseta es obligatorio.");
        int camiseta = Integer.parseInt(camisetaTxt);

        return new Jugador(nombre, id, contacto, posicion, camiseta);
    }

    /**
     * Llena los campos del formulario con los datos de un jugador.
     *
     * @param jugador objeto Jugador con los datos a mostrar
     */
    private void llenarFormulario(Jugador jugador) {
        txtIdentificacion.setText(jugador.getIdentificacion());
        txtNombre.setText(jugador.getNombre());
        txtContacto.setText(jugador.getContacto());
        cmbPosicion.setValue(jugador.getPosicion());
        txtNumeroCamiseta.setText(String.valueOf(jugador.getNumeroCamiseta()));
        lblMensaje.setText("");
    }

    /**
     * Recarga la tabla con la lista actualizada de jugadores desde el service.
     */
    private void refrescarTabla() {
        ObservableList<Jugador> datos =
            FXCollections.observableArrayList(jugadorService.listarJugadores());
        tablaJugadores.setItems(datos);
    }

    /** Muestra un mensaje de exito en verde. */
    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
    }

    /** Muestra un mensaje de error en rojo. */
    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #c62828;");
    }
}
