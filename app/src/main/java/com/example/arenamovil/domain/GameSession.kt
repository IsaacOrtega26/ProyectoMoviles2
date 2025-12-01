package com.example.arenamovil.domain

object GameSession {

    var estadoCombate: EstadoCombate? = null

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
            jugador2 = jugador2
            // Luego aquí ajustamos vida según raza, etc.
        )
    }

    fun limpiar() {
        estadoCombate = null
    }
}