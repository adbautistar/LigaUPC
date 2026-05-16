# ArbitroService — Capa de negocio de Arbitro

**Capa:** Service  
**Archivo:** `src/main/java/ligaupc/service/ArbitroService.java`

---

## ¿Qué es?

Concentra la lógica de negocio de árbitros: validaciones al registrar, coordinación con el DAO, y reglas que definen qué datos son aceptables en el sistema.

---

## Patrones y principios aplicados

| Concepto | Aplicación |
|---|---|
| **GRASP High Cohesion** | Solo gestiona reglas de negocio de árbitros |
| **SOLID SRP** | Cambia únicamente si cambian las reglas de árbitros |
| **SOLID OCP** | Depende de `ICRUD<Arbitro>`, no de `ArbitroDAO` directamente |

---

## Manejo de errores: `IllegalArgumentException`

```java
// Service: detecta el problema
public void registrarArbitro(Arbitro arbitro) {
    if (arbitro.getCategoriaCertificacion().isBlank())
        throw new IllegalArgumentException("La categoría de certificación del árbitro es obligatoria.");
}

// View: captura y presenta
try {
    service.registrarArbitro(arbitro);
    System.out.println("Árbitro registrado exitosamente.");
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

## Inyección de dependencia

```java
public ArbitroService(ICRUD<Arbitro> arbitroDAO) { this.arbitroDAO = arbitroDAO; }
public ArbitroService() { this.arbitroDAO = new ArbitroDAO(); }
```

---

## Validaciones en `registrarArbitro()`

```
¿arbitro es null?                    → IllegalArgumentException
¿nombre está vacío?                  → IllegalArgumentException
¿identificación está vacía?          → IllegalArgumentException
¿categoriaCertificacion está vacía?  → IllegalArgumentException  ← validación propia de Arbitro
¿ya existe esa ID?                   → IllegalArgumentException
Todo válido                          → dao.crear()
```

---

## Métodos

| Método | Retorno | Lanza excepción si... |
|---|---|---|
| `registrarArbitro(Arbitro)` | `void` | Dato inválido o ID duplicada |
| `listarArbitros()` | `List<Arbitro>` | — |
| `buscarArbitro(String)` | `Arbitro` | No existe esa identificación |
| `actualizarArbitro(Arbitro)` | `void` | No existe esa identificación |
| `eliminarArbitro(String)` | `void` | No existe esa identificación |

---

## Los tres Services simples: patrón consolidado

Con `JugadorService`, `TecnicoService` y `ArbitroService` completos, el patrón de la capa Service queda establecido:

```
XService
  ├── private final ICRUD<X> dao       ← contrato, no implementación (OCP)
  ├── registrarX(X)   → void           ← lanza excepción si falla (SRP)
  ├── listarX()       → List<X>        ← sin excepciones, lista vacía es válida
  ├── buscarX(id)     → X              ← lanza excepción si no existe
  ├── actualizarX(X)  → void           ← lanza excepción si no existe
  └── eliminarX(id)   → void           ← lanza excepción si no existe
```

La View siempre envuelve las llamadas al Service en `try-catch`. El Service nunca produce texto para el usuario.

---

## Relación con otras clases

```
ArbitroView    →  ArbitroService  →  ICRUD<Arbitro>  ←  ArbitroDAO
PartidoService →  ArbitroService  (para validar que el árbitro exista antes de asignarlo a un partido)
```
