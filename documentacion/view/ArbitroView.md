# ArbitroView — Vista de consola de Arbitro

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/ArbitroView.java`

---

## ¿Qué es?

Gestiona la interacción con el usuario para todo lo relacionado con árbitros: muestra menús, lee datos del teclado y delega al `ArbitroService`.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo maneja entrada/salida de árbitros |
| **SOLID SRP** | Cambia únicamente si cambia la interfaz de usuario |

---

## Diferencia con JugadorView y TecnicoView

El campo propio es `categoriaCertificacion` (FIFA, Nacional, Regional). Todo lo demás — estructura del menú, try-catch, Scanner — es idéntico.

---

## Las tres Views simples: el patrón completo

Con `JugadorView`, `TecnicoView` y `ArbitroView` implementadas, el patrón de la capa View queda establecido:

```
XView
  ├── private final XService service    ← siempre el Service, no el DAO
  ├── private final Scanner scanner     ← compartido con todas las Views
  ├── mostrarMenu()                     ← bucle do-while con switch
  ├── registrarX()                      ← leer datos → service.registrarX() → try-catch
  ├── listarX()                         ← service.listarX() → imprimir obtenerPerfil()
  ├── buscarX()                         ← service.buscarX() → try-catch
  ├── actualizarX()                     ← verificar existencia → leer nuevos datos
  ├── eliminarX()                       ← service.eliminarX() → try-catch
  └── leerEntero()                      ← privado, maneja NumberFormatException
```

---

## Flujo del menú

```
mostrarMenu()
    ├── 1 → registrarArbitro()   → leer nombre, id, contacto, categoriaCertificacion
    ├── 2 → listarArbitros()     → arbitro.obtenerPerfil() por cada uno
    ├── 3 → buscarArbitro()      → buscar por identificación
    ├── 4 → actualizarArbitro()  → verificar existencia → leer nuevos datos
    ├── 5 → eliminarArbitro()    → eliminar por identificación
    └── 0 → volver
```

---

## Relación con otras clases

```
ArbitroView  →  ArbitroService  →  ICRUD<Arbitro>  ←  ArbitroDAO
             →  Scanner (compartido)
```
