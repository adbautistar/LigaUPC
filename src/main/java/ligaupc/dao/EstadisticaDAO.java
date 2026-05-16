package ligaupc.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ligaupc.model.Estadistica;
import ligaupc.model.ICRUD;

/**
 * GRASP Expert: única responsable de persistir Estadísticas en archivo de texto.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Estadistica>, intercambiable sin tocar el Service.
 *
 * Colabora con EquipoDAO para reconstruir la referencia al equipo.
 * Usa el nombre del equipo como identificador único.
 */
public class EstadisticaDAO implements ICRUD<Estadistica> {

    private static final String ARCHIVO = "estadisticas.txt";
    private static final String SEPARADOR = ";";

    private final EquipoDAO equipoDAO = new EquipoDAO();

    @Override
    public boolean crear(Estadistica estadistica) {
        if (estadistica == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(estadisticaALinea(estadistica));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar estadística: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Estadistica leerPorId(String nombreEquipo) {
        for (Estadistica e : listarTodos()) {
            if (e.getEquipo().getNombre().equalsIgnoreCase(nombreEquipo)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public List<Estadistica> listarTodos() {
        List<Estadistica> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaAEstadistica(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer estadísticas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Estadistica estadisticaActualizada) {
        List<Estadistica> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getEquipo().getNombre()
                    .equalsIgnoreCase(estadisticaActualizada.getEquipo().getNombre())) {
                lista.set(i, estadisticaActualizada);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String nombreEquipo) {
        List<Estadistica> lista = listarTodos();
        boolean eliminado = lista.removeIf(
            e -> e.getEquipo().getNombre().equalsIgnoreCase(nombreEquipo)
        );
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String estadisticaALinea(Estadistica e) {
        return e.getEquipo().getNombre() + SEPARADOR
             + e.getPartidosJugados() + SEPARADOR
             + e.getPartidosGanados() + SEPARADOR
             + e.getPartidosEmpatados() + SEPARADOR
             + e.getPartidosPerdidos() + SEPARADOR
             + e.getGolesAFavor() + SEPARADOR
             + e.getGolesEnContra() + SEPARADOR
             + e.getPuntos();
    }

    private Estadistica lineaAEstadistica(String linea) {
        String[] partes = linea.split(SEPARADOR);

        Estadistica estadistica = new Estadistica();
        estadistica.setEquipo(equipoDAO.leerPorId(partes[0]));
        estadistica.setPartidosJugados(Integer.parseInt(partes[1]));
        estadistica.setPartidosGanados(Integer.parseInt(partes[2]));
        estadistica.setPartidosEmpatados(Integer.parseInt(partes[3]));
        estadistica.setPartidosPerdidos(Integer.parseInt(partes[4]));
        estadistica.setGolesAFavor(Integer.parseInt(partes[5]));
        estadistica.setGolesEnContra(Integer.parseInt(partes[6]));
        estadistica.setPuntos(Integer.parseInt(partes[7]));

        return estadistica;
    }

    private void guardarTodos(List<Estadistica> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Estadistica e : lista) {
                writer.write(estadisticaALinea(e));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
