// view/healthcheck/DetectionHistoryView.kt
package com.example.coffetech.view.healthcheck

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.CheckingInfoCard
import com.example.coffetech.common.ReusableSearchBar
import com.example.coffetech.common.DateDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.healthcheck.DetectionHistoryViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionHistoryView(
    navController: NavController,
    plotId: Int, // Asegúrate de recibir plotId
    plotName: String,
    farmName: String,
    farmId: Int,
    viewModel: DetectionHistoryViewModel = viewModel()
) {
    val context = LocalContext.current

    // Obtener el token de sesión desde SharedPreferences u otro método
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }
    Log.d("DetectionHistoryView", "Token de sesión obtenido: $sessionToken")

    // Cargar las detecciones cuando el composable se monta
    LaunchedEffect(plotId, sessionToken) {
        try {
            Log.d("DetectionHistoryView", "LaunchedEffect iniciado con plotId: $plotId y sessionToken: $sessionToken")
            if (!sessionToken.isNullOrBlank()) {
                viewModel.loadDetections(plotId, sessionToken)
                Log.d("DetectionHistoryView", "Cargando detecciones con plotId: $plotId")
            } else {
                Log.e("DetectionHistoryView", "Token de sesión no disponible.")
                // Manejar el error según sea necesario, por ejemplo, navegar al login
            }
        } catch (e: Exception) {
            Log.e("DetectionHistoryView", "Excepción en LaunchedEffect: ${e.message}", e)
            // Manejar el error según sea necesario
        }
    }

    // Estados del ViewModel
    val detections by viewModel.detections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    HeaderFooterSubView(
        title = "Historial de detecciones",
        currentView = "Fincas",
        navController = navController,
        onBackClick = { navController.navigate("${Routes.PlotInformationView}/$plotId/$farmName/$farmId") },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


                // Información de la finca y el lote
                Text(text = "Finca: $farmName", color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Lote: $plotName", color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                // Indicador de carga, mensaje de error o lista de detecciones
                when {
                    isLoading -> {
                        // Indicador de carga
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cargando detecciones...",
                                color = Color.Black
                            )
                        }
                    }
                    errorMessage.isNotEmpty() -> {
                        // Mostrar mensaje de error
                        Text(text = errorMessage, color = Color.Red)
                    }
                    detections.isEmpty() -> {
                        // Mostrar mensaje si no hay detecciones
                        Text("No hay detecciones para mostrar.", color = Color.Gray)
                    }
                    else -> {
                        // Mostrar lista de detecciones
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(detections) { detection ->
                                // Codificar los parámetros de tipo String
                                val encodedFarmName = URLEncoder.encode(farmName, StandardCharsets.UTF_8.toString())
                                val encodedPlotName = URLEncoder.encode(plotName, StandardCharsets.UTF_8.toString())
                                val encodedPrediction = URLEncoder.encode(detection.result, StandardCharsets.UTF_8.toString())
                                val encodedRecommendation = URLEncoder.encode(detection.recommendation, StandardCharsets.UTF_8.toString())
                                val encodedDate = URLEncoder.encode(detection.date, StandardCharsets.UTF_8.toString())
                                val encodedPerformedBy = URLEncoder.encode(detection.collaborator_name, StandardCharsets.UTF_8.toString())

                                CheckingInfoCard(
                                    date = detection.date,
                                    collaboratorName = detection.collaborator_name,
                                    prediction = detection.result,
                                    recommendation = detection.recommendation,
                                    onEditClick = {
                                        Log.d("DetectionHistoryView", "Editar detección: ${detection.detection_id}")
                                        // Navegar pasando los parámetros codificados
                                        navController.navigate(
                                            "${Routes.EditResultHealthCheckView}/$encodedFarmName/$encodedPlotName/${detection.detection_id}/$encodedPrediction/$encodedRecommendation/$encodedDate/$encodedPerformedBy"
                                        )
                                    },
                                    showEditIcon = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Botón flotante si es necesario
            // Puedes agregar un FloatingActionButton aquí si necesitas alguna acción adicional
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetectionHistoryViewPreview() {
    val navController = rememberNavController()
    CoffeTechTheme {
        DetectionHistoryView(
            navController = navController,
            farmId = 1,
            farmName = "Finca Ejemplo",
            plotName = "Lote 1",
            plotId = 59 // ID de lote de ejemplo
        )
    }
}
