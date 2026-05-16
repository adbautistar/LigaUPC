/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

/**
 *
 * @author alfre
 */
public class Jugador extends Persona{
    private String posicion; // Ejemplo: Delantero, Portero
    private int numeroCamiseta;

    // Constructor vacío
    public Jugador() {
        super();
    }

    // Constructor con parámetros
    public Jugador(String nombre, String identificacion, String contacto, String posicion, int numeroCamiseta) {
        // Invocamos al constructor de la clase padre (Persona)
        super(nombre, identificacion, contacto);
        this.posicion = posicion;
        this.numeroCamiseta = numeroCamiseta;
    }

    // Getters y Setters
    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public void setNumeroCamiseta(int numeroCamiseta) {
        this.numeroCamiseta = numeroCamiseta;
    }

    /**
     * Implementación obligatoria del método abstracto (Polimorfismo).
     * Evita el uso de condicionales en la vista según el patrón GRASP.
     */
    @Override
    public String obtenerPerfil() {
        return "JUGADOR: " + getNombre() + " | Dorsal: " + numeroCamiseta + " | Posición: " + posicion;
    }
}
