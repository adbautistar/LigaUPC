# PartidoView — Vista de consola de Partido

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/PartidoView.java`

---

## ¿Qué es?

Gestiona la interacción con el usuario para todo lo relacionado con partidos. Su operación más importante es **registrar el resultado**, que desencadena la actualización automática de estadísticas.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo maneja entrada/salida de partidos |
| **SOLID SRP** | Cambia únicamente si cambia la interfaz de usuario |

---

## Por qué recibe tres Services

```java
public PartidoView(PartidoService partidoService,
                   EquipoService equipoService,
                   ArbitroService arbitroService,
                   Scanner scanner)
```

Para registrar un partido, la View necesita construir un objeto `Partido` con referencias reales a `Equipo` y `Arbitro`. El usuario ingresa nombres e identificaciones — la View usa `EquipoService` y `ArbitroService` para obtener los objetos antes de construir el `Partido`:

```java
Equipo local     = equipoService.buscarEquipo(nombreLocal);
Equipo visitante = equipoService.buscarEquipo(nombreVisitante);
Arbitro arbitro  = arbitroService.buscarArbitro(idArbitro);
Partido partido  = new Partido(id, fecha, local, visitante, arbitro);
partidoService.registrarPartido(partido);
```

Si alguno no existe, el Service lanza `IllegalArgumentException` y la View lo captura antes de llegar al `PartidoService`.

---

## `leerFecha()`: validación de formato en la View

```java
private Date leerFecha() {
    while (true) {
        try {
            FORMATO_FECHA.setLenient(false);  // rechaza fechas como 32/01/2025
            return FORMATO_FECHA.parse(scanner.nextLine().trim());
        } catch (ParseException e) {
            System.out.print("Formato inválido. Use dd/MM/yyyy: ");
        }
    }
}
```

`setLenient(false)` es importante: sin él, `SimpleDateFormat` acepta fechas inválidas como `32/01/2025` ajustándolas automáticamente. Con `false`, las rechaza y obliga al usuario a corregirlas.

Esta es **validación de formato** (responsabilidad de la View), distinta de la **validación de negocio** (responsabilidad del Service).

---

## `registrarResultado()`: la operación más importante

```java
private void registrarResultado() {
    int id = leerEntero();
    int golesLocal = leerEntero();
    int golesVisitante = leerEntero();

    try {
        partidoService.registrarResultado(id, golesLocal, golesVisitante);
        System.out.println("Resultado registrado. Estadísticas actualizadas.");
    } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
    }
}
```

Tres números. Un método. Pero detrás, `PartidoService` actualiza el partido y las estadísticas de dos equipos. La View no sabe nada de eso — solo recibe el éxito o el error.

---

## Flujo del menú

```
mostrarMenu()
    ├── 1 → programarPartido()    → buscar equipo/árbitro → construir Partido → service
    ├── 2 → listarPartidos()      → mostrar con formato tabular
    ├── 3 → buscarPartido()       → buscar por ID → mostrar detalle
    ├── 4 → registrarResultado()  → ID + goles → service.registrarResultado()
    ├── 5 → eliminarPartido()     → ID → service.eliminarPartido()
    └── 0 → volver
```

---

## Relación con otras clases

```
PartidoView  →  PartidoService   (operaciones de partido y resultado)
             →  EquipoService    (obtener objetos Equipo para construir Partido)
             →  ArbitroService   (obtener objeto Arbitro para construir Partido)
             →  Scanner (compartido)
```
