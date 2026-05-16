package ligaupc.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import ligaupc.model.ICRUD;
import ligaupc.model.Jugador;

/**
 * GRASP Expert: esta clase es la única responsable de persistir Jugadores.
 * SOLID SRP: su única razón de cambio es si cambia el mecanismo de persistencia.
 * SOLID OCP: implementa ICRUD<Jugador>, lo que permite reemplazarla sin tocar el Service.
 */
public class JugadorDAO implements ICRUD<Jugador> {

    private static final String ARCHIVO = "jugadores.txt";
    private static final String SEPARADOR = ";";

    @Override
    public boolean crear(Jugador jugador) {
        if (jugador == null) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(jugadorALinea(jugador));
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar jugador: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Jugador leerPorId(String identificacion) {
        for (Jugador j : listarTodos()) {
            if (j.getIdentificacion().equals(identificacion)) {
                return j;
            }
        }
        return null;
    }

    @Override
    public List<Jugador> listarTodos() {
        List<Jugador> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    lista.add(lineaAJugador(linea));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer jugadores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Jugador jugadorActualizado) {
        List<Jugador> lista = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdentificacion().equals(jugadorActualizado.getIdentificacion())) {
                lista.set(i, jugadorActualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) guardarTodos(lista);
        return encontrado;
    }

    @Override
    public boolean eliminar(String identificacion) {
        List<Jugador> lista = listarTodos();
        boolean eliminado = lista.removeIf(j -> j.getIdentificacion().equals(identificacion));
        if (eliminado) guardarTodos(lista);
        return eliminado;
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    private String jugadorALinea(Jugador j) {
        return j.getNombre() + SEPARADOR
             + j.getIdentificacion() + SEPARADOR
             + j.getContacto() + SEPARADOR
             + j.getPosicion() + SEPARADOR
             + j.getNumeroCamiseta();
    }

    private Jugador lineaAJugador(String linea) {
        String[] partes = linea.split(SEPARADOR);
        return new Jugador(
            partes[0],              // nombre
            partes[1],              // identificacion
            partes[2],              // contacto
            partes[3],              // posicion
            Integer.parseInt(partes[4]) // numeroCamiseta
        );
    }

    private void guardarTodos(List<Jugador> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, false))) {
            for (Jugador j : lista) {
                writer.write(jugadorALinea(j));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al reescribir archivo: " + e.getMessage());
        }
    }
}
