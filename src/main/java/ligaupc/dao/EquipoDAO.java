package ligaupc.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ligaupc.model.Equipo;
import ligaupc.model.ICRUD;
import ligaupc.model.Jugador;
import ligaupc.model.Tecnico;

/**
 * GRASP Expert: única responsable de persistir Equipos en archivo de texto.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Equipo>, intercambiable sin tocar el Service.
 *
 * Colabora con TecnicoDAO y JugadorDAO para reconstruir referencias al leer.
 * Usa el nombre del equipo como identificador único.
 */
public class EquipoDAO implements ICRUD<Equipo> {

    private static final String ARCHIVO = "equipos.txt";
    private static final String SEPARADOR = ";";
    private static final String SEPARADOR_JUGADORES = ",";

    private final TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private final JugadorDAO jugadorDAO = new JugadorDAO();

    @Override
    public boolean crear(Equipo equipo) {
        if (equipo == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(equipoALinea(equipo));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar equipo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Equipo leerPorId(String nombre) {
        for (Equipo e : listarTodos()) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaAEquipo(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer equipos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Equipo equipoActualizado) {
        List<Equipo> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNombre().equalsIgnoreCase(equipoActualizado.getNombre())) {
                lista.set(i, equipoActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String nombre) {
        List<Equipo> lista = listarTodos();
        boolean eliminado = lista.removeIf(e -> e.getNombre().equalsIgnoreCase(nombre));
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String equipoALinea(Equipo equipo) {
        // Serializar el ID del técnico (o vacío si no tiene)
        String idTecnico = (equipo.getTecnico() != null)
                ? equipo.getTecnico().getIdentificacion()
                : "";

        // Serializar los IDs de los jugadores separados por coma
        StringBuilder idsJugadores = new StringBuilder();
        for (Jugador j : equipo.getJugadores()) {
            if (idsJugadores.length() > 0) idsJugadores.append(SEPARADOR_JUGADORES);
            idsJugadores.append(j.getIdentificacion());
        }

        return equipo.getNombre() + SEPARADOR
             + equipo.getSede() + SEPARADOR
             + idTecnico + SEPARADOR
             + idsJugadores;
    }

    private Equipo lineaAEquipo(String linea) {
        String[] partes = linea.split(SEPARADOR, -1);

        Equipo equipo = new Equipo(partes[0], partes[1]);

        // Reconstruir el técnico consultando a TecnicoDAO
        if (!partes[2].isBlank()) {
            Tecnico tecnico = tecnicoDAO.leerPorId(partes[2]);
            equipo.setTecnico(tecnico);
        }

        // Reconstruir cada jugador consultando a JugadorDAO
        if (partes.length > 3 && !partes[3].isBlank()) {
            String[] idsJugadores = partes[3].split(SEPARADOR_JUGADORES);
            for (String id : idsJugadores) {
                Jugador jugador = jugadorDAO.leerPorId(id.trim());
                if (jugador != null) {
                    equipo.agregarJugador(jugador);
                }
            }
        }

        return equipo;
    }

    private void guardarTodos(List<Equipo> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Equipo e : lista) {
                writer.write(equipoALinea(e));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
