package ligaupc.view;

import java.util.List;
import java.util.Scanner;
import ligaupc.model.Jugador;
import ligaupc.service.JugadorService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Jugadores.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario (consola → GUI).
 */
public class JugadorView {

    private final JugadorService jugadorService;
    private final Scanner scanner;

    public JugadorView(JugadorService jugadorService, Scanner scanner) {
        this.jugadorService = jugadorService;
        this.scanner = scanner;
    }

    /**
     * Muestra el menú de gestión de jugadores y procesa la opción elegida.
     */
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|      GESTIÓN DE JUGADORES    |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Registrar jugador        |");
            System.out.println("|  2. Listar jugadores         |");
            System.out.println("|  3. Buscar jugador           |");
            System.out.println("|  4. Actualizar jugador       |");
            System.out.println("|  5. Eliminar jugador         |");
            System.out.println("|  0. Volver al menú principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarJugador();
                case 2 -> listarJugadores();
                case 3 -> buscarJugador();
                case 4 -> actualizarJugador();
                case 5 -> eliminarJugador();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ── Operaciones ──────────────────────────────────────────────────────────

    private void registrarJugador() {
        System.out.println("\n── Registrar Jugador ──");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();
        System.out.print("Contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Posición (Delantero, Defensa, Portero...): ");
        String posicion = scanner.nextLine();
        System.out.print("Número de camiseta: ");
        int numeroCamiseta = leerEntero();

        try {
            Jugador jugador = new Jugador(nombre, identificacion, contacto, posicion, numeroCamiseta);
            jugadorService.registrarJugador(jugador);
            System.out.println("Jugador registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarJugadores() {
        List<Jugador> jugadores = jugadorService.listarJugadores();

        if (jugadores.isEmpty()) {
            System.out.println("\nNo hay jugadores registrados.");
            return;
        }

        System.out.println("\n── Lista de Jugadores ──");
        for (int i = 0; i < jugadores.size(); i++) {
            System.out.println((i + 1) + ". " + jugadores.get(i).obtenerPerfil());
        }
    }

    private void buscarJugador() {
        System.out.println("\n── Buscar Jugador ──");
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();

        try {
            Jugador jugador = jugadorService.buscarJugador(identificacion);
            System.out.println("\nJugador encontrado:");
            System.out.println(jugador.obtenerPerfil());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarJugador() {
        System.out.println("\n── Actualizar Jugador ──");
        System.out.print("Identificación del jugador a actualizar: ");
        String identificacion = scanner.nextLine();

        try {
            jugadorService.buscarJugador(identificacion);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Nueva posición: ");
        String posicion = scanner.nextLine();
        System.out.print("Nuevo número de camiseta: ");
        int numeroCamiseta = leerEntero();

        try {
            Jugador jugador = new Jugador(nombre, identificacion, contacto, posicion, numeroCamiseta);
            jugadorService.actualizarJugador(jugador);
            System.out.println("Jugador actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarJugador() {
        System.out.println("\n── Eliminar Jugador ──");
        System.out.print("Identificación del jugador a eliminar: ");
        String identificacion = scanner.nextLine();

        try {
            jugadorService.eliminarJugador(identificacion);
            System.out.println("Jugador eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private int leerEntero() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Ingrese un número entero: ");
            }
        }
    }
}

