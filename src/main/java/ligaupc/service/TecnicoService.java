package ligaupc.service;

import java.util.List;
import ligaupc.dao.TecnicoDAO;
import ligaupc.model.ICRUD;
import ligaupc.model.Tecnico;

/**
 * GRASP High Cohesion: gestiona únicamente la lógica de negocio de Técnicos.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de negocio de técnicos.
 * SOLID OCP: depende de ICRUD<Tecnico>, no de TecnicoDAO directamente.
 */
public class TecnicoService {

    private final ICRUD<Tecnico> tecnicoDAO;

    public TecnicoService(ICRUD<Tecnico> tecnicoDAO) {
        this.tecnicoDAO = tecnicoDAO;
    }

    public TecnicoService() {
        this.tecnicoDAO = new TecnicoDAO();
    }

    /**
     * Registra un nuevo técnico tras validar sus datos.
     * @throws IllegalArgumentException si algún dato es inválido o la identificación ya existe.
     */
    public void registrarTecnico(Tecnico tecnico) {
        if (tecnico == null)
            throw new IllegalArgumentException("El técnico no puede ser nulo.");
        if (tecnico.getNombre() == null || tecnico.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del técnico es obligatorio.");
        if (tecnico.getIdentificacion() == null || tecnico.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación del técnico es obligatoria.");
        if (tecnico.getEspecialidad() == null || tecnico.getEspecialidad().isBlank())
            throw new IllegalArgumentException("La especialidad del técnico es obligatoria.");
        if (tecnicoDAO.leerPorId(tecnico.getIdentificacion()) != null)
            throw new IllegalArgumentException("Ya existe un técnico con la identificación " + tecnico.getIdentificacion());

        tecnicoDAO.crear(tecnico);
    }

    /**
     * Retorna la lista completa de técnicos registrados.
     */
    public List<Tecnico> listarTecnicos() {
        return tecnicoDAO.listarTodos();
    }

    /**
     * Busca un técnico por su identificación.
     * @throws IllegalArgumentException si no existe un técnico con esa identificación.
     */
    public Tecnico buscarTecnico(String identificacion) {
        Tecnico tecnico = tecnicoDAO.leerPorId(identificacion);
        if (tecnico == null)
            throw new IllegalArgumentException("No existe un técnico con la identificación " + identificacion);
        return tecnico;
    }

    /**
     * Actualiza los datos de un técnico existente.
     * @throws IllegalArgumentException si el técnico no existe.
     */
    public void actualizarTecnico(Tecnico tecnico) {
        if (tecnico == null || tecnico.getIdentificacion() == null)
            throw new IllegalArgumentException("Datos del técnico inválidos.");
        if (tecnicoDAO.leerPorId(tecnico.getIdentificacion()) == null)
            throw new IllegalArgumentException("No existe un técnico con la identificación " + tecnico.getIdentificacion());

        tecnicoDAO.actualizar(tecnico);
    }

    /**
     * Elimina un técnico por su identificación.
     * @throws IllegalArgumentException si el técnico no existe.
     */
    public void eliminarTecnico(String identificacion) {
        if (tecnicoDAO.leerPorId(identificacion) == null)
            throw new IllegalArgumentException("No existe un técnico con la identificación " + identificacion);

        tecnicoDAO.eliminar(identificacion);
    }
}
