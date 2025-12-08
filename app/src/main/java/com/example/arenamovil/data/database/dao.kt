// data/database/dao.kt
package com.example.arenamovil.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PartidaDao {
    @Insert
    suspend fun insertPartida(partida: PartidaEntity): Long
    
    @Query("SELECT * FROM partidas ORDER BY fecha DESC")
    fun getAllPartidas(): Flow<List<PartidaEntity>>

    // Agregar versiones suspend que devuelvan List directamente:
    @Query("SELECT * FROM partidas ORDER BY fecha DESC")
    suspend fun getAllPartidasList(): List<PartidaEntity>

    @Query("SELECT * FROM partidas WHERE fecha >= :desde ORDER BY fecha DESC")
    suspend fun getPartidasDesdeList(desde: Date): List<PartidaEntity>

    @Query("""
        SELECT * FROM partidas 
        WHERE jugador1_nombre = :nombre OR jugador2_nombre = :nombre 
        ORDER BY fecha DESC
    """)
    suspend fun getPartidasPorJugadorList(nombre: String): List<PartidaEntity>
}

@Dao
interface JugadorEstadisticasDao {
    @Query("SELECT * FROM jugadores_estadisticas ORDER BY partidas_ganadas DESC")
    fun getAllEstadisticas(): Flow<List<JugadorEstadisticasEntity>>

    // Versión List:
    @Query("SELECT * FROM jugadores_estadisticas ORDER BY partidas_ganadas DESC")
    suspend fun getAllEstadisticasList(): List<JugadorEstadisticasEntity>
}