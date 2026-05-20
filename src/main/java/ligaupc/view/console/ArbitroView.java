package ligaupc.view.console;

import java.util.List;
import java.util.Scanner;
import ligaupc.model.Arbitro;
import ligaupc.service.ArbitroService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Árbitros.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario.
 */
public class ArbitroView {

    private final ArbitroService arbitroService;
    private final Scanner scanner;

    public ArbitroView(ArbitroService arbitroService, Scanner scanner) {
        this.arbitroService = arbitroService;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|      GESTIÓN DE ÁRBITROS     |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Registrar árbitro        |");
            System.out.println("|  2. Listar árbitros          |");
            System.out.println("|  3. Buscar árbitro           |");
            System.out.println("|  4. Actualizar árbitro       |");
            System.out.println("|  5. Eliminar árbitro         |");
            System.out.println("|  0. Volver al menú principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarArbitro();
                case 2 -> listarArbitros();
                case 3 -> buscarArbitro();
                case 4 -> actualizarArbitro();
                case 5 -> eliminarArbitro();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void registrarArbitro() {
        System.out.println("\n── Registrar Árbitro ──");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();
        System.out.print("Contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Categoría de certificación (FIFA, Nacional, Regional): ");
        String categoria = scanner.nextLine();

        try {
            Arbitro arbitro = new Arbitro(nombre, identificacion, contacto, categoria);
            arbitroService.registrarArbitro(arbitro);
            System.out.println("Árbitro registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarArbitros() {
        List<Arbitro> arbitros = arbitroService.listarArbitros();

        if (arbitros.isEmpty()) {
            System.out.println("\nNo hay árbitros registrados.");
            return;
        }

        System.out.println("\n── Lista de Árbitros ──");
        for (int i = 0; i < arbitros.size(); i++) {
            System.out.println((i + 1) + ". " + arbitros.get(i).obtenerPerfil());
        }
    }

    private void buscarArbitro() {
        System.out.println("\n── Buscar Árbitro ──");
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();

        try {
            Arbitro arbitro = arbitroService.buscarArbitro(identificacion);
            System.out.println("\nÁrbitro encontrado:");
            System.out.println(arbitro.obtenerPerfil());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarArbitro() {
        System.out.println("\n── Actualizar Árbitro ──");
        System.out.print("Identificación del árbitro a actualizar: ");
        String identificacion = scanner.nextLine();

        try {
            arbitroService.buscarArbitro(identificacion);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Nueva categoría de certificación: ");
        String categoria = scanner.nextLine();

        try {
            Arbitro arbitro = new Arbitro(nombre, identificacion, contacto, categoria);
            arbitroService.actualizarArbitro(arbitro);
            System.out.println("Árbitro actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarArbitro() {
        System.out.println("\n── Eliminar Árbitro ──");
        System.out.print("Identificación del árbitro a eliminar: ");
        String identificacion = scanner.nextLine();

        try {
            arbitroService.eliminarArbitro(identificacion);
            System.out.println("Árbitro eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
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

