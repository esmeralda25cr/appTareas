# Gestión de Tareas (Android Studio - Java)

App móvil desarrollada como trabajo final para SENATI: gestión de tareas con
creación, edición, eliminación y organización por estado (Pendiente, En
progreso, Completada), usando SQLite como almacenamiento local.

> **Paquete:** `com.senati.apptareas` — Java, Gradle en Kotlin DSL
> (`build.gradle.kts` / `settings.gradle.kts`).

## Cómo abrir el proyecto

1. Abre **Android Studio** (versión reciente, ej. Hedgehog o superior).
2. Selecciona **Open** (o **File > Open**) y elige la carpeta `appTareas`
   completa (la que contiene `settings.gradle.kts` y `build.gradle.kts`).
3. Espera a que Android Studio descargue el Gradle Wrapper y sincronice el
   proyecto (necesita conexión a internet la primera vez).
4. Ejecuta la app en un emulador o dispositivo con **Android 7.0 (API 24)** o
   superior.

## Pantallas

- **Login**: usuarios fijos Jazmin, Carlos y Ulises, contraseña `1234` para todos.
- **Inicio**: tarjetas de tareas pendientes/completadas, botón para crear
  tarea nueva y lista de las últimas tareas registradas.
- **Nueva Tarea / Editar Tarea**: título, descripción, estado, fecha de
  vencimiento (selector de calendario) y usuario asignado. La fecha de
  creación se guarda automáticamente.
- **Historial**: lista completa de tareas con filtro por estado. Mantén
  presionada una tarea para editarla, marcarla como completada o eliminarla.
- **Ajustes**: datos del operario y versión de la app.

## Base de datos

SQLite local (`tareas.db`) con una sola tabla `tareas`
(`id, titulo, descripcion, estado, fecha_vencimiento, fecha_creacion,
usuario_asignado`), manejada por `DatabaseHelper`.
