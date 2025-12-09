package com.example.arenamovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arenamovil.data.database.ArenaDatabase
import com.example.arenamovil.data.database.PartidaEntity
import com.example.arenamovil.domain.*
import com.example.arenamovil.ui.theme.ArenaMovilTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

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

// Rutas para la app
sealed class Screen(val route: String) {
    data object Home : Screen("Inicio")
    data object Setup : Screen("Configuracion")
    data object Battle : Screen("Batalla")
    data object Stats : Screen("Estadísticas")
    data object Historial : Screen("Historial")
}

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
        composable(Screen.Historial.route) {
            HistorialScreen(navController)
        }
    }
}

// ============ RECURSOS DE IMÁGENES PARA PERSONAJES ============
object ImageResources {

    @Composable
    fun getRazaIcon(raza: Raza): ImageVector {
        return when (raza) {
            Raza.HUMANO -> Icons.Default.Person
            Raza.ELFO -> Icons.Default.Person
            Raza.ORCO -> Icons.Default.Warning
            Raza.BESTIA -> Icons.Default.Star
        }
    }

    @Composable
    fun getRazaColor(raza: Raza): Color {
        return when (raza) {
            Raza.HUMANO -> Color(0xFF4CAF50)  // Verde
            Raza.ELFO -> Color(0xFF2196F3)    // Azul
            Raza.ORCO -> Color(0xFFF44336)    // Rojo
            Raza.BESTIA -> Color(0xFF9C27B0)  // Púrpura
        }
    }

    // Íconos para estados
    val vidaIcon: ImageVector = Icons.Default.Favorite
    val turnoIcon: ImageVector = Icons.Default.Refresh
    val distanciaIcon: ImageVector = Icons.Default.LocationOn
}

// ============ COMPONENTES REUTILIZABLES ============
@Composable
fun PersonajeAvatar(
    raza: Raza,
    nombre: String,
    vida: Int,
    esTurno: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (esTurno)
                MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = if (esTurno) CardDefaults.cardElevation(8.dp)
        else CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Imagen del personaje
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        ImageResources.getRazaColor(raza).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageResources.getRazaIcon(raza),
                    contentDescription = "Personaje $nombre",
                    modifier = Modifier.size(40.dp),
                    tint = ImageResources.getRazaColor(raza)
                )
            }

            // Nombre del personaje
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Raza
            Text(
                text = raza.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = ImageResources.getRazaColor(raza)
            )

            // Barra de vida
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = ImageResources.vidaIcon,
                        contentDescription = "Vida",
                        modifier = Modifier.size(16.dp),
                        tint = when {
                            vida > 70 -> Color.Green
                            vida > 30 -> Color.Yellow
                            else -> Color.Red
                        }
                    )
                    Text(
                        text = "$vida HP",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                LinearProgressIndicator(
                    progress = vida / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = when {
                        vida > 70 -> Color.Green
                        vida > 30 -> Color.Yellow
                        else -> Color.Red
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Indicador de turno
            if (esTurno) {
                Text(
                    text = "⚔️ TURNO ACTUAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RazaCard(
    raza: Raza,
    razaSeleccionada: Raza,
    onRazaSeleccionada: (Raza) -> Unit,
    modifier: Modifier = Modifier
) {
    val seleccionado = raza == razaSeleccionada
    val color = ImageResources.getRazaColor(raza)
    val icono = ImageResources.getRazaIcon(raza)
    val nombre = when (raza) {
        Raza.HUMANO -> "Humano"
        Raza.ELFO -> "Elfo"
        Raza.ORCO -> "Orco"
        Raza.BESTIA -> "Bestia"
    }

    Card(
        onClick = { onRazaSeleccionada(raza) },
        modifier = modifier.then(
            if (seleccionado) Modifier.border(2.dp, color, MaterialTheme.shapes.medium)
            else Modifier
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) color.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = nombre,
                modifier = Modifier.size(32.dp),
                tint = color
            )

            Text(
                text = nombre,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (seleccionado) FontWeight.Bold
                else FontWeight.Normal
            )
        }
    }
}

// ============ PANTALLAS ============
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Arena Móvil",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Arena Móvil",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Duelo de criaturas por turnos.\nElige tu raza y lucha hasta el final.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(Screen.Setup.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Nueva partida")
                Text("Nueva partida")
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Stats.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Estadísticas")
                Text("Estadísticas")
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Historial.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Historial")
                Text("Historial de Partidas")
            }
        }
    }
}

@Composable
fun SetupScreen(navController: NavHostController) {
    var nombreJ1 by remember { mutableStateOf("") }
    var nombreJ2 by remember { mutableStateOf("") }

    var razaJ1 by remember { mutableStateOf(Raza.HUMANO) }
    var razaJ2 by remember { mutableStateOf(Raza.HUMANO) }

    var armaJ1 by remember { mutableStateOf<Any?>(ArmaHumano.ESCOPETA) }
    var armaJ2 by remember { mutableStateOf<Any?>(ArmaHumano.ESCOPETA) }

    val formularioValido = nombreJ1.isNotBlank() && nombreJ2.isNotBlank()

    // Actualizar arma por defecto cuando cambia la raza
    LaunchedEffect(razaJ1) {
        armaJ1 = when (razaJ1) {
            Raza.HUMANO -> ArmaHumano.ESCOPETA
            Raza.ELFO -> ElementoElfo.FUEGO
            Raza.ORCO -> ArmaOrco.HACHA
            Raza.BESTIA -> AtaqueBestia.ESPADA
        }
    }

    LaunchedEffect(razaJ2) {
        armaJ2 = when (razaJ2) {
            Raza.HUMANO -> ArmaHumano.ESCOPETA
            Raza.ELFO -> ElementoElfo.FUEGO
            Raza.ORCO -> ArmaOrco.HACHA
            Raza.BESTIA -> AtaqueBestia.ESPADA
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de partida",
            style = MaterialTheme.typography.headlineMedium
        )

        // Jugador 1
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar del jugador 1
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                ImageResources.getRazaColor(razaJ1).copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageResources.getRazaIcon(razaJ1),
                            contentDescription = "Jugador 1",
                            modifier = Modifier.size(30.dp),
                            tint = ImageResources.getRazaColor(razaJ1)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jugador 1",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = nombreJ1,
                            onValueChange = { nombreJ1 = it },
                            label = { Text("Nombre del jugador 1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(text = "Selecciona la raza:", style = MaterialTheme.typography.bodyMedium)
                RazaSelectorConImagen(
                    razaSeleccionada = razaJ1,
                    onRazaSeleccionada = { razaJ1 = it }
                )

                // Selector de arma/habilidad según la raza
                Text(text = "Selecciona arma/habilidad:", style = MaterialTheme.typography.bodyMedium)
                when (razaJ1) {
                    Raza.HUMANO -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ1 == ArmaHumano.ESCOPETA,
                                onClick = { armaJ1 = ArmaHumano.ESCOPETA },
                                label = { Text("Escopeta") }
                            )
                            FilterChip(
                                selected = armaJ1 == ArmaHumano.RIFLE_FRANCOTIRADOR,
                                onClick = { armaJ1 = ArmaHumano.RIFLE_FRANCOTIRADOR },
                                label = { Text("Rifle") }
                            )
                        }
                    }
                    Raza.ELFO -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = armaJ1 == ElementoElfo.FUEGO,
                                    onClick = { armaJ1 = ElementoElfo.FUEGO },
                                    label = { Text("Fuego") }
                                )
                                FilterChip(
                                    selected = armaJ1 == ElementoElfo.TIERRA,
                                    onClick = { armaJ1 = ElementoElfo.TIERRA },
                                    label = { Text("Tierra") }
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = armaJ1 == ElementoElfo.AIRE,
                                    onClick = { armaJ1 = ElementoElfo.AIRE },
                                    label = { Text("Aire") }
                                )
                                FilterChip(
                                    selected = armaJ1 == ElementoElfo.AGUA,
                                    onClick = { armaJ1 = ElementoElfo.AGUA },
                                    label = { Text("Agua") }
                                )
                            }
                        }
                    }
                    Raza.ORCO -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ1 == ArmaOrco.HACHA,
                                onClick = { armaJ1 = ArmaOrco.HACHA },
                                label = { Text("Hacha") }
                            )
                            FilterChip(
                                selected = armaJ1 == ArmaOrco.MARTILLO,
                                onClick = { armaJ1 = ArmaOrco.MARTILLO },
                                label = { Text("Martillo") }
                            )
                        }
                    }
                    Raza.BESTIA -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ1 == AtaqueBestia.PUÑOS,
                                onClick = { armaJ1 = AtaqueBestia.PUÑOS },
                                label = { Text("Puños") }
                            )
                            FilterChip(
                                selected = armaJ1 == AtaqueBestia.ESPADA,
                                onClick = { armaJ1 = AtaqueBestia.ESPADA },
                                label = { Text("Espada") }
                            )
                        }
                    }
                }
            }
        }

        // Jugador 2
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar del jugador 2
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                ImageResources.getRazaColor(razaJ2).copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageResources.getRazaIcon(razaJ2),
                            contentDescription = "Jugador 2",
                            modifier = Modifier.size(30.dp),
                            tint = ImageResources.getRazaColor(razaJ2)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jugador 2",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = nombreJ2,
                            onValueChange = { nombreJ2 = it },
                            label = { Text("Nombre del jugador 2") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(text = "Selecciona la raza:", style = MaterialTheme.typography.bodyMedium)
                RazaSelectorConImagen(
                    razaSeleccionada = razaJ2,
                    onRazaSeleccionada = { razaJ2 = it }
                )

                // Selector de arma/habilidad según la raza
                Text(text = "Selecciona arma/habilidad:", style = MaterialTheme.typography.bodyMedium)
                when (razaJ2) {
                    Raza.HUMANO -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ2 == ArmaHumano.ESCOPETA,
                                onClick = { armaJ2 = ArmaHumano.ESCOPETA },
                                label = { Text("Escopeta") }
                            )
                            FilterChip(
                                selected = armaJ2 == ArmaHumano.RIFLE_FRANCOTIRADOR,
                                onClick = { armaJ2 = ArmaHumano.RIFLE_FRANCOTIRADOR },
                                label = { Text("Rifle") }
                            )
                        }
                    }
                    Raza.ELFO -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = armaJ2 == ElementoElfo.FUEGO,
                                    onClick = { armaJ2 = ElementoElfo.FUEGO },
                                    label = { Text("Fuego") }
                                )
                                FilterChip(
                                    selected = armaJ2 == ElementoElfo.TIERRA,
                                    onClick = { armaJ2 = ElementoElfo.TIERRA },
                                    label = { Text("Tierra") }
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = armaJ2 == ElementoElfo.AIRE,
                                    onClick = { armaJ2 = ElementoElfo.AIRE },
                                    label = { Text("Aire") }
                                )
                                FilterChip(
                                    selected = armaJ2 == ElementoElfo.AGUA,
                                    onClick = { armaJ2 = ElementoElfo.AGUA },
                                    label = { Text("Agua") }
                                )
                            }
                        }
                    }
                    Raza.ORCO -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ2 == ArmaOrco.HACHA,
                                onClick = { armaJ2 = ArmaOrco.HACHA },
                                label = { Text("Hacha") }
                            )
                            FilterChip(
                                selected = armaJ2 == ArmaOrco.MARTILLO,
                                onClick = { armaJ2 = ArmaOrco.MARTILLO },
                                label = { Text("Martillo") }
                            )
                        }
                    }
                    Raza.BESTIA -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = armaJ2 == AtaqueBestia.PUÑOS,
                                onClick = { armaJ2 = AtaqueBestia.PUÑOS },
                                label = { Text("Puños") }
                            )
                            FilterChip(
                                selected = armaJ2 == AtaqueBestia.ESPADA,
                                onClick = { armaJ2 = AtaqueBestia.ESPADA },
                                label = { Text("Espada") }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                GameSession.iniciarCombate(
                    nombreJ1 = nombreJ1,
                    razaJ1 = razaJ1,
                    nombreJ2 = nombreJ2,
                    razaJ2 = razaJ2,
                    armaJ1 = armaJ1,
                    armaJ2 = armaJ2
                )
                navController.navigate(Screen.Battle.route)
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Comenzar combate"
                )
                Text("Comenzar combate")
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
                Text("Volver")
            }
        }
    }
}

@Composable
fun RazaSelectorConImagen(
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
            RazaCard(
                raza = Raza.HUMANO,
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )

            RazaCard(
                raza = Raza.ELFO,
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
            RazaCard(
                raza = Raza.ORCO,
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )

            RazaCard(
                raza = Raza.BESTIA,
                razaSeleccionada = razaSeleccionada,
                onRazaSeleccionada = onRazaSeleccionada,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BattleScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //Lee el ROOM
    val db = remember { ArenaDatabase.getDatabase(context) }
    val partidaDao = remember { db.partidaDao() }

    //Estado del combate
    var estado by remember { mutableStateOf(GameSession.estadoCombate) }
    var guardadoEnBD by remember { mutableStateOf(false) }
    var partidaGuardada by remember { mutableStateOf<PartidaEntity?>(null) }
    var mostrarSeleccionArma by remember { mutableStateOf(false) }

    val estadoActual = estado

    if (estadoActual == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Sin partida",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay partida configurada.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Inicio")
                    Text("Volver al inicio")
                }
            }
        }
        return
    }

    val hayGanador = estadoActual.vidaJugador1 <= 0 || estadoActual.vidaJugador2 <= 0

    val textoGanador = when {
        estadoActual.vidaJugador1 <= 0 && estadoActual.vidaJugador2 <= 0 ->
            "¡Empate!"

        estadoActual.vidaJugador2 <= 0 ->
            "¡Ganó ${estadoActual.jugador1.nombre}!"

        estadoActual.vidaJugador1 <= 0 ->
            "¡Ganó ${estadoActual.jugador2.nombre}!"

        else -> null
    }

    //Guardar automáticamente la partida cuando haya ganador
    LaunchedEffect(hayGanador) {
        if (hayGanador && !guardadoEnBD && estadoActual != null) {
            guardadoEnBD = true
            val entidad = crearPartidaDesdeEstado(estadoActual)
            partidaGuardada = entidad
            scope.launch {
                try {
                    partidaDao.insertPartida(entidad)
                    println("DEBUG: Partida guardada exitosamente en BD")
                } catch (e: Exception) {
                    println("DEBUG: Error al guardar partida: ${e.message}")
                    e.printStackTrace()
                }
            }
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado del combate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Combate"
                    )
                    Text(
                        text = "Combate",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ImageResources.turnoIcon,
                        contentDescription = "Turno",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Turno #${estadoActual.turnoActual}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ImageResources.distanciaIcon,
                        contentDescription = "Distancia",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Distancia: $textoDistancia",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Panel de jugadores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Jugador 1
            PersonajeAvatar(
                raza = estadoActual.jugador1.raza,
                nombre = estadoActual.jugador1.nombre,
                vida = estadoActual.vidaJugador1,
                esTurno = estadoActual.turnoJugador1,
                modifier = Modifier.weight(1f)
            )

            // VS en el medio
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Jugador 2
            PersonajeAvatar(
                raza = estadoActual.jugador2.raza,
                nombre = estadoActual.jugador2.nombre,
                vida = estadoActual.vidaJugador2,
                esTurno = !estadoActual.turnoJugador1,
                modifier = Modifier.weight(1f)
            )
        }

        // Resultado del combate CON ESTADÍSTICAS
        if (textoGanador != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ganador",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = textoGanador,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    // ESTADÍSTICAS DE LA PARTIDA
                    if (partidaGuardada != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "📊 Estadísticas de la Partida",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                // Información de los jugadores
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = "Jugador 1:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                        Text(
                                            text = partidaGuardada!!.jugador1Nombre,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Vida final: ${partidaGuardada!!.jugador1VidaFinal}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "Jugador 2:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                        Text(
                                            text = partidaGuardada!!.jugador2Nombre,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Vida final: ${partidaGuardada!!.jugador2VidaFinal}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                Divider()

                                // Detalles del combate
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Turnos totales:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                        Text(
                                            text = partidaGuardada!!.turnosTotales.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Distancia final:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                        Text(
                                            text = when (partidaGuardada!!.distanciaFinal) {
                                                Distancia.CERCA -> "Cerca"
                                                Distancia.MEDIA -> "Media"
                                                Distancia.LEJOS -> "Lejos"
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Información del ganador
                                if (partidaGuardada!!.ganadorNombre != null) {
                                    Divider()
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Ganador",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Ganador: ${partidaGuardada!!.ganadorNombre}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Divider()
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Empate",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Empate",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                // Fecha de la partida
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Fecha",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm",
                                            Locale.getDefault()
                                        )
                                            .format(partidaGuardada!!.fecha),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                GameSession.limpiar()
                                navController.navigate(Screen.Home.route)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Home, contentDescription = "Inicio")
                                Text("Volver al inicio")
                            }
                        }

                        Button(
                            onClick = {
                                GameSession.limpiar()
                                navController.navigate(Screen.Stats.route)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Estadísticas"
                                )
                                Text("Ver más estadísticas")
                            }
                        }
                    }
                }
            }
        }

        // Botones de acción (solo si no hay ganador)
        if (!hayGanador) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Acciones disponibles",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Mostrar selección de arma si está activa
                    if (mostrarSeleccionArma) {
                        val jugadorActual =
                            if (estadoActual.turnoJugador1) estadoActual.jugador1 else estadoActual.jugador2
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Selecciona arma/habilidad:",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                when (jugadorActual.raza) {
                                    Raza.HUMANO -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(ArmaHumano.ESCOPETA)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                enabled = estadoActual.distancia != Distancia.LEJOS,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Escopeta")
                                            }
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(ArmaHumano.RIFLE_FRANCOTIRADOR)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Rifle")
                                            }
                                        }
                                    }

                                    Raza.ELFO -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        GameSession.atacarConArma(ElementoElfo.FUEGO)
                                                            ?.let { nuevo ->
                                                                estado = nuevo
                                                                mostrarSeleccionArma = false
                                                            }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Fuego")
                                                }
                                                Button(
                                                    onClick = {
                                                        GameSession.atacarConArma(ElementoElfo.TIERRA)
                                                            ?.let { nuevo ->
                                                                estado = nuevo
                                                                mostrarSeleccionArma = false
                                                            }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Tierra")
                                                }
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        GameSession.atacarConArma(ElementoElfo.AIRE)
                                                            ?.let { nuevo ->
                                                                estado = nuevo
                                                                mostrarSeleccionArma = false
                                                            }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Aire")
                                                }
                                                Button(
                                                    onClick = {
                                                        GameSession.atacarConArma(ElementoElfo.AGUA)
                                                            ?.let { nuevo ->
                                                                estado = nuevo
                                                                mostrarSeleccionArma = false
                                                            }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Agua")
                                                }
                                            }
                                        }
                                    }

                                    Raza.ORCO -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(ArmaOrco.HACHA)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Hacha")
                                            }
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(ArmaOrco.MARTILLO)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Martillo")
                                            }
                                        }
                                    }

                                    Raza.BESTIA -> {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(AtaqueBestia.PUÑOS)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Puños")
                                            }
                                            Button(
                                                onClick = {
                                                    GameSession.atacarConArma(AtaqueBestia.ESPADA)
                                                        ?.let { nuevo ->
                                                            estado = nuevo
                                                            mostrarSeleccionArma = false
                                                        }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Espada")
                                            }
                                        }
                                    }
                                }
                                Button(
                                    onClick = { mostrarSeleccionArma = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    } else {
                        // Fila 1: Atacar y Curar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    mostrarSeleccionArma = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Atacar"
                                    )
                                    Text("Atacar")
                                }
                            }

                            Button(
                                onClick = {
                                    GameSession.curar()?.let { nuevo ->
                                        estado = nuevo
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Curar"
                                    )
                                    Text("Curar")
                                }
                            }
                        }

                        // Fila 2: Avanzar y Retroceder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    GameSession.avanzar()?.let { nuevo ->
                                        estado = nuevo
                                    }
                                },
                                enabled = estadoActual.distancia != Distancia.CERCA,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "Avanzar"
                                    )
                                    Text("Avanzar")
                                }
                            }

                            Button(
                                onClick = {
                                    GameSession.retroceder()?.let { nuevo ->
                                        estado = nuevo
                                    }
                                },
                                enabled = estadoActual.distancia != Distancia.LEJOS,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Retroceder"
                                    )
                                    Text("Retroceder")
                                }
                            }
                        }
                    }
                }
            }

            // Botón para terminar el combate (solo si no hay ganador)
            if (!hayGanador) {
                Button(
                    onClick = {
                        GameSession.limpiar()
                        navController.navigate(Screen.Home.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Terminar")
                        Text("Terminar combate")
                    }
                }
            }
        }
    }
}

// ============ PANTALLA DE ESTADÍSTICAS SIMPLIFICADA ============
@Composable
fun StatsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var partidas by remember { mutableStateOf<List<PartidaEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        println("DEBUG: Iniciando carga de estadísticas")
        try {
            isLoading = true
            errorMessage = null

            // Usar withContext para manejar mejor las excepciones
            withContext(Dispatchers.IO) {
                println("DEBUG: Obteniendo base de datos...")
                val db = ArenaDatabase.getDatabase(context)
                println("DEBUG: Base de datos obtenida")
                val partidaDao = db.partidaDao()
                println("DEBUG: DAO obtenido")
                partidas = partidaDao.getAllPartidasList()
                println("DEBUG: Partidas cargadas: ${partidas.size}")
            }

        } catch (e: Exception) {
            println("DEBUG: ERROR al cargar estadísticas: ${e.message}")
            e.printStackTrace()
            errorMessage = "Error al cargar datos: ${e.message}"
            partidas = emptyList()
        } finally {
            isLoading = false
            println("DEBUG: Carga finalizada")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando estadísticas...")
                }
            }
        } else if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (partidas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Sin datos",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay estadísticas disponibles")
                    Text(
                        "Juega algunas partidas para ver estadísticas",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            // Estadísticas generales
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumen General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total de partidas:")
                        Text(
                            text = partidas.size.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val victorias = partidas.count { it.ganadorNombre != null }
                    val empates = partidas.count { it.esEmpate }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Victorias:")
                        Text("$victorias")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Empates:")
                        Text("$empates")
                    }
                }
            }

            // Últimas partidas (máximo 5)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Últimas Partidas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    partidas.take(5).reversed().forEachIndexed { index, partida ->
                        SimplePartidaItem(partida = partida)
                        if (index < partidas.size - 1 && index < 4) {
                            Divider()
                        }
                    }

                    if (partidas.size > 5) {
                        Text(
                            text = "... y ${partidas.size - 5} partidas más",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                Text("Volver al inicio")
            }
        }
    }
}

// ============ PANTALLA DE HISTORIAL SIMPLIFICADA ============
@Composable
fun HistorialScreen(navController: NavHostController) {
    val context = LocalContext.current
    var partidas by remember { mutableStateOf<List<PartidaEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        println("DEBUG: Iniciando carga de historial")
        try {
            isLoading = true
            errorMessage = null

            withContext(Dispatchers.IO) {
                val db = ArenaDatabase.getDatabase(context)
                val partidaDao = db.partidaDao()
                partidas = partidaDao.getAllPartidasList()
                println("DEBUG: Historial cargado: ${partidas.size} partidas")
            }

        } catch (e: Exception) {
            println("DEBUG: ERROR al cargar historial: ${e.message}")
            e.printStackTrace()
            errorMessage = "Error al cargar historial: ${e.message}"
            partidas = emptyList()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Historial Completo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando historial...")
                }
            }
        } else if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (partidas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Sin datos",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay partidas registradas")
                    Text(
                        "Juega algunas partidas para ver el historial",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(partidas.reversed()) { partida ->
                    SimplePartidaItem(partida = partida)
                }
            }
        }
    }
}

// ============ COMPONENTES AUXILIARES ============
@Composable
fun SimplePartidaItem(partida: PartidaEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fecha
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(partida.fecha),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Jugadores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = partida.jugador1Nombre,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${partida.jugador1VidaFinal} HP",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            partida.jugador1VidaFinal > 70 -> Color.Green
                            partida.jugador1VidaFinal > 30 -> Color.Yellow
                            else -> Color.Red
                        }
                    )
                }

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = partida.jugador2Nombre,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${partida.jugador2VidaFinal} HP",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            partida.jugador2VidaFinal > 70 -> Color.Green
                            partida.jugador2VidaFinal > 30 -> Color.Yellow
                            else -> Color.Red
                        }
                    )
                }
            }

            // Resultado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Turnos: ${partida.turnosTotales}",
                    style = MaterialTheme.typography.labelSmall
                )

                if (partida.ganadorNombre != null) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = "Ganador: ${partida.ganadorNombre}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = "Empate",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// ============ FUNCIONES HELPER ============
fun vidaInicialPorRaza(raza: Raza): Int =
    when (raza) {
        Raza.HUMANO -> 100
        Raza.ELFO -> 100
        Raza.ORCO -> 110
        Raza.BESTIA -> 120
    }

fun crearPartidaDesdeEstado(estado: EstadoCombate): PartidaEntity {
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