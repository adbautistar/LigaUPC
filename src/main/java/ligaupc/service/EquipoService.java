package ligaupc.service;

import java.util.List;
import ligaupc.dao.EquipoDAO;
import ligaupc.model.Equipo;
import ligaupc.model.ICRUD;
import ligaupc.model.Jugador;
import ligaupc.model.Tecnico;

/**
 * GRASP High Cohesion: gestiona únicamente la lógica de negocio de Equipos.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de negocio de equipos.
 * SOLID OCP: depende de ICRUD<Equipo>, no de EquipoDAO directamente.
 *
 * Colabora con TecnicoService y JugadorService para validar referencias.
 */
public class EquipoService {

    private final ICRUD<Equipo> equipoDAO;
    private final TecnicoService tecnicoService;
    private final JugadorService jugadorService;

    public EquipoService(ICRUD<Equipo> equipoDAO, TecnicoService tecnicoService, JugadorService jugadorService) {
        this.equipoDAO = equipoDAO;
        this.tecnicoService = tecnicoService;
        this.jugadorService = jugadorService;
    }

    public EquipoService() {
        this.equipoDAO = new EquipoDAO();
        this.tecnicoService = new TecnicoService();
        this.jugadorService = new JugadorService();
    }

    /**
     * Registra un nuevo equipo tras validar sus datos.
     * @throws IllegalArgumentException si algún dato es inválido o el nombre ya existe.
     */
    public void registrarEquipo(Equipo equipo) {
        if (equipo == null)
            throw new IllegalArgumentException("El equipo no puede ser nulo.");
        if (equipo.getNombre() == null || equipo.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del equipo es obligatorio.");
        if (equipo.getSede() == null || equipo.getSede().isBlank())
            throw new IllegalArgumentException("La sede del equipo es obligatoria.");
        if (equipoDAO.leerPorId(equipo.getNombre()) != null)
            throw new IllegalArgumentException("Ya existe un equipo con el nombre '" + equipo.getNombre() + "'.");

        equipoDAO.crear(equipo);
    }

    /**
     * Retorna la lista completa de equipos registrados.
     */
    public List<Equipo> listarEquipos() {
        return equipoDAO.listarTodos();
    }

    /**
     * Busca un equipo por su nombre.
     * @throws IllegalArgumentException si no existe un equipo con ese nombre.
     */
    public Equipo buscarEquipo(String nombre) {
        Equipo equipo = equipoDAO.leerPorId(nombre);
        if (equipo == null)
            throw new IllegalArgumentException("No existe un equipo con el nombre '" + nombre + "'.");
        return equipo;
    }

    /**
     * Actualiza los datos básicos de un equipo (nombre y sede).
     * @throws IllegalArgumentException si el equipo no existe.
     */
    public void actualizarEquipo(Equipo equipo) {
        if (equipo == null || equipo.getNombre() == null)
            throw new IllegalArgumentException("Datos del equipo inválidos.");
        if (equipoDAO.leerPorId(equipo.getNombre()) == null)
            throw new IllegalArgumentException("No existe un equipo con el nombre '" + equipo.getNombre() + "'.");

        equipoDAO.actualizar(equipo);
    }

    /**
     * Elimina un equipo por su nombre.
     * @throws IllegalArgumentException si el equipo no existe.
     */
    public void eliminarEquipo(String nombre) {
        if (equipoDAO.leerPorId(nombre) == null)
            throw new IllegalArgumentException("No existe un equipo con el nombre '" + nombre + "'.");

        equipoDAO.eliminar(nombre);
    }

    /**
     * Asigna un técnico existente a un equipo existente.
     * Delega la validación del técnico a TecnicoService.
     * @throws IllegalArgumentException si el equipo o el técnico no existen.
     */
    public void asignarTecnico(String nombreEquipo, String idTecnico) {
        Equipo equipo = buscarEquipo(nombreEquipo);
        Tecnico tecnico = tecnicoService.buscarTecnico(idTecnico);

        equipo.setTecnico(tecnico);
        equipoDAO.actualizar(equipo);
    }

    /**
     * Agrega un jugador existente a un equipo existente.
     * Delega la validación del jugador a JugadorService.
     * @throws IllegalArgumentException si el equipo o el jugador no existen,
     *         o si el jugador ya pertenece al equipo.
     */
    public void agregarJugador(String nombreEquipo, String idJugador) {
        Equipo equipo = buscarEquipo(nombreEquipo);
        Jugador jugador = jugadorService.buscarJugador(idJugador);

        boolean yaEstaEnEquipo = equipo.getJugadores().stream()
                .anyMatch(j -> j.getIdentificacion().equals(idJugador));
        if (yaEstaEnEquipo)
            throw new IllegalArgumentException("El jugador ya pertenece al equipo '" + nombreEquipo + "'.");

        equipo.agregarJugador(jugador);
        equipoDAO.actualizar(equipo);
    }

    /**
     * Quita un jugador de un equipo.
     * SOLID SRP: valida la regla de negocio aqui; la View solo muestra el resultado.
     * @throws IllegalArgumentException si el equipo no existe o el jugador no pertenece a el.
     */
    public void quitarJugador(String nombreEquipo, String idJugador) {
        Equipo equipo = buscarEquipo(nombreEquipo);

        boolean removido = equipo.quitarJugador(idJugador);
        if (!removido)
            throw new IllegalArgumentException(
                "El jugador con ID '" + idJugador + "' no pertenece al equipo '" + nombreEquipo + "'.");

        equipoDAO.actualizar(equipo);
    }
}
