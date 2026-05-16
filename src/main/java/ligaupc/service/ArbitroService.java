package ligaupc.service;

import java.util.List;
import ligaupc.dao.ArbitroDAO;
import ligaupc.model.Arbitro;
import ligaupc.model.ICRUD;

/**
 * GRASP High Cohesion: gestiona únicamente la lógica de negocio de Árbitros.
 * SOLID SRP: su única razón de cambio es si cambian las reglas de negocio de árbitros.
 * SOLID OCP: depende de ICRUD<Arbitro>, no de ArbitroDAO directamente.
 */
public class ArbitroService {

    private final ICRUD<Arbitro> arbitroDAO;

    public ArbitroService(ICRUD<Arbitro> arbitroDAO) {
        this.arbitroDAO = arbitroDAO;
    }

    public ArbitroService() {
        this.arbitroDAO = new ArbitroDAO();
    }

    /**
     * Registra un nuevo árbitro tras validar sus datos.
     * @throws IllegalArgumentException si algún dato es inválido o la identificación ya existe.
     */
    public void registrarArbitro(Arbitro arbitro) {
        if (arbitro == null)
            throw new IllegalArgumentException("El árbitro no puede ser nulo.");
        if (arbitro.getNombre() == null || arbitro.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del árbitro es obligatorio.");
        if (arbitro.getIdentificacion() == null || arbitro.getIdentificacion().isBlank())
            throw new IllegalArgumentException("La identificación del árbitro es obligatoria.");
        if (arbitro.getCategoriaCertificacion() == null || arbitro.getCategoriaCertificacion().isBlank())
            throw new IllegalArgumentException("La categoría de certificación del árbitro es obligatoria.");
        if (arbitroDAO.leerPorId(arbitro.getIdentificacion()) != null)
            throw new IllegalArgumentException("Ya existe un árbitro con la identificación " + arbitro.getIdentificacion());

        arbitroDAO.crear(arbitro);
    }

    /**
     * Retorna la lista completa de árbitros registrados.
     */
    public List<Arbitro> listarArbitros() {
        return arbitroDAO.listarTodos();
    }

    /**
     * Busca un árbitro por su identificación.
     * @throws IllegalArgumentException si no existe un árbitro con esa identificación.
     */
    public Arbitro buscarArbitro(String identificacion) {
        Arbitro arbitro = arbitroDAO.leerPorId(identificacion);
        if (arbitro == null)
            throw new IllegalArgumentException("No existe un árbitro con la identificación " + identificacion);
        return arbitro;
    }

    /**
     * Actualiza los datos de un árbitro existente.
     * @throws IllegalArgumentException si el árbitro no existe.
     */
    public void actualizarArbitro(Arbitro arbitro) {
        if (arbitro == null || arbitro.getIdentificacion() == null)
            throw new IllegalArgumentException("Datos del árbitro inválidos.");
        if (arbitroDAO.leerPorId(arbitro.getIdentificacion()) == null)
            throw new IllegalArgumentException("No existe un árbitro con la identificación " + arbitro.getIdentificacion());

        arbitroDAO.actualizar(arbitro);
    }

    /**
     * Elimina un árbitro por su identificación.
     * @throws IllegalArgumentException si el árbitro no existe.
     */
    public void eliminarArbitro(String identificacion) {
        if (arbitroDAO.leerPorId(identificacion) == null)
            throw new IllegalArgumentException("No existe un árbitro con la identificación " + identificacion);

        arbitroDAO.eliminar(identificacion);
    }
}
