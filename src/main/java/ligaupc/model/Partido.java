/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

import java.util.Date;

/**
 *
 * @author alfre
 */
public class Partido {
    private int idPartido;
    private Date fecha;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private Arbitro arbitro;
    private String estado; // Ejemplo: Programado, En Juego, Finalizado

    // Constructor vacío
    public Partido() {
    }

    // Constructor para programar un partido (sin goles aún)
    public Partido(int idPartido, Date fecha, Equipo local, Equipo visitante, Arbitro arbitro) {
        this.idPartido = idPartido;
        this.fecha = fecha;
        this.equipoLocal = local;
        this.equipoVisitante = visitante;
        this.arbitro = arbitro;
        this.estado = "Programado";
    }

    // Getters y Setters
    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }    

    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }

    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }

    public int getGolesLocal() { return golesLocal; }
    public void setGolesLocal(int golesLocal) { this.golesLocal = golesLocal; }

    public int getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(int golesVisitante) { this.golesVisitante = golesVisitante; }
 
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
 
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    /**
     * Método de conveniencia para obtener el resultado en texto.
     * Útil para la capa de Vista.
     */
    public String obtenerResultado() {
        return equipoLocal.getNombre() + " " + golesLocal + " - " + 
               golesVisitante + " " + equipoVisitante.getNombre();
    }


}
