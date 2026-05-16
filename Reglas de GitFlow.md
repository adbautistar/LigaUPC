# Git Workflow & Coding Rules — LigaUPC (Full Traceability Mode)

You are an expert Senior Software Engineer. You are operating in a single-developer repository where **absolute historical traceability** is the highest priority. Every branch and every atomic commit must be permanently preserved in the Git graph.

---

## 1. Branching Strategy

- NEVER write code or make commits directly on the `main` branch.
- Create explicit branches from `main` for every task using these prefixes:
  - `feature/short-description` — New capabilities
  - `fix/short-description` — Bug fixes
  - `refactor/short-description` — Code quality or architecture improvements
  - `docs/short-description` — Documentation updates
- **CRITICAL:** Do NOT delete branches after merging. They must remain in the repository history indefinitely for traceability.

---

## 2. Commit Granularity (Atomic Activities)

- Make small, focused, atomic commits for each logical sub-task or activity.
- Verify that the code compiles (`mvn compile`) before making each commit.
- Use the Conventional Commits standard strictly:
  - `feat(scope): description` — nueva funcionalidad
  - `fix(scope): description` — corrección de bug
  - `refactor(scope): description` — reestructura sin cambio de comportamiento
  - `docs(scope): description` — solo documentación
  - `chore(scope): description` — configuración, dependencias, build

---

## 3. Merging Protocol for Full History Preservation

When a task is fully completed and verified, merge it into `main` preserving the exact commit graph:

1. Switch to `main`:
   ```bash
   git checkout main
   ```
2. Execute the merge using `--no-ff` (No Fast-Forward):
   ```bash
   git merge --no-ff branch-name -m "merge: integrate feature/short-description into main"
   ```
3. Push to remote:
   ```bash
   git push origin main
   ```

---

## 4. Documentation Rule

- Every phase produces exactly **one documentation file** in `documentacion/fases/`.
- The docs commit is always the **last commit** of the branch, before the merge.
- Format: `docs(faseN): add faseN-description phase documentation`

---

## 5. Compile Verification Rule

- Every commit that touches `.java` or `pom.xml` **must** be preceded by a successful `mvn compile`.
- If compilation fails, fix first — never commit broken code.

---

---

# Plan de Implementación — LigaUPC: GitHub + JavaFX

## Estado base confirmado

| Elemento | Estado |
|---|---|
| `pom.xml` — compiler release | ⚠️ `25` → corregir a `17` |
| `pom.xml` — dependencias JavaFX | ❌ No existen |
| `src/main/resources/` | ❌ No existe |
| model / dao / service | ✅ Completos — no se tocan |
| view (consola) | ✅ Completa — se mueve a `view/console/` |

**Versiones JavaFX a usar:**
- `javafx-controls` 21.0.2
- `javafx-fxml` 21.0.2
- `javafx-maven-plugin` 0.0.8 (compatible con Java 17 LTS)

---

## FASE 1 — GitHub Setup

**Rama:** `feature/fase1-github-setup`

| # | Commit | Archivos |
|---|---|---|
| 1.1 | `chore(config): add .gitignore for NetBeans Maven Java` | `.gitignore` |
| 1.2 | `chore(config): fix compiler release from 25 to 17 in pom.xml` | `pom.xml` |
| 1.3 | `docs(fase1): add fase1-github-setup phase documentation` | `documentacion/fases/fase1-github-setup.md` |
| — | `merge: integrate feature/fase1-github-setup into main` | merge --no-ff → push |

---

## FASE 2 — Configuración JavaFX en Maven

**Rama:** `feature/fase2-javafx-config`

| # | Commit | Archivos |
|---|---|---|
| 2.1 | `chore(config): add JavaFX 21.0.2 dependencies and plugin to pom.xml` | `pom.xml` |
| 2.2 | `chore(config): create resources directory structure for FXML files` | `src/main/resources/ligaupc/view/fx/.gitkeep` |
| 2.3 | `docs(fase2): add fase2-javafx-config phase documentation` | `documentacion/fases/fase2-javafx-config.md` |
| — | `merge: integrate feature/fase2-javafx-config into main` | merge --no-ff → push |

> Validación: `mvn compile` debe pasar sin errores antes del merge.

---

## FASE 3 — Refactor View Consola

**Rama:** `refactor/fase3-console-subpackage`

| # | Commit | Archivos |
|---|---|---|
| 3.1 | `refactor(view): move console views to view/console subpackage` | 6 × `*View.java` |
| 3.2 | `refactor(main): update LigaUPC.java imports after view package move` | `LigaUPC.java` |
| 3.3 | `docs(fase3): add fase3-refactor-console phase documentation` | `documentacion/fases/fase3-refactor-console.md` |
| — | `merge: integrate refactor/fase3-console-subpackage into main` | merge --no-ff → push |

> Por que: Conservar las views de consola como referencia pedagogica junto a las vistas JavaFX. Ilustra OCP: el proyecto se extiende sin borrar lo anterior.

---

## FASE 4 — Punto de Entrada JavaFX (MainApp)

**Rama:** `feature/fase4-mainapp`

| # | Commit | Archivos |
|---|---|---|
| 4.1 | `feat(main): add MainApp JavaFX Application with ServiceLocator` | `src/main/java/ligaupc/MainApp.java` |
| 4.2 | `refactor(main): retire console LigaUPC entry point` | `LigaUPC.java` |
| 4.3 | `docs(fase4): add fase4-mainapp phase documentation` | `documentacion/fases/fase4-mainapp.md` |
| — | `merge: integrate feature/fase4-mainapp into main` | merge --no-ff → push |

> MainApp instancia todos los Services en init() y los expone con getters estaticos — patron ServiceLocator para JavaFX sin framework de inyeccion.

---

## FASE 5 — Ventana Principal y Navegacion

**Rama:** `feature/fase5-main-navigation`

| # | Commit | Archivos |
|---|---|---|
| 5.1 | `feat(view/fx): add main.fxml sidebar layout` | `src/main/resources/ligaupc/view/fx/main.fxml` |
| 5.2 | `feat(view/fx): add MainController navigation logic` | `src/main/java/ligaupc/view/fx/MainController.java` |
| 5.3 | `docs(fase5): add fase5-main-navigation phase documentation` | `documentacion/fases/fase5-main-navigation.md` |
| — | `merge: integrate feature/fase5-main-navigation into main` | merge --no-ff → push |

> Validacion: `mvn javafx:run` — la ventana principal debe abrir.

---

## FASE 6 — Formulario Jugador

**Rama:** `feature/fase6-jugador-fx`

| # | Commit | Archivos |
|---|---|---|
| 6.1 | `feat(view/fx): add jugador.fxml CRUD form` | `jugador.fxml` |
| 6.2 | `feat(view/fx): add JugadorController with service integration` | `JugadorController.java` |
| 6.3 | `docs(fase6): add fase6-jugador-fx phase documentation` | `documentacion/fases/fase6-jugador-fx.md` |
| — | `merge: integrate feature/fase6-jugador-fx into main` | merge --no-ff → push |

---

## FASE 7 — Formulario Tecnico

**Rama:** `feature/fase7-tecnico-fx`

| # | Commit | Archivos |
|---|---|---|
| 7.1 | `feat(view/fx): add tecnico.fxml CRUD form` | `tecnico.fxml` |
| 7.2 | `feat(view/fx): add TecnicoController with service integration` | `TecnicoController.java` |
| 7.3 | `docs(fase7): add fase7-tecnico-fx phase documentation` | `documentacion/fases/fase7-tecnico-fx.md` |
| — | `merge: integrate feature/fase7-tecnico-fx into main` | merge --no-ff → push |

---

## FASE 8 — Formulario Arbitro

**Rama:** `feature/fase8-arbitro-fx`

| # | Commit | Archivos |
|---|---|---|
| 8.1 | `feat(view/fx): add arbitro.fxml CRUD form` | `arbitro.fxml` |
| 8.2 | `feat(view/fx): add ArbitroController with service integration` | `ArbitroController.java` |
| 8.3 | `docs(fase8): add fase8-arbitro-fx phase documentation` | `documentacion/fases/fase8-arbitro-fx.md` |
| — | `merge: integrate feature/fase8-arbitro-fx into main` | merge --no-ff → push |

---

## FASE 9 — Vista Estadistica (solo lectura)

**Rama:** `feature/fase9-estadistica-fx`

| # | Commit | Archivos |
|---|---|---|
| 9.1 | `feat(view/fx): add estadistica.fxml read-only view` | `estadistica.fxml` |
| 9.2 | `feat(view/fx): add EstadisticaController with service integration` | `EstadisticaController.java` |
| 9.3 | `docs(fase9): add fase9-estadistica-fx phase documentation` | `documentacion/fases/fase9-estadistica-fx.md` |
| — | `merge: integrate feature/fase9-estadistica-fx into main` | merge --no-ff → push |

---

## FASE 10 — Formulario Equipo (complejo)

**Rama:** `feature/fase10-equipo-fx`

| # | Commit | Archivos |
|---|---|---|
| 10.1 | `feat(view/fx): add equipo.fxml CRUD form with player assignment` | `equipo.fxml` |
| 10.2 | `feat(view/fx): add EquipoController with EquipoService JugadorService TecnicoService` | `EquipoController.java` |
| 10.3 | `docs(fase10): add fase10-equipo-fx phase documentation` | `documentacion/fases/fase10-equipo-fx.md` |
| — | `merge: integrate feature/fase10-equipo-fx into main` | merge --no-ff → push |

---

## FASE 11 — Formulario Partido (complejo)

**Rama:** `feature/fase11-partido-fx`

| # | Commit | Archivos |
|---|---|---|
| 11.1 | `feat(view/fx): add partido.fxml scheduling and result form` | `partido.fxml` |
| 11.2 | `feat(view/fx): add PartidoController with PartidoService EquipoService ArbitroService` | `PartidoController.java` |
| 11.3 | `docs(fase11): add fase11-partido-fx phase documentation` | `documentacion/fases/fase11-partido-fx.md` |
| — | `merge: integrate feature/fase11-partido-fx into main` | merge --no-ff → push |

---

## Estructura de archivos FINAL

```
LigaUPC_/
├── .gitignore                                        <- FASE 1
├── pom.xml                                           <- FASES 1 y 2
├── Reglas de GitFlow.md                              <- Este documento
└── src/main/
    ├── java/ligaupc/
    │   ├── MainApp.java                              <- FASE 4
    │   ├── LigaUPC.java                              <- FASE 4 (retirado)
    │   ├── model/                                    <- SIN CAMBIOS (8 clases)
    │   ├── dao/                                      <- SIN CAMBIOS (6 clases)
    │   ├── service/                                  <- SIN CAMBIOS (8 clases)
    │   └── view/
    │       ├── console/                              <- FASE 3 (6 clases movidas)
    │       └── fx/                                   <- FASES 5-11 (7 controllers)
    │           ├── MainController.java
    │           ├── JugadorController.java
    │           ├── TecnicoController.java
    │           ├── ArbitroController.java
    │           ├── EstadisticaController.java
    │           ├── EquipoController.java
    │           └── PartidoController.java
    └── resources/ligaupc/view/fx/                   <- FASE 2
        ├── main.fxml
        ├── jugador.fxml
        ├── tecnico.fxml
        ├── arbitro.fxml
        ├── estadistica.fxml
        ├── equipo.fxml
        └── partido.fxml

documentacion/fases/                                  <- Una por fase (11 archivos)
    fase1-github-setup.md
    fase2-javafx-config.md
    fase3-refactor-console.md
    fase4-mainapp.md
    fase5-main-navigation.md
    fase6-jugador-fx.md
    fase7-tecnico-fx.md
    fase8-arbitro-fx.md
    fase9-estadistica-fx.md
    fase10-equipo-fx.md
    fase11-partido-fx.md
```

---

## Patrones GRASP/SOLID en la capa JavaFX

| Patron | Aplicacion |
|---|---|
| GRASP Controller | `MainApp` orquesta el arranque; `MainController` gestiona la navegacion |
| GRASP High Cohesion | Cada `*Controller` gestiona solo una entidad |
| GRASP Low Coupling | Controllers acceden a Services, nunca a DAOs directamente |
| SOLID SRP | Si cambia el formulario de Jugadores, solo cambia `JugadorController` |
| SOLID OCP | Agregar un modulo = crear FXML + Controller sin tocar los existentes |

---

## Resumen del plan

| Elemento | Cantidad |
|---|---|
| Fases | 11 |
| Ramas git (nunca se borran) | 11 |
| Commits atomicos | ~33 |
| Clases nuevas | 8 (MainApp + 7 Controllers) |
| FXML nuevos | 7 |
| Docs de fase | 11 |
| Clases sin tocar | 22 (model + dao + service) |
