# TecnicoDAO — Data Access Object de Tecnico

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/TecnicoDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar técnicos** usando un archivo de texto plano (`tecnicos.txt`). Es la única clase del sistema que sabe cómo se guarda un técnico en disco.

---

## Patrón GRASP: **Expert**

`TecnicoDAO` es el **experto** en persistencia de técnicos. El patrón Expert dice:

> *"Asigna la responsabilidad a la clase que tiene la información necesaria para cumplirla."*

Nadie más sabe serializar un `Tecnico` a texto ni reconstruirlo desde una línea. Esa lógica vive aquí y solo aquí. Si el `JugadorService` o `EquipoService` necesitan un técnico, se lo piden a `TecnicoDAO` — no intentan leer el archivo ellos mismos.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`TecnicoDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia (de `.txt` a base de datos, a JSON, etc.). No cambia si:
- Cambia la lógica de negocio → eso es responsabilidad de `TecnicoService`
- Cambia la forma de mostrar datos → eso es responsabilidad de `TecnicoView`
- Se agregan reglas de validación → eso es responsabilidad del Service

### OCP — Open/Closed Principle
Implementa `ICRUD<Tecnico>`. Si mañana se necesita persistencia en base de datos, se crea `TecnicoDAOSQL implements ICRUD<Tecnico>` sin tocar este archivo ni el Service.

```
          ICRUD<Tecnico>        ← contrato fijo, nunca cambia
               ↑
    ┌──────────┴──────────┐
 TecnicoDAO          TecnicoDAOSQL
(archivos .txt)     (base de datos)
```

---

## Formato del archivo `tecnicos.txt`

Cada técnico ocupa una línea. Los campos se separan con punto y coma:

```
nombre;identificacion;contacto;especialidad
```

**Ejemplo:**
```
Juan Martínez;2001;3201234567;4-4-2
Luisa Fernández;2002;3157654321;Ofensiva
```

`Tecnico` tiene un solo atributo propio (`especialidad`), por eso su línea tiene 4 campos frente a los 5 de `Jugador`.

---

## Métodos implementados

### `crear(Tecnico)`
Abre el archivo en modo **append** (`true`) y agrega una línea al final. No borra el contenido existente.

```java
new FileWriter(ARCHIVO, true)  // true = modo append
```

### `listarTodos()`
Lee el archivo línea por línea y reconstruye cada `Tecnico` usando `lineaATecnico()`. Si el archivo no existe todavía, retorna una lista vacía en lugar de lanzar error.

### `leerPorId(String)`
Recorre la lista completa y retorna el técnico cuya identificación coincida. Retorna `null` si no se encuentra. Se apoya en `listarTodos()` para no duplicar la lógica de lectura.

### `actualizar(Tecnico)`
Usa el patrón **carga → modifica → reescribe**:
1. Carga todos los técnicos en memoria con `listarTodos()`
2. Localiza por identificación y reemplaza en la lista
3. Reescribe el archivo completo con `guardarTodos()`

### `eliminar(String)`
Usa el mismo patrón que `actualizar`, pero en lugar de reemplazar usa `removeIf()` para quitar el registro de la lista antes de reescribir.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `tecnicoALinea(Tecnico)` | Convierte un objeto a línea de texto (serialización) |
| `lineaATecnico(String)` | Convierte una línea de texto a objeto (deserialización) |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

Estos métodos son `private` porque son un **detalle de implementación** — nadie fuera del DAO necesita saber cómo se serializa un técnico.

---

## Diagrama de flujo: `actualizar()` y `eliminar()`

```
tecnicos.txt
    │
    ▼ listarTodos()
List<Tecnico> en memoria
    │
    ▼ modificar / eliminar en la lista
List<Tecnico> modificada
    │
    ▼ guardarTodos()
tecnicos.txt reescrito
```

En archivos planos no existe la edición "en sitio" — siempre es carga total, modificación en memoria, y reescritura total.

---

## Consistencia con JugadorDAO

`TecnicoDAO` sigue exactamente el mismo patrón que `JugadorDAO`. La única diferencia real está en los métodos privados de serialización:

```java
// JugadorDAO — 5 campos, último es int
return j.getNombre() + ";" + j.getIdentificacion() + ";" +
       j.getContacto() + ";" + j.getPosicion() + ";" + j.getNumeroCamiseta();

// TecnicoDAO — 4 campos, todos String
return t.getNombre() + ";" + t.getIdentificacion() + ";" +
       t.getContacto() + ";" + t.getEspecialidad();
```

Cada DAO encapsula esa diferencia — el resto del sistema no la ve.
