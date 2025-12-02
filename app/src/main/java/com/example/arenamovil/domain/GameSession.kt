package com.example.arenamovil.domain

import kotlin.math.max
import kotlin.math.min


object GameSession {

    var estadoCombate: EstadoCombate? = null
        private set

    private fun vidaInicial(raza: Raza): Int =
        when (raza) {
            Raza.HUMANO -> 100
            Raza.ELFO -> 100
            Raza.ORCO -> 110
            Raza.BESTIA -> 120
        }

    fun iniciarCombate(
        nombreJ1: String,
        razaJ1: Raza,
        nombreJ2: String,
        razaJ2: Raza
    ) {
        val jugador1 = JugadorConfig(nombre = nombreJ1, raza = razaJ1)
        val jugador2 = JugadorConfig(nombre = nombreJ2, raza = razaJ2)

        estadoCombate = EstadoCombate(
            jugador1 = jugador1,
            jugador2 = jugador2,
            vidaJugador1 = vidaInicial(razaJ1),
            vidaJugador2 = vidaInicial(razaJ2),
            distancia = Distancia.MEDIA,
            turnoJugador1 = true,
            turnoActual = 1
        )
    }

    fun limpiar() {
        estadoCombate = null
    }

    /** Atacar */
    fun atacar(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        val atacanteEsJ1 = actual.turnoJugador1
        val razaAtacante = if (atacanteEsJ1) {
            actual.jugador1.raza
        } else {
            actual.jugador2.raza
        }

        val base = when (razaAtacante) {
            Raza.HUMANO -> 10
            Raza.ELFO -> 12
            Raza.ORCO -> 14
            Raza.BESTIA -> 18
        }

        val factor = when (actual.distancia) {
            Distancia.CERCA -> 100
            Distancia.MEDIA -> 80
            Distancia.LEJOS -> 60
        }

        val danio = base * factor / 100
        val autoDanio = if (razaAtacante == Raza.BESTIA) 5 else 0

        var nuevaVidaJ1 = actual.vidaJugador1
        var nuevaVidaJ2 = actual.vidaJugador2

        if (atacanteEsJ1) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - danio)
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - autoDanio)
        } else {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - danio)
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - autoDanio)
        }

        val nuevoEstado = actual.copy(
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    /** Avanzar: se acerca (distancia -1) y pasa turno */
    fun avanzar(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        val nuevoNivel = max(1, actual.distancia.nivel - 1)
        val nuevaDistancia = when (nuevoNivel) {
            1 -> Distancia.CERCA
            2 -> Distancia.MEDIA
            else -> Distancia.LEJOS
        }

        val nuevoEstado = actual.copy(
            distancia = nuevaDistancia,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    /** Retroceder: se aleja (distancia +1) y pasa turno */
    fun retroceder(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        val nuevoNivel = min(3, actual.distancia.nivel + 1)
        val nuevaDistancia = when (nuevoNivel) {
            1 -> Distancia.CERCA
            2 -> Distancia.MEDIA
            else -> Distancia.LEJOS
        }

        val nuevoEstado = actual.copy(
            distancia = nuevaDistancia,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    /** Curar: recupera algo de vida y pasa turno */
    fun curar(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        val atacanteEsJ1 = actual.turnoJugador1

        val maxVidaJ1 = vidaInicial(actual.jugador1.raza)
        val maxVidaJ2 = vidaInicial(actual.jugador2.raza)

        val curacion = 10

        val nuevaVidaJ1 = if (atacanteEsJ1) {
            min(maxVidaJ1, actual.vidaJugador1 + curacion)
        } else {
            actual.vidaJugador1
        }

        val nuevaVidaJ2 = if (!atacanteEsJ1) {
            min(maxVidaJ2, actual.vidaJugador2 + curacion)
        } else {
            actual.vidaJugador2
        }

        val nuevoEstado = actual.copy(
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }
}
