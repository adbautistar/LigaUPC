/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ligaupc.model;

import java.util.List;

/**
 *
 * @author alfre
 */
public interface ICRUD<Persona> {
    boolean crear(Persona entidad);
    Persona leerPorId(String id); // Usamos String por si el ID es la identificación
    List<Persona> listarTodos();
    boolean actualizar(Persona entidad);
    boolean eliminar(String id);
}
