# Jugador — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Jugador.java`

---

## ¿Qué es?

Representa a un jugador inscrito en la liga. Extiende `Persona` y agrega los atributos propios de un jugador: su posición en el campo y su número de camiseta.

---

## Atributos propios

| Atributo | Tipo | Ejemplo |
|---|---|---|
| `posicion` | String | "Delantero", "Portero", "Defensa" |
| `numeroCamiseta` | int | 9, 1, 5 |

Los atributos heredados (`nombre`, `identificacion`, `contacto`) vienen de `Persona`.

---

## Polimorfismo: `obtenerPerfil()`

```java
@Override
public String obtenerPerfil() {
    return "JUGADOR: " + getNombre() + " | Dorsal: " + numeroCamiseta + " | Posición: " + posicion;
}
```

Al sobreescribir (`@Override`) el método abstracto de `Persona`, `Jugador` define su propia forma de presentarse. Esto es **polimorfismo en acción**: el mismo mensaje `obtenerPerfil()` produce resultados distintos según el tipo real del objeto.

**Ejemplo de salida:**
```
JUGADOR: Carlos Pérez | Dorsal: 9 | Posición: Delantero
```

---

## Constructores

```java
// Constructor vacío: necesario para reconstruir objetos desde el archivo .txt
public Jugador() { super(); }

// Constructor completo: para crear un jugador nuevo
public Jugador(String nombre, String identificacion, String contacto,
               String posicion, int numeroCamiseta)
```

El constructor vacío es clave para la capa DAO: cuando se lee una línea del archivo y se reconstruye el objeto campo por campo.

---

## Principio SOLID aplicado

**SRP:** `Jugador` solo representa los datos de un jugador. No sabe cómo guardarse, ni cómo mostrarse en pantalla. Esas responsabilidades pertenecen a `JugadorDAO` y `JugadorView` respectivamente.
