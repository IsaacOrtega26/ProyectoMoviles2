package com.example.arenamovil.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.arenamovil.domain.Distancia
import com.example.arenamovil.domain.Raza
import java.util.Date

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "fecha")
    val fecha: Date,

    // Jugador 1
    @ColumnInfo(name = "jugador1_nombre")
    val jugador1Nombre: String,

    @ColumnInfo(name = "jugador1_raza")
    val jugador1Raza: Raza,

    @ColumnInfo(name = "jugador1_vida_inicial")
    val jugador1VidaInicial: Int,

    @ColumnInfo(name = "jugador1_vida_final")
    val jugador1VidaFinal: Int,

    // Jugador 2
    @ColumnInfo(name = "jugador2_nombre")
    val jugador2Nombre: String,

    @ColumnInfo(name = "jugador2_raza")
    val jugador2Raza: Raza,

    @ColumnInfo(name = "jugador2_vida_inicial")
    val jugador2VidaInicial: Int,

    @ColumnInfo(name = "jugador2_vida_final")
    val jugador2VidaFinal: Int,

    // Información del combate
    @ColumnInfo(name = "turnos_totales")
    val turnosTotales: Int,

    @ColumnInfo(name = "distancia_final")
    val distanciaFinal: Distancia,

    @ColumnInfo(name = "ganador_nombre")
    val ganadorNombre: String?,

    @ColumnInfo(name = "ganador_raza")
    val ganadorRaza: Raza?,

    @ColumnInfo(name = "es_empate")
    val esEmpate: Boolean
)

@Entity(tableName = "jugadores_estadisticas")
data class JugadorEstadisticasEntity(
    @PrimaryKey
    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "raza_preferida")
    val razaPreferida: Raza,

    @ColumnInfo(name = "partidas_jugadas")
    val partidasJugadas: Int,

    @ColumnInfo(name = "partidas_ganadas")
    val partidasGanadas: Int,

    @ColumnInfo(name = "partidas_perdidas")
    val partidasPerdidas: Int,

    @ColumnInfo(name = "empates")
    val empates: Int,

    @ColumnInfo(name = "vida_total_perdida")
    val vidaTotalPerdida: Int,

    @ColumnInfo(name = "vida_total_causada")
    val vidaTotalCausada: Int,

    @ColumnInfo(name = "ultima_partida")
    val ultimaPartida: Date
)