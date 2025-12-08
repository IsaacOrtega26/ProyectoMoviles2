package com.example.arenamovil.domain

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


object GameSession {

    var estadoCombate: EstadoCombate? = null
        private set

    private fun vidaInicial(raza: Raza, elementoElfo: ElementoElfo? = null): Int =
        when (raza) {
            Raza.HUMANO -> 100
            Raza.ELFO -> if (elementoElfo == ElementoElfo.AGUA) 115 else 100
            Raza.ORCO -> 110
            Raza.BESTIA -> 120
        }

    fun iniciarCombate(
        nombreJ1: String,
        razaJ1: Raza,
        nombreJ2: String,
        razaJ2: Raza,
        armaJ1: Any? = null,
        armaJ2: Any? = null
    ) {
        val jugador1 = when (razaJ1) {
            Raza.HUMANO -> JugadorConfig(nombre = nombreJ1, raza = razaJ1, armaHumano = armaJ1 as? ArmaHumano ?: ArmaHumano.ESCOPETA)
            Raza.ELFO -> JugadorConfig(nombre = nombreJ1, raza = razaJ1, elementoElfo = armaJ1 as? ElementoElfo ?: ElementoElfo.FUEGO)
            Raza.ORCO -> JugadorConfig(nombre = nombreJ1, raza = razaJ1, armaOrco = armaJ1 as? ArmaOrco ?: ArmaOrco.HACHA)
            Raza.BESTIA -> JugadorConfig(nombre = nombreJ1, raza = razaJ1, ataqueBestia = armaJ1 as? AtaqueBestia ?: AtaqueBestia.ESPADA)
        }
        
        val jugador2 = when (razaJ2) {
            Raza.HUMANO -> JugadorConfig(nombre = nombreJ2, raza = razaJ2, armaHumano = armaJ2 as? ArmaHumano ?: ArmaHumano.ESCOPETA)
            Raza.ELFO -> JugadorConfig(nombre = nombreJ2, raza = razaJ2, elementoElfo = armaJ2 as? ElementoElfo ?: ElementoElfo.FUEGO)
            Raza.ORCO -> JugadorConfig(nombre = nombreJ2, raza = razaJ2, armaOrco = armaJ2 as? ArmaOrco ?: ArmaOrco.HACHA)
            Raza.BESTIA -> JugadorConfig(nombre = nombreJ2, raza = razaJ2, ataqueBestia = armaJ2 as? AtaqueBestia ?: AtaqueBestia.ESPADA)
        }

        estadoCombate = EstadoCombate(
            jugador1 = jugador1,
            jugador2 = jugador2,
            vidaJugador1 = vidaInicial(razaJ1, jugador1.elementoElfo),
            vidaJugador2 = vidaInicial(razaJ2, jugador2.elementoElfo),
            distancia = Distancia.MEDIA,
            turnoJugador1 = true,
            turnoActual = 1
        )
    }

    fun limpiar() {
        estadoCombate = null
    }

    /** Atacar con arma específica */
    fun atacarConArma(arma: Any?): EstadoCombate? {
        val actual = estadoCombate ?: return null
        val atacanteEsJ1 = actual.turnoJugador1
        val razaAtacante = if (atacanteEsJ1) actual.jugador1.raza else actual.jugador2.raza
        val jugadorAtacante = if (atacanteEsJ1) actual.jugador1 else actual.jugador2

        val danio = calcularDanio(razaAtacante, jugadorAtacante, actual.distancia, arma)
        val autoDanio = if (razaAtacante == Raza.BESTIA && arma == AtaqueBestia.PUÑOS) 10 else 0
        val aplicarSangrado = razaAtacante == Raza.ORCO && arma == ArmaOrco.HACHA

        var nuevaVidaJ1 = actual.vidaJugador1
        var nuevaVidaJ2 = actual.vidaJugador2
        var nuevoSangradoJ1 = max(0, actual.sangradoJ1 - 1)
        var nuevoSangradoJ2 = max(0, actual.sangradoJ2 - 1)

        // Aplicar sangrado pendiente
        if (actual.sangradoJ1 > 0) {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - 3)
        }
        if (actual.sangradoJ2 > 0) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - 3)
        }

        // Aplicar curación pendiente (Orcos)
        if (actual.curacionPendienteJ1 > 0) {
            val maxVida = vidaInicial(actual.jugador1.raza, actual.jugador1.elementoElfo)
            nuevaVidaJ1 = min(maxVida, nuevaVidaJ1 + actual.curacionPendienteJ1)
        }
        if (actual.curacionPendienteJ2 > 0) {
            val maxVida = vidaInicial(actual.jugador2.raza, actual.jugador2.elementoElfo)
            nuevaVidaJ2 = min(maxVida, nuevaVidaJ2 + actual.curacionPendienteJ2)
        }

        if (atacanteEsJ1) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - danio)
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - autoDanio)
            if (aplicarSangrado) nuevoSangradoJ2 = 2
        } else {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - danio)
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - autoDanio)
            if (aplicarSangrado) nuevoSangradoJ1 = 2
        }

        val nuevoEstado = actual.copy(
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1,
            sangradoJ1 = nuevoSangradoJ1,
            sangradoJ2 = nuevoSangradoJ2,
            curacionPendienteJ1 = 0,
            curacionPendienteJ2 = 0
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    private fun calcularDanio(raza: Raza, jugador: JugadorConfig, distancia: Distancia, arma: Any?): Int {
        return when (raza) {
            Raza.HUMANO -> {
                when (arma as? ArmaHumano) {
                    ArmaHumano.ESCOPETA -> {
                        val base = Random.nextInt(1, 6)
                        val extra = Random.nextInt(0, 3)
                        val factor = when (distancia) {
                            Distancia.CERCA -> 120
                            Distancia.MEDIA -> 100
                            Distancia.LEJOS -> 60
                        }
                        (base + extra) * factor / 100
                    }
                    ArmaHumano.RIFLE_FRANCOTIRADOR -> {
                        val base = Random.nextInt(1, 6)
                        val factor = when (distancia) {
                            Distancia.CERCA -> 60
                            Distancia.MEDIA -> 100
                            Distancia.LEJOS -> 150
                        }
                        base * factor / 100
                    }
                    else -> Random.nextInt(1, 6)
                }
            }
            Raza.ELFO -> {
                val base = Random.nextInt(1, 6)
                when (jugador.elementoElfo) {
                    ElementoElfo.FUEGO -> (base * 1.2).toInt()
                    ElementoElfo.TIERRA -> (base * 1.1).toInt()
                    ElementoElfo.AIRE -> base
                    ElementoElfo.AGUA -> base
                    else -> base
                }
            }
            Raza.ORCO -> {
                when (arma as? ArmaOrco) {
                    ArmaOrco.HACHA -> Random.nextInt(1, 6)
                    ArmaOrco.MARTILLO -> Random.nextInt(2, 8)
                    else -> Random.nextInt(1, 6)
                }
            }
            Raza.BESTIA -> {
                when (arma as? AtaqueBestia) {
                    AtaqueBestia.PUÑOS -> Random.nextInt(20, 31)
                    AtaqueBestia.ESPADA -> Random.nextInt(1, 11)
                    else -> Random.nextInt(1, 11)
                }
            }
        }
    }

    /** Avanzar: se acerca (distancia -1) y pasa turno */
    fun avanzar(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        // Aplicar efectos pendientes
        var nuevaVidaJ1 = actual.vidaJugador1
        var nuevaVidaJ2 = actual.vidaJugador2
        var nuevoSangradoJ1 = max(0, actual.sangradoJ1 - 1)
        var nuevoSangradoJ2 = max(0, actual.sangradoJ2 - 1)

        if (actual.sangradoJ1 > 0) {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - 3)
        }
        if (actual.sangradoJ2 > 0) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - 3)
        }

        if (actual.curacionPendienteJ1 > 0) {
            val maxVida = vidaInicial(actual.jugador1.raza, actual.jugador1.elementoElfo)
            nuevaVidaJ1 = min(maxVida, nuevaVidaJ1 + actual.curacionPendienteJ1)
        }
        if (actual.curacionPendienteJ2 > 0) {
            val maxVida = vidaInicial(actual.jugador2.raza, actual.jugador2.elementoElfo)
            nuevaVidaJ2 = min(maxVida, nuevaVidaJ2 + actual.curacionPendienteJ2)
        }

        val nuevoNivel = max(1, actual.distancia.nivel - 1)
        val nuevaDistancia = when (nuevoNivel) {
            1 -> Distancia.CERCA
            2 -> Distancia.MEDIA
            else -> Distancia.LEJOS
        }

        val nuevoEstado = actual.copy(
            distancia = nuevaDistancia,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1,
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            sangradoJ1 = nuevoSangradoJ1,
            sangradoJ2 = nuevoSangradoJ2,
            curacionPendienteJ1 = 0,
            curacionPendienteJ2 = 0
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    /** Retroceder: se aleja (distancia +1) y pasa turno */
    fun retroceder(): EstadoCombate? {
        val actual = estadoCombate ?: return null

        // Aplicar efectos pendientes
        var nuevaVidaJ1 = actual.vidaJugador1
        var nuevaVidaJ2 = actual.vidaJugador2
        var nuevoSangradoJ1 = max(0, actual.sangradoJ1 - 1)
        var nuevoSangradoJ2 = max(0, actual.sangradoJ2 - 1)

        if (actual.sangradoJ1 > 0) {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - 3)
        }
        if (actual.sangradoJ2 > 0) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - 3)
        }

        if (actual.curacionPendienteJ1 > 0) {
            val maxVida = vidaInicial(actual.jugador1.raza, actual.jugador1.elementoElfo)
            nuevaVidaJ1 = min(maxVida, nuevaVidaJ1 + actual.curacionPendienteJ1)
        }
        if (actual.curacionPendienteJ2 > 0) {
            val maxVida = vidaInicial(actual.jugador2.raza, actual.jugador2.elementoElfo)
            nuevaVidaJ2 = min(maxVida, nuevaVidaJ2 + actual.curacionPendienteJ2)
        }

        val nuevoNivel = min(3, actual.distancia.nivel + 1)
        val nuevaDistancia = when (nuevoNivel) {
            1 -> Distancia.CERCA
            2 -> Distancia.MEDIA
            else -> Distancia.LEJOS
        }

        val nuevoEstado = actual.copy(
            distancia = nuevaDistancia,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1,
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            sangradoJ1 = nuevoSangradoJ1,
            sangradoJ2 = nuevoSangradoJ2,
            curacionPendienteJ1 = 0,
            curacionPendienteJ2 = 0
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    /** Curar: recupera vida según la raza y pasa turno */
    fun curar(): EstadoCombate? {
        val actual = estadoCombate ?: return null
        val atacanteEsJ1 = actual.turnoJugador1
        val razaAtacante = if (atacanteEsJ1) actual.jugador1.raza else actual.jugador2.raza
        val jugadorAtacante = if (atacanteEsJ1) actual.jugador1 else actual.jugador2

        val maxVidaJ1 = vidaInicial(actual.jugador1.raza, actual.jugador1.elementoElfo)
        val maxVidaJ2 = vidaInicial(actual.jugador2.raza, actual.jugador2.elementoElfo)

        // Aplicar efectos pendientes primero
        var nuevaVidaJ1 = actual.vidaJugador1
        var nuevaVidaJ2 = actual.vidaJugador2
        var nuevoSangradoJ1 = max(0, actual.sangradoJ1 - 1)
        var nuevoSangradoJ2 = max(0, actual.sangradoJ2 - 1)
        var nuevaCuracionPendienteJ1 = 0
        var nuevaCuracionPendienteJ2 = 0

        if (actual.sangradoJ1 > 0) {
            nuevaVidaJ1 = max(0, nuevaVidaJ1 - 3)
        }
        if (actual.sangradoJ2 > 0) {
            nuevaVidaJ2 = max(0, nuevaVidaJ2 - 3)
        }

        if (actual.curacionPendienteJ1 > 0) {
            nuevaVidaJ1 = min(maxVidaJ1, nuevaVidaJ1 + actual.curacionPendienteJ1)
        }
        if (actual.curacionPendienteJ2 > 0) {
            nuevaVidaJ2 = min(maxVidaJ2, nuevaVidaJ2 + actual.curacionPendienteJ2)
        }

        // Calcular curación según raza
        val vidaPerdida = if (atacanteEsJ1) {
            maxVidaJ1 - nuevaVidaJ1
        } else {
            maxVidaJ2 - nuevaVidaJ2
        }

        when (razaAtacante) {
            Raza.HUMANO -> {
                // 40-50% de la vida perdida
                val porcentaje = Random.nextInt(40, 51)
                val curacion = (vidaPerdida * porcentaje / 100)
                if (atacanteEsJ1) {
                    nuevaVidaJ1 = min(maxVidaJ1, nuevaVidaJ1 + curacion)
                } else {
                    nuevaVidaJ2 = min(maxVidaJ2, nuevaVidaJ2 + curacion)
                }
            }
            Raza.ELFO -> {
                val porcentaje = if (jugadorAtacante.elementoElfo == ElementoElfo.AGUA) {
                    Random.nextInt(75, 91) // 75-90%
                } else {
                    65 // 65%
                }
                val curacion = (vidaPerdida * porcentaje / 100)
                if (atacanteEsJ1) {
                    nuevaVidaJ1 = min(maxVidaJ1, nuevaVidaJ1 + curacion)
                } else {
                    nuevaVidaJ2 = min(maxVidaJ2, nuevaVidaJ2 + curacion)
                }
            }
            Raza.ORCO -> {
                // 25-45% inicial + 5-25% en siguiente turno
                val porcentajeInicial = Random.nextInt(25, 46)
                val curacionInicial = (vidaPerdida * porcentajeInicial / 100)
                val porcentajeExtra = Random.nextInt(5, 26)
                val curacionExtra = (vidaPerdida * porcentajeExtra / 100)
                
                if (atacanteEsJ1) {
                    nuevaVidaJ1 = min(maxVidaJ1, nuevaVidaJ1 + curacionInicial)
                    nuevaCuracionPendienteJ1 = curacionExtra
                } else {
                    nuevaVidaJ2 = min(maxVidaJ2, nuevaVidaJ2 + curacionInicial)
                    nuevaCuracionPendienteJ2 = curacionExtra
                }
            }
            Raza.BESTIA -> {
                // 50% de la vida perdida
                val curacion = vidaPerdida / 2
                if (atacanteEsJ1) {
                    nuevaVidaJ1 = min(maxVidaJ1, nuevaVidaJ1 + curacion)
                } else {
                    nuevaVidaJ2 = min(maxVidaJ2, nuevaVidaJ2 + curacion)
                }
            }
        }

        val nuevoEstado = actual.copy(
            vidaJugador1 = nuevaVidaJ1,
            vidaJugador2 = nuevaVidaJ2,
            turnoJugador1 = !actual.turnoJugador1,
            turnoActual = actual.turnoActual + 1,
            sangradoJ1 = nuevoSangradoJ1,
            sangradoJ2 = nuevoSangradoJ2,
            curacionPendienteJ1 = nuevaCuracionPendienteJ1,
            curacionPendienteJ2 = nuevaCuracionPendienteJ2
        )

        estadoCombate = nuevoEstado
        return nuevoEstado
    }

    // Mantener compatibilidad con código existente
    fun atacar(): EstadoCombate? {
        val actual = estadoCombate ?: return null
        val atacanteEsJ1 = actual.turnoJugador1
        val jugadorAtacante = if (atacanteEsJ1) actual.jugador1 else actual.jugador2
        
        val arma = when (jugadorAtacante.raza) {
            Raza.HUMANO -> jugadorAtacante.armaHumano
            Raza.ELFO -> jugadorAtacante.elementoElfo
            Raza.ORCO -> jugadorAtacante.armaOrco
            Raza.BESTIA -> jugadorAtacante.ataqueBestia
        }
        
        return atacarConArma(arma)
    }
}
