# Estadistica — Clase concreta

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/Estadistica.java`

---

## ¿Qué es?

Almacena el acumulado de resultados de un equipo a lo largo de la liga. Es la "tabla de posiciones" de un equipo concreto.

---

## Atributos

| Atributo | Tipo | Descripción |
|---|---|---|
| `equipo` | Equipo | El equipo al que pertenece esta estadística |
| `partidosJugados` | int | Total de partidos disputados |
| `partidosGanados` | int | Partidos ganados |
| `partidosEmpatados` | int | Partidos empatados |
| `partidosPerdidos` | int | Partidos perdidos |
| `golesAFavor` | int | Total de goles anotados |
| `golesEnContra` | int | Total de goles recibidos |
| `puntos` | int | Puntos acumulados en la liga |

---

## Estado actual del diseño

Actualmente `Estadistica` solo tiene getters y setters. La lógica de **cómo se calculan los puntos** (3 por victoria, 1 por empate, 0 por derrota) no vive aquí.

¿Por qué? Porque esa regla puede cambiar (torneo especial, penalizaciones, etc.). Si la lógica viviera en `Estadistica`, habría que modificarla cada vez que cambia la regla — eso violaría el **OCP**.

La solución es la interfaz `ReglasPuntuacion`, que se implementa en la capa Service.

---

## Relación con otras clases

```
Estadistica
├── referencia → Equipo
└── es actualizada por → GestorPartido (service)
                       → EstadisticaDAO (dao)
```

---

## Nota pedagógica

La separación entre `Estadistica` (datos) y `ReglasPuntuacion` (lógica de cálculo) es un ejemplo claro del **SRP**: la clase de datos no sabe las reglas, y las reglas no saben cómo guardarse.
