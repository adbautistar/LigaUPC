package ligaupc.view.fx;

import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ligaupc.MainApp;
import ligaupc.model.Estadistica;
import ligaupc.service.EstadisticaService;

/**
 * Controlador JavaFX de la vista de Estadisticas (Tabla de Posiciones).
 *
 * <h2>Vista de solo lectura</h2>
 * <p>No tiene formulario de creacion ni edicion. Las estadisticas las genera
 * automaticamente {@link ligaupc.service.PartidoService} al registrar resultados.</p>
 *
 * <h2>Patron GRASP Expert</h2>
 * <p>{@link EstadisticaService} es el experto en ordenar la clasificacion.
 * Este Controller solo pide los datos ordenados y los muestra en la tabla.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>Accede a {@link EstadisticaService} via {@link MainApp#getEstadisticaService()}.
 * No conoce al DAO ni al algoritmo de ordenamiento.</p>
 *
 * <h2>Patron SOLID SRP</h2>
 * <p>Si cambia el criterio de clasificacion, solo cambia EstadisticaService.
 * Este Controller no necesita modificarse.</p>
 *
 * <h2>Cell factories con lambda</h2>
 * <p>Estadistica contiene un objeto {@link ligaupc.model.Equipo}, no un String.
 * PropertyValueFactory no puede acceder a atributos anidados (equipo.getNombre()).
 * Por eso se usan lambdas con {@link SimpleStringProperty} y {@link SimpleIntegerProperty}.</p>
 */
public class EstadisticaController {

    // ── Tabla ────────────────────────────────────────────────────────────────
    @FXML private TableView<Estadistica>           tablaEstadisticas;
    @FXML private TableColumn<Estadistica, Number> colPos;
    @FXML private TableColumn<Estadistica, String> colEquipo;
    @FXML private TableColumn<Estadistica, Number> colPJ;
    @FXML private TableColumn<Estadistica, Number> colPG;
    @FXML private TableColumn<Estadistica, Number> colPE;
    @FXML private TableColumn<Estadistica, Number> colPP;
    @FXML private TableColumn<Estadistica, Number> colGF;
    @FXML private TableColumn<Estadistica, Number> colGC;
    @FXML private TableColumn<Estadistica, Number> colDG;
    @FXML private TableColumn<Estadistica, Number> colPts;

    // ── Mensaje ──────────────────────────────────────────────────────────────
    @FXML private Label lblMensaje;

    // ── Service (via ServiceLocator) ─────────────────────────────────────────
    private final EstadisticaService estadisticaService = MainApp.getEstadisticaService();

    /**
     * JavaFX invoca este metodo despues de cargar el FXML.
     * Configura las cell factories con lambda (necesario por el campo Equipo anidado)
     * y carga la clasificacion inicial.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        cargarClasificacion();
    }

    /** Recarga la tabla de posiciones desde el service. */
    @FXML
    private void refrescar() {
        cargarClasificacion();
        mostrarExito("Tabla actualizada.");
    }

    // ── Metodos privados ─────────────────────────────────────────────────────

    /**
     * Configura las cell factories de cada columna usando lambdas.
     *
     * <p>Se usa {@link SimpleStringProperty} / {@link SimpleIntegerProperty}
     * porque JavaFX requiere que los valores de celda sean observables (Property).
     * Con PropertyValueFactory esto es automatico para campos directos,
     * pero para campos anidados (equipo.getNombre()) se debe hacer manualmente.</p>
     */
    private void configurarColumnas() {
        // Columna # — posicion calculada dinamicamente segun el indice en la lista
        colPos.setCellValueFactory(cellData -> {
            int index = tablaEstadisticas.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(index);
        });

        // Columna Equipo — nombre del objeto Equipo anidado
        colEquipo.setCellValueFactory(cellData -> {
            Estadistica e = cellData.getValue();
            String nombre = (e.getEquipo() != null) ? e.getEquipo().getNombre() : "—";
            return new SimpleStringProperty(nombre);
        });

        // Columnas numericas — acceso directo a los atributos int
        colPJ .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPartidosJugados()));
        colPG .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPartidosGanados()));
        colPE .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPartidosEmpatados()));
        colPP .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPartidosPerdidos()));
        colGF .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGolesAFavor()));
        colGC .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGolesEnContra()));

        // Columna DG — diferencia de goles calculada (GF - GC)
        colDG .setCellValueFactory(c -> new SimpleIntegerProperty(
            c.getValue().getGolesAFavor() - c.getValue().getGolesEnContra()
        ));

        colPts.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPuntos()));
    }

    /**
     * Carga la clasificacion ordenada desde EstadisticaService y la pone en la tabla.
     * El ordenamiento (puntos > DG > GF) lo hace el service — no este Controller.
     */
    private void cargarClasificacion() {
        List<Estadistica> clasificacion = estadisticaService.listarClasificacion();
        tablaEstadisticas.setItems(FXCollections.observableArrayList(clasificacion));
        lblMensaje.setText("");
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
    }
}
