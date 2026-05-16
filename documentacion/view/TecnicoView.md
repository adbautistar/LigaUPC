# TecnicoView — Vista de consola de Tecnico

**Capa:** View  
**Archivo:** `src/main/java/ligaupc/view/TecnicoView.java`

---

## ¿Qué es?

Gestiona la interacción con el usuario para todo lo relacionado con técnicos: muestra menús, lee datos del teclado y delega al `TecnicoService`.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo maneja entrada/salida de técnicos |
| **SOLID SRP** | Cambia únicamente si cambia la interfaz de usuario |

---

## Diferencia con JugadorView

La única diferencia real respecto a `JugadorView` está en:
- El campo leído: `especialidad` en lugar de `posicion` y `numeroCamiseta`
- El tipo del objeto construido: `Tecnico` en lugar de `Jugador`
- El Service llamado: `TecnicoService` en lugar de `JugadorService`

El patrón de menú, try-catch, y manejo de Scanner es idéntico. Esto es el resultado de una arquitectura consistente: **predecible y fácil de mantener**.

---

## Flujo del menú

```
mostrarMenu()
    ├── 1 → registrarTecnico()   → leer nombre, id, contacto, especialidad
    ├── 2 → listarTecnicos()     → tecnico.obtenerPerfil() por cada uno
    ├── 3 → buscarTecnico()      → buscar por identificación
    ├── 4 → actualizarTecnico()  → verificar existencia → leer nuevos datos
    ├── 5 → eliminarTecnico()    → eliminar por identificación
    └── 0 → volver
```

---

## Relación con otras clases

```
TecnicoView  →  TecnicoService  →  ICRUD<Tecnico>  ←  TecnicoDAO
             →  Scanner (compartido)
```
