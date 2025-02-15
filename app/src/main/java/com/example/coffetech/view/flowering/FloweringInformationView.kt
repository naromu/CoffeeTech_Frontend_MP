package com.example.coffetech.view.flowering

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.*
import com.example.coffetech.model.Flowering
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.farm.FarmInformationViewModel
import com.example.coffetech.viewmodel.flowering.FloweringInformationViewModel
import kotlinx.coroutines.flow.map


/**
 * Función composable que muestra la vista de información de floraciones,
 * permitiendo al usuario ver floraciones activas e historial de floraciones de un lote específico.
 *
 * @param navController Controlador de navegación para manejar acciones de navegación.
 * @param plotId ID del lote asociado a las floraciones.
 * @param plotName Nombre del lote.
 * @param farmName Nombre de la finca.
 * @param farmId ID de la finca.
 * @param viewModel ViewModel que gestiona el estado y lógica para mostrar la información de floraciones.
 */

@Composable
fun FloweringInformationView(
    navController: NavController,
    plotId: Int,
    plotName: String,
    farmName: String,
    farmId: Int,
    viewModel: FloweringInformationViewModel = viewModel() // Inyecta el ViewModel aquí
) {

    // Obtener el contexto para acceder a SharedPreferences o cualquier otra fuente del sessionToken
    val context = LocalContext.current
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }
    Log.d("EditFloweringView", "Recibiendo datos : /$plotId/$plotName/$farmName/$farmId")

    // Llamar a loadActiveFloweringsAndPlotDetails cuando la vista se cargue
    LaunchedEffect(plotId) {
        sessionToken?.let {
            viewModel.loadActiveFlowerings(plotId, it)
            viewModel.loadFloweringHistory(plotId, it)
        } ?: run {
            viewModel.setErrorMessage("Session token no encontrado. Por favor, inicia sesión.")
        }
    }

    // Obtener los estados del ViewModel
    val selectedFloweringName by viewModel.selectedFloweringName
    val expanded by viewModel.isDropdownExpanded
    val flowerings by viewModel.flowerings.collectAsState() // Aquí están las floraciones filtradas
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery
    val floweringHistory by viewModel.floweringHistory.collectAsState() // Historial de floraciones
    val selectedTypeFilter by viewModel.historyTypeFilter.collectAsState()
    val selectedOrderFilter by viewModel.historyOrderFilter.collectAsState()
    val filteredFloweringHistory by viewModel.filteredFloweringHistory.collectAsState()

    // Acción para editar una floración
    val onEditClick: (Flowering) -> Unit = { flowering ->
        Log.d("EditFloweringView", "Enviando EditFloweringView con ID: ${flowering.flowering_id}/${flowering.flowering_type_name}/${flowering.flowering_date}/${flowering.harvest_date ?: ""}/$plotId")
        navController.navigate("${Routes.EditFloweringView}/${flowering.flowering_id}/${flowering.flowering_type_name}/${flowering.flowering_date}/$plotId")
    }

    // Acción para mostrar información de la floración
    val onInfoClick: (Flowering) -> Unit = { flowering ->
        navController.navigate("${Routes.RecommendationFloweringViewPreview}/$plotId/$plotName/$farmName/$farmId/${flowering.flowering_id}")
    }

    // Vista principal que muestra información general de las floraciones activas e historial
    HeaderFooterSubView(
        title = "Floraciones",
        currentView = "Fincas",
        navController = navController,
        onBackClick = { navController.navigate("${Routes.PlotInformationView}/$plotId/$farmName/$farmId") },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            // Contenido desplazable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Hacer la columna scrolleable verticalmente
            ) {
                /* (isLoading) {
                    // Mostrar un indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cargando floraciones...",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar el error si ocurrió algún problema
                    Text(text = errorMessage, color = Color.Red)
                } else {*/

                    // Mostrar el nombre del lote
                    Text(text = "Lote: ${plotName.ifEmpty { "Sin Nombre de lote" }}", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    val activeFlowerings = flowerings.take(2) // Obtener las primeras dos floraciones activas

                    activeFlowerings.forEach { flowering ->
                        FloweringGeneralInfoCard(
                            flowering_type_name = flowering.flowering_type_name,
                            status = flowering.status,
                            flowering_date = flowering.flowering_date,
                            onEditClick = { onEditClick(flowering) },
                            onInfoClick = { onInfoClick(flowering) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Historial de Floraciones",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Filtros para el historial de floraciones
                    HistoryFilterDropdowns(
                        selectedTypeFilter = selectedTypeFilter,
                        onTypeFilterChange = { selectedType ->
                            viewModel.selectHistoryTypeFilter(selectedType)
                        },
                        selectedOrderFilter = selectedOrderFilter,
                        onOrderFilterChange = { selectedOrder ->
                            viewModel.selectHistoryOrderFilter(selectedOrder)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de Floraciones usando el FloweringList personalizado
                    FloweringList(
                        flowerings = filteredFloweringHistory, // Lista del historial
                        modifier = Modifier.fillMaxWidth(),
                        onEditClick = onEditClick,
                        onInfoClick = onInfoClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                //}
            }
            // Botón flotante alineado al fondo derecho
            CustomFloatingActionButton(
                onAddClick = {
                    // Navegar a la pantalla para crear una nueva floración
                    navController.navigate("${Routes.AddFloweringView}/$plotId")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}


/**
 * Función de vista previa para mostrar la vista de información de floraciones en el modo de previsualización de Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun FloweringInformationViewPreview() {
    CoffeTechTheme {
        FloweringInformationView(
            navController = NavController(LocalContext.current),
            plotId = 1, // Valor simulado de plotId para la previsualización
            plotName= "",
            farmName = "Finca Ejemplo",
            farmId = 1,
        )
    }
}