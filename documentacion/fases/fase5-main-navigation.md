# Fase 5 — Ventana Principal y Navegacion

## Objetivo

Crear la ventana principal de la aplicacion JavaFX con barra lateral de navegacion
y area de contenido central. Al terminar esta fase `mvn javafx:run` abre la ventana
y la navegacion entre modulos funciona (con placeholder para los aun no implementados).

## Rama

`feature/fase5-main-navigation`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 5.1 | `feat(view/fx): add main.fxml sidebar layout` | Estructura BorderPane con cabecera, barra lateral y area de contenido |
| 5.2 | `feat(view/fx): add MainController navigation logic` | Carga FXML de modulo en contentArea; placeholder para modulos pendientes |
| 5.3 | `docs(fase5): add fase5-main-navigation phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/main.fxml` | Layout principal: BorderPane con cabecera, sidebar y StackPane central |
| `src/main/java/ligaupc/view/fx/MainController.java` | Controlador de navegacion: carga un FXML por modulo en contentArea |
| `documentacion/fases/fase5-main-navigation.md` | Este documento |

## Estructura del layout (main.fxml)

```
BorderPane
├── top    -> HBox (cabecera azul con titulo)
├── left   -> VBox (barra lateral oscura con 6 botones de navegacion)
└── center -> StackPane fx:id="contentArea"
               └── VBox (vista de bienvenida por defecto)
```

## Como funciona la navegacion

```
Usuario hace click en "Jugadores"
  -> MainController.loadJugadores()
     -> loadModule("jugador")
        -> FXMLLoader.load("/ligaupc/view/fx/jugador.fxml")
           [si existe]  -> contentArea.getChildren().setAll(vista)
           [si no existe] -> mostrarPlaceholder("jugador")
```

El metodo `loadModule()` captura `IOException` y `NullPointerException`
(cuando `getResource()` devuelve null por FXML no existente) y muestra
un panel temporal. Esto permite que la navegacion funcione antes de que
todos los formularios esten implementados.

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP Controller | MainController recibe eventos de UI y coordina la navegacion sin logica de negocio |
| GRASP High Cohesion | Solo gestiona navegacion — nada mas |
| GRASP Low Coupling | No conoce ninguna clase Controller de modulo. Solo conoce rutas de FXML |
| SOLID OCP | Agregar modulo nuevo = crear FXML + un metodo load<Modulo>() sin tocar lo existente |
| SOLID SRP | Si cambia la navegacion, solo cambia MainController |

## Validacion

- `mvn compile` -> BUILD SUCCESS (30 fuentes)
- `mvn javafx:run` -> ventana abre correctamente con:
  - Cabecera azul con titulo del sistema
  - Barra lateral con 6 botones de navegacion
  - Area central con mensaje de bienvenida
  - Al hacer click en cualquier boton -> muestra placeholder "Modulo en construccion"
