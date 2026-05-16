# EstadisticaView — Vista de consola de Estadistica

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/EstadisticaView.java`

---

## ¿Qué es?

Muestra las estadísticas de la liga al usuario. Su función principal es presentar la **tabla de posiciones** de forma tabular y clara. No tiene operaciones de registro ni actualización — esas son consecuencia automática de registrar resultados.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo presenta estadísticas, no las modifica |
| **SOLID SRP** | Cambia únicamente si cambia la presentación de estadísticas |

---

## Menú reducido: por qué solo dos opciones

```
1. Ver tabla de posiciones
2. Buscar estadística por equipo
```

No hay "Registrar", "Actualizar" ni "Eliminar" porque las estadísticas no se ingresan manualmente — son el resultado de partidos registrados. Exponer esas operaciones al usuario rompería la integridad del sistema.

---

## `mostrarClasificacion()`: formato tabular

```
╔═══╦══════════════════╦═══╦═══╦═══╦═══╦═════╦═════╦══════╦═════╗
║ # ║ Equipo           ║ J ║ G ║ E ║ P ║  GF ║  GC ║  DG  ║ Pts ║
╠═══╬══════════════════╬═══╬═══╬═══╬═══╬═════╬═════╬══════╬═════╣
║ 1 ║ Ingeniería       ║ 5 ║ 3 ║ 1 ║ 1 ║  8  ║  4  ║  4   ║ 10  ║
║ 2 ║ Medicina         ║ 5 ║ 2 ║ 2 ║ 1 ║  6  ║  5  ║  1   ║  8  ║
╚═══╩══════════════════╩═══╩═══╩═══╩═══╩═════╩═════╩══════╩═════╝
```

La tabla muestra: posición, equipo, partidos jugados, ganados, empatados, perdidos, goles a favor, goles en contra, diferencia de goles y puntos.

El ordenamiento (por puntos → diferencia de goles → goles a favor) lo hace `EstadisticaService.listarClasificacion()` — la View solo imprime la lista en el orden que recibe.

---

## Separación de responsabilidades en la tabla

| Responsabilidad | Clase |
|---|---|
| Ordenar por puntos y criterios de desempate | `EstadisticaService` |
| Calcular diferencia de goles para mostrar | `EstadisticaView` |
| Calcular puntos tras cada partido | `ReglasPuntuacion` |
| Persistir estadísticas | `EstadisticaDAO` |

La diferencia de goles (`GF - GC`) se calcula en la View porque es solo para mostrar — no es un dato que se persiste en `Estadistica`.

---

## Relación con otras clases

```
EstadisticaView  →  EstadisticaService  →  ICRUD<Estadistica>  ←  EstadisticaDAO
                 →  Scanner (compartido)
```
