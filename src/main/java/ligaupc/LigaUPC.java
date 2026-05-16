package ligaupc;

import java.util.Scanner;
import ligaupc.service.*;
import ligaupc.view.*;

/**
 * Punto de entrada del sistema Liga UPC.
 * Ensambla todas las capas: instancia Services y Views, comparte el Scanner,
 * y presenta el menú principal de navegación.
 */
public class LigaUPC {

    public static void main(String[] args) {

        // ── Un solo Scanner para toda la aplicación ──────────────────────────
        Scanner scanner = new Scanner(System.in);

        // ── Instanciar Services ───────────────────────────────────────────────
        JugadorService jugadorService       = new JugadorService();
        TecnicoService tecnicoService       = new TecnicoService();
        ArbitroService arbitroService       = new ArbitroService();
        EquipoService equipoService         = new EquipoService();
        PartidoService partidoService       = new PartidoService();
        EstadisticaService estadisticaService = new EstadisticaService();

        // ── Instanciar Views (inyectando sus dependencias) ────────────────────
        JugadorView jugadorView         = new JugadorView(jugadorService, scanner);
        TecnicoView tecnicoView         = new TecnicoView(tecnicoService, scanner);
        ArbitroView arbitroView         = new ArbitroView(arbitroService, scanner);
        EquipoView equipoView           = new EquipoView(equipoService, jugadorService, scanner);
        PartidoView partidoView         = new PartidoView(partidoService, equipoService, arbitroService, scanner);
        EstadisticaView estadisticaView = new EstadisticaView(estadisticaService, scanner);

        // ── Menú principal ────────────────────────────────────────────────────
        int opcion;
        do {
            System.out.println("\n+----------------------------------+");
            System.out.println("|     LIGA UPC — MENÚ PRINCIPAL    |");
            System.out.println("+----------------------------------+");
            System.out.println("|  1. Gestión de Jugadores         |");
            System.out.println("|  2. Gestión de Técnicos          |");
            System.out.println("|  3. Gestión de Árbitros          |");
            System.out.println("|  4. Gestión de Equipos           |");
            System.out.println("|  5. Gestión de Partidos          |");
            System.out.println("|  6. Estadísticas de la Liga      |");
            System.out.println("|  0. Salir                        |");
            System.out.println("+----------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero(scanner);

            switch (opcion) {
                case 1 -> jugadorView.mostrarMenu();
                case 2 -> tecnicoView.mostrarMenu();
                case 3 -> arbitroView.mostrarMenu();
                case 4 -> equipoView.mostrarMenu();
                case 5 -> partidoView.mostrarMenu();
                case 6 -> estadisticaView.mostrarMenu();
                case 0 -> System.out.println("\n¡Hasta luego! Sistema Liga UPC cerrado.");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static int leerEntero(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Ingrese un número entero: ");
            }
        }
    }
}

