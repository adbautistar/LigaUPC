# EquipoDAO — Data Access Object de Equipo

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/EquipoDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar equipos** usando el archivo `equipos.txt`. Es más compleja que los DAOs anteriores porque `Equipo` contiene referencias a otros objetos (`Tecnico` y `List<Jugador>`), no solo datos primitivos.

---

## Patrón GRASP: **Expert**

`EquipoDAO` es el **experto** en persistencia de equipos. Sabe exactamente cómo convertir un `Equipo` (con su técnico y sus jugadores) en una línea de texto, y cómo reconstruirlo al leer.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`EquipoDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia de equipos. La lógica de negocio (qué jugadores puede tener un equipo, cómo se asigna un técnico) pertenece al `EquipoService`.

### OCP — Open/Closed Principle
Implementa `ICRUD<Equipo>`. Si se necesita otra forma de persistencia, se crea una nueva clase sin tocar el Service.

```
          ICRUD<Equipo>         ← contrato fijo, nunca cambia
               ↑
    ┌──────────┴──────────┐
 EquipoDAO           EquipoDAOSQL
(archivos .txt)     (base de datos)
```

---

## Concepto nuevo: Serialización por referencia

`Equipo` contiene un `Tecnico` y una lista de `Jugador`. No podemos guardar esos objetos completos dentro de `equipos.txt` — eso crearía **duplicación de datos**:

```
// MAL: datos duplicados, inconsistentes si el técnico cambia
Ingeniería;Sede Norte;Juan Martínez;2001;3201234567;4-4-2;...
```

Si el técnico cambia su contacto, habría que actualizar `equipos.txt` Y `tecnicos.txt`. Eso viola el SRP.

La solución correcta es guardar solo el **identificador** de cada referencia:

```
// BIEN: solo IDs, los objetos viven en sus propios archivos
Ingeniería;Sede Norte;2001;1001,1002,1003
```

Al leer, se reconstruyen los objetos consultando a sus respectivos DAOs.

---

## Formato del archivo `equipos.txt`

```
nombre;sede;idTecnico;idJugador1,idJugador2,idJugador3
```

**Ejemplo:**
```
Ingeniería;Sede Norte;2001;1001,1002,1003
Medicina;Sede Sur;2002;1004,1005
Derecho;Sede Centro;;
```

- El tercer campo (`idTecnico`) puede estar vacío si el equipo aún no tiene técnico asignado.
- El cuarto campo (`idJugadores`) puede estar vacío si el equipo no tiene jugadores aún.
- Los IDs de jugadores se separan con coma dentro del mismo campo.

---

## Concepto nuevo: Colaboración entre DAOs

`EquipoDAO` necesita ayuda para reconstruir los objetos referenciados:

```java
private final TecnicoDAO tecnicoDAO = new TecnicoDAO();
private final JugadorDAO jugadorDAO = new JugadorDAO();
```

Cuando se lee una línea, `EquipoDAO` delega:
- La reconstrucción del técnico → `tecnicoDAO.leerPorId(idTecnico)`
- La reconstrucción de cada jugador → `jugadorDAO.leerPorId(idJugador)`

Esto es **colaboración entre expertos**: cada DAO sigue siendo responsable de su propia entidad. `EquipoDAO` no sabe cómo leer un técnico del archivo — le delega esa tarea a quien sí sabe.

```
lineaAEquipo()
    │
    ├── tecnicoDAO.leerPorId("2001")  → devuelve Tecnico reconstruido
    │
    └── jugadorDAO.leerPorId("1001")  → devuelve Jugador reconstruido
        jugadorDAO.leerPorId("1002")
        jugadorDAO.leerPorId("1003")
```

---

## Métodos implementados

### `crear(Equipo)`
Abre el archivo en modo **append** y agrega la línea serializada. Los jugadores y el técnico se guardan solo por sus IDs.

### `listarTodos()`
Lee línea por línea y reconstruye cada `Equipo` completo (con técnico y jugadores) usando `lineaAEquipo()`.

### `leerPorId(String)`
Busca por **nombre del equipo** (no existe un ID numérico en `Equipo`). La comparación es insensible a mayúsculas (`equalsIgnoreCase`).

### `actualizar(Equipo)`
Patrón **carga → modifica → reescribe**: carga todos los equipos, reemplaza el que coincide por nombre, reescribe el archivo.

### `eliminar(String)`
Mismo patrón, pero elimina usando `removeIf()` con el nombre como criterio.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `equipoALinea(Equipo)` | Serializa el equipo guardando solo IDs de referencias |
| `lineaAEquipo(String)` | Deserializa y reconstruye el equipo completo con sus referencias |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

### Detalle de `equipoALinea()`

```java
// Técnico: guarda solo su ID, o cadena vacía si no tiene
String idTecnico = (equipo.getTecnico() != null)
        ? equipo.getTecnico().getIdentificacion()
        : "";

// Jugadores: une todos los IDs con coma
StringBuilder idsJugadores = new StringBuilder();
for (Jugador j : equipo.getJugadores()) {
    if (idsJugadores.length() > 0) idsJugadores.append(",");
    idsJugadores.append(j.getIdentificacion());
}
```

### Detalle de `lineaAEquipo()`

```java
String[] partes = linea.split(SEPARADOR, -1);  // -1 conserva campos vacíos al final
```

El `-1` en `split` es importante: sin él, Java descarta los campos vacíos al final de la línea, lo que causaría un `ArrayIndexOutOfBoundsException` cuando el equipo no tiene jugadores.

---

## Diagrama de flujo: lectura completa

```
equipos.txt
    │  "Ingeniería;Sede Norte;2001;1001,1002"
    ▼ lineaAEquipo()
Equipo vacío creado
    │
    ├── tecnicoDAO.leerPorId("2001")
    │       └── lee tecnicos.txt → Tecnico("Juan Martínez", ...)
    │
    ├── jugadorDAO.leerPorId("1001")
    │       └── lee jugadores.txt → Jugador("Carlos Pérez", ...)
    │
    └── jugadorDAO.leerPorId("1002")
            └── lee jugadores.txt → Jugador("María López", ...)

Equipo completo con Tecnico y List<Jugador> reconstruidos
```
