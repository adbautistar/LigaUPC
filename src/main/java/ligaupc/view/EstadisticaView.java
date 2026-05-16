package ligaupc.view;

import java.util.List;
import java.util.Scanner;
import ligaupc.model.Estadistica;
import ligaupc.service.EstadisticaService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Estadísticas.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario.
 */
public class EstadisticaView {

    private final EstadisticaService estadisticaService;
    private final Scanner scanner;

    public EstadisticaView(EstadisticaService estadisticaService, Scanner scanner) {
        this.estadisticaService = estadisticaService;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|    ESTADÍSTICAS DE LA LIGA   |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Ver tabla de posiciones  |");
            System.out.println("|  2. Buscar estadística       |");
            System.out.println("|  0. Volver al menú principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> mostrarClasificacion();
                case 2 -> buscarEstadistica();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ── Operaciones ──────────────────────────────────────────────────────────

    private void mostrarClasificacion() {
        List<Estadistica> clasificacion = estadisticaService.listarClasificacion();

        if (clasificacion.isEmpty()) {
            System.out.println("\nNo hay estadísticas registradas aún.");
            return;
        }

        System.out.println("\n+------------------------------------------------------------------+");
        System.out.println("|                    TABLA DE POSICIONES                          |");
        System.out.println("+---+------------------+---+---+---+---+-----+-----+------+-----+");
        System.out.println("| # | Equipo           | J | G | E | P |  GF |  GC |  DG  | Pts |");
        System.out.println("+---+------------------+---+---+---+---+-----+-----+------+-----+");

        for (int i = 0; i < clasificacion.size(); i++) {
            Estadistica e = clasificacion.get(i);
            int dg = e.getGolesAFavor() - e.getGolesEnContra();
            System.out.printf("| %-2d| %-17s| %-2d| %-2d| %-2d| %-2d|  %-4d|  %-4d|  %-5d|  %-3d|%n",
                i + 1,
                e.getEquipo().getNombre(),
                e.getPartidosJugados(),
                e.getPartidosGanados(),
                e.getPartidosEmpatados(),
                e.getPartidosPerdidos(),
                e.getGolesAFavor(),
                e.getGolesEnContra(),
                dg,
                e.getPuntos());
        }
        System.out.println("+---+------------------+---+---+---+---+-----+-----+------+-----+");
        System.out.println("  J=Jugados  G=Ganados  E=Empatados  P=Perdidos");
        System.out.println("  GF=Goles a favor  GC=Goles en contra  DG=Diferencia  Pts=Puntos");
    }

    private void buscarEstadistica() {
        System.out.println("\n── Buscar Estadística ──");
        System.out.print("Nombre del equipo: ");
        String nombre = scanner.nextLine();

        try {
            Estadistica e = estadisticaService.buscarEstadistica(nombre);
            int dg = e.getGolesAFavor() - e.getGolesEnContra();
            System.out.println("\nEstadísticas de " + e.getEquipo().getNombre() + ":");
            System.out.println("  Partidos jugados : " + e.getPartidosJugados());
            System.out.println("  Ganados          : " + e.getPartidosGanados());
            System.out.println("  Empatados        : " + e.getPartidosEmpatados());
            System.out.println("  Perdidos         : " + e.getPartidosPerdidos());
            System.out.println("  Goles a favor    : " + e.getGolesAFavor());
            System.out.println("  Goles en contra  : " + e.getGolesEnContra());
            System.out.println("  Diferencia goles : " + dg);
            System.out.println("  Puntos           : " + e.getPuntos());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Ingrese un número entero: ");
            }
        }
    }
}

