# ICRUD\<T\> — Interfaz genérica de persistencia

**Capa:** Model  
**Archivo:** `src/main/java/ligaupc/model/ICRUD.java`

---

## ¿Qué es?

Es el contrato que debe cumplir **cualquier clase que persista datos** en el sistema. Define las cinco operaciones básicas de toda persistencia: Crear, Leer, Listar, Actualizar y Eliminar (**CRUD**).

```java
public interface ICRUD<T> {
    boolean crear(T entidad);
    T leerPorId(String id);
    List<T> listarTodos();
    boolean actualizar(T entidad);
    boolean eliminar(String id);
}
```

---

## El parámetro genérico `<T>`

La `T` es un **tipo genérico** — un marcador de posición que se reemplaza por el tipo real al implementar la interfaz:

```java
JugadorDAO implements ICRUD<Jugador>   // T = Jugador
TecnicoDAO implements ICRUD<Tecnico>   // T = Tecnico
ArbitroDAO implements ICRUD<Arbitro>   // T = Arbitro
```

Esto evita duplicar la firma de los métodos en cada DAO. Una sola interfaz sirve para todos.

---

## Principio SOLID: **OCP** — Open/Closed Principle

`ICRUD<T>` es la aplicación más clara del OCP en este proyecto:

```
                    ICRUD<Jugador>
                         ↑
              ┌──────────┴──────────┐
       JugadorDAO             JugadorDAOSQL
      (archivos .txt)        (base de datos)
```

- El `JugadorService` solo conoce `ICRUD<Jugador>` — nunca la implementación concreta.
- Para cambiar de archivos a base de datos: se crea `JugadorDAOSQL`, **sin tocar** `JugadorService`.
- El sistema está **cerrado para modificación** (no se toca lo que funciona) y **abierto para extensión** (se agrega la nueva implementación).

---

## ¿Por qué está en el paquete `model` y no en `dao`?

Porque `ICRUD<T>` es un **contrato del dominio**: define *qué* se puede hacer con los datos, no *cómo*. Los Service (que están por encima del DAO) también necesitan conocer esta interfaz para inyectar dependencias. Si estuviera en `dao`, los Service dependerían del paquete de persistencia — eso aumentaría el acoplamiento.
