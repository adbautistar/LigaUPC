# ArbitroDAO — Data Access Object de Arbitro

**Capa:** DAO  
**Archivo:** `src/main/java/ligaupc/dao/ArbitroDAO.java`

---

## ¿Qué es?

Es la clase responsable de **persistir y recuperar árbitros** usando un archivo de texto plano (`arbitros.txt`). Es la única clase del sistema que sabe cómo se guarda un árbitro en disco.

---

## Patrón GRASP: **Expert**

`ArbitroDAO` es el **experto** en persistencia de árbitros. El patrón Expert dice:

> *"Asigna la responsabilidad a la clase que tiene la información necesaria para cumplirla."*

Nadie más sabe serializar un `Arbitro` a texto ni reconstruirlo desde una línea. Cuando `PartidoDAO` necesita un árbitro para reconstruir un partido, se lo pide a `ArbitroDAO` — no lee `arbitros.txt` directamente.

---

## Principios SOLID aplicados

### SRP — Single Responsibility Principle
`ArbitroDAO` tiene **una sola razón para cambiar**: si cambia el mecanismo de persistencia. No cambia si:
- Cambia la lógica de asignación de árbitros → eso es responsabilidad de `ArbitroService`
- Cambia la forma de listar árbitros en pantalla → eso es responsabilidad de `ArbitroView`

### OCP — Open/Closed Principle
Implementa `ICRUD<Arbitro>`. Si se necesita otra forma de persistencia, se crea una nueva clase que implemente `ICRUD<Arbitro>` sin tocar este archivo ni el Service.

```
          ICRUD<Arbitro>        ← contrato fijo, nunca cambia
               ↑
    ┌──────────┴──────────┐
 ArbitroDAO          ArbitroDAOSQL
(archivos .txt)     (base de datos)
```

---

## Formato del archivo `arbitros.txt`

Cada árbitro ocupa una línea. Los campos se separan con punto y coma:

```
nombre;identificacion;contacto;categoriaCertificacion
```

**Ejemplo:**
```
Roberto Díaz;3001;3104567890;Nacional
Sandra Ruiz;3002;3187654321;FIFA
```

---

## Métodos implementados

### `crear(Arbitro)`
Abre el archivo en modo **append** (`true`) y agrega una línea al final. No borra el contenido existente.

```java
new FileWriter(ARCHIVO, true)  // true = modo append
```

### `listarTodos()`
Lee el archivo línea por línea y reconstruye cada `Arbitro` usando `lineaAArbitro()`. Si el archivo no existe todavía, retorna una lista vacía en lugar de lanzar error.

### `leerPorId(String)`
Recorre la lista completa y retorna el árbitro cuya identificación coincida. Retorna `null` si no se encuentra. Se apoya en `listarTodos()` para no duplicar la lógica de lectura.

### `actualizar(Arbitro)`
Usa el patrón **carga → modifica → reescribe**:
1. Carga todos los árbitros en memoria con `listarTodos()`
2. Localiza por identificación y reemplaza en la lista
3. Reescribe el archivo completo con `guardarTodos()`

### `eliminar(String)`
Usa el mismo patrón que `actualizar`, pero en lugar de reemplazar usa `removeIf()` para quitar el registro de la lista antes de reescribir.

---

## Métodos privados de apoyo

| Método | Propósito |
|---|---|
| `arbitroALinea(Arbitro)` | Convierte un objeto a línea de texto (serialización) |
| `lineaAArbitro(String)` | Convierte una línea de texto a objeto (deserialización) |
| `guardarTodos(List)` | Reescribe el archivo completo desde una lista |

Estos métodos son `private` porque son un **detalle de implementación** — nadie fuera del DAO necesita saber cómo se serializa un árbitro.

---

## Diagrama de flujo: `actualizar()` y `eliminar()`

```
arbitros.txt
    │
    ▼ listarTodos()
List<Arbitro> en memoria
    │
    ▼ modificar / eliminar en la lista
List<Arbitro> modificada
    │
    ▼ guardarTodos()
arbitros.txt reescrito
```

En archivos planos no existe la edición "en sitio" — siempre es carga total, modificación en memoria, y reescritura total.

---

## Reflexión: ¿no sería mejor un solo DAO genérico?

Una pregunta válida de los estudiantes: *"¿Por qué no hacer un `PersonaDAO` que sirva para Jugador, Tecnico y Arbitro?"*

La respuesta está en el **SRP y el Expert**:
- Cada tipo tiene atributos distintos → serialización distinta
- Un `PersonaDAO` genérico tendría que conocer los detalles de los tres tipos → baja cohesión
- Si se agrega `Dirigente`, el `PersonaDAO` tendría que modificarse → viola el OCP

Tres clases pequeñas y enfocadas son mejor que una clase grande y acoplada.
