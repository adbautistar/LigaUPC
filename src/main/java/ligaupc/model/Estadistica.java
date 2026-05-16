/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ligaupc.model;

/**
 *
 * @author alfre
 */
public class Estadistica {
    private Equipo equipo;
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    private int golesAFavor;
    private int golesEnContra;
    private int puntos;

    public Estadistica() {}
    
    // Getters y Setters puros
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    public int getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(int partidosGanados) { this.partidosGanados = partidosGanados; }

    public int getPartidosEmpatados() { return partidosEmpatados; }
    public void setPartidosEmpatados(int partidosEmpatados) { this.partidosEmpatados = partidosEmpatados; }

    public int getPartidosPerdidos() { return partidosPerdidos; }
    public void setPartidosPerdidos(int partidosPerdidos) { this.partidosPerdidos = partidosPerdidos; }

    public int getGolesAFavor() { return golesAFavor; }
    public void setGolesAFavor(int golesAFavor) { this.golesAFavor = golesAFavor; }

    public int getGolesEnContra() { return golesEnContra; }
    public void setGolesEnContra(int golesEnContra) { this.golesEnContra = golesEnContra; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    
    
}
