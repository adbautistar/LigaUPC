# EquipoView — Vista de consola de Equipo

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/EquipoView.java`

---

## ¿Qué es?

Gestiona la interacción con el usuario para todo lo relacionado con equipos. Es más completa que las Views anteriores porque expone operaciones de dominio propias de `Equipo`: asignar técnico, agregar jugadores al plantel y retirar jugadores del plantel.

Recibe `JugadorService` como dependencia adicional para poder mostrar al usuario la lista de jugadores disponibles antes de pedir un ID — mejora la experiencia sin romper SRP, porque la View solo la usa para **consulta de presentación**.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo maneja entrada/salida de equipos |
| **SOLID SRP** | Cambia únicamente si cambia la interfaz de usuario |

---

## Menú extendido

A diferencia de las Views anteriores, `EquipoView` tiene 8 opciones en lugar de 5:

```
1. Registrar equipo          <- CRUD básico
2. Listar equipos
3. Buscar equipo
4. Actualizar equipo
5. Eliminar equipo
6. Asignar técnico           <- operaciones de dominio
7. Agregar jugador
8. Quitar jugador
```

Las opciones 6, 7 y 8 corresponden a métodos de dominio de `EquipoService`. La View solo pide los identificadores necesarios y delega — no sabe cómo se valida ni cómo se persiste. Solo recibe el éxito o el mensaje de error.

---

## `listarEquipos()`: información jerárquica

Al listar, la View muestra la estructura completa del equipo:

```
Equipo: Ingeniería | Sede: Sede Norte
   TÉCNICO: Juan Martínez | Especialidad: 4-4-2
   Jugadores (3):
     - JUGADOR: Carlos Pérez | Dorsal: 9 | Posición: Delantero
     - JUGADOR: María López  | Dorsal: 1 | Posición: Portera
     - JUGADOR: Luis Gómez   | Dorsal: 5 | Posición: Defensa
```

Cada línea de técnico y jugadores usa `obtenerPerfil()` — el polimorfismo de `Persona` se ve en acción sin un solo `instanceof`.

---

## `asignarTecnico()`

```java
private void asignarTecnico() {
    System.out.print("Nombre del equipo: ");
    String nombreEquipo = scanner.nextLine();
    System.out.print("Identificacion del técnico: ");
    String idTecnico = scanner.nextLine();

    try {
        equipoService.asignarTecnico(nombreEquipo, idTecnico);
        System.out.println("Técnico asignado exitosamente.");
    } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
    }
}
```

La View solo pide dos datos: el nombre del equipo y la identificación del técnico. Todo lo demás (verificar que ambos existan, actualizar el archivo) lo hace el Service.

---

## `agregarJugador()`: con lista previa

Antes de pedir el ID, la View consulta `jugadorService.listarJugadores()` y muestra todos los jugadores registrados con sus IDs. Así el usuario no tiene que memorizar identificaciones:

```
Jugadores disponibles:
  JUGADOR: Carlos Pérez | Dorsal: 9 | Posición: Delantero | ID: 1001
  JUGADOR: María López  | Dorsal: 1 | Posición: Portera   | ID: 1002

Nombre del equipo: Ingeniería
Identificacion del jugador: 1001
Jugador agregado al equipo exitosamente.
```

**¿Por qué `JugadorService` en `EquipoView`?**  
La View recibe `JugadorService` en su constructor solo para hacer esta consulta de presentación. No lo usa para escribir ni para validar negocio — eso lo hace `EquipoService` internamente. Es una dependencia de **lectura para ayuda al usuario**, lo cual no rompe SRP porque la única razón de cambio de `EquipoView` sigue siendo la interfaz de usuario.

---

## `quitarJugador()`: con lista del equipo actual

Antes de pedir el ID a quitar, muestra los jugadores que actualmente pertenecen al equipo, para que el usuario sepa qué puede retirar:

```
-- Quitar Jugador de Equipo --
Nombre del equipo: Ingeniería

Jugadores actuales del equipo:
  JUGADOR: Carlos Pérez | Dorsal: 9 | Posición: Delantero | ID: 1001
  JUGADOR: María López  | Dorsal: 1 | Posición: Portera   | ID: 1002

Identificacion del jugador a quitar: 1001
Jugador retirado del equipo exitosamente.
```

Si el equipo no tiene jugadores, informa al usuario y regresa al menú sin pedir ningún dato. Esto evita que el usuario ingrese un ID que nunca podría ser válido.

**Decisión de diseño — dos consultas al Service:**  
La View llama `buscarEquipo()` primero solo para mostrar la lista. Luego llama `quitarJugador()`. El Service vuelve a buscar el equipo internamente — hay una doble lectura. Esto es un trade-off aceptable: mantiene la responsabilidad del Service intacta y evita que la View manipule objetos `Equipo` directamente para pasarlos al Service.

---

## Relación con otras clases

```
EquipoView  →  EquipoService  →  ICRUD<Equipo>     ←  EquipoDAO
                              →  TecnicoService     (validación interna)
                              →  JugadorService     (validación interna)
            →  Scanner (compartido)
```
