// data/database/ArenaDatabase.kt
package com.example.arenamovil.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.arenamovil.domain.Raza
import com.example.arenamovil.domain.Distancia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(
    entities = [PartidaEntity::class, JugadorEstadisticasEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArenaDatabase : RoomDatabase() {
    abstract fun partidaDao(): PartidaDao
    abstract fun jugadorEstadisticasDao(): JugadorEstadisticasDao

    companion object {
        @Volatile
        private var Instance: ArenaDatabase? = null

        fun getDatabase(context: Context): ArenaDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ArenaDatabase::class.java,
                    "arena_database"
                )
                    .addCallback(DatabaseCallback)
                    .build()
                    .also { Instance = it }
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Puedes agregar datos iniciales aquí si es necesario
        }// data/database/ArenaDatabase.kt
        package com.example.arenamovil.data.database

        import android.content.Context
        import androidx.room.*
        import androidx.sqlite.db.SupportSQLiteDatabase
        import com.example.arenamovil.domain.Raza
        import com.example.arenamovil.domain.Distancia
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.launch
        import java.util.*

        @Database(
            entities = [PartidaEntity::class, JugadorEstadisticasEntity::class],
            version = 1,
            exportSchema = false
        )
        @TypeConverters(Converters::class)
        abstract class ArenaDatabase : RoomDatabase() {
            abstract fun partidaDao(): PartidaDao
            abstract fun jugadorEstadisticasDao(): JugadorEstadisticasDao

            companion object {
                @Volatile
                private var Instance: ArenaDatabase? = null

                fun getDatabase(context: Context): ArenaDatabase {
                    return Instance ?: synchronized(this) {
                        Room.databaseBuilder(
                            context,
                            ArenaDatabase::class.java,
                            "arena_database"
                        )
                            .addCallback(DatabaseCallback)
                            .build()
                            .also { Instance = it }
                    }
                }
            }

            private class DatabaseCallback : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Puedes agregar datos iniciales aquí si es necesario
                }
            }
        }

        // Converters para tipos personalizados
        class Converters {
            @TypeConverter
            fun fromRaza(raza: Raza): String = raza.name

            @TypeConverter
            fun toRaza(razaString: String): Raza = Raza.valueOf(razaString)

            @TypeConverter
            fun fromDistancia(distancia: Distancia): String = distancia.name

            @TypeConverter
            fun toDistancia(distanciaString: String): Distancia = Distancia.valueOf(distanciaString)

            @TypeConverter
            fun fromDate(date: Date): Long = date.time

            @TypeConverters
            fun toDate(timestamp: Long): Date = Date(timestamp)
        }
    }
}

// Converters para tipos personalizados
class Converters {
    @TypeConverter
    fun fromRaza(raza: Raza): String = raza.name

    @TypeConverter
    fun toRaza(razaString: String): Raza = Raza.valueOf(razaString)

    @TypeConverter
    fun fromDistancia(distancia: Distancia): String = distancia.name

    @TypeConverter
    fun toDistancia(distanciaString: String): Distancia = Distancia.valueOf(distanciaString)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)
}