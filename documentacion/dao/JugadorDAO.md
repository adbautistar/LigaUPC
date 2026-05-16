# JugadorDAO — Data Access Object de Jugador

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/JugadorDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar jugadores** usando un archivo de texto plano (`jugadores.txt`). Es la única clase del sistema que sabe cómo se guarda un jugador en disco.

---

## Patrón GRASP: **Expert**

`JugadorDAO` es el **experto** en persistencia de jugadores. El patrón Expert dice:

> *"Asigna la responsabilidad a la clase que tiene la información necesaria para cumplirla."*

Nadie más sabe serializar un `Jugador` a texto ni reconstruirlo desde una línea. Esa lógica vive aquí y solo aquí.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`JugadorDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia (de `.txt` a base de datos, a JSON, etc.). No cambia si cambia la lógica de negocio — eso es responsabilidad de `JugadorService`.

### OCP — Open/Closed Principle
Implementa `ICRUD<Jugador>`. Si mañana se necesita persistencia en base de datos, se crea `JugadorDAOSQL implements ICRUD<Jugador>` sin tocar este archivo ni el Service.

---

## Formato del archivo `jugadores.txt`

Cada jugador ocupa una línea. Los campos se separan con punto y coma:

```
nombre;identificacion;contacto;posicion;numeroCamiseta
```

**Ejemplo:**
```
Carlos Pérez;1001;3001234567;Delantero;9
María López;1002;3109876543;Portera;1
```

---

## Métodos implementados

### `crear(Jugador)`
Abre el archivo en modo **append** (`true`) y agrega una línea al final. No borra el contenido existente.

```java
new FileWriter(ARCHIVO, true)  // true = modo append
```

### `listarTodos()`
Lee el archivo línea por línea y reconstruye cada `Jugador` usando `lineaAJugador()`.

### `leerPorId(String)`
Recorre la lista completa y retorna el jugador cuya identificación coincida. Retorna `null` si no se encuentra.

### `actualizar(Jugador)`
Usa el patrón **carga → modifica → reescribe**:
1. Carga todos los jugadores en memoria
2. Reemplaza el que tenga la misma identificación
3. Reescribe el archivo completo

### `eliminar(String)`
Usa el mismo patrón que `actualizar`, pero en lugar de reemplazar, elimina el registro con `removeIf()`.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `jugadorALinea(Jugador)` | Convierte un objeto a línea de texto (serialización) |
| `lineaAJugador(String)` | Convierte una línea de texto a objeto (deserialización) |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

Estos métodos son `private` porque son un **detalle de implementación** — nadie fuera del DAO necesita saber cómo se serializa un jugador.

---

## Diagrama de flujo: `actualizar()` y `eliminar()`

```
archivo.txt
    │
    ▼ listarTodos()
List<Jugador> en memoria
    │
    ▼ modificar / eliminar en la lista
List<Jugador> modificada
    │
    ▼ guardarTodos()
archivo.txt reescrito
```

En archivos planos no existe la edición "en sitio" — siempre es carga total, modificación en memoria, y reescritura total.
