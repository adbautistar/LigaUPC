# Fase 9 — Vista Estadistica (JavaFX)

## Objetivo

Implementar la vista de Tabla de Posiciones en JavaFX.
Es una vista de SOLO LECTURA — no tiene formulario de creacion ni edicion.
Las estadisticas las genera automaticamente PartidoService al registrar resultados.

## Rama

`feature/fase9-estadistica-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 9.1 | `feat(view/fx): add estadistica.fxml read-only view` | Layout con boton Refrescar y TableView de 10 columnas |
| 9.2 | `feat(view/fx): add EstadisticaController with service integration` | Cell factories con lambda para campo Equipo anidado |
| 9.3 | `docs(fase9): add fase9-estadistica-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/estadistica.fxml` | Vista con boton Refrescar y TableView de posiciones |
| `src/main/java/ligaupc/view/fx/EstadisticaController.java` | Controller con cell factories lambda y carga de clasificacion |
| `documentacion/fases/fase9-estadistica-fx.md` | Este documento |

## Columnas de la tabla de posiciones

| Columna | Descripcion | Origen |
|---|---|---|
| # | Posicion en la tabla | Calculado por indice |
| Equipo | Nombre del equipo | `equipo.getNombre()` (campo anidado) |
| PJ | Partidos Jugados | `getPartidosJugados()` |
| PG | Partidos Ganados | `getPartidosGanados()` |
| PE | Partidos Empatados | `getPartidosEmpatados()` |
| PP | Partidos Perdidos | `getPartidosPerdidos()` |
| GF | Goles a Favor | `getGolesAFavor()` |
| GC | Goles en Contra | `getGolesEnContra()` |
| DG | Diferencia de Goles | Calculado: GF - GC |
| Pts | Puntos | `getPuntos()` |

## Por que lambdas en lugar de PropertyValueFactory

`PropertyValueFactory` busca getters directos en el objeto de la fila.
Funciona bien para `nombre`, `puntos`, etc. — campos primitivos directos.

Pero `Estadistica.equipo` es un objeto anidado. Para mostrar `equipo.getNombre()`
se necesita acceso en dos niveles, que PropertyValueFactory no soporta.

Solucion con lambda:
```java
colEquipo.setCellValueFactory(cellData -> {
    Estadistica e = cellData.getValue();
    String nombre = (e.getEquipo() != null) ? e.getEquipo().getNombre() : "-";
    return new SimpleStringProperty(nombre);
});
```

Lo mismo aplica para los campos `int`: se usan `SimpleIntegerProperty`
porque JavaFX requiere que los valores de celda sean observables.

## Criterio de ordenamiento (delegado a EstadisticaService)

```
1. Puntos (descendente)
2. Diferencia de goles (descendente)
3. Goles a favor (descendente)
```

El Controller NO conoce este criterio — solo llama `listarClasificacion()`.
Si el criterio cambia, solo cambia EstadisticaService. Esto es SOLID SRP.

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP Expert | EstadisticaService calcula y ordena — el Controller solo muestra |
| GRASP High Cohesion | EstadisticaController solo gestiona la vista de estadisticas |
| GRASP Low Coupling | Accede a EstadisticaService via MainApp — no conoce al DAO |
| SOLID SRP | Cambio en criterio de clasificacion -> solo cambia EstadisticaService |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al hacer click en "Estadisticas" -> tabla visible con 10 columnas
- Boton "Refrescar" recarga los datos en tiempo real
- Si no hay partidos registrados -> placeholder informativo visible
