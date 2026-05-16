package ligaupc.view;

import java.util.List;
import java.util.Scanner;
import ligaupc.model.Equipo;
import ligaupc.model.Jugador;
import ligaupc.service.EquipoService;
import ligaupc.service.JugadorService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Equipos.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario.
 */
public class EquipoView {

    private final EquipoService equipoService;
    private final JugadorService jugadorService;
    private final Scanner scanner;

    public EquipoView(EquipoService equipoService, JugadorService jugadorService, Scanner scanner) {
        this.equipoService = equipoService;
        this.jugadorService = jugadorService;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|       GESTIÓN DE EQUIPOS     |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Registrar equipo         |");
            System.out.println("|  2. Listar equipos           |");
            System.out.println("|  3. Buscar equipo            |");
            System.out.println("|  4. Actualizar equipo        |");
            System.out.println("|  5. Eliminar equipo          |");
            System.out.println("|  6. Asignar tecnico          |");
            System.out.println("|  7. Agregar jugador          |");
            System.out.println("|  8. Quitar jugador           |");
            System.out.println("|  0. Volver al menu principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarEquipo();
                case 2 -> listarEquipos();
                case 3 -> buscarEquipo();
                case 4 -> actualizarEquipo();
                case 5 -> eliminarEquipo();
                case 6 -> asignarTecnico();
                case 7 -> agregarJugador();
                case 8 -> quitarJugador();
                case 0 -> System.out.println("Volviendo al menu principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ── Operaciones CRUD ─────────────────────────────────────────────────────

    private void registrarEquipo() {
        System.out.println("\n── Registrar Equipo ──");
        System.out.print("Nombre del equipo: ");
        String nombre = scanner.nextLine();
        System.out.print("Sede: ");
        String sede = scanner.nextLine();

        try {
            Equipo equipo = new Equipo(nombre, sede);
            equipoService.registrarEquipo(equipo);
            System.out.println("Equipo registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarEquipos() {
        List<Equipo> equipos = equipoService.listarEquipos();

        if (equipos.isEmpty()) {
            System.out.println("\nNo hay equipos registrados.");
            return;
        }

        System.out.println("\n── Lista de Equipos ──");
        for (Equipo e : equipos) {
            System.out.println("Equipo: " + e.getNombre() + " | Sede: " + e.getSede());
            if (e.getTecnico() != null) {
                System.out.println("   " + e.getTecnico().obtenerPerfil());
            } else {
                System.out.println("   Sin técnico asignado.");
            }
            System.out.println("   Jugadores (" + e.getJugadores().size() + "):");
            if (e.getJugadores().isEmpty()) {
                System.out.println("     Sin jugadores registrados.");
            } else {
                for (Jugador j : e.getJugadores()) {
                    System.out.println("     - " + j.obtenerPerfil());
                }
            }
        }
    }

    private void buscarEquipo() {
        System.out.println("\n── Buscar Equipo ──");
        System.out.print("Nombre del equipo: ");
        String nombre = scanner.nextLine();

        try {
            Equipo equipo = equipoService.buscarEquipo(nombre);
            System.out.println("\nEquipo encontrado:");
            System.out.println("Nombre: " + equipo.getNombre() + " | Sede: " + equipo.getSede());
            if (equipo.getTecnico() != null) {
                System.out.println(equipo.getTecnico().obtenerPerfil());
            }
            System.out.println("Jugadores: " + equipo.getJugadores().size());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarEquipo() {
        System.out.println("\n── Actualizar Equipo ──");
        System.out.print("Nombre del equipo a actualizar: ");
        String nombre = scanner.nextLine();

        try {
            equipoService.buscarEquipo(nombre);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Nueva sede: ");
        String sede = scanner.nextLine();

        try {
            Equipo equipo = new Equipo(nombre, sede);
            equipoService.actualizarEquipo(equipo);
            System.out.println("Equipo actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarEquipo() {
        System.out.println("\n── Eliminar Equipo ──");
        System.out.print("Nombre del equipo a eliminar: ");
        String nombre = scanner.nextLine();

        try {
            equipoService.eliminarEquipo(nombre);
            System.out.println("Equipo eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Operaciones de dominio ────────────────────────────────────────────────

    private void asignarTecnico() {
        System.out.println("\n── Asignar Técnico a Equipo ──");
        System.out.print("Nombre del equipo: ");
        String nombreEquipo = scanner.nextLine();
        System.out.print("Identificación del técnico: ");
        String idTecnico = scanner.nextLine();

        try {
            equipoService.asignarTecnico(nombreEquipo, idTecnico);
            System.out.println("Técnico asignado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void agregarJugador() {
        System.out.println("\n-- Agregar Jugador a Equipo --");

        // Mostrar jugadores registrados para que el usuario conozca los IDs
        List<Jugador> todos = jugadorService.listarJugadores();
        if (todos.isEmpty()) {
            System.out.println("No hay jugadores registrados en el sistema. Registre un jugador primero.");
            return;
        }
        System.out.println("Jugadores disponibles:");
        for (Jugador j : todos) {
            System.out.println("  " + j.obtenerPerfil() + " | ID: " + j.getIdentificacion());
        }

        System.out.print("\nNombre del equipo: ");
        String nombreEquipo = scanner.nextLine();
        System.out.print("Identificacion del jugador: ");
        String idJugador = scanner.nextLine();

        try {
            equipoService.agregarJugador(nombreEquipo, idJugador);
            System.out.println("Jugador agregado al equipo exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void quitarJugador() {
        System.out.println("\n-- Quitar Jugador de Equipo --");
        System.out.print("Nombre del equipo: ");
        String nombreEquipo = scanner.nextLine();

        // Mostrar jugadores actuales del equipo antes de pedir el ID
        try {
            Equipo equipo = equipoService.buscarEquipo(nombreEquipo);
            List<Jugador> jugadores = equipo.getJugadores();
            if (jugadores.isEmpty()) {
                System.out.println("El equipo '" + nombreEquipo + "' no tiene jugadores asignados.");
                return;
            }
            System.out.println("Jugadores actuales del equipo:");
            for (Jugador j : jugadores) {
                System.out.println("  " + j.obtenerPerfil() + " | ID: " + j.getIdentificacion());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Identificacion del jugador a quitar: ");
        String idJugador = scanner.nextLine();

        try {
            equipoService.quitarJugador(nombreEquipo, idJugador);
            System.out.println("Jugador retirado del equipo exitosamente.");
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

