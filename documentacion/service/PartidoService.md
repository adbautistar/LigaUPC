# PartidoService — Capa de negocio de Partido

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/PartidoService.java`

---

## ¿Qué es?

Es el Service más complejo del sistema. Además del CRUD de partidos, su responsabilidad principal es **orquestar el registro de resultados**: cuando un partido finaliza, coordina la actualización del partido y de las estadísticas de ambos equipos.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP Controller** | Orquesta múltiples objetos al registrar un resultado |
| **GRASP High Cohesion** | Solo gestiona lógica de negocio de partidos |
| **GRASP Low Coupling** | Delega a Services especializados y a `ReglasPuntuacion` |
| **SOLID SRP** | Cambia solo si cambian las reglas de partidos |
| **SOLID OCP** | Depende de `ReglasPuntuacion` e `ICRUD<Partido>`, ambas intercambiables |

---

## Patrón GRASP: **Controller**

El Controller es el patrón que responde a eventos del sistema. En este caso, el evento es **"se registra el resultado de un partido"**. `PartidoService` coordina todo lo que debe ocurrir:

```
registrarResultado(idPartido, golesLocal, golesVisitante)
    │
    ├── 1. Validar goles no negativos
    ├── 2. Buscar el partido (verificar que existe y no está finalizado)
    ├── 3. Actualizar goles y estado del partido → partidoDAO.actualizar()
    ├── 4. Actualizar estadística del equipo local  → actualizarEstadistica()
    └── 5. Actualizar estadística del equipo visitante → actualizarEstadistica()
```

Ningún objeto hace todo esto solo. El Controller lo coordina.

---

## OCP: `ReglasPuntuacion` como dependencia inyectada

`PartidoService` nunca contiene la lógica de puntos directamente:

```java
// En el constructor se inyecta la implementación
private final ReglasPuntuacion reglasPuntuacion;

// En actualizarEstadistica() solo se invoca el contrato
int puntos = reglasPuntuacion.calcularPuntos(golesAFavor, golesEnContra);
```

Para cambiar de la regla estándar a una especial basta con:

```java
// Sin tocar PartidoService
PartidoService service = new PartidoService(
    new PartidoDAO(),
    new EstadisticaDAO(),
    new EquipoService(),
    new ArbitroService(),
    new ReglasPuntuacionEspecial()  // ← solo cambia esto
);
```

---

## Inyección de dependencia

`PartidoService` recibe cinco dependencias:

```java
public PartidoService(ICRUD<Partido> partidoDAO,
                      ICRUD<Estadistica> estadisticaDAO,
                      EquipoService equipoService,
                      ArbitroService arbitroService,
                      ReglasPuntuacion reglasPuntuacion) { ... }
```

El constructor por defecto las instancia con las implementaciones estándar.

---

## Métodos

| Método | Retorno | Descripción |
|---|---|---|
| `registrarPartido(Partido)` | `void` | Valida y persiste un nuevo partido |
| `listarPartidos()` | `List<Partido>` | Lista todos los partidos |
| `buscarPartido(int)` | `Partido` | Busca por ID numérico |
| `eliminarPartido(int)` | `void` | Solo elimina si no está finalizado |
| `registrarResultado(int, int, int)` | `void` | **Método Controller principal** |

### Validaciones en `registrarPartido()`

```
¿partido es null?               → IllegalArgumentException
¿fecha es null?                 → IllegalArgumentException
¿equipos son null?              → IllegalArgumentException
¿local == visitante?            → IllegalArgumentException  (no puede jugar contra sí mismo)
¿árbitro es null?               → IllegalArgumentException
¿equipo local existe?           → delega a equipoService (lanza exc. si no)
¿equipo visitante existe?       → delega a equipoService
¿árbitro existe?                → delega a arbitroService
Todo válido                     → estado="Programado" → dao.crear()
```

### Validaciones en `registrarResultado()`

```
¿goles negativos?               → IllegalArgumentException
¿partido existe?                → IllegalArgumentException
¿partido ya finalizado?         → IllegalArgumentException
Todo válido                     → actualizar partido + estadísticas
```

---

## Método privado: `actualizarEstadistica()`

Este método privado encapsula la lógica de actualizar las estadísticas de un equipo tras un partido:

```java
private void actualizarEstadistica(Equipo equipo, int golesAFavor, int golesEnContra) {
    // 1. Buscar estadística existente o crear una nueva
    Estadistica e = estadisticaDAO.leerPorId(equipo.getNombre());
    if (e == null) { e = new Estadistica(); e.setEquipo(equipo); }

    // 2. Sumar contadores
    e.setPartidosJugados(e.getPartidosJugados() + 1);
    e.setGolesAFavor(e.getGolesAFavor() + golesAFavor);
    e.setGolesEnContra(e.getGolesEnContra() + golesEnContra);

    // 3. Calcular puntos con la regla inyectada (OCP)
    e.setPuntos(e.getPuntos() + reglasPuntuacion.calcularPuntos(golesAFavor, golesEnContra));

    // 4. Clasificar el resultado
    if (golesAFavor > golesEnContra)       e.setPartidosGanados(e.getPartidosGanados() + 1);
    else if (golesAFavor == golesEnContra) e.setPartidosEmpatados(e.getPartidosEmpatados() + 1);
    else                                   e.setPartidosPerdidos(e.getPartidosPerdidos() + 1);

    // 5. Persistir
    if (estadisticaDAO.leerPorId(equipo.getNombre()) == null) estadisticaDAO.crear(e);
    else estadisticaDAO.actualizar(e);
}
```

Se llama dos veces por partido — una para el equipo local (con sus goles) y otra para el visitante (con los goles invertidos):

```java
actualizarEstadistica(partido.getEquipoLocal(),     golesLocal,     golesVisitante);
actualizarEstadistica(partido.getEquipoVisitante(), golesVisitante, golesLocal);
```

---

## Flujo completo: registrar resultado

```
PartidoView
    │  llama registrarResultado(1, 2, 1)
    ▼
PartidoService.registrarResultado(1, 2, 1)
    ├── buscarPartido(1)          → Partido encontrado, estado="En Juego"
    ├── partido.setGolesLocal(2)
    ├── partido.setGolesVisitante(1)
    ├── partido.setEstado("Finalizado")
    ├── partidoDAO.actualizar()   → partidos.txt actualizado
    │
    ├── actualizarEstadistica(equipoLocal, 2, 1)
    │       ├── reglasPuntuacion.calcularPuntos(2,1) → 3 puntos (victoria)
    │       └── estadisticaDAO.actualizar()          → estadisticas.txt
    │
    └── actualizarEstadistica(equipoVisitante, 1, 2)
            ├── reglasPuntuacion.calcularPuntos(1,2) → 0 puntos (derrota)
            └── estadisticaDAO.actualizar()          → estadisticas.txt

View muestra "Resultado registrado exitosamente."
```

---

## Relación con otras clases

```
PartidoView  →  PartidoService  →  ICRUD<Partido>      ←  PartidoDAO
                               →  ICRUD<Estadistica>   ←  EstadisticaDAO
                               →  EquipoService
                               →  ArbitroService
                               →  ReglasPuntuacion     ←  ReglasPuntuacionEstandar
```
