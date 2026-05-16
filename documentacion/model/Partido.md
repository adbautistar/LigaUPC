# Partido — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Partido.java`

---

## ¿Qué es?

Representa un encuentro entre dos equipos. Es la clase central del negocio: conecta equipos, árbitros, fechas y resultados en un solo objeto.

---

## Atributos

| Atributo | Tipo | Descripción |
|---|---|---|
| `idPartido` | int | Identificador único del partido |
| `fecha` | Date | Fecha en que se juega |
| `equipoLocal` | Equipo | Equipo que juega en casa |
| `equipoVisitante` | Equipo | Equipo visitante |
| `golesLocal` | int | Goles del equipo local |
| `golesVisitante` | int | Goles del equipo visitante |
| `arbitro` | Arbitro | Árbitro asignado al partido |
| `estado` | String | "Programado", "En Juego", "Finalizado" |

---

## Constructor principal

```java
public Partido(int idPartido, Date fecha, Equipo local, Equipo visitante, Arbitro arbitro) {
    // ...
    this.estado = "Programado"; // Estado inicial siempre es Programado
}
```

El estado arranca en `"Programado"` automáticamente — el sistema lo actualiza conforme avanza el partido.

---

## Método `obtenerResultado()`

```java
public String obtenerResultado() {
    return equipoLocal.getNombre() + " " + golesLocal + " - " +
           golesVisitante + " " + equipoVisitante.getNombre();
}
```

**Ejemplo de salida:**
```
Ingeniería 2 - 1 Medicina
```

Este método es un ejemplo de **High Cohesion**: la lógica de formatear el resultado vive en `Partido` porque `Partido` tiene todos los datos necesarios. La vista solo llama `partido.obtenerResultado()`.

---

## Relación con otras clases

```
Partido
├── referencia → Equipo (local y visitante)
├── referencia → Arbitro
└── es gestionado por → GestorPartido (service)
                      → PartidoDAO (dao)
```

---

## Nota sobre la persistencia

`Partido` es la clase más compleja de persistir porque contiene referencias a otros objetos (`Equipo`, `Arbitro`). El `PartidoDAO` guardará solo los identificadores (nombre del equipo, ID del árbitro) y reconstruirá las referencias al leer. Este proceso se llama **serialización por referencia**.
