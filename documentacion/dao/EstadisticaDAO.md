# EstadisticaDAO — Data Access Object de Estadistica

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/EstadisticaDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar estadísticas** usando el archivo `estadisticas.txt`. Mantiene el acumulado de resultados de cada equipo a lo largo de la liga (puntos, goles, partidos jugados, etc.).

---

## Patrón GRASP: **Expert**

`EstadisticaDAO` es el **experto** en persistencia de estadísticas. Sabe cómo convertir un objeto `Estadistica` en una línea de texto con todos sus campos numéricos, y cómo reconstruirlo al leer. Colabora con `EquipoDAO` para recuperar la referencia al equipo.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`EstadisticaDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia. La lógica de **cómo se actualizan las estadísticas** tras un partido (sumar puntos, goles, etc.) pertenece al `EstadisticaService` y a `ReglasPuntuacion` — no a este DAO.

### OCP — Open/Closed Principle
Implementa `ICRUD<Estadistica>`. Si se necesita persistencia en base de datos, se crea `EstadisticaDAOSQL` sin tocar el Service.

---

## Formato del archivo `estadisticas.txt`

```
nombreEquipo;partidosJugados;partidosGanados;partidosEmpatados;partidosPerdidos;golesAFavor;golesEnContra;puntos
```

**Ejemplo:**
```
Ingeniería;5;3;1;1;8;4;10
Medicina;5;2;2;1;6;5;8
Derecho;4;1;1;2;3;6;4
```

El nombre del equipo actúa como identificador único, igual que en `EquipoDAO`.

---

## Métodos implementados

### `crear(Estadistica)`
Agrega una nueva fila al archivo. Normalmente se llama una sola vez por equipo, al inicio de la liga, con todos los contadores en cero.

### `listarTodos()`
Lee el archivo y reconstruye cada `Estadistica` con su referencia a `Equipo`. Este método es el más usado: la tabla de posiciones de la liga se construye llamando `listarTodos()` y ordenando por puntos.

### `leerPorId(String)`
Busca por nombre del equipo. Se usa cuando se necesita actualizar la estadística de un equipo específico tras registrar un partido.

### `actualizar(Estadistica)`
El método más importante en el flujo del sistema. Después de cada partido, `EstadisticaService` recalcula los puntos y llama a `actualizar()` para los dos equipos involucrados. Sigue el patrón **carga → modifica → reescribe**.

### `eliminar(String)`
Elimina la estadística de un equipo por nombre. Se usa si un equipo es descalificado o retirado de la liga.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `estadisticaALinea(Estadistica)` | Serializa todos los campos numéricos + nombre del equipo |
| `lineaAEstadistica(String)` | Deserializa y reconstruye la estadística con su equipo |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

### Detalle de `lineaAEstadistica()`

```java
Estadistica estadistica = new Estadistica();
estadistica.setEquipo(equipoDAO.leerPorId(partes[0]));   // referencia reconstruida
estadistica.setPartidosJugados(Integer.parseInt(partes[1]));
estadistica.setPartidosGanados(Integer.parseInt(partes[2]));
// ... y así con todos los campos
```

Todos los campos numéricos requieren `Integer.parseInt()` porque se leyeron como texto desde el archivo.

---

## Diagrama de flujo: actualización tras un partido

```
Partido finalizado
    │
    ▼ EstadisticaService (próximo paso)
    ├── estadisticaDAO.leerPorId("Ingeniería")  → Estadistica actual
    │       └── equipoDAO.leerPorId("Ingeniería") → Equipo reconstruido
    │
    ├── [calcular nuevos valores con ReglasPuntuacion]
    │
    └── estadisticaDAO.actualizar(estadisticaActualizada)
            └── guardarTodos() → estadisticas.txt reescrito
```

---

## Cierre de la capa DAO

Con `EstadisticaDAO` se completa la capa DAO. Resumen de los seis DAOs implementados:

| DAO | Archivo | Identificador | Colabora con |
|---|---|---|---|
| `JugadorDAO` | `jugadores.txt` | identificacion | — |
| `TecnicoDAO` | `tecnicos.txt` | identificacion | — |
| `ArbitroDAO` | `arbitros.txt` | identificacion | — |
| `EquipoDAO` | `equipos.txt` | nombre | TecnicoDAO, JugadorDAO |
| `PartidoDAO` | `partidos.txt` | idPartido | EquipoDAO, ArbitroDAO |
| `EstadisticaDAO` | `estadisticas.txt` | nombre equipo | EquipoDAO |

Los tres primeros son independientes. Los tres últimos colaboran con los anteriores — forman una jerarquía de dependencias que respeta el **Low Coupling**: cada DAO solo conoce a los que están "por debajo" de él en la jerarquía.
