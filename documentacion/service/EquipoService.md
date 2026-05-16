# EquipoService — Capa de negocio de Equipo

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/EquipoService.java`

---

## ¿Qué es?

Concentra la lógica de negocio de equipos. Además del CRUD básico, gestiona tres operaciones propias del dominio: **asignar un técnico** a un equipo, **agregar un jugador** al plantel y **quitar un jugador** del plantel. Para estas operaciones, colabora con `TecnicoService` y `JugadorService`.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo gestiona reglas de negocio de equipos |
| **GRASP Low Coupling** | Delega validaciones a los Services especializados |
| **SOLID SRP** | Cambia únicamente si cambian las reglas de equipos |
| **SOLID OCP** | Depende de `ICRUD<Equipo>`, no de `EquipoDAO` directamente |

---

## Concepto clave: Colaboración entre Services

`EquipoService` no valida por sí solo si un técnico o jugador existen — esa responsabilidad pertenece a `TecnicoService` y `JugadorService` respectivamente. En lugar de duplicar esa lógica, **delega**:

```java
public void asignarTecnico(String nombreEquipo, String idTecnico) {
    Equipo equipo = buscarEquipo(nombreEquipo);          // valida que el equipo exista
    Tecnico tecnico = tecnicoService.buscarTecnico(idTecnico); // delega validación al experto

    equipo.setTecnico(tecnico);
    equipoDAO.actualizar(equipo);
}
```

Si `buscarTecnico()` no encuentra al técnico, lanza `IllegalArgumentException` automáticamente — `EquipoService` no necesita agregar lógica extra.

Esto aplica **Low Coupling**: `EquipoService` no sabe *cómo* se verifica un técnico, solo sabe *a quién preguntarle*.

---

## Inyección de dependencia

`EquipoService` recibe tres dependencias: su propio DAO y los dos Services colaboradores.

```java
public EquipoService(ICRUD<Equipo> equipoDAO,
                     TecnicoService tecnicoService,
                     JugadorService jugadorService) { ... }

// Constructor por defecto para uso normal
public EquipoService() { ... }
```

---

## Métodos CRUD

| Método | Retorno | Lanza excepción si... |
|---|---|---|
| `registrarEquipo(Equipo)` | `void` | Dato inválido o nombre duplicado |
| `listarEquipos()` | `List<Equipo>` | — |
| `buscarEquipo(String)` | `Equipo` | No existe ese nombre |
| `actualizarEquipo(Equipo)` | `void` | No existe ese nombre |
| `eliminarEquipo(String)` | `void` | No existe ese nombre |

El identificador de `Equipo` es su **nombre** (no tiene campo ID numérico).

---

## Métodos de dominio

### `asignarTecnico(String nombreEquipo, String idTecnico)`

```
buscarEquipo(nombreEquipo)         → IllegalArgumentException si no existe
tecnicoService.buscarTecnico(id)   → IllegalArgumentException si no existe
equipo.setTecnico(tecnico)
equipoDAO.actualizar(equipo)
```

### `agregarJugador(String nombreEquipo, String idJugador)`

```
buscarEquipo(nombreEquipo)         → IllegalArgumentException si no existe
jugadorService.buscarJugador(id)   → IllegalArgumentException si no existe
¿jugador ya está en el equipo?     → IllegalArgumentException (no duplicados)
equipo.agregarJugador(jugador)
equipoDAO.actualizar(equipo)
```

La verificación de duplicado usa `stream().anyMatch()` sobre la lista de jugadores del equipo — compara por identificación, no por referencia de objeto.

### `quitarJugador(String nombreEquipo, String idJugador)`

```
buscarEquipo(nombreEquipo)         → IllegalArgumentException si no existe
equipo.quitarJugador(idJugador)    → retorna false si el jugador no estaba
¿removido == false?                → IllegalArgumentException (no pertenece al equipo)
equipoDAO.actualizar(equipo)
```

**Responsabilidad dividida entre capas:**
- El **modelo** (`Equipo`) sabe *cómo* quitar un jugador de su lista (`removeIf`).
- El **Service** decide *si está permitido* quitarlo (valida que el equipo exista y que el jugador realmente pertenezca a él).
- La **View** solo muestra el resultado al usuario.

Esto es **SOLID SRP** en acción: cada capa tiene exactamente una razón para cambiar.

---

## Flujo: agregar jugador a equipo

```
EquipoView
    │  llama agregarJugador("Ingeniería", "1001")
    ▼
EquipoService
    ├── buscarEquipo("Ingeniería")
    │       └── equipoDAO.leerPorId("Ingeniería") → Equipo encontrado
    │
    ├── jugadorService.buscarJugador("1001")
    │       └── jugadorDAO.leerPorId("1001") → Jugador encontrado
    │
    ├── ¿jugador ya en equipo? → No → continúa
    │
    ├── equipo.agregarJugador(jugador)  ← GRASP Expert en el model
    │
    └── equipoDAO.actualizar(equipo)    → equipos.txt reescrito

View muestra "Jugador agregado exitosamente."
```

## Flujo: quitar jugador de equipo

```
EquipoView
    │  llama quitarJugador("Ingeniería", "1001")
    ▼
EquipoService
    ├── buscarEquipo("Ingeniería")
    │       └── equipoDAO.leerPorId("Ingeniería") → Equipo encontrado
    │
    ├── equipo.quitarJugador("1001")     ← GRASP Expert: el modelo gestiona su lista
    │       └── removeIf(...) → retorna true si fue removido, false si no existía
    │
    ├── ¿removido == false? → IllegalArgumentException
    │
    └── equipoDAO.actualizar(equipo)     → equipos.txt reescrito

View muestra "Jugador retirado del equipo exitosamente."
```

---

## Relación con otras clases

```
EquipoView  →  EquipoService  →  ICRUD<Equipo>     ←  EquipoDAO
                             →  TecnicoService      (colaboración: asignarTecnico)
                             →  JugadorService      (colaboración: agregar/quitarJugador)
```
