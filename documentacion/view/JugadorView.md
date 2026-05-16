# JugadorView — Vista de consola de Jugador

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/JugadorView.java`

---

## ¿Qué es?

Gestiona la interacción con el usuario para todo lo relacionado con jugadores: muestra menús, lee datos del teclado y delega las operaciones al `JugadorService`.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo maneja entrada/salida de jugadores |
| **SOLID SRP** | Cambia únicamente si cambia la interfaz de usuario |

---

## Responsabilidades de la View

La View hace exactamente tres cosas:

1. **Mostrar** — menús, listas y mensajes al usuario
2. **Leer** — datos del teclado con `Scanner`
3. **Delegar** — llamar al Service y capturar sus excepciones

Lo que la View **no hace**:
- Validar datos (eso es del Service)
- Acceder al DAO directamente (eso viola la arquitectura en capas)
- Contener lógica de negocio

---

## El try-catch como frontera entre capas

Aquí es donde el diseño con excepciones cobra sentido completo:

```java
try {
    jugadorService.registrarJugador(jugador);
    System.out.println("Jugador registrado exitosamente.");  // mensaje de éxito: View
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());          // mensaje de error: Service
}
```

- El texto de **éxito** lo decide la View
- El texto de **error** lo decide el Service (via `e.getMessage()`)
- La View no sabe qué validación falló — solo muestra lo que recibe

---

## Inyección de dependencias

```java
public JugadorView(JugadorService jugadorService, Scanner scanner) { ... }
```

Tanto el Service como el `Scanner` se reciben por constructor. El `Scanner` se comparte entre todas las Views para evitar conflictos con el buffer de entrada — un solo `Scanner` por aplicación.

---

## Método `leerEntero()`

```java
private int leerEntero() {
    while (true) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.print("Valor inválido. Ingrese un número entero: ");
        }
    }
}
```

Usa `scanner.nextLine()` en lugar de `scanner.nextInt()` para evitar el problema clásico de Java: `nextInt()` no consume el salto de línea, dejando el buffer sucio y causando que el siguiente `nextLine()` lea una cadena vacía.

---

## Flujo del menú

```
mostrarMenu()
    │
    ├── 1 → registrarJugador()
    │         ├── leer datos del usuario
    │         ├── service.registrarJugador()
    │         └── mostrar éxito o error
    │
    ├── 2 → listarJugadores()
    │         ├── service.listarJugadores()
    │         └── imprimir cada jugador.obtenerPerfil()  ← polimorfismo en acción
    │
    ├── 3 → buscarJugador()
    ├── 4 → actualizarJugador()
    ├── 5 → eliminarJugador()
    └── 0 → volver al menú principal
```

---

## `obtenerPerfil()` en la View

Al listar jugadores, la View llama:
```java
System.out.println(jugadores.get(i).obtenerPerfil());
```

Esto es el **polimorfismo de `Persona`** funcionando en la práctica. Si en el futuro la lista fuera de `Persona` en lugar de `Jugador`, el mismo código mostraría correctamente técnicos y árbitros — sin ningún `if/else`.

---

## Relación con otras clases

```
JugadorView  →  JugadorService  →  ICRUD<Jugador>  ←  JugadorDAO
             →  Scanner (compartido con todas las Views)
```
