# Fase 10 — Formulario Equipo (JavaFX)

## Objetivo

Implementar el formulario completo de Equipos en JavaFX.
Es el primer modulo complejo: usa 3 Services y gestiona relaciones entre entidades
(equipo <-> tecnico, equipo <-> jugadores).

## Rama

`feature/fase10-equipo-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 10.1 | `feat(view/fx): add equipo.fxml CRUD form with player assignment` | Layout con 4 secciones y dos TableView side-by-side |
| 10.2 | `feat(view/fx): add EquipoController with EquipoService JugadorService TecnicoService` | Controller con 3 services y StringConverter para ComboBox<Tecnico> |
| 10.3 | `docs(fase10): add fase10-equipo-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/equipo.fxml` | Layout con 4 secciones: datos, tecnico, jugadores, tablas |
| `src/main/java/ligaupc/view/fx/EquipoController.java` | Controller con 3 services y 7 operaciones |
| `documentacion/fases/fase10-equipo-fx.md` | Este documento |

## Estructura del formulario (4 secciones)

```
Seccion 1: Datos basicos
  [Nombre*] [Sede*]
  [Registrar] [Buscar] [Actualizar] [Eliminar] [Limpiar]

Seccion 2: Asignar Tecnico
  [ComboBox<Tecnico>] [Asignar]

Seccion 3: Gestion de jugadores
  [ID Jugador] [Agregar Jugador] [Quitar Jugador]

Seccion 4: Tablas side-by-side
  | Equipos (nombre, sede, tecnico) | Jugadores del equipo seleccionado |
```

## Services usados y por que

| Service | Uso en este Controller |
|---|---|
| `EquipoService` | CRUD principal, asignarTecnico, agregarJugador, quitarJugador |
| `TecnicoService` | Poblar ComboBox con tecnicos existentes |
| `JugadorService` | No se usa directamente — EquipoService lo usa internamente |

Nota: `JugadorService` lo inyecta EquipoService internamente.
El Controller lo obtiene de MainApp para uso futuro (extensible via OCP).

## StringConverter para ComboBox de Tecnico

JavaFX no sabe como mostrar un objeto `Tecnico` en texto.
Sin StringConverter mostraria el resultado de `Tecnico.toString()` (poco legible).

Solucion:
```java
cmbTecnico.setConverter(new StringConverter<Tecnico>() {
    @Override
    public String toString(Tecnico t) {
        return (t != null) ? t.getNombre() + " (" + t.getIdentificacion() + ")" : "";
    }
    @Override
    public Tecnico fromString(String s) { return null; }
});
```

Muestra: "Carlos Perez (98765432)" — nombre + ID entre parentesis.

## Lambda para columna Tecnico anidado

Mismo patron que EstadisticaController — el tecnico es un objeto anidado:
```java
colTecnico.setCellValueFactory(cellData -> {
    Tecnico t = cellData.getValue().getTecnico();
    return new SimpleStringProperty(t != null ? t.getNombre() : "Sin asignar");
});
```

## Tabla de jugadores dinamica

Al seleccionar un equipo en la tabla izquierda:
1. Se llena el formulario con sus datos
2. La tabla derecha muestra los jugadores de ese equipo con su conteo
3. El label de la tabla derecha actualiza: "Jugadores de: Ingenieria (3)"

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP Low Coupling | EquipoController obtiene los 3 services via MainApp — cero acoplamiento directo |
| GRASP Expert | EquipoService valida: jugador ya en equipo, tecnico inexistente, etc. |
| GRASP High Cohesion | EquipoController solo gestiona la UI de Equipos |
| SOLID SRP | Si cambia la logica de asignar tecnico, solo cambia EquipoService |
| SOLID OCP | Agregar una operacion nueva al equipo = metodo en EquipoService + boton aqui |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al hacer click en "Equipos" -> formulario con 4 secciones visible
- ComboBox muestra tecnicos como "Nombre (ID)"
- Al seleccionar equipo en tabla -> se muestran sus jugadores en tabla derecha
- Agregar/Quitar jugador actualiza la tabla de jugadores en tiempo real
