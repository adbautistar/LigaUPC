package ligaupc.service;

import java.util.Comparator;
import java.util.List;
import ligaupc.dao.EstadisticaDAO;
import ligaupc.model.Estadistica;
import ligaupc.model.ICRUD;

/**
 * GRASP High Cohesion: gestiona la consulta y clasificación de estadísticas.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de clasificación.
 * SOLID OCP: depende de ICRUD<Estadistica>, no de EstadisticaDAO directamente.
 *
 * Nota: crear y actualizar estadísticas es responsabilidad de PartidoService.
 * Este Service se enfoca en consultas y en construir la tabla de posiciones.
 */
public class EstadisticaService {

    private final ICRUD<Estadistica> estadisticaDAO;

    public EstadisticaService(ICRUD<Estadistica> estadisticaDAO) {
        this.estadisticaDAO = estadisticaDAO;
    }

    public EstadisticaService() {
        this.estadisticaDAO = new EstadisticaDAO();
    }

    /**
     * Retorna las estadísticas de todos los equipos sin orden específico.
     */
    public List<Estadistica> listarEstadisticas() {
        return estadisticaDAO.listarTodos();
    }

    /**
     * Retorna la tabla de posiciones ordenada por:
     * 1. Puntos (descendente)
     * 2. Diferencia de goles (descendente)
     * 3. Goles a favor (descendente)
     */
    public List<Estadistica> listarClasificacion() {
        List<Estadistica> lista = estadisticaDAO.listarTodos();

        lista.sort(Comparator
            .comparingInt(Estadistica::getPuntos).reversed()
            .thenComparingInt(e -> (e.getGolesAFavor() - e.getGolesEnContra()) * -1)
            .thenComparingInt(e -> e.getGolesAFavor() * -1));

        return lista;
    }

    /**
     * Busca las estadísticas de un equipo por su nombre.
     * @throws IllegalArgumentException si no existen estadísticas para ese equipo.
     */
    public Estadistica buscarEstadistica(String nombreEquipo) {
        Estadistica estadistica = estadisticaDAO.leerPorId(nombreEquipo);
        if (estadistica == null)
            throw new IllegalArgumentException("No existen estadísticas para el equipo '" + nombreEquipo + "'.");
        return estadistica;
    }

    /**
     * Elimina las estadísticas de un equipo.
     * Se usa cuando un equipo es retirado de la liga.
     * @throws IllegalArgumentException si no existen estadísticas para ese equipo.
     */
    public void eliminarEstadistica(String nombreEquipo) {
        if (estadisticaDAO.leerPorId(nombreEquipo) == null)
            throw new IllegalArgumentException("No existen estadísticas para el equipo '" + nombreEquipo + "'.");

        estadisticaDAO.eliminar(nombreEquipo);
    }
}
