# Fase 1 — GitHub Setup

## Objetivo

Preparar el repositorio para control de versiones profesional: inicializar Git, configurar los archivos ignorados, corregir la version del compilador y subir el proyecto a GitHub.

## Rama

`feature/fase1-github-setup`

## Tareas ejecutadas

| # | Commit | Descripcion |
|---|---|---|
| 1.1 | `chore(config): add .gitignore for NetBeans Maven Java` | Excluye carpetas generadas (target/, build/), archivos de IDE, datos de ejecucion (.txt) y carpeta .claude/ |
| 1.2 | `chore(config): fix compiler release from 25 to 17 in pom.xml` | Corrige la version del compilador Maven a Java 17 LTS |
| 1.3 | `docs(fase1): add fase1-github-setup phase documentation` | Este archivo |

## Archivos creados / modificados

| Archivo | Accion | Descripcion |
|---|---|---|
| `.gitignore` | Creado | Reglas de exclusion para NetBeans, Maven, Java, OS y datos de ejecucion |
| `pom.xml` | Modificado | `maven.compiler.release`: `25` → `17` |
| `documentacion/fases/fase1-github-setup.md` | Creado | Este documento |

## Verificacion

- `mvn compile` ejecutado antes del commit 1.2 → `BUILD SUCCESS` con `javac [debug release 17]`
- Repositorio remoto: https://github.com/adbautistar/LigaUPC.git

## Por que Java 17

Java 17 es la version LTS (Long Term Support) activa. El valor `25` en el `pom.xml` original apuntaba a una version que no existe como LTS, causando posibles errores de compilacion en entornos distintos al local.

## Patrones aplicados

Ninguno en esta fase — es configuracion pura de infraestructura. La base sobre la que se construira la migracion a JavaFX.
