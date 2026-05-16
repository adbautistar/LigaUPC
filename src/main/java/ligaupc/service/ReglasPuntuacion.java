package ligaupc.service;

/**
 * GRASP Polymorphism + SOLID OCP:
 * Define el contrato para calcular puntos. Cualquier variante del torneo
 * implementa esta interfaz sin modificar PartidoService.
 */
public interface ReglasPuntuacion {

    /**
     * Calcula los puntos que obtiene un equipo según sus goles.
     * @param golesAFavor  goles anotados por el equipo
     * @param golesEnContra goles recibidos por el equipo
     * @return puntos obtenidos
     */
    int calcularPuntos(int golesAFavor, int golesEnContra);
}
