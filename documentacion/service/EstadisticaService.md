# EstadisticaService — Capa de negocio de Estadistica

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/EstadisticaService.java`

---

## ¿Qué es?

Gestiona las consultas sobre estadísticas y construye la tabla de posiciones de la liga. A diferencia de los otros Services, no expone métodos de creación ni actualización al usuario — esas operaciones las realiza `PartidoService` internamente al registrar resultados.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo gestiona consultas y clasificación de estadísticas |
| **SOLID SRP** | Cambia únicamente si cambia cómo se ordena la tabla de posiciones |
| **SOLID OCP** | Depende de `ICRUD<Estadistica>`, no de `EstadisticaDAO` directamente |

---

## ¿Por qué no tiene `registrar()` ni `actualizar()`?

Esta es una decisión de diseño importante. Las estadísticas **no se modifican directamente** desde la interfaz de usuario — son una consecuencia del registro de un resultado. Permitir que la View llame `actualizarEstadistica()` directamente podría dejar el sistema en un estado inconsistente (estadísticas que no corresponden a ningún partido real).

```
FLUJO CORRECTO:
PartidoView → PartidoService.registrarResultado() → actualiza estadísticas internamente

FLUJO INCORRECTO (evitado):
EstadisticaView → EstadisticaService.actualizar() → inconsistencia posible
```

Esto es **High Cohesion**: `EstadisticaService` solo hace lo que le corresponde.

---

## Método clave: `listarClasificacion()`

Este método es donde el Service agrega valor real más allá de delegar al DAO:

```java
lista.sort(Comparator
    .comparingInt(Estadistica::getPuntos).reversed()           // 1° por puntos
    .thenComparingInt(e -> (e.getGolesAFavor() - e.getGolesEnContra()) * -1)  // 2° por diferencia de goles
    .thenComparingInt(e -> e.getGolesAFavor() * -1));          // 3° por goles a favor
```

El ordenamiento de la tabla de posiciones es una **regla de negocio**: primero por puntos, en caso de empate por diferencia de goles, y si persiste el empate por goles anotados. Esa lógica vive en el Service — no en el DAO (que solo persiste) ni en la View (que solo muestra).

---

## Métodos

| Método | Retorno | Descripción |
|---|---|---|
| `listarEstadisticas()` | `List<Estadistica>` | Sin orden garantizado |
| `listarClasificacion()` | `List<Estadistica>` | Ordenada por puntos → diferencia goles → goles a favor |
| `buscarEstadistica(String)` | `Estadistica` | Por nombre de equipo |
| `eliminarEstadistica(String)` | `void` | Al retirar un equipo de la liga |

---

## Relación con otras clases

```
EstadisticaView  →  EstadisticaService  →  ICRUD<Estadistica>  ←  EstadisticaDAO
PartidoService   →  EstadisticaDAO directamente (para crear/actualizar tras partido)
```

---

## Cierre de la capa Service

Con `EstadisticaService` se completa la capa Service. Todos los Services siguen el mismo contrato:

| Service | Colabora con |
|---|---|
| `JugadorService` | `ICRUD<Jugador>` |
| `TecnicoService` | `ICRUD<Tecnico>` |
| `ArbitroService` | `ICRUD<Arbitro>` |
| `EquipoService` | `ICRUD<Equipo>`, `TecnicoService`, `JugadorService` |
| `PartidoService` | `ICRUD<Partido>`, `ICRUD<Estadistica>`, `EquipoService`, `ArbitroService`, `ReglasPuntuacion` |
| `EstadisticaService` | `ICRUD<Estadistica>` |

Ningún Service salta capas: la View habla con el Service, el Service habla con el DAO (o con otros Services), el DAO habla con los archivos.
