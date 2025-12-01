# Modelo de datos – Arena Móvil

## Entidad Partida
Estructura básica para guardar el historial de todos los combates:

- `id` (entero, autoincremental)
- `jugador1Nombre` (texto)
- `jugador2Nombre` (texto)
- `razaJugador1` (texto)
- `razaJugador2` (texto)
- `ganadorNombre` (texto)
- `turnosTotales` (entero)
- `fecha` (texto o datetime)

---

## Entidad Jugador (opcional)
Para estadísticas generales por jugador:

- `id`
- `nombre`
- `partidasGanadas`
- `partidasPerdidas`
- `partidasEmpatadas` (es algo opcional)

---

## Notas de implementación

- Se recomienda usar **Room (SQLite)** para persistencia.
- Se debe almacenar mínimo la entidad `Partida` para cumplir con el requisito del historial.
