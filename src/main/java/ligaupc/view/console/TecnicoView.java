package ligaupc.view.console;

import java.util.List;
import java.util.Scanner;
import ligaupc.model.Tecnico;
import ligaupc.service.TecnicoService;

/**
 * GRASP High Cohesion: gestiona únicamente la interacción con el usuario para Técnicos.
 * SOLID SRP: su única razón de cambio es si cambia la interfaz de usuario.
 */
public class TecnicoView {

    private final TecnicoService tecnicoService;
    private final Scanner scanner;

    public TecnicoView(TecnicoService tecnicoService, Scanner scanner) {
        this.tecnicoService = tecnicoService;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n+------------------------------+");
            System.out.println("|      GESTIÓN DE TÉCNICOS     |");
            System.out.println("+------------------------------+");
            System.out.println("|  1. Registrar técnico        |");
            System.out.println("|  2. Listar técnicos          |");
            System.out.println("|  3. Buscar técnico           |");
            System.out.println("|  4. Actualizar técnico       |");
            System.out.println("|  5. Eliminar técnico         |");
            System.out.println("|  0. Volver al menú principal |");
            System.out.println("+------------------------------+");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarTecnico();
                case 2 -> listarTecnicos();
                case 3 -> buscarTecnico();
                case 4 -> actualizarTecnico();
                case 5 -> eliminarTecnico();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void registrarTecnico() {
        System.out.println("\n── Registrar Técnico ──");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();
        System.out.print("Contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Especialidad (ej: 4-4-2, Ofensiva): ");
        String especialidad = scanner.nextLine();

        try {
            Tecnico tecnico = new Tecnico(nombre, identificacion, contacto, especialidad);
            tecnicoService.registrarTecnico(tecnico);
            System.out.println("Técnico registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarTecnicos() {
        List<Tecnico> tecnicos = tecnicoService.listarTecnicos();

        if (tecnicos.isEmpty()) {
            System.out.println("\nNo hay técnicos registrados.");
            return;
        }

        System.out.println("\n── Lista de Técnicos ──");
        for (int i = 0; i < tecnicos.size(); i++) {
            System.out.println((i + 1) + ". " + tecnicos.get(i).obtenerPerfil());
        }
    }

    private void buscarTecnico() {
        System.out.println("\n── Buscar Técnico ──");
        System.out.print("Identificación: ");
        String identificacion = scanner.nextLine();

        try {
            Tecnico tecnico = tecnicoService.buscarTecnico(identificacion);
            System.out.println("\nTécnico encontrado:");
            System.out.println(tecnico.obtenerPerfil());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarTecnico() {
        System.out.println("\n── Actualizar Técnico ──");
        System.out.print("Identificación del técnico a actualizar: ");
        String identificacion = scanner.nextLine();

        try {
            tecnicoService.buscarTecnico(identificacion);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo contacto: ");
        String contacto = scanner.nextLine();
        System.out.print("Nueva especialidad: ");
        String especialidad = scanner.nextLine();

        try {
            Tecnico tecnico = new Tecnico(nombre, identificacion, contacto, especialidad);
            tecnicoService.actualizarTecnico(tecnico);
            System.out.println("Técnico actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarTecnico() {
        System.out.println("\n── Eliminar Técnico ──");
        System.out.print("Identificación del técnico a eliminar: ");
        String identificacion = scanner.nextLine();

        try {
            tecnicoService.eliminarTecnico(identificacion);
            System.out.println("Técnico eliminado exitosamente.");
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

