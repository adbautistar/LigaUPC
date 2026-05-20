# Fase 3 — Refactor View Consola

## Objetivo

Mover las 6 vistas de consola al subpaquete `view/console/` para separarlas fisicamente de las futuras vistas JavaFX (`view/fx/`). Esto preserva el codigo de consola como referencia pedagogica y prepara la estructura para la migracion.

## Rama

`refactor/fase3-console-subpackage`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 3.1 | `refactor(view): move console views to view/console subpackage` | Mueve 6 `*View.java` con `git mv` y actualiza la declaracion `package` |
| 3.2 | `refactor(main): update LigaUPC.java imports after view package move` | Cambia `import ligaupc.view.*` por `import ligaupc.view.console.*` |
| 3.3 | `docs(fase3): add fase3-refactor-console phase documentation` | Este archivo |

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `view/ArbitroView.java` → `view/console/ArbitroView.java` | Ruta + `package ligaupc.view.console;` |
| `view/EquipoView.java` → `view/console/EquipoView.java` | Ruta + `package ligaupc.view.console;` |
| `view/EstadisticaView.java` → `view/console/EstadisticaView.java` | Ruta + `package ligaupc.view.console;` |
| `view/JugadorView.java` → `view/console/JugadorView.java` | Ruta + `package ligaupc.view.console;` |
| `view/PartidoView.java` → `view/console/PartidoView.java` | Ruta + `package ligaupc.view.console;` |
| `view/TecnicoView.java` → `view/console/TecnicoView.java` | Ruta + `package ligaupc.view.console;` |
| `LigaUPC.java` | `import ligaupc.view.*` → `import ligaupc.view.console.*` |

## Estructura resultante

```
view/
├── console/          <- vistas de consola (referencia pedagogica)
│   ├── ArbitroView.java
│   ├── EquipoView.java
│   ├── EstadisticaView.java
│   ├── JugadorView.java
│   ├── PartidoView.java
│   └── TecnicoView.java
└── fx/               <- vistas JavaFX (se implementan en Fases 5-11)
```

## Por que usar git mv

`git mv` en lugar de borrar y crear preserva el historial de cada archivo.
Git detecta el rename con 99% de similitud, lo que permite ver el historial
completo del archivo antes y despues del movimiento con `git log --follow`.

Esto es fundamental para trazabilidad: un estudiante puede ver la evolucion
completa de `JugadorView.java` desde su creacion hasta la version JavaFX.

## Patron SOLID aplicado — OCP (Open/Closed Principle)

El proyecto se **extiende** agregando `view/fx/` sin **modificar** ni eliminar
`view/console/`. La capa de consola queda intacta como referencia de como
se implementaba la misma funcionalidad sin interfaz grafica.

## Verificacion

- `mvn compile` ejecutado despues del commit 3.2 → `BUILD SUCCESS` con 29 fuentes
- Git registro los 6 archivos como `rename (99%)`, historial preservado
