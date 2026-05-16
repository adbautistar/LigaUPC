package ligaupc.service;

import java.util.List;
import ligaupc.dao.JugadorDAO;
import ligaupc.model.ICRUD;
import ligaupc.model.Jugador;

/**
 * GRASP High Cohesion: gestiona únicamente la lógica de negocio de Jugadores.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de negocio de jugadores.
 * SOLID OCP: depende de ICRUD<Jugador>, no de JugadorDAO directamente.
 */
public class JugadorService {

    private final ICRUD<Jugador> jugadorDAO;

    public JugadorService(ICRUD<Jugador> jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
    }

    public JugadorService() {
        this.jugadorDAO = new JugadorDAO();
    }

    /**
     * Registra un nuevo jugador tras validar sus datos.
     * @throws IllegalArgumentException si algún dato es inválido o la identificación ya existe.
     */
    public void registrarJugador(Jugador jugador) {
        if (jugador == null)
            throw new IllegalArgumentException("El jugador no puede ser nulo.");
        if (jugador.getNombre() == null || jugador.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
        if (jugador.getIdentificacion() == null || jugador.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación del jugador es obligatoria.");
        if (jugador.getNumeroCamiseta() <= 0)
            throw new IllegalArgumentException("El número de camiseta debe ser mayor a cero.");
        if (jugadorDAO.leerPorId(jugador.getIdentificacion()) != null)
            throw new IllegalArgumentException("Ya existe un jugador con la identificación " + jugador.getIdentificacion());

        jugadorDAO.crear(jugador);
    }

    /**
     * Retorna la lista completa de jugadores registrados.
     */
    public List<Jugador> listarJugadores() {
        return jugadorDAO.listarTodos();
    }

    /**
     * Busca un jugador por su identificación.
     * @throws IllegalArgumentException si no existe un jugador con esa identificación.
     */
    public Jugador buscarJugador(String identificacion) {
        Jugador jugador = jugadorDAO.leerPorId(identificacion);
        if (jugador == null)
            throw new IllegalArgumentException("No existe un jugador con la identificación " + identificacion);
        return jugador;
    }

    /**
     * Actualiza los datos de un jugador existente.
     * @throws IllegalArgumentException si el jugador no existe.
     */
    public void actualizarJugador(Jugador jugador) {
        if (jugador == null || jugador.getIdentificacion() == null)
            throw new IllegalArgumentException("Datos del jugador inválidos.");
        if (jugadorDAO.leerPorId(jugador.getIdentificacion()) == null)
            throw new IllegalArgumentException("No existe un jugador con la identificación " + jugador.getIdentificacion());

        jugadorDAO.actualizar(jugador);
    }

    /**
     * Elimina un jugador por su identificación.
     * @throws IllegalArgumentException si el jugador no existe.
     */
    public void eliminarJugador(String identificacion) {
        if (jugadorDAO.leerPorId(identificacion) == null)
            throw new IllegalArgumentException("No existe un jugador con la identificación " + identificacion);

        jugadorDAO.eliminar(identificacion);
    }
}
