# Equipo — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Equipo.java`

---

## ¿Qué es?

Representa a un equipo de fútbol inscrito en la liga. Agrupa a sus jugadores y tiene asignado un técnico y una sede.

---

## Atributos

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Nombre del equipo |
| `sede` | String | Sede universitaria de origen |
| `tecnico` | Tecnico | Director técnico asignado |
| `jugadores` | List\<Jugador\> | Nómina de jugadores |

---

## Patrón GRASP: **Expert** y **Creator**

### Expert
`Equipo` es el **experto** en gestionar su propia nómina. Por eso tiene los métodos `agregarJugador()` y `quitarJugador()`:

```java
public void agregarJugador(Jugador jugador) {
    this.jugadores.add(jugador);
}

public boolean quitarJugador(String idJugador) {
    return this.jugadores.removeIf(j -> j.getIdentificacion().equals(idJugador));
}
```

Nadie más debería agregar o quitar jugadores a un equipo directamente — esa responsabilidad le pertenece al equipo porque es quien tiene la lista.

`quitarJugador()` retorna `boolean` para informar al Service si el jugador realmente existía en el plantel. Si retorna `false`, el Service sabe que debe lanzar una excepción; si retorna `true`, la operación fue exitosa. Esto evita que el modelo conozca las reglas de negocio (eso sería romper SRP).

### Creator
`Equipo` inicializa su propia lista de jugadores en el constructor:

```java
public Equipo() {
    this.jugadores = new ArrayList<>();
}
```

El patrón **Creator** dice: quien contiene o agrega objetos es quien debe crearlos o inicializarlos. `Equipo` contiene `Jugador`es, entonces `Equipo` inicializa la colección.

---

## Relación con otras clases

```
Equipo
├── tiene un → Tecnico
├── tiene muchos → Jugador (List<Jugador>)
└── es referenciado por → Partido (como local y visitante)
                        → Estadistica
```

---

## Nota de diseño

`Equipo` no extiende `Persona` porque un equipo no es una persona. La herencia solo se usa cuando existe una relación real de tipo "es un". Un equipo **tiene** personas, no **es** una persona.
