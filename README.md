## Arena Móvil

- Juego de combate por turnos desarrollado en Android Studio con Kotlin + Jetpack Compose.
El usuario configura dos jugadores, selecciona sus razas y se enfrentan en una arena donde deben atacar, avanzar, retroceder o sanar para ganar el combate.
El juego almacena automáticamente el resultado de cada partida usando Room (SQLite).

## Integrantes              Rol
-Vanessa Amador Jiménez     Programación principal, lógica de combate, UI Compose
-Isaac Ortega Sanchez       Implementación de base de datos Room, DAOs, entidades

## Instalación del APK

# 1 Descarga el archivo ArenaMovil.apk desde la sección Releases del repositorio.
- En tu teléfono Android, activa:
- Ajustes - Seguridad → Permitir instalación de orígenes desconocidos
- Abre el APK y selecciona Instalar.
- Una vez instalada, busca Arena Móvil en tu lista de aplicaciones

- ##Cómo ejecutar el juego
# Requisitos mínimos
- Android 7.0 (API 24) o superior.
- 100 MB de almacenamiento libre.
- 2 GB de RAM.

# Para usar en emulador
- Android Studio - AVD Manager
- Crear un dispositivo con Android 13 recomendado
- 4 GB RAM + x86_64 Image
  
# Para usar en dispositivo físico
- Activar Opciones de desarrollador
- Activar Depuración USB
- Conectar por cable

## Instrucciones de juego
# 1 Pantalla de inicio
- Selecciona Nueva partida o Estadísticas.

# 2 Configuración de jugadores
- Escribe el nombre del Jugador 1 y Jugador 2.
- Selecciona una raza para cada uno:
- Humano - Equilibrado
- Elfo - Preciso, daño variable
- Orco - Mucho daño, menos precisión
- Bestia - Daño alto pero recibe auto-daño
- Presiona Comenzar combate.

Combate por turnos

En cada turno puedes elegir:

## Acción      	Efecto
- Atacar	      Causa daño según la raza y la distancia actual
- Avanzar     	Reduce la distancia (Cerca - Media - Lejos)
- Retroceder	  Aumenta la distancia
- Curar	        Recupera una pequeña cantidad de vida
El turno pasa automáticamente al otro jugador tras cada acción.

## ¿Cómo se gana?
Un jugador gana cuando:
- La vida del oponente llega a 0
- Si ambos llegan a 0 - Empate

## Al finalizar:
- Se guardará la partida automáticamente en la base de datos.
- Podrás ver el historial en Estadísticas.

### Tecnologías utilizadas
- Componente	Tecnología
- IDE	Android Studio Iguana
- Lenguaje	Kotlin
- UI Framework	Jetpack Compose + Material 3
- Navegación	Navigation Compose
- Persistencia	Room (SQLite)
- Corrutinas	Kotlin Coroutines
- Patrones	MVVM simplificado + Domain Logic











