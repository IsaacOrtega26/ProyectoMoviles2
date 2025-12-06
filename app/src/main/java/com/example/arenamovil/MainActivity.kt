package com.example.arenamovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arenamovil.ui.theme.ArenaMovilTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.arenamovil.domain.Raza
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.arenamovil.domain.GameSession
import com.example.arenamovil.domain.Distancia
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.arenamovil.domain.*
import com.example.arenamovil.data.database.ArenaDatabase
import com.example.arenamovil.data.database.PartidaEntity
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.runtime.LaunchedEffect








class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArenaMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArenaMovilApp()
                }
            }
        }
    }
}

//Rutas para la app
sealed class Screen(val route: String) {
    data object Home : Screen("Inicio")
    data object Setup : Screen("Configuracion")
    data object Battle : Screen("Batalla")
    data object Stats : Screen("Estadísticas")
}

/*Raíz de la app con NavHost, es el contenedor de navegación de Compose
Con el NavHost se lleva el control de en que pantalla estara el usuario
*/
@Composable
fun ArenaMovilApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Setup.route) {
            SetupScreen(navController)
        }
        composable(Screen.Battle.route) {
            BattleScreen(navController)
        }
        composable(Screen.Stats.route) {
            StatsScreen(navController)
        }
    }
}

/**
 * Pantalla de Inicio
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Arena Móvil",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Duelo de criaturas por turnos.\nElige tu raza y lucha hasta el final.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = { navController.navigate(Screen.Setup.route) }) {
            Text("Nueva partida")
        }

        Button(onClick = { navController.navigate(Screen.Stats.route) }) {
            Text("Estadísticas")
        }

        Button(onClick = { /* más adelante: créditos / acerca de */ }) {
            Text("Acerca de")
        }
    }
}

/**
 * Pantalla de configuración de partida
 * (por ahora solo texto y navegación)
 */
@Composable
fun SetupScreen(navController: NavHostController) {
    var nombreJ1 by remember { mutableStateOf("") }
    var nombreJ2 by remember { mutableStateOf("") }

    var razaJ1 by remember { mutableStateOf(Raza.HUMANO) }
    var razaJ2 by remember { mutableStateOf(Raza.HUMANO) }

    val formularioValido = nombreJ1.isNotBlank() && nombreJ2.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de partida",
            style = MaterialTheme.typography.headlineMedium
        )

        //Jugador 1
        Text(
            text = "Jugador 1",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = nombreJ1,
            onValueChange = { nombreJ1 = it },
            label = { Text("Nombre del jugador 1") },
            singleLine = true
        )

        Text(text = "Raza jugador 1")
        RazaSelector(
            razaSeleccionada = razaJ1,
            onRazaSeleccionada = { razaJ1 = it }
        )

        //Jugador 2
        Text(
            text = "Jugador 2",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = nombreJ2,
            onValueChange = { nombreJ2 = it },
            label = { Text("Nombre del jugador 2") },
            singleLine = true
        )

        Text(text = "Raza jugador 2")
        RazaSelector(
            razaSeleccionada = razaJ2,
            onRazaSeleccionada = { razaJ2 = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Inicializamos el combate con los datos del formulario
                GameSession.iniciarCombate(
                    nombreJ1 = nombreJ1,
                    razaJ1 = razaJ1,
                    nombreJ2 = nombreJ2,
                    razaJ2 = razaJ2
                )

                navController.navigate(Screen.Battle.route)
            },
            enabled = formularioValido
        ) {
            Text("Comenzar combate")
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}

@Composable
fun RazaSelector(
    razaSeleccionada: Raza,
    onRazaSeleccionada: (Raza) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila 1: Humano / Elfo
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RazaButton(
                raza = Raza.HUMANO,
                texto = "Humano",
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )

            RazaButton(
                raza = Raza.ELFO,
                texto = "Elfo",
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: Orco / Bestia
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RazaButton(
                raza = Raza.ORCO,
                texto = "Orco",
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )

            RazaButton(
                raza = Raza.BESTIA,
                texto = "Bestia",
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RazaButton(
    raza: Raza,
    texto: String,
    razaSeleccionada: Raza,
    onRazaSeleccionada: (Raza) -> Unit,
    modifier: Modifier = Modifier
) {
    val seleccionado = raza == razaSeleccionada

    Button(
        onClick = { onRazaSeleccionada(raza) },
        enabled = !seleccionado,
        modifier = modifier
    ) {
        Text(texto)
    }
}


/**
 * Pantalla de combate
 */
@Composable
fun BattleScreen(navController: NavHostController) {
    val context = LocalContext.current
    //val scope = rememberCoroutineScope()

    //Lee el ROOM
    val db = remember { ArenaDatabase.getDatabase(context) }
    val partidaDao = remember { db.partidaDao() }

    //Estado del combate
    var estado by remember { mutableStateOf(GameSession.estadoCombate) }
    var guardadoEnBD by remember { mutableStateOf(false) }

    val estadoActual = estado

    if (estadoActual == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay partida configurada.")
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Text("Volver al inicio")
            }
        }
        return
    }

    val hayGanador = estadoActual.vidaJugador1 <= 0 || estadoActual.vidaJugador2 <= 0

    val textoGanador = when {
        estadoActual.vidaJugador1 <= 0 && estadoActual.vidaJugador2 <= 0 ->
            "Empate"
        estadoActual.vidaJugador2 <= 0 ->
            "Ganó ${estadoActual.jugador1.nombre}"
        estadoActual.vidaJugador1 <= 0 ->
            "Ganó ${estadoActual.jugador2.nombre}"
        else -> null
    }

    //Guardar automáticamente la partida cuando haya ganador (solo una vez)
    LaunchedEffect(hayGanador) {
        if (hayGanador && !guardadoEnBD && estadoActual != null) {
            guardadoEnBD = true
            val entidad = crearPartidaDesdeEstado(estadoActual)
            partidaDao.insertPartida(entidad)
        }
    }


    val textoDistancia = when (estadoActual.distancia) {
        Distancia.CERCA -> "Cerca"
        Distancia.MEDIA -> "Media"
        Distancia.LEJOS -> "Lejos"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Combate",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Turno #${estadoActual.turnoActual}")

        Text("Jugador 1: ${estadoActual.jugador1.nombre} (${estadoActual.jugador1.raza})")
        Text("Vida J1: ${estadoActual.vidaJugador1}")

        Text("Jugador 2: ${estadoActual.jugador2.nombre} (${estadoActual.jugador2.raza})")
        Text("Vida J2: ${estadoActual.vidaJugador2}")

        Text("Distancia actual: $textoDistancia")
        Text("Turno de: ${if (estadoActual.turnoJugador1) estadoActual.jugador1.nombre else estadoActual.jugador2.nombre}")

        if (textoGanador != null) {
            Text(
                text = textoGanador,
                style = MaterialTheme.typography.titleLarge
            )

            //Botones al terminar (ya está guardado en BD)
            Button(onClick = {
                GameSession.limpiar()
                navController.navigate(Screen.Home.route)
            }) {
                Text("Volver al inicio")
            }

            Button(onClick = {
                GameSession.limpiar()
                navController.navigate(Screen.Stats.route)
            }) {
                Text("Ver estadísticas")
            }
        }

        //Botones de acción (solo si no hay ganador)
        if (!hayGanador) {
            Button(
                onClick = {
                    GameSession.atacar()?.let { nuevo ->
                        estado = nuevo
                    }
                }
            ) {
                Text("Atacar")
            }

            Button(
                onClick = {
                    GameSession.avanzar()?.let { nuevo ->
                        estado = nuevo
                    }
                }
            ) {
                Text("Avanzar")
            }

            Button(
                onClick = {
                    GameSession.retroceder()?.let { nuevo ->
                        estado = nuevo
                    }
                }
            ) {
                Text("Retroceder")
            }

            Button(
                onClick = {
                    GameSession.curar()?.let { nuevo ->
                        estado = nuevo
                    }
                }
            ) {
                Text("Curar")
            }
        }

        //Botón de terminar combate manual (es una opcion opcional)
        Button(onClick = {
            GameSession.limpiar()
            navController.navigate(Screen.Home.route)
        }) {
            Text("Terminar combate")
        }
    }
}


/**
 * Pantalla de estadísticas
 */
@Composable
fun StatsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Aquí mostraremos:\n" +
                    "- Partidas ganadas / perdidas\n" +
                    "- Raza más usada\n" +
                    "- Últimas partidas",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}

private fun vidaInicialPorRaza(raza: Raza): Int =
    when (raza) {
        Raza.HUMANO -> 100
        Raza.ELFO -> 100
        Raza.ORCO -> 110
        Raza.BESTIA -> 120
    }

private fun crearPartidaDesdeEstado(estado: EstadoCombate): PartidaEntity {
    val vidaInicialJ1 = vidaInicialPorRaza(estado.jugador1.raza)
    val vidaInicialJ2 = vidaInicialPorRaza(estado.jugador2.raza)

    val esEmpate = estado.vidaJugador1 <= 0 && estado.vidaJugador2 <= 0

    val ganadorNombre: String?
    val ganadorRaza: Raza?

    when {
        esEmpate -> {
            ganadorNombre = null
            ganadorRaza = null
        }
        estado.vidaJugador2 <= 0 -> {
            ganadorNombre = estado.jugador1.nombre
            ganadorRaza = estado.jugador1.raza
        }
        else -> {
            ganadorNombre = estado.jugador2.nombre
            ganadorRaza = estado.jugador2.raza
        }
    }

    val turnosTotales = (estado.turnoActual - 1).coerceAtLeast(1)

    return PartidaEntity(
        fecha = Date(),

        jugador1Nombre = estado.jugador1.nombre,
        jugador1Raza = estado.jugador1.raza,
        jugador1VidaInicial = vidaInicialJ1,
        jugador1VidaFinal = estado.vidaJugador1,

        jugador2Nombre = estado.jugador2.nombre,
        jugador2Raza = estado.jugador2.raza,
        jugador2VidaInicial = vidaInicialJ2,
        jugador2VidaFinal = estado.vidaJugador2,

        turnosTotales = turnosTotales,
        distanciaFinal = estado.distancia,
        ganadorNombre = ganadorNombre,
        ganadorRaza = ganadorRaza,
        esEmpate = esEmpate
    )
}



