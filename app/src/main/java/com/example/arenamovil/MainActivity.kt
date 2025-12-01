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

        // ---- Jugador 1 ----
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

        // ---- Jugador 2 ----
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
    val razas = listOf(
        Raza.HUMANO,
        Raza.ELFO,
        Raza.ORCO,
        Raza.BESTIA
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        razas.forEach { raza ->
            val seleccionado = raza == razaSeleccionada
            val texto = when (raza) {
                Raza.HUMANO -> "Humano"
                Raza.ELFO -> "Elfo"
                Raza.ORCO -> "Orco"
                Raza.BESTIA -> "Bestia"
            }

            Button(
                onClick = { onRazaSeleccionada(raza) },
                enabled = !seleccionado
            ) {
                Text(texto)
            }
        }
    }
}

/**
 * Pantalla de combate
 */
@Composable
fun BattleScreen(navController: NavHostController) {
    val estado = GameSession.estadoCombate

    if (estado == null) {
        // Si entramos sin configuración previa, volvemos al inicio
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

        // Info jugador 1
        Text("Jugador 1: ${estado.jugador1.nombre} (${estado.jugador1.raza})")
        Text("Vida J1: ${estado.vidaJugador1}")

        // Info jugador 2
        Text("Jugador 2: ${estado.jugador2.nombre} (${estado.jugador2.raza})")
        Text("Vida J2: ${estado.vidaJugador2}")

        Text("Distancia actual: ${estado.distancia.name}")
        Text("Turno: ${if (estado.turnoJugador1) "Jugador 1" else "Jugador 2"}")

        Button(onClick = { /* luego: acción Atacar */ }) {
            Text("Atacar (placeholder)")
        }
        Button(onClick = { /* luego: acción Avanzar */ }) {
            Text("Avanzar (placeholder)")
        }
        Button(onClick = { /* luego: acción Retroceder */ }) {
            Text("Retroceder (placeholder)")
        }
        Button(onClick = { /* luego: acción Curar */ }) {
            Text("Curar (placeholder)")
        }

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