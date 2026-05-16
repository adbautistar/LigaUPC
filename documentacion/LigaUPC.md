# LigaUPC — Clase principal (main)

**Archivo:** `src/main/java/ligaupc/LigaUPC.java`

---

## ¿Qué es?

Es el punto de entrada del sistema. Su única responsabilidad es **ensamblar** todas las capas: instanciar los Services y Views, inyectar dependencias, y mostrar el menú principal de navegación.

---

## Principio SOLID: **SRP**

`LigaUPC` tiene una sola razón para cambiar: si cambia la estructura del menú principal o se agrega un nuevo módulo al sistema. No contiene lógica de negocio ni accede a ningún DAO directamente.

---

## El Scanner compartido

```java
Scanner scanner = new Scanner(System.in);
```

Se crea **una sola instancia** de `Scanner` y se pasa a todas las Views. Esto es crítico:

- `Scanner(System.in)` abre el flujo de entrada estándar
- Si cada View creara su propio `Scanner`, podrían interferir entre sí al leer del mismo buffer
- Al cerrar el programa, `scanner.close()` cierra el flujo correctamente

---

## Orden de instanciación

```java
// 1. Services primero (sin dependencias entre sí en el constructor por defecto)
JugadorService   jugadorService    = new JugadorService();
TecnicoService   tecnicoService    = new TecnicoService();
ArbitroService   arbitroService    = new ArbitroService();
EquipoService    equipoService     = new EquipoService();
PartidoService   partidoService    = new PartidoService();
EstadisticaService estadisticaService = new EstadisticaService();

// 2. Views después, inyectando sus dependencias
JugadorView    jugadorView    = new JugadorView(jugadorService, scanner);
EquipoView     equipoView     = new EquipoView(equipoService, jugadorService, scanner);
PartidoView    partidoView    = new PartidoView(partidoService, equipoService, arbitroService, scanner);
// ...
```

Las Views se instancian después porque necesitan los Services ya creados.

- `EquipoView` recibe `jugadorService` además de `equipoService` porque necesita listar jugadores disponibles antes de agregar o quitar uno del equipo (ayuda de presentación al usuario).
- `PartidoView` recibe tres Services porque necesita buscar `Equipo` y `Arbitro` para construir un `Partido`.

---

## Menú principal

El menú principal es un bucle `do-while` que redirige a la View correspondiente:

```
Liga UPC — Menú Principal
  1 → JugadorView.mostrarMenu()
  2 → TecnicoView.mostrarMenu()
  3 → ArbitroView.mostrarMenu()
  4 → EquipoView.mostrarMenu()
  5 → PartidoView.mostrarMenu()
  6 → EstadisticaView.mostrarMenu()
  0 → salir
```

Cada View tiene su propio bucle interno — el usuario navega dentro de un módulo y vuelve al menú principal con la opción `0`.

---

## Diagrama de ensamblaje completo

```
LigaUPC (main)
    │
    ├── Scanner  ──────────────────────────── compartido con todas las Views
    │
    ├── JugadorService ────────────────────── JugadorDAO → jugadores.txt
    ├── TecnicoService ────────────────────── TecnicoDAO → tecnicos.txt
    ├── ArbitroService ────────────────────── ArbitroDAO → arbitros.txt
    ├── EquipoService  ────────────────────── EquipoDAO  → equipos.txt
    │       └── colabora con JugadorService, TecnicoService
    ├── PartidoService ────────────────────── PartidoDAO    → partidos.txt
    │       ├── colabora con EquipoService, ArbitroService
    │       ├── EstadisticaDAO → estadisticas.txt
    │       └── ReglasPuntuacionEstandar
    └── EstadisticaService ────────────────── EstadisticaDAO → estadisticas.txt
```

---

## Flujo típico de uso

```
1. Registrar técnico      (TecnicoView → TecnicoService → TecnicoDAO → tecnicos.txt)
2. Registrar jugadores    (JugadorView → JugadorService → JugadorDAO → jugadores.txt)
3. Registrar árbitro      (ArbitroView → ArbitroService → ArbitroDAO → arbitros.txt)
4. Registrar equipo       (EquipoView  → EquipoService  → EquipoDAO  → equipos.txt)
5. Asignar técnico        (EquipoView  → EquipoService  → TecnicoService → actualizar equipo)
6. Agregar jugadores      (EquipoView  → EquipoService  → JugadorService → actualizar equipo)
6b. Quitar jugador        (EquipoView  → EquipoService  → Equipo.quitarJugador() → actualizar equipo)
7. Programar partido      (PartidoView → PartidoService → PartidoDAO → partidos.txt)
8. Registrar resultado    (PartidoView → PartidoService → actualizar partido + estadísticas)
9. Ver tabla posiciones   (EstadisticaView → EstadisticaService → estadisticas.txt)
```
