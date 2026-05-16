# PartidoDAO — Data Access Object de Partido

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/PartidoDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar partidos** usando el archivo `partidos.txt`. Es el DAO más complejo del sistema porque `Partido` referencia dos `Equipo` y un `Arbitro`, y cada `Equipo` a su vez tiene sus propias referencias.

---

## Patrón GRASP: **Expert**

`PartidoDAO` es el **experto** en persistencia de partidos. Sabe cómo convertir un `Partido` completo (con sus equipos y árbitro) en una línea de texto, y cómo reconstruirlo al leer.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`PartidoDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia de partidos. La lógica de negocio (calcular estadísticas tras un partido, notificar técnicos) pertenece al `PartidoService`.

### OCP — Open/Closed Principle
Implementa `ICRUD<Partido>`. Si se necesita otra forma de persistencia, se crea una nueva clase sin tocar el Service.

```
          ICRUD<Partido>        ← contrato fijo, nunca cambia
               ↑
    ┌──────────┴──────────┐
 PartidoDAO          PartidoDAOSQL
(archivos .txt)     (base de datos)
```

---

## Formato del archivo `partidos.txt`

```
idPartido;fecha;nombreEquipoLocal;nombreEquipoVisitante;golesLocal;golesVisitante;idArbitro;estado
```

**Ejemplo:**
```
1;15/03/2025;Ingeniería;Medicina;2;1;3001;Finalizado
2;22/03/2025;Derecho;Sistemas;0;0;3002;Programado
```

Todos los objetos referenciados (`Equipo`, `Arbitro`) se guardan solo por su identificador, no por su contenido completo.

---

## Concepto: Colaboración en cadena

Al reconstruir un partido desde el archivo se desencadena una cadena de consultas entre DAOs:

```
PartidoDAO.lineaAPartido()
    │
    ├── equipoDAO.leerPorId("Ingeniería")
    │       ├── tecnicoDAO.leerPorId("2001")   → Tecnico reconstruido
    │       ├── jugadorDAO.leerPorId("1001")   → Jugador reconstruido
    │       └── jugadorDAO.leerPorId("1002")   → Jugador reconstruido
    │
    ├── equipoDAO.leerPorId("Medicina")
    │       └── ...
    │
    └── arbitroDAO.leerPorId("3001")           → Arbitro reconstruido
```

Cada DAO sigue siendo experto de su propia entidad. `PartidoDAO` no sabe leer equipos ni árbitros — delega. Esto es **Low Coupling**: si mañana cambia cómo se persiste un `Equipo`, `PartidoDAO` no se ve afectado.

---

## Concepto: Serialización de `Date`

`Date` no puede guardarse directamente como texto legible. Se usa `SimpleDateFormat` para convertirla:

```java
private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

// Date → texto
FORMATO_FECHA.format(partido.getFecha())   // "15/03/2025"

// texto → Date
FORMATO_FECHA.parse("15/03/2025")          // objeto Date
```

El método privado `parsearFecha()` encapsula el manejo del `ParseException` — si el formato del archivo está corrupto, retorna la fecha actual en lugar de lanzar una excepción que detenga todo el sistema.

---

## Métodos implementados

### `crear(Partido)`
Abre el archivo en modo **append** y agrega la línea serializada. Las referencias se guardan solo como IDs o nombres.

### `listarTodos()`
Lee línea por línea y reconstruye cada `Partido` completo usando `lineaAPartido()`, que a su vez consulta a `EquipoDAO` y `ArbitroDAO`.

### `leerPorId(String)`
Busca por `idPartido` (int convertido a String para coincidir con la firma de `ICRUD`).

```java
String.valueOf(p.getIdPartido()).equals(idPartido)
```

### `actualizar(Partido)`
Patrón **carga → modifica → reescribe**: compara por `idPartido` (int), no por nombre.

### `eliminar(String)`
Mismo patrón, convirtiendo el ID de String a int para la comparación.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `partidoALinea(Partido)` | Serializa el partido guardando solo IDs de referencias |
| `lineaAPartido(String)` | Deserializa y reconstruye el partido completo |
| `parsearFecha(String)` | Convierte texto a `Date`, maneja errores de formato |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

---

## Diagrama de flujo: escritura vs lectura

```
ESCRITURA (crear / guardarTodos)
─────────────────────────────────────────────────────
Partido (con objetos completos)
    │
    ▼ partidoALinea()
"1;15/03/2025;Ingeniería;Medicina;2;1;3001;Finalizado"
    │
    ▼
partidos.txt


LECTURA (listarTodos / leerPorId)
─────────────────────────────────────────────────────
partidos.txt
    │  "1;15/03/2025;Ingeniería;Medicina;2;1;3001;Finalizado"
    ▼ lineaAPartido()
    ├── equipoDAO.leerPorId("Ingeniería")  → Equipo completo
    ├── equipoDAO.leerPorId("Medicina")    → Equipo completo
    └── arbitroDAO.leerPorId("3001")       → Arbitro completo
Partido completo reconstruido
```

---

## Comparación de complejidad entre los DAOs

| DAO | Referencias externas | Colabora con |
|---|---|---|
| `JugadorDAO` | Ninguna | — |
| `TecnicoDAO` | Ninguna | — |
| `ArbitroDAO` | Ninguna | — |
| `EquipoDAO` | Tecnico + List\<Jugador\> | TecnicoDAO, JugadorDAO |
| `PartidoDAO` | Equipo x2 + Arbitro | EquipoDAO, ArbitroDAO |

La complejidad crece con las relaciones del modelo, pero cada DAO sigue teniendo **una sola responsabilidad**.
