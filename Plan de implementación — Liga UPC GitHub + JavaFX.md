Excelente plan. Antes de presentártelo, déjame verificar el `pom.xml` actual para que el plan refleje la realidad exacta del proyecto:

Confirmado — el `pom.xml` tiene `release 25` que es incorrecto. Aquí está el plan completo organizado:

---

# Plan de implementación — Liga UPC: GitHub + JavaFX

## Estado actual confirmado

| Elemento | Estado |
|---|---|
| `pom.xml` — compiler release | ⚠️ Dice `25` — debe corregirse a `17` |
| `pom.xml` — dependencias JavaFX | ❌ No existen — hay que agregarlas |
| `src/main/resources/` | ❌ No existe — hay que crearla |
| model / dao / service | ✅ Completos, no se tocan |
| view (consola) | ✅ Completa — se mueve a subpaquete `console/` |

---

## FASE 1 — Subir a GitHub (3 tareas)

| # | Tarea | Archivos |
|---|---|---|
| 1.1 | Crear `.gitignore` (NetBeans + Maven + Java) | `.gitignore` → Crear |
| 1.2 | Corregir `pom.xml`: release `25` → `17` | `pom.xml` → Modificar |
| 1.3 | `git init` → commit inicial → push | Comandos git |

> ⚠️ **Riesgo identificado:** El proyecto está en `OneDrive`. OneDrive sincroniza en tiempo real y puede corromper los índices de Git. Recomendación: pausar OneDrive antes de cada operación `git push`.

---

## FASE 2 — Configurar JavaFX en Maven (3 tareas)

| # | Tarea | Archivos |
|---|---|---|
| 2.1 | Agregar dependencias y plugin JavaFX al `pom.xml` | `pom.xml` → Modificar |
| 2.2 | Crear directorio `src/main/resources/ligaupc/view/fx/` | Directorio → Crear |
| 2.3 | ✅ Validar: `mvn compile` debe pasar sin errores | — |

**Versiones exactas a usar:**
```xml
javafx-controls    21.0.2   (compatible con Java 17 LTS)
javafx-fxml        21.0.2   (siempre mismo release que controls)
javafx-maven-plugin 0.0.8   (plugin oficial de OpenJFX)
```

---

## FASE 3 — Refactor de la capa view consola (1 tarea)

| # | Tarea | Archivos |
|---|---|---|
| 3.1 | Mover vistas de consola al subpaquete `view/console/` — solo cambia la línea `package` | 6 × `*View.java` → Modificar |

> 💡 **¿Por qué moverlas?** Para conservarlas como referencia pedagógica lado a lado con las vistas JavaFX. Ilustra perfectamente el principio **OCP**: el proyecto se extiende con JavaFX sin borrar lo anterior.

---

## FASE 4 — Nuevo punto de entrada JavaFX (2 tareas)

| # | Tarea | Archivos |
|---|---|---|
| 4.1 | Crear `MainApp.java` (extiende `Application`) | `MainApp.java` → Crear |
| 4.2 | Actualizar `LigaUPC.java` (o eliminarlo) | `LigaUPC.java` → Modificar/Eliminar |

`MainApp` instancia todos los Services en `init()` y los expone mediante getters estáticos para que los Controllers los usen. Esto es un **ServiceLocator** — patrón pragmático para JavaFX sin framework de inyección.

---

## FASE 5 — Ventana principal y navegación (2 tareas)

| # | Tarea | Archivos |
|---|---|---|
| 5.1 | Crear `main.fxml` — barra lateral + área de contenido central | `main.fxml` → Crear |
| 5.2 | Crear `MainController.java` — carga cada módulo al hacer click | `MainController.java` → Crear |
| 5.3 | ✅ Validar: `mvn javafx:run` → ventana principal debe abrir | — |

---

## FASES 6–8 — Un formulario por entidad (12 tareas = 6 FXML + 6 Controllers)

Orden de implementación según complejidad (simple → complejo):

| Fase | Módulo | Complejidad | Dependencias de Services |
|---|---|---|---|
| 6 | **Jugador** | 🟢 Simple | 1 service |
| 7 | **Técnico** | 🟢 Simple | 1 service |
| 8 | **Árbitro** | 🟢 Simple | 1 service |
| 9 | **Estadística** | 🟡 Media | 1 service — solo lectura, sin formulario |
| 10 | **Equipo** | 🔴 Complejo | 3 services (Equipo + Jugador + Técnico) |
| 11 | **Partido** | 🔴 Complejo | 3 services (Partido + Equipo + Árbitro) |

> Cada módulo = 1 archivo FXML + 1 Controller. Patrón idéntico al de la capa consola: **High Cohesion** (un controller por entidad), **Low Coupling** (accede al Service, no al DAO).

---

## FASE 9 — Commit final

```
git commit -m "feat: migracion view consola a JavaFX con FXML y controllers"
git push
```

---

## Estructura de archivos FINAL

```
LigaUPC/
├── .gitignore                          ← NUEVO
├── pom.xml                             ← MODIFICADO
└── src/main/
    ├── java/ligaupc/
    │   ├── MainApp.java                ← NUEVO (reemplaza LigaUPC.java)
    │   ├── model/                      ← SIN CAMBIOS (8 clases)
    │   ├── dao/                        ← SIN CAMBIOS (6 clases)
    │   ├── service/                    ← SIN CAMBIOS (8 clases)
    │   └── view/
    │       ├── console/                ← MOVIDAS (6 clases, solo cambia package)
    │       └── fx/                     ← NUEVAS (7 controllers)
    └── resources/ligaupc/view/fx/      ← NUEVO directorio
        ├── main.fxml
        ├── jugador.fxml
        ├── tecnico.fxml
        ├── arbitro.fxml
        ├── equipo.fxml
        ├── partido.fxml
        └── estadistica.fxml
```

**Total clases nuevas:** 8 (MainApp + 7 Controllers)
**Total FXML nuevos:** 7
**Clases sin tocar:** 22 (todo model + dao + service)

---

## Resumen de patrones GRASP/SOLID en la nueva capa

| Patrón | Aplicación en JavaFX |
|---|---|
| **GRASP Controller** | `MainApp` orquesta el arranque; `MainController` gestiona la navegación |
| **GRASP High Cohesion** | Cada `*Controller` gestiona solo una entidad |
| **GRASP Low Coupling** | Los Controllers acceden a Services, nunca a DAOs |
| **SOLID SRP** | Si cambia el formulario de Jugadores, solo cambia `JugadorController` |
| **SOLID OCP** | Agregar un nuevo módulo = crear un FXML + Controller sin tocar los existentes |

---

¿Arrancamos con la **Fase 1 — GitHub**?