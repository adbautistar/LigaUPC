package ligaupc.view.fx;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controlador de la ventana principal del sistema Liga UPC.
 *
 * <h2>Patron GRASP Controller</h2>
 * <p>MainController es el receptor de los eventos de navegacion del sistema.
 * No contiene logica de negocio — delega en los Controllers de cada modulo.
 * Su unica responsabilidad es cargar el FXML correcto en el area de contenido.</p>
 *
 * <h2>Patron GRASP High Cohesion</h2>
 * <p>Solo gestiona la navegacion entre modulos. Nada mas.</p>
 *
 * <h2>Patron GRASP Low Coupling</h2>
 * <p>No conoce a ningun Controller de modulo. Solo conoce la ruta de sus FXML.
 * Cada Controller de modulo se instancia internamente por FXMLLoader.</p>
 *
 * <h2>Patron SOLID OCP</h2>
 * <p>Agregar un nuevo modulo = crear su FXML + agregar un metodo load aqui.
 * No se modifica la logica de los modulos existentes.</p>
 */
public class MainController {

    /** Area central donde se cargan los FXML de cada modulo. */
    @FXML
    private StackPane contentArea;

    // ── Manejadores de eventos de la barra lateral ───────────────────────────

    /** Carga el formulario de gestion de Jugadores. */
    @FXML
    private void loadJugadores() {
        loadModule("jugador");
    }

    /** Carga el formulario de gestion de Tecnicos. */
    @FXML
    private void loadTecnicos() {
        loadModule("tecnico");
    }

    /** Carga el formulario de gestion de Arbitros. */
    @FXML
    private void loadArbitros() {
        loadModule("arbitro");
    }

    /** Carga el formulario de gestion de Equipos. */
    @FXML
    private void loadEquipos() {
        loadModule("equipo");
    }

    /** Carga el formulario de gestion de Partidos. */
    @FXML
    private void loadPartidos() {
        loadModule("partido");
    }

    /** Carga la vista de Estadisticas de la liga. */
    @FXML
    private void loadEstadisticas() {
        loadModule("estadistica");
    }

    // ── Metodo privado de navegacion ─────────────────────────────────────────

    /**
     * Carga un modulo FXML en el area de contenido central.
     *
     * <p>Si el FXML del modulo aun no existe (fases de implementacion pendientes),
     * muestra un placeholder informativo en lugar de lanzar una excepcion.
     * Esto permite navegar la aplicacion aunque no todos los modulos esten listos.</p>
     *
     * @param modulo nombre del archivo FXML sin extension (ej: "jugador")
     */
    private void loadModule(String modulo) {
        String ruta = "/ligaupc/view/fx/" + modulo + ".fxml";
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(ruta));
            contentArea.getChildren().setAll(vista);
        } catch (IOException | NullPointerException e) {
            mostrarPlaceholder(modulo);
        }
    }

    /**
     * Muestra un panel temporal cuando el FXML del modulo aun no esta implementado.
     *
     * @param modulo nombre del modulo pendiente
     */
    private void mostrarPlaceholder(String modulo) {
        VBox placeholder = new VBox(12);
        placeholder.setAlignment(Pos.CENTER);

        Label titulo = new Label("Modulo en construccion: " + capitalize(modulo));
        titulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        Label info = new Label("Este formulario se implementara en la siguiente fase.");
        info.setStyle("-fx-font-size: 13; -fx-text-fill: #757575;");

        placeholder.getChildren().addAll(titulo, info);
        contentArea.getChildren().setAll(placeholder);
    }

    /**
     * Capitaliza la primera letra de un texto.
     *
     * @param texto texto a capitalizar
     * @return texto con primera letra en mayuscula
     */
    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
