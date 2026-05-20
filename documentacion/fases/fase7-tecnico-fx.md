# Fase 7 — Formulario Tecnico (JavaFX)

## Objetivo

Implementar el formulario CRUD de Tecnicos en JavaFX.
Mismo patron que la Fase 6 (Jugador), adaptado a los campos de Tecnico:
nombre, identificacion, contacto y especialidad tactica.

## Rama

`feature/fase7-tecnico-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 7.1 | `feat(view/fx): add tecnico.fxml CRUD form` | Layout con GridPane de 4 campos y TableView de 4 columnas |
| 7.2 | `feat(view/fx): add TecnicoController with service integration` | Controller CRUD via TecnicoService |
| 7.3 | `docs(fase7): add fase7-tecnico-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/tecnico.fxml` | Formulario CRUD con 4 campos, 5 botones y TableView |
| `src/main/java/ligaupc/view/fx/TecnicoController.java` | Controller con initialize(), CRUD y helpers |
| `documentacion/fases/fase7-tecnico-fx.md` | Este documento |

## Campos del formulario

| Campo | Tipo UI | Obligatorio | Validacion |
|---|---|---|---|
| Identificacion | TextField | Si | TecnicoService verifica duplicados |
| Nombre | TextField | Si | TecnicoService verifica que no este en blanco |
| Contacto | TextField | No | Libre |
| Especialidad | ComboBox | Si | 7 opciones predefinidas |

## Especialidades disponibles

Ofensiva, Defensiva, Tactica General, Formacion de Juveniles,
Preparacion Fisica, Porteros, Analitica de Datos.

## Diferencia con JugadorController

Tecnico es mas simple que Jugador: no tiene numeroCamiseta ni posicion atletica.
El patron es identico — solo cambian los campos y el service. Esto ilustra
**GRASP High Cohesion**: cada Controller es pequeno y enfocado en su entidad.

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP High Cohesion | TecnicoController gestiona solo la UI de Tecnicos |
| GRASP Low Coupling | Accede a TecnicoService via MainApp — no conoce al DAO |
| GRASP Expert | Validaciones delegadas a TecnicoService |
| SOLID SRP | Si cambia el formulario de tecnicos, solo cambia TecnicoController |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al hacer click en "Tecnicos" en la barra lateral -> formulario visible
- CRUD funcional con mensajes de exito/error
- Click en fila de tabla -> llena formulario automaticamente
