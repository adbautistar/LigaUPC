package ligaupc.service;

/**
 * Implementación estándar de puntuación: victoria=3, empate=1, derrota=0.
 * SOLID OCP: si las reglas cambian, se crea otra implementación de ReglasPuntuacion
 * sin tocar esta clase ni PartidoService.
 */
public class ReglasPuntuacionEstandar implements ReglasPuntuacion {

    @Override
    public int calcularPuntos(int golesAFavor, int golesEnContra) {
        if (golesAFavor > golesEnContra) return 3;
        if (golesAFavor == golesEnContra) return 1;
        return 0;
    }
}
