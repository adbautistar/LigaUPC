/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

/**
 *
 * @author alfre
 */
public abstract class Persona {
    // Atributos privados para cumplir con el Encapsulamiento
    private String nombre;
    private String identificacion;
    private String contacto;

    // Constructor vacío (necesario para persistencia o frameworks)
    public Persona() {
    }

    // Constructor con parámetros
    public Persona(String nombre, String identificacion, String contacto) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.contacto = contacto;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    /**
     * Método abstracto para aplicar Polimorfismo en las clases hijas.
     * Cada tipo de persona mostrará su información de forma distinta.
     */
    public abstract String obtenerPerfil();
}
