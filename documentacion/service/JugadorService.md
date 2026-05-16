# JugadorService — Capa de negocio de Jugador

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/JugadorService.java`

---

## ¿Qué es?

Es la clase que concentra toda la **lógica de negocio** relacionada con jugadores: validaciones, reglas de registro y coordinación con el DAO. Es el intermediario entre la View y el DAO.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo gestiona reglas de negocio de jugadores |
| **SOLID SRP** | Cambia únicamente si cambian las reglas de jugadores |
| **SOLID OCP** | Depende de `ICRUD<Jugador>`, no de `JugadorDAO` directamente |

---

## Dónde viven los mensajes: Service vs View

Una pregunta clave de arquitectura en capas:

> ¿El Service debe retornar mensajes como `"Error: nombre obligatorio"`?

**No.** Si el Service produce texto para el usuario, tiene dos razones para cambiar: las reglas de negocio Y el lenguaje/formato del mensaje. Eso viola el **SRP**.

La separación correcta es:

| Capa | Responsabilidad |
|---|---|
| **Service** | Detectar *qué* salió mal y lanzar una excepción con el motivo |
| **View** | Capturar la excepción y decidir *cómo* mostrarlo al usuario |

---

## Manejo de errores: `IllegalArgumentException`

El Service usa excepciones para comunicar errores de negocio:

```java
// Service: lanza la excepción con el motivo técnico
public void registrarJugador(Jugador jugador) {
    if (jugador.getNombre().isBlank())
        throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
    // ...
}
```

```java
// View: captura la excepción y decide qué mostrar
try {
    service.registrarJugador(jugador);
    System.out.println("Jugador registrado exitosamente.");
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```

`IllegalArgumentException` es una excepción de tipo **unchecked** (no obliga a declararla con `throws`). Es la estándar de Java para indicar que un argumento viola una regla de negocio.

---

## Inyección de dependencia

```java
// Recibe cualquier implementación de ICRUD<Jugador>
public JugadorService(ICRUD<Jugador> jugadorDAO) {
    this.jugadorDAO = jugadorDAO;
}

// Por defecto usa archivos .txt
public JugadorService() {
    this.jugadorDAO = new JugadorDAO();
}
```

---

## Métodos y sus responsabilidades

### `registrarJugador(Jugador)` → `void`

```
¿jugador es null?           → IllegalArgumentException
¿nombre está vacío?         → IllegalArgumentException
¿identificación está vacía? → IllegalArgumentException
¿numeroCamiseta <= 0?       → IllegalArgumentException
¿ya existe esa ID?          → IllegalArgumentException
Todo válido                 → dao.crear()  [sin retorno, la ausencia de excepción = éxito]
```

### `listarJugadores()` → `List<Jugador>`
Delega al DAO. Sin excepciones — una lista vacía es un resultado válido.

### `buscarJugador(String)` → `Jugador`
Lanza `IllegalArgumentException` si no encuentra al jugador. La View decide si eso es un error crítico o simplemente muestra "no encontrado".

### `actualizarJugador(Jugador)` → `void`
Valida que exista antes de intentar actualizar. Evita operaciones inútiles en el DAO.

### `eliminarJugador(String)` → `void`
Valida existencia antes de eliminar. Lanza excepción descriptiva si no se encuentra.

---

## Flujo completo: registrar un jugador

```
JugadorView
    │  construye Jugador y llama registrarJugador()
    ▼
JugadorService.registrarJugador(jugador)
    ├── validaciones → alguna falla → IllegalArgumentException
    │                                       ▲
    │                               View la captura y muestra e.getMessage()
    │
    └── validaciones → todas pasan → jugadorDAO.crear(jugador)
                                            ▼
                                     jugadores.txt actualizado
                                            ▼
                              View muestra "Registrado exitosamente."
```

---

## Relación con otras clases

```
JugadorView  →  JugadorService  →  ICRUD<Jugador>  ←  JugadorDAO
                                                    ←  JugadorDAOSQL (futuro)
```
