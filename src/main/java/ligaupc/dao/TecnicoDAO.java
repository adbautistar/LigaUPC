package ligaupc.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ligaupc.model.ICRUD;
import ligaupc.model.Tecnico;

/**
 * GRASP Expert: única responsable de persistir Técnicos en archivo de texto.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Tecnico>, intercambiable sin tocar el Service.
 */
public class TecnicoDAO implements ICRUD<Tecnico> {

    private static final String ARCHIVO = "tecnicos.txt";
    private static final String SEPARADOR = ";";

    @Override
    public boolean crear(Tecnico tecnico) {
        if (tecnico == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(tecnicoALinea(tecnico));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar técnico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Tecnico leerPorId(String identificacion) {
        for (Tecnico t : listarTodos()) {
            if (t.getIdentificacion().equals(identificacion)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public List<Tecnico> listarTodos() {
        List<Tecnico> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaATecnico(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer técnicos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Tecnico tecnicoActualizado) {
        List<Tecnico> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdentificacion().equals(tecnicoActualizado.getIdentificacion())) {
                lista.set(i, tecnicoActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String identificacion) {
        List<Tecnico> lista = listarTodos();
        boolean eliminado = lista.removeIf(t -> t.getIdentificacion().equals(identificacion));
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String tecnicoALinea(Tecnico t) {
        return t.getNombre() + SEPARADOR
             + t.getIdentificacion() + SEPARADOR
             + t.getContacto() + SEPARADOR
             + t.getEspecialidad();
    }

    private Tecnico lineaATecnico(String linea) {
        String[] partes = linea.split(SEPARADOR);
        return new Tecnico(
            partes[0],  // nombre
            partes[1],  // identificacion
            partes[2],  // contacto
            partes[3]   // especialidad
        );
    }

    private void guardarTodos(List<Tecnico> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Tecnico t : lista) {
                writer.write(tecnicoALinea(t));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
