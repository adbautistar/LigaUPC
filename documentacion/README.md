# LigaUPC — Documentación del Proyecto

Sistema de gestión de una Liga de Fútbol Universitaria, desarrollado como taller pedagógico para el curso **Programación de Computadores III (SS462)** de la Universidad Popular del Cesar.

---

## Objetivos de aprendizaje

- Aplicar patrones de diseño **GRASP** en un sistema real
- Comprender y aplicar los primeros dos principios **SOLID** (SRP y OCP)
- Implementar una **arquitectura en capas** bien separada

---

## Arquitectura en capas

```
ligaupc/
├── model/      Entidades del dominio (clases de negocio puras)
├── dao/        Persistencia en archivos de texto plano (.txt)
├── service/    Lógica de negocio y reglas del sistema
└── view/       Interfaz de usuario por consola
```

Cada entidad recorre verticalmente todas las capas:

```
Jugador (model) → JugadorDAO (dao) → JugadorService (service) → JugadorView (view)
```

---

## Patrones GRASP aplicados

| Patrón | Definición resumida | Dónde se aplica |
|---|---|---|
| **Expert** | Asigna responsabilidades a quien tiene la información | `JugadorDAO`, `Equipo` |
| **Creator** | Quien usa un objeto es quien lo crea | `Equipo` crea y gestiona `Jugador` |
| **Controller** | Un objeto coordina eventos del sistema | `GestorPartido` |
| **Low Coupling** | Minimizar dependencias entre clases | Interfaces `ICRUD`, `RepositorioPartido` |
| **High Cohesion** | Cada clase tiene un propósito enfocado | Toda la arquitectura en capas |
| **Polymorphism** | Reemplazar condicionales con polimorfismo | `obtenerPerfil()` en `Persona` |
| **Pure Fabrication** | Crear clases artificiales para no violar cohesión | `PartidoDAO`, `ServicioNotificacion` |

---

## Principios SOLID aplicados

| Principio | Definición resumida | Dónde se aplica |
|---|---|---|
| **SRP** — Single Responsibility | Una clase, una razón para cambiar | Separación DAO / Service / View |
| **OCP** — Open/Closed | Abierto para extender, cerrado para modificar | `ICRUD<T>`, `ReglasPuntuacion` |

---

## Índice de documentación

### Capa Model
- [Persona.md](model/Persona.md)
- [Jugador.md](model/Jugador.md)
- [Tecnico.md](model/Tecnico.md)
- [Arbitro.md](model/Arbitro.md)
- [Equipo.md](model/Equipo.md)
- [Partido.md](model/Partido.md)
- [Estadistica.md](model/Estadistica.md)
- [ICRUD.md](model/ICRUD.md)

### Capa DAO
- [JugadorDAO.md](dao/JugadorDAO.md)
- [TecnicoDAO.md](dao/TecnicoDAO.md)
- [ArbitroDAO.md](dao/ArbitroDAO.md)
- [EquipoDAO.md](dao/EquipoDAO.md)
- [PartidoDAO.md](dao/PartidoDAO.md)
- [EstadisticaDAO.md](dao/EstadisticaDAO.md)

### Capa Service
- [JugadorService.md](service/JugadorService.md)
- [TecnicoService.md](service/TecnicoService.md)
- [ArbitroService.md](service/ArbitroService.md)
- [EquipoService.md](service/EquipoService.md)
- [ReglasPuntuacion.md](service/ReglasPuntuacion.md)
- [PartidoService.md](service/PartidoService.md)
- [EstadisticaService.md](service/EstadisticaService.md)

### Capa View
- [JugadorView.md](view/JugadorView.md)
- [TecnicoView.md](view/TecnicoView.md)
- [ArbitroView.md](view/ArbitroView.md)
- [EquipoView.md](view/EquipoView.md)
- [PartidoView.md](view/PartidoView.md)
- [EstadisticaView.md](view/EstadisticaView.md)

### Ensamblaje
- [LigaUPC.md](LigaUPC.md)
