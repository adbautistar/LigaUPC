# Arbitro — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Arbitro.java`

---

## ¿Qué es?

Representa al árbitro que dirige un partido. Extiende `Persona` y agrega su categoría de certificación. Al ser una clase concreta, puede instanciarse directamente.

---

## Atributos

| Atributo | Tipo | Ejemplo | Heredado de |
|---|---|---|---|
| `nombre` | String | "Roberto Díaz" | `Persona` |
| `identificacion` | String | "3001" | `Persona` |
| `contacto` | String | "3104567890" | `Persona` |
| `categoriaCertificacion` | String | "FIFA", "Nacional", "Regional" | Propio |

---

## Constructores

```java
// Constructor vacío: necesario para reconstruir desde archivo .txt
public Arbitro() { super(); }

// Constructor completo: para crear un árbitro nuevo
public Arbitro(String nombre, String identificacion, String contacto, String categoriaCertificacion)
```

Al igual que en `Tecnico`, el `super()` garantiza que el constructor de `Persona` se ejecute primero, inicializando los atributos heredados antes de asignar los propios.

---

## Polimorfismo: `obtenerPerfil()`

```java
@Override
public String obtenerPerfil() {
    return "ÁRBITRO: " + getNombre() + " | Certificación: " + categoriaCertificacion;
}
```

Sobreescribe el método abstracto de `Persona` con la presentación específica de un árbitro.

**Ejemplo de salida:**
```
ÁRBITRO: Roberto Díaz | Certificación: Nacional
```

---

## Patrón GRASP: **High Cohesion**

`Arbitro` solo representa los datos de un árbitro. No sabe cómo guardarse ni cómo mostrarse. Cada responsabilidad tiene su clase:

| Responsabilidad | Clase encargada |
|---|---|
| Persistir árbitros | `ArbitroDAO` |
| Mostrar árbitros en consola | `ArbitroView` |
| Asignar árbitro a partido | `PartidoService` |

---

## Principio SOLID: **SRP**

`Arbitro` tiene una sola razón para cambiar: si cambian los datos propios de un árbitro (por ejemplo, agregar `añosExperiencia` o `nacionalidad`). No cambia si cambia cómo se persiste ni cómo se muestra.

---

## Las tres subclases: el patrón completo

`Jugador`, `Tecnico` y `Arbitro` son tres manifestaciones del mismo principio de diseño:

| Aspecto | Jugador | Tecnico | Arbitro |
|---|---|---|---|
| Hereda de | `Persona` | `Persona` | `Persona` |
| Atributos propios | posicion, numeroCamiseta | especialidad | categoriaCertificacion |
| Implementa | `obtenerPerfil()` | `obtenerPerfil()` | `obtenerPerfil()` |
| Sabe persistirse | No | No | No |
| Sabe mostrarse | No | No | No |

Esta uniformidad es el resultado de aplicar **High Cohesion** y **SRP** de manera consistente.

---

## Relación con otras clases

`Arbitro` es referenciado por `Partido`: cada partido tiene un árbitro asignado.

```java
// En Partido.java
private Arbitro arbitro;
```

El árbitro no conoce al partido — esa es una relación unidireccional que reduce el acoplamiento.
