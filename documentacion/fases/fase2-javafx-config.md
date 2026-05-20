# Fase 2 — Configuracion JavaFX en Maven

## Objetivo

Incorporar JavaFX 21.0.2 al proyecto Maven como dependencia y configurar el plugin para poder ejecutar la aplicacion grafica con `mvn javafx:run`. Crear ademas la estructura de directorios donde viviran los archivos FXML.

## Rama

`feature/fase2-javafx-config`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 2.1 | `chore(config): add JavaFX 21.0.2 dependencies and plugin to pom.xml` | Agrega javafx-controls, javafx-fxml y javafx-maven-plugin al pom.xml |
| 2.2 | `chore(config): create resources directory structure for FXML files` | Crea `src/main/resources/ligaupc/view/fx/` con `.gitkeep` |
| 2.3 | `docs(fase2): add fase2-javafx-config phase documentation` | Este archivo |

## Archivos creados / modificados

| Archivo | Accion | Descripcion |
|---|---|---|
| `pom.xml` | Modificado | Agrega dependencias y plugin JavaFX, centraliza version en propiedad `javafx.version` |
| `src/main/resources/ligaupc/view/fx/.gitkeep` | Creado | Mantiene el directorio en git (git no versiona directorios vacios) |
| `documentacion/fases/fase2-javafx-config.md` | Creado | Este documento |

## Dependencias agregadas

| Artefacto | Version | Proposito |
|---|---|---|
| `org.openjfx:javafx-controls` | 21.0.2 | Componentes UI: Button, TableView, TextField, ComboBox, etc. |
| `org.openjfx:javafx-fxml` | 21.0.2 | Motor de carga de archivos .fxml y anotacion @FXML |
| `org.openjfx:javafx-maven-plugin` | 0.0.8 | Permite ejecutar con `mvn javafx:run` sin configuracion manual del modulepath |

## Por que JavaFX 21.0.2

- Version LTS alineada con Java 17 LTS
- Ambas versiones (21) comparten el mismo ciclo de soporte
- Compatible con el plugin 0.0.8 que es el ultimo estable del proyecto OpenJFX

## Estructura de recursos

```
src/main/resources/
└── ligaupc/
    └── view/
        └── fx/          <- aqui iran todos los .fxml (Fases 5-11)
            └── .gitkeep
```

Maven copia automaticamente todo lo que esta en `src/main/resources/` al classpath.
Eso permite cargar los FXML con `getClass().getResource("/ligaupc/view/fx/main.fxml")`.

## Verificacion

- `mvn compile` ejecutado antes del commit 2.1 → `BUILD SUCCESS` con 29 fuentes compiladas
- Las dependencias JavaFX se resolvieron correctamente desde Maven Central

## Nota sobre mainClass en el plugin

El plugin tiene configurado `ligaupc.MainApp` como clase principal.
Esta clase **no existe todavia** — se creara en la Fase 4.
Por eso `mvn javafx:run` fallara hasta completar la Fase 4-5.
`mvn compile` funciona correctamente desde ya.
