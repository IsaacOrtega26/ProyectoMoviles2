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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de partida",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Aquí irán:\n" +
                    "- Nombre Jugador 1 y raza\n" +
                    "- Nombre Jugador 2 y raza\n" +
                    "- Armas / elementos",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = { navController.navigate(Screen.Battle.route) }) {
            Text("Comenzar combate (placeholder)")
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}

/**
 * Pantalla de combate
 * Más adelante le metemos la lógica real.
 */
@Composable
fun BattleScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Combate",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Aquí se mostrarán:\n" +
                    "- Vida de ambos jugadores\n" +
                    "- Imagen de cada criatura\n" +
                    "- Distancia (cerca / media / lejos)\n" +
                    "- Turno actual",
            style = MaterialTheme.typography.bodyMedium
        )

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

        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text("Terminar combate (volver al inicio)")
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