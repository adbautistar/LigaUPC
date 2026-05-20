package ligaupc.view.console;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import ligaupc.model.Arbitro;
import ligaupc.model.Equipo;
import ligaupc.model.Partido;
import ligaupc.service.ArbitroService;
import ligaupc.service.EquipoService;
import ligaupc.service.PartidoService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Partidos.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario.
 */
public class PartidoView {

    private final PartidoService partidoService;
    private final EquipoService equipoService;
    private final ArbitroService arbitroService;
    private final Scanner scanner;
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    public PartidoView(PartidoService partidoService,
                       EquipoService equipoService,
                       ArbitroService arbitroService,
                       Scanner scanner) {
        this.partidoService = partidoService;
        this.equipoService = equipoService;
        this.arbitroService = arbitroService;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|      GESTIÓN DE PARTIDOS     |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Programar partido        |");
            System.out.println("|  2. Listar partidos          |");
            System.out.println("|  3. Buscar partido           |");
            System.out.println("|  4. Registrar resultado      |");
            System.out.println("|  5. Eliminar partido         |");
            System.out.println("|  0. Volver al menú principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> programarPartido();
                case 2 -> listarPartidos();
                case 3 -> buscarPartido();
                case 4 -> registrarResultado();
                case 5 -> eliminarPartido();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ── Operaciones ──────────────────────────────────────────────────────────

    private void programarPartido() {
        System.out.println("\n── Programar Partido ──");
        System.out.print("ID del partido: ");
        int id = leerEntero();
        System.out.print("Fecha (dd/MM/yyyy): ");
        Date fecha = leerFecha();
        System.out.print("Nombre equipo local: ");
        String nombreLocal = scanner.nextLine();
        System.out.print("Nombre equipo visitante: ");
        String nombreVisitante = scanner.nextLine();
        System.out.print("Identificación del árbitro: ");
        String idArbitro = scanner.nextLine();

        try {
            Equipo local = equipoService.buscarEquipo(nombreLocal);
            Equipo visitante = equipoService.buscarEquipo(nombreVisitante);
            Arbitro arbitro = arbitroService.buscarArbitro(idArbitro);

            Partido partido = new Partido(id, fecha, local, visitante, arbitro);
            partidoService.registrarPartido(partido);
            System.out.println("Partido programado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarPartidos() {
        List<Partido> partidos = partidoService.listarPartidos();

        if (partidos.isEmpty()) {
            System.out.println("\nNo hay partidos registrados.");
            return;
        }

        System.out.println("\n── Lista de Partidos ──");
        for (Partido p : partidos) {
            System.out.printf("ID: %-3d | %s | Estado: %-11s | %s%n",
                p.getIdPartido(),
                FORMATO_FECHA.format(p.getFecha()),
                p.getEstado(),
                p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre());
            if ("Finalizado".equals(p.getEstado())) {
                System.out.println("        Resultado: " + p.obtenerResultado());
            }
        }
    }

    private void buscarPartido() {
        System.out.println("\n── Buscar Partido ──");
        System.out.print("ID del partido: ");
        int id = leerEntero();

        try {
            Partido partido = partidoService.buscarPartido(id);
            System.out.println("\nPartido encontrado:");
            System.out.println("ID: " + partido.getIdPartido()
                + " | Fecha: " + FORMATO_FECHA.format(partido.getFecha())
                + " | Estado: " + partido.getEstado());
            System.out.println(partido.getEquipoLocal().getNombre()
                + " vs " + partido.getEquipoVisitante().getNombre());
            if ("Finalizado".equals(partido.getEstado())) {
                System.out.println("Resultado: " + partido.obtenerResultado());
            }
            System.out.println("Árbitro: " + partido.getArbitro().obtenerPerfil());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarResultado() {
        System.out.println("\n── Registrar Resultado ──");
        System.out.print("ID del partido: ");
        int id = leerEntero();
        System.out.print("Goles equipo local: ");
        int golesLocal = leerEntero();
        System.out.print("Goles equipo visitante: ");
        int golesVisitante = leerEntero();

        try {
            partidoService.registrarResultado(id, golesLocal, golesVisitante);
            System.out.println("Resultado registrado exitosamente. Estadísticas actualizadas.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarPartido() {
        System.out.println("\n── Eliminar Partido ──");
        System.out.print("ID del partido a eliminar: ");
        int id = leerEntero();

        try {
            partidoService.eliminarPartido(id);
            System.out.println("Partido eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private Date leerFecha() {
        while (true) {
            String texto = scanner.nextLine().trim();
            try {
                FORMATO_FECHA.setLenient(false);
                return FORMATO_FECHA.parse(texto);
            } catch (ParseException e) {
                System.out.print("Formato inválido. Use dd/MM/yyyy: ");
            }
        }
    }

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

