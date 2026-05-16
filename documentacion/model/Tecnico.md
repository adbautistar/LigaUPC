# Tecnico — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Tecnico.java`

---

## ¿Qué es?

Representa al director técnico o entrenador de un equipo. Extiende `Persona` y agrega su especialidad táctica. Al ser una clase concreta (no abstracta), puede instanciarse directamente.

---

## Atributos

| Atributo | Tipo | Ejemplo | Heredado de |
|---|---|---|---|
| `nombre` | String | "Juan Martínez" | `Persona` |
| `identificacion` | String | "2001" | `Persona` |
| `contacto` | String | "3201234567" | `Persona` |
| `especialidad` | String | "Ofensiva", "4-4-2" | Propio |

---

## Constructores

```java
// Constructor vacío: necesario para reconstruir desde archivo .txt
public Tecnico() { super(); }

// Constructor completo: para crear un técnico nuevo
public Tecnico(String nombre, String identificacion, String contacto, String especialidad)
```

El `super()` en el constructor vacío invoca al constructor vacío de `Persona`, garantizando que la cadena de inicialización de la jerarquía se respete.

---

## Polimorfismo: `obtenerPerfil()`

```java
@Override
public String obtenerPerfil() {
    return "TÉCNICO: " + getNombre() + " | Especialidad: " + especialidad;
}
```

Al sobreescribir (`@Override`) el método abstracto de `Persona`, `Tecnico` define su propia forma de presentarse. El `@Override` no es obligatorio en Java, pero es buena práctica porque:
- Le dice al compilador que esta firma debe existir en la clase padre
- Si se cambia el nombre del método en `Persona`, el compilador avisa el error aquí

**Ejemplo de salida:**
```
TÉCNICO: Juan Martínez | Especialidad: 4-4-2
```

---

## Patrón GRASP: **High Cohesion**

`Tecnico` solo representa los datos de un director técnico. No sabe cómo guardarse, ni cómo mostrarse en pantalla, ni a qué equipo pertenece. Esas responsabilidades son de otras clases:

| Responsabilidad | Clase encargada |
|---|---|
| Persistir técnicos | `TecnicoDAO` |
| Mostrar técnicos en consola | `TecnicoView` |
| Asignar técnico a equipo | `EquipoService` |

---

## Principio SOLID: **SRP**

`Tecnico` tiene una sola razón para cambiar: si cambian los datos propios de un técnico (por ejemplo, agregar `añosExperiencia`). No cambia si cambia cómo se persiste ni cómo se muestra.

---

## Relación con otras clases

`Tecnico` es referenciado por `Equipo`: cada equipo tiene un técnico asignado.

```java
// En Equipo.java
private Tecnico tecnico;
```

Esta relación es una **asociación**: el equipo conoce a su técnico, pero el técnico no conoce al equipo. Esto mantiene el **bajo acoplamiento** — si `Equipo` cambia, `Tecnico` no se ve afectado.
