/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

import java.util.*;

/**
 *
 * @author alfre
 */
public class Equipo {
    private String nombre;
    private String sede; 
    private Tecnico tecnico;
    private List<Jugador> jugadores;

    // Constructor vacío
    public Equipo() {
        this.jugadores = new ArrayList<>();
    }

    // Constructor con parámetros básicos
    public Equipo(String nombre, String sede) {
        this.nombre = nombre;
        this.sede = sede;
        this.jugadores = new ArrayList<>();
    }

    // Métodos para gestionar la relación (Patrón Experto)
    public void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
    }

    /**
     * GRASP Expert: el Equipo es el experto en gestionar su propia lista de jugadores.
     * Retorna true si el jugador existía y fue removido, false si no pertenecía al equipo.
     */
    public boolean quitarJugador(String idJugador) {
        return this.jugadores.removeIf(j -> j.getIdentificacion().equals(idJugador));
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }
}
