package ligaupc.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ligaupc.model.Arbitro;
import ligaupc.model.ICRUD;

/**
 * GRASP Expert: única responsable de persistir Árbitros en archivo de texto.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Arbitro>, intercambiable sin tocar el Service.
 */
public class ArbitroDAO implements ICRUD<Arbitro> {

    private static final String ARCHIVO = "arbitros.txt";
    private static final String SEPARADOR = ";";

    @Override
    public boolean crear(Arbitro arbitro) {
        if (arbitro == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(arbitroALinea(arbitro));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar árbitro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Arbitro leerPorId(String identificacion) {
        for (Arbitro a : listarTodos()) {
            if (a.getIdentificacion().equals(identificacion)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<Arbitro> listarTodos() {
        List<Arbitro> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaAArbitro(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer árbitros: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Arbitro arbitroActualizado) {
        List<Arbitro> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdentificacion().equals(arbitroActualizado.getIdentificacion())) {
                lista.set(i, arbitroActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String identificacion) {
        List<Arbitro> lista = listarTodos();
        boolean eliminado = lista.removeIf(a -> a.getIdentificacion().equals(identificacion));
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String arbitroALinea(Arbitro a) {
        return a.getNombre() + SEPARADOR
             + a.getIdentificacion() + SEPARADOR
             + a.getContacto() + SEPARADOR
             + a.getCategoriaCertificacion();
    }

    private Arbitro lineaAArbitro(String linea) {
        String[] partes = linea.split(SEPARADOR);
        return new Arbitro(
            partes[0],  // nombre
            partes[1],  // identificacion
            partes[2],  // contacto
            partes[3]   // categoriaCertificacion
        );
    }

    private void guardarTodos(List<Arbitro> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Arbitro a : lista) {
                writer.write(arbitroALinea(a));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
