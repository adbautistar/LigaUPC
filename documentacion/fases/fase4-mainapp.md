# Fase 4 — Punto de Entrada JavaFX (MainApp)

## Objetivo

Crear `MainApp.java` como el nuevo punto de entrada del sistema usando JavaFX.
Implementa el patron ServiceLocator para distribuir los Services a todos los
Controllers sin acoplamiento directo entre ellos.

## Rama

`feature/fase4-mainapp`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 4.1 | `feat(main): add MainApp JavaFX Application with ServiceLocator` | Crea MainApp.java con init(), start() y getters estaticos |
| 4.2 | `refactor(main): retire console LigaUPC entry point, promote MainApp as active` | Actualiza pom.xml y agrega nota OCP en LigaUPC.java |
| 4.3 | `docs(fase4): add fase4-mainapp phase documentation` | Este archivo |

## Archivos creados / modificados

| Archivo | Accion | Descripcion |
|---|---|---|
| `src/main/java/ligaupc/MainApp.java` | Creado | Punto de entrada JavaFX con ServiceLocator |
| `src/main/java/ligaupc/LigaUPC.java` | Modificado | Javadoc actualizado indicando que es version consola de referencia |
| `pom.xml` | Modificado | `exec.mainClass`: `ligaupc.LigaUPC` → `ligaupc.MainApp` |
| `documentacion/fases/fase4-mainapp.md` | Creado | Este documento |

## Arquitectura de MainApp

```
MainApp (extends Application)
│
├── init()                     <- hilo no-UI, inicializa Services
│   ├── jugadorService     = new JugadorService()
│   ├── tecnicoService     = new TecnicoService()
│   ├── arbitroService     = new ArbitroService()
│   ├── equipoService      = new EquipoService()
│   ├── partidoService     = new PartidoService()
│   └── estadisticaService = new EstadisticaService()
│
├── start(Stage)               <- hilo UI, carga main.fxml
│   └── FXMLLoader.load("/ligaupc/view/fx/main.fxml")
│
└── getters estaticos          <- ServiceLocator
    ├── getJugadorService()
    ├── getTecnicoService()
    ├── getArbitroService()
    ├── getEquipoService()
    ├── getPartidoService()
    └── getEstadisticaService()
```

## Como usan los Controllers el ServiceLocator

```java
// Dentro de cualquier Controller, en el metodo initialize():
private JugadorService jugadorService = MainApp.getJugadorService();
```

No hay acoplamiento entre Controllers. Cada uno toma solo lo que necesita.

## Por que ServiceLocator y no inyeccion de dependencias

JavaFX instancia los Controllers internamente desde FXMLLoader usando el
constructor sin parametros. No existe un mecanismo nativo de DI (como Spring).

Las alternativas son:
1. ServiceLocator (estatico) <- elegida: simple, sin frameworks externos
2. Pasar el controllerFactory al FXMLLoader (verboso, mas acoplado)
3. Usar un framework DI (CDI, Guice) <- excesivo para un proyecto educativo

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP Creator | MainApp crea los Services porque los usa como punto de distribucion |
| GRASP Controller | MainApp orquesta el arranque del sistema completo |
| GRASP Low Coupling | Cada Controller depende solo de MainApp (1 dependencia) no de otros Controllers |
| SOLID OCP | LigaUPC.java se conserva intacta como referencia — no se modifico su comportamiento |

## Nota sobre mvn javafx:run en esta fase

`mvn javafx:run` fallara porque `main.fxml` no existe aun.
La validacion completa con ventana visible se realiza al final de la Fase 5.
`mvn compile` funciona correctamente: BUILD SUCCESS con 30 fuentes compiladas.

## Verificacion

- `mvn compile` ejecutado en commits 4.1 y 4.2 → `BUILD SUCCESS` en ambos
