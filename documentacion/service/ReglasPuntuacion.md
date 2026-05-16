# ReglasPuntuacion — Interfaz de puntuación

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/ReglasPuntuacion.java`

---

## ¿Qué es?

Es el contrato que define cómo se calculan los puntos que obtiene un equipo según el resultado de un partido. Cualquier variante de puntuación que exista o se agregue en el futuro debe implementar esta interfaz.

```java
public interface ReglasPuntuacion {
    int calcularPuntos(int golesAFavor, int golesEnContra);
}
```

---

## Patrón GRASP: **Polymorphism**

Sin esta interfaz, `PartidoService` tendría que usar condicionales para decidir qué regla aplicar:

```java
// MAL: condicionales que crecen con cada variante
if (tipoTorneo.equals("estandar")) {
    if (golesAFavor > golesEnContra) puntos = 3;
    ...
} else if (tipoTorneo.equals("especial")) {
    if (golesAFavor > golesEnContra) puntos = 5;
    ...
}
```

Con `ReglasPuntuacion`, el `PartidoService` simplemente llama:

```java
// BIEN: no importa qué regla es, solo se llama el método
int puntos = reglasPuntuacion.calcularPuntos(golesAFavor, golesEnContra);
```

El polimorfismo reemplaza los condicionales.

---

## Principio SOLID: **OCP**

`ReglasPuntuacion` es el OCP en su forma más directa:

```
ReglasPuntuacion  ← contrato fijo
       ↑
┌──────┴──────────────────┐
ReglasPuntuacionEstandar  ReglasPuntuacionTorneo  ReglasPuntuacionEliminatoria
   (3-1-0)                    (5-2-0)                  (solo victoria cuenta)
```

- `PartidoService` depende de `ReglasPuntuacion` → **cerrado para modificación**
- Agregar una nueva variante = crear una nueva clase → **abierto para extensión**
- **Nunca se toca `PartidoService`** cuando cambian las reglas

---

## Implementaciones disponibles

| Clase | Regla |
|---|---|
| `ReglasPuntuacionEstandar` | Victoria=3, Empate=1, Derrota=0 |
| *(futuras)* | Cualquier variante sin tocar el Service |
