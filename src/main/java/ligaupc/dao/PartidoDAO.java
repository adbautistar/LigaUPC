package ligaupc.dao;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ligaupc.model.ICRUD;
import ligaupc.model.Partido;

/**
 * GRASP Expert: única responsable de persistir Partidos en archivo de texto.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Partido>, intercambiable sin tocar el Service.
 *
 * Colabora con EquipoDAO y ArbitroDAO para reconstruir referencias al leer.
 * Usa idPartido como identificador único.
 */
public class PartidoDAO implements ICRUD<Partido> {

    private static final String ARCHIVO = "partidos.txt";
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final ArbitroDAO arbitroDAO = new ArbitroDAO();

    @Override
    public boolean crear(Partido partido) {
        if (partido == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(partidoALinea(partido));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar partido: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Partido leerPorId(String idPartido) {
        for (Partido p : listarTodos()) {
            if (String.valueOf(p.getIdPartido()).equals(idPartido)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Partido> listarTodos() {
        List<Partido> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaAPartido(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer partidos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Partido partidoActualizado) {
        List<Partido> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdPartido() == partidoActualizado.getIdPartido()) {
                lista.set(i, partidoActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String idPartido) {
        List<Partido> lista = listarTodos();
        boolean eliminado = lista.removeIf(
            p -> String.valueOf(p.getIdPartido()).equals(idPartido)
        );
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String partidoALinea(Partido p) {
        return p.getIdPartido() + SEPARADOR
             + FORMATO_FECHA.format(p.getFecha()) + SEPARADOR
             + p.getEquipoLocal().getNombre() + SEPARADOR
             + p.getEquipoVisitante().getNombre() + SEPARADOR
             + p.getGolesLocal() + SEPARADOR
             + p.getGolesVisitante() + SEPARADOR
             + p.getArbitro().getIdentificacion() + SEPARADOR
             + p.getEstado();
    }

    private Partido lineaAPartido(String linea) {
        String[] partes = linea.split(SEPARADOR, -1);

        Partido partido = new Partido();
        partido.setIdPartido(Integer.parseInt(partes[0]));
        partido.setFecha(parsearFecha(partes[1]));
        partido.setEquipoLocal(equipoDAO.leerPorId(partes[2]));
        partido.setEquipoVisitante(equipoDAO.leerPorId(partes[3]));
        partido.setGolesLocal(Integer.parseInt(partes[4]));
        partido.setGolesVisitante(Integer.parseInt(partes[5]));
        partido.setArbitro(arbitroDAO.leerPorId(partes[6]));
        partido.setEstado(partes[7]);

        return partido;
    }

    private Date parsearFecha(String texto) {
        try {
            return FORMATO_FECHA.parse(texto);
        } catch (ParseException e) {
            System.out.println("Fecha inválida en archivo: " + texto);
            return new Date();
        }
    }

    private void guardarTodos(List<Partido> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Partido p : lista) {
                writer.write(partidoALinea(p));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
