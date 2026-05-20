# Fase 8 — Formulario Arbitro (JavaFX)

## Objetivo

Implementar el formulario CRUD de Arbitros en JavaFX.
Mismo patron que Fases 6 y 7, adaptado a los campos de Arbitro:
nombre, identificacion, contacto y categoria de certificacion.

## Rama

`feature/fase8-arbitro-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 8.1 | `feat(view/fx): add arbitro.fxml CRUD form` | Layout con GridPane de 4 campos y TableView de 4 columnas |
| 8.2 | `feat(view/fx): add ArbitroController with service integration` | Controller CRUD via ArbitroService |
| 8.3 | `docs(fase8): add fase8-arbitro-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/arbitro.fxml` | Formulario CRUD con 4 campos, 5 botones y TableView |
| `src/main/java/ligaupc/view/fx/ArbitroController.java` | Controller con initialize(), CRUD y helpers |
| `documentacion/fases/fase8-arbitro-fx.md` | Este documento |

## Campos del formulario

| Campo | Tipo UI | Obligatorio | Validacion |
|---|---|---|---|
| Identificacion | TextField | Si | ArbitroService verifica duplicados |
| Nombre | TextField | Si | ArbitroService verifica que no este en blanco |
| Contacto | TextField | No | Libre |
| Certificacion | ComboBox | Si | FIFA, CONMEBOL, Nacional, Regional, Local |

## PropertyValueFactory y getter no estandar

La columna `colCertificacion` usa:
```java
colCertificacion.setCellValueFactory(new PropertyValueFactory<>("categoriaCertificacion"));
```
JavaFX busca el getter `getCategoriaCertificacion()` por reflexion.
El nombre del campo en PropertyValueFactory DEBE coincidir exactamente
con el nombre del atributo en el modelo (sin mayusculas extras).

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP High Cohesion | ArbitroController gestiona solo la UI de Arbitros |
| GRASP Low Coupling | Accede a ArbitroService via MainApp — no conoce al DAO |
| GRASP Expert | Validaciones delegadas a ArbitroService |
| SOLID SRP | Si cambia el formulario de arbitros, solo cambia ArbitroController |
| SOLID OCP | El patron es el mismo que Jugador y Tecnico — extensible sin modificar los anteriores |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al hacer click en "Arbitros" -> formulario visible con 5 certificaciones en ComboBox
- CRUD funcional con mensajes de exito/error
- Click en fila de tabla -> llena formulario automaticamente
