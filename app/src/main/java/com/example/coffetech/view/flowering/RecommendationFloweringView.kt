package com.example.coffetech.view.flowering

import android.net.Uri
import android.widget.Toast
import com.example.coffetech.viewmodel.flowering.RecommendationFloweringViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.*
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView


/**
 * Vista para mostrar recomendaciones específicas para la floración de un lote.
 * Obtiene las recomendaciones del ViewModel y las presenta en una lista.
 *
 * @param navController Controlador de navegación para manejar las rutas de la aplicación.
 * @param plotId ID del lote asociado a las recomendaciones de floración.
 * @param plotName Nombre del lote.
 * @param farmName Nombre de la finca.
 * @param farmId ID de la finca.
 * @param floweringId ID de la floración específica.
 * @param viewModel ViewModel que gestiona la lógica de negocio y el estado de la vista.
 */

@Composable
fun RecommendationFloweringView(
    navController: NavController,
    plotId: Int,
    plotName: String,
    farmName: String,
    farmId: Int,
    floweringId: Int,
    viewModel: RecommendationFloweringViewModel = viewModel() // Inyecta el ViewModel aquí
) {
    // Obtener el contexto para acceder a SharedPreferences o cualquier otra fuente del sessionToken
    val context = LocalContext.current
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Llamada a fetchRecommendations cuando se carga la vista para obtener las recomendaciones
    LaunchedEffect(key1 = floweringId) {
        sessionToken?.let {
            viewModel.fetchRecommendations(floweringId, it, context)
        } ?: run {
            viewModel._errorMessage.value = "Session token no encontrado. Por favor, inicia sesión."
            // Mostrar un Toast o manejar el error adecuadamente
        }
    }

    // Obtener los estados del ViewModel
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val plotNameState by viewModel.plotName.collectAsState()
    val floweringDate by viewModel.flowering_date.collectAsState()
    val currentDate by viewModel.current_date.collectAsState()

    // Limitar el nombre del lote a un máximo de 21 caracteres, agregando "..." si es más largo
    val displayedPlotName = if (plotName.length > 21) {
        plotName.take(17) + "..." // Si tiene más de 21 caracteres, corta y añade "..."
    } else {
        plotName // Si es menor o igual a 21 caracteres, lo dejamos como está
    }

    // Vista principal que envuelve la cabecera, el contenido y el pie de página
    HeaderFooterSubView(
        title = "Recomendaciones\nFloración Principal",
        currentView = "Fincas",
        navController = navController,
        onBackClick = {
            navController.navigate("${Routes.FloweringInformationView}/$plotId/$plotName/$farmName/$farmId")
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            // Contenedor vertical con scroll para las recomendaciones y tareas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Hacer la columna scrolleable verticalmente
            ) {
                if (isLoading) {
                    // Muestra un indicador de carga mientras se obtienen los datos
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cargando recomendaciones...",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar el error si ocurrió algún problema
                    Text(text = errorMessage, color = Color.Red)
                } else {
                    // Muestra información del lote y la fecha actual
                    Text(text = "Lote: ${displayedPlotName.ifEmpty { "Sin Nombre de lote" }}", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar la fecha actual obtenida de la llamada
                    Text(text = "Fecha Actual: ${currentDate.ifEmpty { "Sin Fecha" }}", color = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de tareas recomendadas para el lote, obtenida del ViewModel
                    TasksList(
                        tasks = tasks,
                        onProgramClick = { task ->
                            // Definir el tipo de labor y codificar parámetros antes de navegar a la vista de detalles de la labor
                            val tipoLabor = if (task.task == "Chequeo de Salud") "Chequeo de Salud" else "Chequeo de estado de maduración"
                            val encodedDate = Uri.encode(currentDate)
                            val encodedPlotName = Uri.encode(plotName)
                            val encodedTipoLabor = Uri.encode(tipoLabor)
                            navController.navigate(
                                "${Routes.AddCulturalWorkView2}/$plotId/$encodedPlotName/$encodedTipoLabor/$encodedDate"
                            )
                                         },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * Vista de previsualización para ver cómo se muestra RecommendationFloweringView en Android Studio.
 */

@Preview(showBackground = true)
@Composable
fun RecommendationFloweringViewPreview() {
    CoffeTechTheme {
        RecommendationFloweringView(
            navController = NavController(LocalContext.current),
            plotId = 1,
            plotName = "Lote de Prueba",
            farmName = "Finca Ejemplo",
            farmId = 1,
            floweringId = 19
        )
    }
}
