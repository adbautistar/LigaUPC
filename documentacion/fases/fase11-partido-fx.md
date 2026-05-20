# Fase 11 — Formulario Partido (JavaFX)

## Objetivo

Implementar el formulario completo de Partidos en JavaFX.
Es el modulo mas complejo del sistema: gestiona dos flujos de trabajo distintos
y usa 3 services. El flujo de "registrar resultado" coordina la actualizacion
automatica de estadisticas de ambos equipos (GRASP Controller).

## Rama

`feature/fase11-partido-fx`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 11.1 | `feat(view/fx): add partido.fxml scheduling and result form` | Layout con 3 secciones: programar, resultado, tabla |
| 11.2 | `feat(view/fx): add PartidoController with PartidoService EquipoService ArbitroService` | Controller con 3 services, 2 flujos y StringConverters para Equipo y Arbitro |
| 11.3 | `docs(fase11): add fase11-partido-fx phase documentation` | Este archivo |

## Archivos creados

| Archivo | Descripcion |
|---|---|
| `src/main/resources/ligaupc/view/fx/partido.fxml` | Layout con 3 secciones: programar, resultado, tabla |
| `src/main/java/ligaupc/view/fx/PartidoController.java` | Controller con 3 services y 5 operaciones |
| `documentacion/fases/fase11-partido-fx.md` | Este documento |

## Estructura del formulario (3 secciones)

```
Seccion 1: Programar partido
  [ID Partido*] [Fecha* DatePicker]
  [Equipo Local* ComboBox] [Equipo Visitante* ComboBox]
  [Arbitro* ComboBox]
  [Programar] [Buscar] [Eliminar] [Limpiar]

Seccion 2: Registrar resultado
  [ID Partido] [Goles Local] [Goles Visitante] [Registrar Resultado]

Seccion 3: Tabla
  | ID | Fecha | Local | Visitante | Resultado | Arbitro | Estado |
```

## Services usados y por que

| Service | Uso en este Controller |
|---|---|
| `PartidoService` | Programar, buscar, eliminar, registrar resultado (flujo principal) |
| `EquipoService` | Poblar los dos ComboBox de equipos (local y visitante) |
| `ArbitroService` | Poblar el ComboBox de arbitros |

## Flujo: Programar partido

1. Usuario ingresa ID, selecciona fecha en DatePicker, elige equipos y arbitro
2. `programar()` convierte `LocalDate` a `java.util.Date`:
   ```java
   Date fecha = Date.from(
       dateFecha.getValue()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
   );
   ```
3. Crea `new Partido(id, fecha, local, visitante, arbitro)`
4. Llama `partidoService.registrarPartido(partido)` → estado "Programado"

## Flujo: Registrar resultado (GRASP Controller)

1. Usuario ingresa el ID del partido y los goles de cada equipo
2. `registrarResultado()` llama `partidoService.registrarResultado(id, gl, gv)`
3. PartidoService (GRASP Controller) orquesta internamente:
   - Cambia el estado del partido a "Finalizado"
   - Actualiza estadisticas del equipo local (goles, puntos, G/E/P)
   - Actualiza estadisticas del equipo visitante
   El Controller solo delega — toda la logica vive en el Service.

## StringConverter para ComboBox de dominio

Equipo y Arbitro son objetos de dominio. Sin StringConverter JavaFX mostraria
el `toString()` del objeto (poco legible).

```java
// ComboBox<Equipo>: muestra solo el nombre
cmbEquipoLocal.setConverter(new StringConverter<Equipo>() {
    @Override
    public String toString(Equipo e) {
        return (e != null) ? e.getNombre() : "";
    }
    @Override
    public Equipo fromString(String s) { return null; }
});

// ComboBox<Arbitro>: muestra nombre + ID entre parentesis
cmbArbitro.setConverter(new StringConverter<Arbitro>() {
    @Override
    public String toString(Arbitro a) {
        return (a != null) ? a.getNombre() + " (" + a.getIdentificacion() + ")" : "";
    }
    @Override
    public Arbitro fromString(String s) { return null; }
});
```

## Lambdas para columnas con objetos anidados

Todos los campos de Partido son objetos anidados o necesitan formateo especial.
No se puede usar `PropertyValueFactory` — se usan lambdas con `SimpleStringProperty`.

```java
// Columna Resultado: "X - Y" para finalizados, "—" para programados
colGoles.setCellValueFactory(c -> {
    Partido p = c.getValue();
    if ("Finalizado".equals(p.getEstado())) {
        return new SimpleStringProperty(p.getGolesLocal() + " - " + p.getGolesVisitante());
    }
    return new SimpleStringProperty("—");
});

// Columna Equipo Local (objeto anidado)
colLocal.setCellValueFactory(c -> {
    Equipo e = c.getValue().getEquipoLocal();
    return new SimpleStringProperty(e != null ? e.getNombre() : "—");
});
```

## Patrones GRASP/SOLID aplicados

| Patron | Aplicacion |
|---|---|
| GRASP Controller | PartidoService orquesta el flujo mas complejo: resultado → estadisticas de 2 equipos |
| GRASP Low Coupling | PartidoController obtiene los 3 services via MainApp — cero acoplamiento directo |
| GRASP Expert | PartidoService conoce las reglas: partido ya finalizado, goles negativos, etc. |
| GRASP High Cohesion | PartidoController solo gestiona la UI de Partidos |
| SOLID SRP | Si cambia el calculo de estadisticas, solo cambia PartidoService |
| SOLID OCP | ReglasPuntuacion es intercambiable (inyectada en PartidoService) |

## Verificacion

- `mvn compile` -> BUILD SUCCESS
- Al hacer click en "Partidos" -> formulario con 3 secciones visible
- ComboBox Equipos muestra solo el nombre del equipo
- ComboBox Arbitro muestra "Nombre (ID)"
- Programar partido con fecha, equipos y arbitro -> aparece en tabla con estado "Programado"
- Registrar resultado -> estado cambia a "Finalizado", columna Resultado muestra "X - Y"
- Estadisticas se actualizan automaticamente al registrar resultado
