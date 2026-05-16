/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

/**
 *
 * @author alfre
 */
public class Arbitro extends Persona{
    private String categoriaCertificacion; // Ejemplo: FIFA, Regional, Nacional

    // Constructor vacío
    public Arbitro() {
        super();
    }

    // Constructor con parámetros
    public Arbitro(String nombre, String identificacion, String contacto, String categoriaCertificacion) {
        super(nombre, identificacion, contacto);
        this.categoriaCertificacion = categoriaCertificacion;
    }

    // Getters y Setters
    public String getCategoriaCertificacion() { return categoriaCertificacion; }

    public void setCategoriaCertificacion(String categoriaCertificacion) { this.categoriaCertificacion = categoriaCertificacion; }

    /**
     * Implementación de Polimorfismo.
     * Muestra la acreditación del árbitro en el sistema.
     */
    @Override
    public String obtenerPerfil() {
        return "ÁRBITRO: " + getNombre() + " | Certificación: " + categoriaCertificacion;
    }
}
