/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

/**
 *
 * @author alfre
 */
public class Tecnico extends Persona{

    private String especialidad; // Ejemplo: Ofensiva, 4-4-2, Formación de juveniles

    // Constructor vacío
    public Tecnico() { super(); }

    // Constructor con parámetros
    public Tecnico(String nombre, String identificacion, String contacto, String especialidad) {
        super(nombre, identificacion, contacto);
        this.especialidad = especialidad;
    }

    // Getters y Setters
    public String getEspecialidad() { return especialidad; }

    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    /**
     * Implementación de Polimorfismo.
     * Permite identificar al técnico y su enfoque táctico.
     */
    @Override
    public String obtenerPerfil() {
        return "TÉCNICO: " + getNombre() + " | Especialidad: " + especialidad;
    }
}
