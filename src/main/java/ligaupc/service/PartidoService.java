package ligaupc.service;

import java.util.Date;
import java.util.List;
import ligaupc.dao.EstadisticaDAO;
import ligaupc.dao.PartidoDAO;
import ligaupc.model.Equipo;
import ligaupc.model.Estadistica;
import ligaupc.model.ICRUD;
import ligaupc.model.Partido;

/**
 * GRASP Controller: orquesta el registro de partidos y la actualización de estadísticas.
 * GRASP High Cohesion: gestiona únicamente la lógica de negocio de partidos.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de negocio de partidos.
 * SOLID OCP: depende de ICRUD<Partido> y ReglasPuntuacion, ambas intercambiables.
 */
public class PartidoService {

    private final ICRUD<Partido> partidoDAO;
    private final ICRUD<Estadistica> estadisticaDAO;
    private final EquipoService equipoService;
    private final ArbitroService arbitroService;
    private final ReglasPuntuacion reglasPuntuacion;

    public PartidoService(ICRUD<Partido> partidoDAO,
                          ICRUD<Estadistica> estadisticaDAO,
                          EquipoService equipoService,
                          ArbitroService arbitroService,
                          ReglasPuntuacion reglasPuntuacion) {
        this.partidoDAO = partidoDAO;
        this.estadisticaDAO = estadisticaDAO;
        this.equipoService = equipoService;
        this.arbitroService = arbitroService;
        this.reglasPuntuacion = reglasPuntuacion;
    }

    public PartidoService() {
        this.partidoDAO = new PartidoDAO();
        this.estadisticaDAO = new EstadisticaDAO();
        this.equipoService = new EquipoService();
        this.arbitroService = new ArbitroService();
        this.reglasPuntuacion = new ReglasPuntuacionEstandar();
    }

    /**
     * Registra un nuevo partido tras validar sus datos.
     * @throws IllegalArgumentException si los datos son inválidos.
     */
    public void registrarPartido(Partido partido) {
        if (partido == null)
            throw new IllegalArgumentException("El partido no puede ser nulo.");
        if (partido.getFecha() == null)
            throw new IllegalArgumentException("La fecha del partido es obligatoria.");
        if (partido.getEquipoLocal() == null || partido.getEquipoVisitante() == null)
            throw new IllegalArgumentException("Los equipos del partido son obligatorios.");
        if (partido.getEquipoLocal().getNombre().equalsIgnoreCase(partido.getEquipoVisitante().getNombre()))
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo.");
        if (partido.getArbitro() == null)
            throw new IllegalArgumentException("El árbitro del partido es obligatorio.");

        // Validar que los equipos y el árbitro existan en el sistema
        equipoService.buscarEquipo(partido.getEquipoLocal().getNombre());
        equipoService.buscarEquipo(partido.getEquipoVisitante().getNombre());
        arbitroService.buscarArbitro(partido.getArbitro().getIdentificacion());

        partido.setEstado("Programado");
        partidoDAO.crear(partido);
    }

    /**
     * Retorna la lista completa de partidos.
     */
    public List<Partido> listarPartidos() {
        return partidoDAO.listarTodos();
    }

    /**
     * Busca un partido por su ID.
     * @throws IllegalArgumentException si no existe un partido con ese ID.
     */
    public Partido buscarPartido(int idPartido) {
        Partido partido = partidoDAO.leerPorId(String.valueOf(idPartido));
        if (partido == null)
            throw new IllegalArgumentException("No existe un partido con el ID " + idPartido);
        return partido;
    }

    /**
     * Elimina un partido por su ID.
     * @throws IllegalArgumentException si el partido no existe o ya está finalizado.
     */
    public void eliminarPartido(int idPartido) {
        Partido partido = buscarPartido(idPartido);
        if ("Finalizado".equals(partido.getEstado()))
            throw new IllegalArgumentException("No se puede eliminar un partido ya finalizado.");

        partidoDAO.eliminar(String.valueOf(idPartido));
    }

    /**
     * GRASP Controller: registra el resultado de un partido y actualiza estadísticas.
     * Coordina la actualización del partido y de las estadísticas de ambos equipos.
     * @throws IllegalArgumentException si el partido no existe o ya está finalizado.
     */
    public void registrarResultado(int idPartido, int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0)
            throw new IllegalArgumentException("Los goles no pueden ser negativos.");

        Partido partido = buscarPartido(idPartido);

        if ("Finalizado".equals(partido.getEstado()))
            throw new IllegalArgumentException("El partido con ID " + idPartido + " ya está finalizado.");

        // Actualizar datos del partido
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado("Finalizado");
        partidoDAO.actualizar(partido);

        // Actualizar estadísticas de ambos equipos
        actualizarEstadistica(partido.getEquipoLocal(), golesLocal, golesVisitante);
        actualizarEstadistica(partido.getEquipoVisitante(), golesVisitante, golesLocal);
    }

    // ── Métodos privados de apoyo ────────────────────────────────────────────

    /**
     * Actualiza o crea la estadística de un equipo tras un partido jugado.
     * Delega el cálculo de puntos a ReglasPuntuacion (OCP).
     */
    private void actualizarEstadistica(Equipo equipo, int golesAFavor, int golesEnContra) {
        Estadistica estadistica = estadisticaDAO.leerPorId(equipo.getNombre());

        if (estadistica == null) {
            estadistica = new Estadistica();
            estadistica.setEquipo(equipo);
        }

        estadistica.setPartidosJugados(estadistica.getPartidosJugados() + 1);
        estadistica.setGolesAFavor(estadistica.getGolesAFavor() + golesAFavor);
        estadistica.setGolesEnContra(estadistica.getGolesEnContra() + golesEnContra);

        int puntos = reglasPuntuacion.calcularPuntos(golesAFavor, golesEnContra);
        estadistica.setPuntos(estadistica.getPuntos() + puntos);

        if (golesAFavor > golesEnContra) {
            estadistica.setPartidosGanados(estadistica.getPartidosGanados() + 1);
        } else if (golesAFavor == golesEnContra) {
            estadistica.setPartidosEmpatados(estadistica.getPartidosEmpatados() + 1);
        } else {
            estadistica.setPartidosPerdidos(estadistica.getPartidosPerdidos() + 1);
        }

        if (estadisticaDAO.leerPorId(equipo.getNombre()) == null) {
            estadisticaDAO.crear(estadistica);
        } else {
            estadisticaDAO.actualizar(estadistica);
        }
    }
}
