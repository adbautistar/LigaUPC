package ligaupc;

/**
 * Clase de arranque para NetBeans e IDEs que no configuran
 * el module-path de JavaFX automaticamente.
 *
 * <h2>Por que existe esta clase</h2>
 * <p>En Java 11+ (con el sistema de modulos), si una clase extiende
 * {@code javafx.application.Application} y se intenta ejecutar directamente
 * con {@code java MiClase}, la JVM lanza el error:<br>
 * <em>"Error: el metodo principal debe devolver un valor del tipo void"</em><br>
 * porque detecta que la clase es una {@code Application} JavaFX y exige
 * que el module-path este configurado explicitamente.</p>
 *
 * <h2>Solucion</h2>
 * <p>Esta clase NO extiende {@code Application}, por lo que la JVM la trata
 * como una clase Java normal con un {@code main()} valido.
 * Delega inmediatamente en {@link MainApp#main(String[])} que invoca
 * {@code Application.launch()} con el toolkit de JavaFX.</p>
 *
 * <h2>Configuracion en NetBeans</h2>
 * <p>En las propiedades del proyecto (clic derecho → Properties → Run)
 * establecer como clase principal: {@code ligaupc.Launcher}</p>
 */
public class Launcher {

    /**
     * Punto de entrada real del sistema desde NetBeans.
     * Delega en MainApp que extiende Application y arranca JavaFX.
     *
     * @param args argumentos de linea de comandos (no usados)
     */
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
