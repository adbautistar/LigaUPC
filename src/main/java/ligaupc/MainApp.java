package ligaupc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ligaupc.service.ArbitroService;
import ligaupc.service.EquipoService;
import ligaupc.service.EstadisticaService;
import ligaupc.service.JugadorService;
import ligaupc.service.PartidoService;
import ligaupc.service.TecnicoService;

/**
 * Punto de entrada JavaFX del sistema Liga UPC.
 *
 * <h2>Patron aplicado: ServiceLocator</h2>
 * <p>JavaFX instancia los Controllers internamente via FXMLLoader, sin permitir
 * constructores parametrizados. Por eso MainApp actua como ServiceLocator:
 * crea todos los Services en {@code init()} y los expone con getters estaticos
 * para que cualquier Controller los consuma sin acoplarse entre si.</p>
 *
 * <h2>GRASP Creator</h2>
 * <p>MainApp crea los Services porque es quien los agrega, los usa como punto
 * de partida y tiene la informacion de qué dependencias existen.</p>
 *
 * <h2>GRASP Controller</h2>
 * <p>MainApp es el Controller del sistema completo: orquesta el arranque,
 * la inicializacion de recursos y la carga de la ventana principal.</p>
 */
public class MainApp extends Application {

    // ── Instancias unicas de cada Service (ServiceLocator) ──────────────────
    private static JugadorService jugadorService;
    private static TecnicoService tecnicoService;
    private static ArbitroService arbitroService;
    private static EquipoService equipoService;
    private static PartidoService partidoService;
    private static EstadisticaService estadisticaService;

    /**
     * Se ejecuta antes de {@code start()}, en un hilo no-UI.
     * Lugar correcto para inicializar recursos pesados (Services, DAOs)
     * sin bloquear el hilo de JavaFX.
     */
    @Override
    public void init() {
        jugadorService     = new JugadorService();
        tecnicoService     = new TecnicoService();
        arbitroService     = new ArbitroService();
        equipoService      = new EquipoService();
        partidoService     = new PartidoService();
        estadisticaService = new EstadisticaService();
    }

    /**
     * Carga la ventana principal desde {@code main.fxml} y la muestra.
     *
     * @param stage ventana principal provista por el runtime de JavaFX
     * @throws Exception si el archivo FXML no se encuentra en el classpath
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
            getClass().getResource("/ligaupc/view/fx/main.fxml")
        );
        Scene scene = new Scene(root, 960, 620);
        stage.setTitle("Liga UPC — Sistema de Gestion Futbol Universitario");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    // ── Getters estaticos del ServiceLocator ────────────────────────────────

    /** @return instancia unica de JugadorService */
    public static JugadorService getJugadorService()         { return jugadorService; }

    /** @return instancia unica de TecnicoService */
    public static TecnicoService getTecnicoService()         { return tecnicoService; }

    /** @return instancia unica de ArbitroService */
    public static ArbitroService getArbitroService()         { return arbitroService; }

    /** @return instancia unica de EquipoService */
    public static EquipoService getEquipoService()           { return equipoService; }

    /** @return instancia unica de PartidoService */
    public static PartidoService getPartidoService()         { return partidoService; }

    /** @return instancia unica de EstadisticaService */
    public static EstadisticaService getEstadisticaService() { return estadisticaService; }

    /**
     * Punto de entrada JVM. Delega en {@code Application.launch()} que
     * inicializa el toolkit de JavaFX e invoca init() y start().
     *
     * @param args argumentos de linea de comandos (no usados)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
