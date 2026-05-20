# Fase 6 — Formulario Jugador (JavaFX)

## Objetivo

Implementar el formulario CRUD completo de Jugadores en JavaFX.
El formulario permite registrar, buscar, actualizar y eliminar jugadores,
y muestra la lista completa en una TableView que se auto-actualiza.

## Rama

`feature/fase6-jugador-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 6.1 | `feat(view/fx): add jugador.fxml CRUD form` | Layout VBox con GridPane de campos, botones y TableView |
| 6.2 | `feat(view/fx): add JugadorController with service integration` | Controller con CRUD completo via JugadorService |
| 6.3 | `docs(fase6): add fase6-jugador-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/jugador.fxml` | Formulario CRUD con GridPane, botones y TableView |
| `src/main/java/ligaupc/view/fx/JugadorController.java` | Controller con initialize(), CRUD y helpers |
| `documentacion/fases/fase6-jugador-fx.md` | Este documento |

## Campos del formulario

| Campo | Tipo UI | Obligatorio | Validacion |
|---|---|---|---|
| Identificacion | TextField | Si | JugadorService verifica duplicados |
| Nombre | TextField | Si | JugadorService verifica que no este en blanco |
| Contacto | TextField | No | Libre |
| Posicion | ComboBox | Si | 9 opciones predefinidas |
| Numero Camiseta | TextField | Si | Entero > 0 (Integer.parseInt) |

## Flujo de interaccion

```
initialize()
  -> configura PropertyValueFactory en cada columna
  -> carga posiciones en ComboBox
  -> registra listener: seleccion en tabla -> llenarFormulario()
  -> refrescarTabla() -> jugadorService.listarJugadores()

registrar()
  -> construirJugadorDesdeFormulario()
  -> jugadorService.registrarJugador(jugador)
  -> limpiar() + refrescarTabla()

buscar()
  -> jugadorService.buscarJugador(id)
  -> llenarFormulario(jugador)

actualizar()
  -> construirJugadorDesdeFormulario()
  -> jugadorService.actualizarJugador(jugador)
  -> refrescarTabla()

eliminar()
  -> jugadorService.eliminarJugador(id)
  -> limpiar() + refrescarTabla()
```

## Patron PropertyValueFactory

```java
colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
```

JavaFX usa reflexion para llamar `getNombre()` en cada objeto Jugador
de la lista. Esto funciona con cualquier clase Java con getters estandar.
No requiere que el modelo extienda Observable o use Properties de JavaFX.

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP High Cohesion | JugadorController gestiona solo la UI de Jugadores |
| GRASP Low Coupling | Depende de JugadorService via MainApp.getJugadorService() — no conoce al DAO |
| GRASP Expert | Las validaciones de negocio las hace JugadorService, no el Controller |
| SOLID SRP | Si cambia el formulario de jugadores, solo cambia JugadorController |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al abrir la aplicacion y hacer click en "Jugadores":
  - Se muestra el formulario con todos los campos
  - La tabla carga los jugadores existentes (si hay datos en jugadores.txt)
  - Al seleccionar una fila, el formulario se llena automaticamente
  - Los botones ejecutan las operaciones CRUD con mensajes de exito/error
