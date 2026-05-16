# Persona — Clase abstracta base

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Persona.java`

---

## ¿Qué es?

`Persona` es la clase padre abstracta de todos los participantes de la liga. No puede instanciarse directamente porque representa un concepto general — en la realidad siempre tenemos un jugador, un técnico o un árbitro concreto, nunca una "persona genérica".

La palabra clave `abstract` en Java garantiza esto: si alguien intenta escribir `new Persona()`, el compilador lo rechaza.

---

## Atributos

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Nombre completo |
| `identificacion` | String | Número de documento (actúa como ID único) |
| `contacto` | String | Teléfono o correo |

Todos los atributos son `private` — esto aplica **encapsulamiento**: el estado interno solo se puede leer o modificar a través de getters y setters, nunca directamente desde otra clase.

---

## Constructores

```java
// Constructor vacío: necesario para frameworks y reconstrucción desde archivos
public Persona() {}

// Constructor completo: para crear una persona con todos sus datos
public Persona(String nombre, String identificacion, String contacto)
```

El constructor vacío no parece útil a primera vista, pero es esencial para la capa DAO: cuando se reconstruye un objeto desde una línea del archivo `.txt`, se necesita poder crear primero un objeto vacío y luego asignarle los campos.

---

## Método abstracto `obtenerPerfil()`

```java
public abstract String obtenerPerfil();
```

Este método **obliga** a todas las subclases a definir cómo se presentan. Es el corazón del polimorfismo en este sistema.

**¿Por qué es importante?** Sin este método, la vista tendría que hacer algo como:

```java
// MAL: condicionales que crecen cada vez que se agrega un tipo nuevo
if (persona instanceof Jugador) {
    System.out.println("JUGADOR: ...");
} else if (persona instanceof Tecnico) {
    System.out.println("TÉCNICO: ...");
}
```

Esto viola el **OCP**: cada vez que se agrega un nuevo tipo de persona, hay que modificar la vista.

Con `obtenerPerfil()` abstracto, la vista simplemente llama:

```java
// BIEN: la vista no sabe ni le importa de qué tipo es la persona
System.out.println(persona.obtenerPerfil());
```

Si mañana se agrega `Dirigente extends Persona`, la vista no se toca — solo se implementa `obtenerPerfil()` en `Dirigente`.

---

## Patrón GRASP: **High Cohesion**

`Persona` solo gestiona los datos comunes a todos los participantes. No sabe nada de:
- Persistencia → eso es responsabilidad de los DAO
- Equipos o partidos → eso es responsabilidad de `Equipo` y `Partido`
- Pantallas → eso es responsabilidad de las View

Cada clase hace una sola cosa bien hecha. Eso es alta cohesión.

---

## Principio SOLID: **SRP**

`Persona` tiene una sola razón para cambiar: si cambian los datos comunes a todos los participantes (por ejemplo, agregar un atributo `fechaNacimiento`). No cambia por razones de persistencia, visualización o reglas de negocio.

---

## Relación con otras clases

```
Persona (abstract)
├── Jugador     → agrega posicion y numeroCamiseta
├── Tecnico     → agrega especialidad
└── Arbitro     → agrega categoriaCertificacion
```
