# TecnicoService — Capa de negocio de Tecnico

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/TecnicoService.java`

---

## ¿Qué es?

Concentra la lógica de negocio de técnicos: validaciones al registrar, coordinación con el DAO, y reglas que definen qué datos son aceptables en el sistema.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo gestiona reglas de negocio de técnicos |
| **SOLID SRP** | Cambia únicamente si cambian las reglas de técnicos |
| **SOLID OCP** | Depende de `ICRUD<Tecnico>`, no de `TecnicoDAO` directamente |

---

## Manejo de errores: `IllegalArgumentException`

El Service lanza excepciones para comunicar errores de negocio. La View es quien captura y decide cómo mostrarlos:

```java
// Service: detecta el problema y lo comunica
public void registrarTecnico(Tecnico tecnico) {
    if (tecnico.getEspecialidad().isBlank())
        throw new IllegalArgumentException("La especialidad del técnico es obligatoria.");
}

// View: captura y presenta al usuario
try {
    service.registrarTecnico(tecnico);
    System.out.println("Técnico registrado exitosamente.");
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

## Inyección de dependencia

```java
public TecnicoService(ICRUD<Tecnico> tecnicoDAO) { this.tecnicoDAO = tecnicoDAO; }
public TecnicoService() { this.tecnicoDAO = new TecnicoDAO(); }
```

---

## Validaciones en `registrarTecnico()`

```
¿tecnico es null?           → IllegalArgumentException
¿nombre está vacío?         → IllegalArgumentException
¿identificación está vacía? → IllegalArgumentException
¿especialidad está vacía?   → IllegalArgumentException  ← validación propia de Tecnico
¿ya existe esa ID?          → IllegalArgumentException
Todo válido                 → dao.crear()
```

La única diferencia con `JugadorService` es el atributo validado: `especialidad` en lugar de `posicion` y `numeroCamiseta`.

---

## Métodos

| Método | Retorno | Lanza excepción si... |
|---|---|---|
| `registrarTecnico(Tecnico)` | `void` | Dato inválido o ID duplicada |
| `listarTecnicos()` | `List<Tecnico>` | — |
| `buscarTecnico(String)` | `Tecnico` | No existe esa identificación |
| `actualizarTecnico(Tecnico)` | `void` | No existe esa identificación |
| `eliminarTecnico(String)` | `void` | No existe esa identificación |

---

## Relación con otras clases

```
TecnicoView   →  TecnicoService  →  ICRUD<Tecnico>  ←  TecnicoDAO
EquipoService →  TecnicoService  (para validar que el técnico exista antes de asignarlo)
```
