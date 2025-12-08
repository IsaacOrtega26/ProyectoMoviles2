package com.example.arenamovil.domain

//Razas disponibles que tendra el juego
enum class Raza {
    HUMANO,
    ELFO,
    ORCO,
    BESTIA
}

//Distancia entre los jugadores (1 = cerca,2 = Media, 3 = lejos)
enum class Distancia(val nivel: Int) {
    CERCA(1),
    MEDIA(2),
    LEJOS(3)
}

// Armas para Humanos
enum class ArmaHumano {
    ESCOPETA,
    RIFLE_FRANCOTIRADOR
}

// Elementos mágicos para Elfos
enum class ElementoElfo {
    FUEGO,
    TIERRA,
    AIRE,
    AGUA
}

// Armas para Orcos
enum class ArmaOrco {
    HACHA,
    MARTILLO
}

// Ataques para Bestias
enum class AtaqueBestia {
    PUÑOS,
    ESPADA
}

//Configuración de un jugador antes del combate (dependiendo de la raza seran las habilidades)
data class JugadorConfig(
    val nombre: String,
    val raza: Raza,
    val armaHumano: ArmaHumano? = null,
    val elementoElfo: ElementoElfo? = null,
    val armaOrco: ArmaOrco? = null,
    val ataqueBestia: AtaqueBestia? = null
)

//Estado completo del combate (se usara en la pantalla del combate)
data class EstadoCombate(
    val jugador1: JugadorConfig,
    val jugador2: JugadorConfig,
    val vidaJugador1: Int = 100,
    val vidaJugador2: Int = 100,
    val distancia: Distancia = Distancia.MEDIA,
    val turnoJugador1: Boolean = true,
    val turnoActual: Int = 1,
    val sangradoJ1: Int = 0, // Turnos restantes de sangrado
    val sangradoJ2: Int = 0,
    val curacionPendienteJ1: Int = 0, // Curación pendiente para el siguiente turno (Orcos)
    val curacionPendienteJ2: Int = 0
)