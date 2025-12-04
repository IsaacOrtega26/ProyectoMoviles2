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

    @Query("SELECT * FROM partidas WHERE fecha >= :desde ORDER BY fecha DESC")
    fun getPartidasDesde(desde: Date): Flow<List<PartidaEntity>>

    @Query("SELECT COUNT(*) FROM partidas")
    suspend fun getTotalPartidas(): Int

    @Query("""
        SELECT * FROM partidas 
        WHERE jugador1_nombre = :nombre OR jugador2_nombre = :nombre 
        ORDER BY fecha DESC
    """)
    fun getPartidasPorJugador(nombre: String): Flow<List<PartidaEntity>>

    @Query("DELETE FROM partidas")
    suspend fun deleteAllPartidas()
}

@Dao
interface JugadorEstadisticasDao {
    @Upsert
    suspend fun upsertEstadisticas(estadisticas: JugadorEstadisticasEntity)

    @Query("SELECT * FROM jugadores_estadisticas ORDER BY partidas_ganadas DESC")
    fun getAllEstadisticas(): Flow<List<JugadorEstadisticasEntity>>

    @Query("SELECT * FROM jugadores_estadisticas WHERE nombre = :nombre")
    suspend fun getEstadisticasPorNombre(nombre: String): JugadorEstadisticasEntity?

    @Query("DELETE FROM jugadores_estadisticas")
    suspend fun deleteAllEstadisticas()
}