# Examen Final — Desarrollo de Software II (SS462)
## Sistema de Gestión de Liga de Fútbol Universitaria

**Universidad Popular del Cesar**
**Programa:** Ingeniería de Sistemas
**Docente:** Ing. Alfredo David Bautista Romero
**Modalidad:** Individual o parejas
**Tiempo:** Según lo indicado en clase

---

## 1. Contexto del Problema

La Universidad Popular del Cesar organiza una **Liga de Fútbol Universitaria** en la que
participan equipos conformados por estudiantes. La dirección deportiva necesita un sistema
de escritorio que permita gestionar de forma completa el torneo: inscribir participantes,
programar partidos, registrar resultados y generar automáticamente la tabla de posiciones.

Actualmente, todo se gestiona en papel. El sistema que usted construya debe digitalizar y
automatizar ese proceso.

---

## 2. Descripción del Sistema

El sistema se llama **LigaUPC** y debe cubrir los siguientes módulos:

### 2.1 Módulo Jugador
Registrar, buscar, actualizar y eliminar jugadores de la liga.
- Cada jugador tiene: nombre, identificación (clave única), contacto, posición en cancha y número de camiseta.
- Posiciones válidas: `Portero`, `Defensa Central`, `Lateral Derecho`, `Lateral Izquierdo`, `Mediocampista`, `Extremo Derecho`, `Extremo Izquierdo`, `Delantero Centro`.
- El número de camiseta debe ser mayor a cero.
- No pueden existir dos jugadores con la misma identificación.

### 2.2 Módulo Técnico
Registrar, buscar, actualizar y eliminar técnicos (entrenadores).
- Cada técnico tiene: nombre, identificación (clave única), contacto y especialidad.
- Especialidades válidas: `Director Técnico`, `Preparador Físico`, `Asistente Técnico`, `Analista Táctico`, `Portero`, `Psicólogo Deportivo`, `Médico Deportivo`.
- No pueden existir dos técnicos con la misma identificación.

### 2.3 Módulo Árbitro
Registrar, buscar, actualizar y eliminar árbitros.
- Cada árbitro tiene: nombre, identificación (clave única), contacto y categoría de certificación.
- Categorías válidas: `FIFA`, `CONMEBOL`, `Nacional`, `Regional`, `Local`.
- No pueden existir dos árbitros con la misma identificación.

### 2.4 Módulo Equipo
Registrar y gestionar los equipos participantes.
- Cada equipo tiene: nombre (clave única), sede, técnico asignado y lista de jugadores.
- Operaciones requeridas:
  - Registrar un nuevo equipo con nombre y sede.
  - Buscar, actualizar sede y eliminar equipo.
  - **Asignar técnico** a un equipo (validar que el técnico exista).
  - **Agregar jugador** al equipo (validar que el jugador exista y no esté ya en ese equipo).
  - **Quitar jugador** del equipo (validar que el jugador pertenezca al equipo).

### 2.5 Módulo Partido
Gestionar los partidos del torneo en dos flujos:

**Flujo 1 — Programar partido:**
- Datos: ID entero, fecha, equipo local, equipo visitante y árbitro.
- El estado inicial es `"Programado"`.
- Validar que los equipos y el árbitro existan.
- Un equipo no puede jugar contra sí mismo.

**Flujo 2 — Registrar resultado:**
- Recibe: ID del partido, goles del local, goles del visitante.
- Solo se puede registrar resultado a partidos en estado `"Programado"`.
- Al registrar el resultado:
  - El partido pasa a estado `"Finalizado"`.
  - Se actualizan automáticamente las **estadísticas de ambos equipos**.
- Los goles no pueden ser negativos.

### 2.6 Módulo Estadísticas (Tabla de Posiciones)
Vista de solo lectura generada automáticamente.
- Columnas: Pos, Equipo, PJ, PG, PE, PP, GF, GC, DG (GF−GC), Pts.
- Ordenamiento: primero por Puntos (desc), luego por Diferencia de Goles (desc), luego por Goles a Favor (desc).
- **Puntuación estándar:** victoria = 3 pts, empate = 1 pt, derrota = 0 pts.

---

## 3. Arquitectura Obligatoria

El sistema debe implementarse en **cuatro capas claramente separadas**:

```
ligaupc/
├── model/          ← Entidades del dominio (clases de datos)
├── dao/            ← Acceso a datos (persistencia en archivos .txt)
├── service/        ← Lógica de negocio y validaciones
└── view/           ← Interfaz de usuario (consola o JavaFX)
```

### 3.1 Capa Model

Debe existir una jerarquía de herencia para las personas del sistema:

```
Persona (abstracta)
    ├── Jugador
    ├── Tecnico
    └── Arbitro
```

- `Persona` tiene los atributos comunes: `nombre`, `identificacion`, `contacto`.
- `Persona` define el método abstracto `obtenerPerfil()` para aplicar **polimorfismo**.
- Cada subclase implementa `obtenerPerfil()` devolviendo una descripción específica de su rol.

Entidades independientes (no heredan de Persona):
- `Equipo`: nombre, sede, `Tecnico` (objeto), `List<Jugador>`.
- `Partido`: idPartido, fecha (`java.util.Date`), equipoLocal, equipoVisitante, golesLocal, golesVisitante, `Arbitro` (objeto), estado.
- `Estadistica`: `Equipo` (objeto), PJ, PG, PE, PP, GF, GC, Pts.

### 3.2 Capa DAO

Cada entidad tiene su propio DAO. Todos deben implementar la siguiente interfaz genérica:

```java
public interface ICRUD<T> {
    boolean crear(T entidad);
    T leerPorId(String id);
    List<T> listarTodos();
    boolean actualizar(T entidad);
    boolean eliminar(String id);
}
```

**Persistencia:** los datos se guardan en archivos de texto plano (`.txt`), uno por entidad,
usando `;` como separador de campos. El DAO es responsable de serializar y deserializar
los objetos hacia y desde el archivo.

Archivos esperados en tiempo de ejecución:
`jugadores.txt`, `tecnicos.txt`, `arbitros.txt`, `equipos.txt`, `partidos.txt`, `estadisticas.txt`.

### 3.3 Capa Service

Cada módulo tiene su propio Service que:
- Recibe el DAO vía constructor (inyección de dependencias).
- Contiene **todas las validaciones de negocio** (nunca la vista valida datos de negocio).
- Lanza `IllegalArgumentException` con un mensaje descriptivo cuando una regla es violada.
- Expone métodos: `registrar*`, `buscar*`, `listar*`, `actualizar*`, `eliminar*`.

**`EquipoService`** colabora con `JugadorService` y `TecnicoService`.

**`PartidoService`** colabora con `EquipoService`, `ArbitroService` y usa una interfaz `ReglasPuntuacion`:

```java
public interface ReglasPuntuacion {
    int calcularPuntos(int golesAFavor, int golesEnContra);
}
```

Implementación estándar (`ReglasPuntuacionEstandar`):
- Victoria: 3 puntos
- Empate: 1 punto
- Derrota: 0 puntos

### 3.4 Capa View

El estudiante debe elegir **UNA** de las siguientes opciones de vista:

**Opción A — Consola (mínimo):**
- Menú principal con opciones numeradas.
- Cada módulo tiene su propio submenú dentro de una clase `*View`.
- Las Views reciben los Services por constructor (no los instancian internamente).

**Opción B — JavaFX con FXML (máximo puntaje):**
- Ventana principal con barra de navegación lateral y área de contenido dinámico.
- Cada módulo tiene su propio archivo `.fxml` y su `*Controller`.
- Los Controllers obtienen los Services mediante un patrón **ServiceLocator** (clase `MainApp`).
- Los datos de los formularios se muestran y editan en `TableView` con cell factories.

---

## 4. Patrones de Diseño Obligatorios

### 4.1 Patrones GRASP

| Patrón | Aplicación requerida |
|--------|---------------------|
| **Expert** | Cada clase gestiona su propia información. Ejemplo: `Equipo` sabe quitar/agregar jugadores de su propia lista. |
| **Creator** | El Service crea los objetos del dominio; el DAO los persiste. `LigaUPC` / `MainApp` crea los Services. |
| **Controller** | `PartidoService` orquesta el flujo más complejo: registrar resultado → actualizar estadísticas de dos equipos. |
| **Low Coupling** | Los Controllers (JavaFX) o las Views (consola) no se conocen entre sí. Solo conocen al Service que les corresponde. |
| **High Cohesion** | Cada clase tiene una única responsabilidad claramente definida. |
| **Polymorphism** | `obtenerPerfil()` en `Persona`; `calcularPuntos()` en `ReglasPuntuacion`. |

### 4.2 Principios SOLID

| Principio | Aplicación requerida |
|-----------|---------------------|
| **SRP** | Una clase, una razón de cambio. El DAO solo cambia si cambia el mecanismo de persistencia. El Service solo cambia si cambian las reglas de negocio. |
| **OCP** | `ICRUD<T>` permite reemplazar el DAO sin modificar el Service. `ReglasPuntuacion` permite cambiar las reglas de puntuación sin tocar `PartidoService`. |
| **LSP** | Las subclases de `Persona` pueden sustituirse entre sí en cualquier lugar donde se use `Persona`. |
| **ISP** | La interfaz `ICRUD<T>` es pequeña y coherente; no agrupa responsabilidades ajenas. |
| **DIP** | Los Services dependen de `ICRUD<T>` (abstracción), no de `JugadorDAO` (implementación concreta). |

---

## 5. Flujo Completo de Uso del Sistema

El orden correcto de operación es:

```
1. Registrar técnicos
2. Registrar jugadores
3. Registrar árbitros
4. Registrar equipos
5. Asignar técnico a cada equipo
6. Agregar jugadores a cada equipo
7. Programar partidos (equipos + árbitro + fecha)
8. Registrar resultados de los partidos
9. Consultar tabla de posiciones (generada automáticamente)
```

---

## 6. Diagrama de Clases — Modelo de Dominio

```
                    ┌──────────────┐
                    │  «abstract»  │
                    │   Persona    │
                    │─────────────│
                    │ nombre       │
                    │ identificacion│
                    │ contacto     │
                    │─────────────│
                    │+obtenerPerfil()│ ← abstracto
                    └──────┬───────┘
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
   ┌────────────┐  ┌────────────┐  ┌────────────┐
   │  Jugador   │  │  Tecnico   │  │  Arbitro   │
   │────────────│  │────────────│  │────────────│
   │ posicion   │  │especialidad│  │categoriaCert│
   │numeroCamiseta│ └────────────┘  └────────────┘
   └────────────┘

   ┌────────────────────────────────────┐
   │               Equipo               │
   │────────────────────────────────────│
   │ nombre (PK)                        │
   │ sede                               │
   │ tecnico: Tecnico                   │
   │ jugadores: List<Jugador>           │
   │────────────────────────────────────│
   │ +agregarJugador(Jugador)           │
   │ +quitarJugador(String): boolean    │
   └────────────────────────────────────┘

   ┌────────────────────────────────────┐
   │              Partido               │
   │────────────────────────────────────│
   │ idPartido (PK)                     │
   │ fecha: Date                        │
   │ equipoLocal: Equipo                │
   │ equipoVisitante: Equipo            │
   │ golesLocal: int                    │
   │ golesVisitante: int                │
   │ arbitro: Arbitro                   │
   │ estado: String                     │
   └────────────────────────────────────┘

   ┌────────────────────────────────────┐
   │            Estadistica             │
   │────────────────────────────────────│
   │ equipo: Equipo                     │
   │ partidosJugados: int               │
   │ partidosGanados: int               │
   │ partidosEmpatados: int             │
   │ partidosPerdidos: int              │
   │ golesAFavor: int                   │
   │ golesEnContra: int                 │
   │ puntos: int                        │
   └────────────────────────────────────┘
```

---

## 7. Diagrama de Arquitectura por Capas

```
┌─────────────────────────────────────────────────┐
│                  CAPA VIEW                       │
│  JugadorView / JugadorController                │
│  TecnicoView / TecnicoController                │
│  ArbitroView / ArbitroController                │
│  EquipoView  / EquipoController                 │
│  PartidoView / PartidoController                │
│  EstadisticaView / EstadisticaController        │
└────────────────────┬────────────────────────────┘
                     │ usa
┌────────────────────▼────────────────────────────┐
│                 CAPA SERVICE                     │
│  JugadorService    TecnicoService               │
│  ArbitroService    EquipoService                │
│  PartidoService    EstadisticaService           │
│  ReglasPuntuacion  ReglasPuntuacionEstandar     │
└────────────────────┬────────────────────────────┘
                     │ usa (via ICRUD<T>)
┌────────────────────▼────────────────────────────┐
│                  CAPA DAO                        │
│  JugadorDAO   TecnicoDAO   ArbitroDAO           │
│  EquipoDAO    PartidoDAO   EstadisticaDAO       │
│          implementan ICRUD<T>                   │
└────────────────────┬────────────────────────────┘
                     │ lee / escribe
┌────────────────────▼────────────────────────────┐
│              PERSISTENCIA (.txt)                 │
│  jugadores.txt    tecnicos.txt   arbitros.txt   │
│  equipos.txt      partidos.txt   estadisticas.txt│
└─────────────────────────────────────────────────┘
```

---

## 8. Requisitos Técnicos del Entorno

| Elemento | Versión |
|----------|---------|
| Lenguaje | Java 17 LTS |
| IDE | Apache NetBeans 21 (o superior) |
| Build tool | Apache Maven 3.x |
| Interfaz (Opción B) | JavaFX 21.0.2 |
| Plugin FXML (Opción B) | `javafx-maven-plugin 0.0.8` |
| Control de versiones | Git + GitHub |

**`pom.xml` mínimo requerido (Opción B):**
```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.2</version>
    </dependency>
</dependencies>
```

> **Nota importante (JavaFX + NetBeans):** Si al ejecutar desde NetBeans aparece el error
> *"el método principal debe devolver un tipo void"*, cree una clase `Launcher.java` que
> **NO** extienda `Application` y que llame a `MainApp.main(args)`. Configúrela como
> clase principal en las propiedades del proyecto.

---

## 9. Control de Versiones con Git (Obligatorio)

El proyecto debe estar publicado en **GitHub** con la siguiente estructura de ramas:

### 9.1 Nomenclatura de ramas
```
main               ← rama principal, solo recibe merges
feature/<nombre>   ← nuevas funcionalidades
fix/<nombre>       ← corrección de errores
refactor/<nombre>  ← reestructuración sin cambiar funcionalidad
docs/<nombre>      ← documentación
```

### 9.2 Reglas obligatorias
1. **Nunca hacer commit directo a `main`.**
2. Cada módulo o tarea significativa se desarrolla en su propia rama.
3. Al finalizar, se hace merge a `main` con `--no-ff`:
   ```
   git merge --no-ff feature/mi-rama
   ```
4. **No eliminar ramas** después del merge — deben permanecer visibles en el historial.
5. Los mensajes de commit siguen **Conventional Commits**:
   ```
   feat(model): add Jugador class with posicion and numeroCamiseta
   feat(dao): add JugadorDAO with file persistence
   feat(service): add JugadorService with CRUD validations
   feat(view): add JugadorView console menu
   fix(service): prevent duplicate identificacion in JugadorService
   refactor(dao): extract jugadorALinea helper method
   docs: add LigaUPC architecture documentation
   ```
6. Verificar con `mvn compile` (BUILD SUCCESS) antes de cada commit.

### 9.3 Historial mínimo esperado
El `git log` debe mostrar al menos un commit por capa por módulo.
Ejemplo para el módulo Jugador:
```
feat(model): add Jugador class
feat(dao): add JugadorDAO
feat(service): add JugadorService
feat(view): add JugadorView
merge: integrate feature/jugador into main
```

---

## 10. Entregables

| # | Entregable | Descripción |
|---|-----------|-------------|
| 1 | **Repositorio GitHub** | URL del repositorio público con todo el código fuente |
| 2 | **Código fuente compilable** | `mvn compile` debe ejecutarse sin errores desde la raíz del proyecto |
| 3 | **Sistema funcional** | El sistema debe ejecutarse con `mvn javafx:run` (Opción B) o `mvn exec:java` (Opción A) |
| 4 | **Historial Git** | Mínimo 3 commits por módulo en ramas separadas, mergeadas a `main` con `--no-ff` |
| 5 | **Documentación** | Carpeta `documentacion/` con al menos un `.md` que explique la arquitectura del sistema |

---

## 11. Rúbrica de Evaluación

### 11.1 Modelo de Dominio — 20 puntos

| Criterio | Pts |
|----------|-----|
| Jerarquía `Persona → Jugador / Tecnico / Arbitro` con herencia correcta | 5 |
| Método abstracto `obtenerPerfil()` implementado con polimorfismo en las 3 subclases | 5 |
| `Equipo` con lista de jugadores y métodos `agregarJugador` / `quitarJugador` | 5 |
| `Partido` con todos los campos requeridos y `Estadistica` con los contadores correctos | 5 |

### 11.2 Capa DAO — 15 puntos

| Criterio | Pts |
|----------|-----|
| Interfaz `ICRUD<T>` genérica implementada por todos los DAOs | 5 |
| Persistencia real en archivos `.txt` con separador `;` | 5 |
| Serialización y deserialización correctas (el sistema recarga datos al reiniciar) | 5 |

### 11.3 Capa Service — 25 puntos

| Criterio | Pts |
|----------|-----|
| Inyección de dependencias vía constructor (Service recibe `ICRUD<T>`, no `DAO` concreto) | 5 |
| Validaciones de negocio en el Service (no en la vista) con `IllegalArgumentException` descriptivo | 8 |
| `EquipoService` gestiona correctamente la relación equipo-jugador y equipo-técnico | 6 |
| `PartidoService` registra resultado y actualiza estadísticas de ambos equipos automáticamente | 6 |

### 11.4 Interfaz de Usuario — 20 puntos

| Criterio | Opción A (Consola) | Opción B (JavaFX) | Pts |
|----------|--------------------|-------------------|-----|
| Módulos de CRUD simples (Jugador, Técnico, Árbitro) | Menús funcionales | TableView + formulario + botones CRUD | 8 |
| Módulo Equipo (asignar técnico + gestionar jugadores) | Submenú funcional | TableView dual (equipo + jugadores), ComboBox<Tecnico> | 6 |
| Módulo Partido (programar + registrar resultado) | Flujo de dos pasos | Secciones separadas, DatePicker, ComboBox de equipos y árbitro | 6 |

### 11.5 Patrones GRASP / SOLID — 10 puntos

| Criterio | Pts |
|----------|-----|
| Evidencia de mínimo 3 patrones GRASP identificados y aplicados correctamente | 5 |
| Evidencia de mínimo SRP, OCP y DIP aplicados correctamente | 5 |

### 11.6 Control de Versiones Git — 10 puntos

| Criterio | Pts |
|----------|-----|
| Repositorio público en GitHub con código completo | 2 |
| Ramas por módulo con nombres correctos (`feature/`, `fix/`, etc.) | 3 |
| Merges con `--no-ff` y ramas visibles en historial | 3 |
| Mensajes de commit siguen Conventional Commits | 2 |

**Total: 100 puntos**

---

## 12. Casos de Prueba Sugeridos

Para verificar que el sistema funciona correctamente, ejecute el siguiente flujo:

```
1. Registrar técnico:   "Carlos Perez", ID "98765", contacto "300-111", especialidad "Director Técnico"
2. Registrar jugadores:
   - "Luis Gomez",  ID "11111", contacto "301-001", posición "Portero",       camiseta 1
   - "Ana Torres",  ID "22222", contacto "301-002", posición "Delantero Centro", camiseta 9
   - "Juan Rios",   ID "33333", contacto "301-003", posición "Defensa Central",  camiseta 4
   - "Sara Niño",   ID "44444", contacto "301-004", posición "Mediocampista",    camiseta 8
3. Registrar árbitro:   "Pedro Luna", ID "77777", contacto "310-555", categoría "Nacional"
4. Registrar equipos:
   - Equipo "Ingenieria", sede "Campus Norte"
   - Equipo "Sistemas",   sede "Campus Sur"
5. Asignar técnico "98765" al equipo "Ingenieria"
6. Agregar jugadores "11111" y "22222" al equipo "Ingenieria"
7. Agregar jugadores "33333" y "44444" al equipo "Sistemas"
8. Programar partido ID=1, fecha=hoy, local="Ingenieria", visitante="Sistemas", árbitro="77777"
9. Registrar resultado: partido 1, goles local=2, goles visitante=1
10. Verificar tabla de posiciones:
    - "Ingenieria": PJ=1, PG=1, PE=0, PP=0, GF=2, GC=1, DG=+1, Pts=3
    - "Sistemas":   PJ=1, PG=0, PE=0, PP=1, GF=1, GC=2, DG=-1, Pts=0
```

---

## 13. Errores Comunes y Cómo Evitarlos

| Error | Causa | Solución |
|-------|-------|----------|
| La lógica de validación está en la vista | SRP violado | Mover TODA validación de negocio al Service |
| El DAO hace `new Service()` internamente | Acoplamiento alto | El DAO solo lee/escribe archivos — nunca conoce al Service |
| `NullPointerException` al cargar la tabla | Objeto anidado nulo en la celda | Usar lambdas en `setCellValueFactory` con verificación `!= null` |
| `catch (A | B)` donde B hereda de A | `NumberFormatException` extiende `IllegalArgumentException` | Usar solo `catch (IllegalArgumentException e)` |
| El sistema no recuerda los datos al reiniciar | El DAO no persiste en archivo | Verificar que `BufferedWriter` abre el archivo en modo append o reescritura completa |
| JavaFX da error de módulos en NetBeans | `MainApp extends Application` sin module-path | Crear clase `Launcher` simple que llame `MainApp.main(args)` |
| `ComboBox` muestra nombre de clase del objeto | Sin `StringConverter` | Implementar `StringConverter<T>` con el texto legible deseado |

---

## 14. Preguntas de Sustentación

Prepare respuestas para las siguientes preguntas:

1. ¿Por qué `Persona` es abstracta y no una clase concreta?
2. Explique la diferencia entre **GRASP Expert** y **SOLID SRP** y dé un ejemplo de cada uno en su proyecto.
3. ¿Por qué el Service recibe `ICRUD<T>` en el constructor en lugar de instanciar `new JugadorDAO()` internamente? ¿Qué principio SOLID aplica?
4. ¿Qué pasaría si las reglas de puntuación cambian de 3-1-0 a 2-1-0? ¿Cuántos archivos tendría que modificar en su proyecto y por qué?
5. ¿Por qué `PartidoService` es el GRASP Controller del sistema? Trace el flujo completo desde que el usuario hace clic en "Registrar Resultado" hasta que la tabla de posiciones se actualiza.
6. ¿Por qué se usa `--no-ff` en los merges de Git? ¿Qué diferencia hay con un merge normal?
7. En JavaFX, ¿por qué no se puede usar `PropertyValueFactory` para mostrar `equipo.getNombre()` en una columna de la tabla? ¿Cuál es la solución?

---

*Documento generado para el curso SS462 — Desarrollo de Software II*
*Universidad Popular del Cesar — Ingeniería de Sistemas*
