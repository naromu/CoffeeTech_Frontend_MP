
package com.example.coffetech.view.CulturalWorkTask

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.CustomFloatingActionButton
import com.example.coffetech.common.FloatingActionButtonGroup
import com.example.coffetech.common.ReusableSearchBar
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.cultural.CulturalWorkTaskViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
/**
 * Vista que muestra la información detallada de las tareas de labor cultural asignadas a un lote específico.
 * Permite filtrar, buscar, ordenar y navegar para editar o agregar nuevas tareas de labor cultural.
 *
 * @param navController Controlador de navegación para gestionar las rutas de navegación.
 * @param plotId ID del lote asociado a las tareas.
 * @param plotName Nombre del lote.
 * @param farmId ID de la finca.
 * @param farmName Nombre de la finca.
 * @param viewModel ViewModel que maneja el estado y la lógica de esta vista.
 */
@Composable
fun CulturalWorkTaskInformationView(
    navController: NavController,
    plotId: Int,
    plotName: String,
    farmId: Int,
    farmName: String,
    viewModel: CulturalWorkTaskViewModel = viewModel()
) {
    val context = LocalContext.current

    // Obtener el token de sesión desde SharedPreferences u otro método
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }
    Log.d("CulturalWorkTaskInfoView", "Token de sesión obtenido: $sessionToken")

    // Cargar las tareas cuando el composable se monta
    LaunchedEffect(plotId, sessionToken) {
        try {
            Log.d("CulturalWorkTaskInfoView", "LaunchedEffect iniciado con plotId: $plotId y sessionToken: $sessionToken")
            if (!sessionToken.isNullOrBlank()) {
                viewModel.loadTasks(plotId, sessionToken)
                Log.d("CulturalWorkTaskInfoView", "Cargando tareas con plotId: $plotId")
            } else {
                Log.e("CulturalWorkTaskInfoView", "Token de sesión no disponible.")
                viewModel.setErrorMessage("Token de sesión no disponible.")
                Toast.makeText(context, "Error: Token de sesión no disponible.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("CulturalWorkTaskInfoView", "Excepción en LaunchedEffect: ${e.message}", e)
            viewModel.setErrorMessage("Error inesperado al cargar tareas.")
            Toast.makeText(context, "Error inesperado al cargar tareas.", Toast.LENGTH_LONG).show()
        }
    }

    // Obtener estados del ViewModel
    val tasks by viewModel.filteredTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val orderFilter by viewModel.orderFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Log.d("CulturalWorkTaskInfoView", "Estado de carga: $isLoading, Mensaje de error: $errorMessage, Número de tareas: ${tasks.size}")

    HeaderFooterSubView(
        title = "Tarea Labor Cultural",
        currentView = "Fincas",
        navController = navController,
        onBackClick = {
            Log.d("CulturalWorkTaskInfoView", "Navegando hacia PlotInformationView")
            navController.navigate("${Routes.PlotInformationView}/$plotId/$farmName/$farmId")
        },
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
                if (isLoading) {
                    // Indicador de carga mientras se obtienen los datos
                    Log.d("CulturalWorkTaskInfoView", "Mostrando indicador de carga")
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cargando tareas de labor cultural...",
                            color = Color.Black
                        )
                    }
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar mensaje de error si ocurre algún problema
                    Log.e("CulturalWorkTaskInfoView", "Mostrando mensaje de error: $errorMessage")
                    Text(text = errorMessage, color = Color.Red)
                } else {
                    // Mostrar información de finca y lote
                    Log.d("CulturalWorkTaskInfoView", "Mostrando información de finca y lote")
                    Text(text = "Finca: $farmName", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Lote: $plotName", color = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    //Barra de búsqueda
                    ReusableSearchBar(
                        query = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        text = "Buscar tarea por 'Asignado a'",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdowns para filtrar y ordenar tareas
                    CulturalTaskFilterDropdowns(
                        selectedStatusFilter = statusFilter,
                        onStatusFilterChange = { selectedStatus ->
                            viewModel.selectStatusFilter(selectedStatus)
                        },
                        selectedOrderFilter = orderFilter,
                        onOrderFilterChange = { selectedOrder ->
                            viewModel.selectOrderFilter(selectedOrder)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de tareas culturales
                    if (tasks.isEmpty()) {
                        Log.d("CulturalWorkTaskInfoView", "No hay tareas para mostrar")
                        Text("No hay tareas de labor cultural para mostrar", color = Color.Gray)
                    } else {
                        Log.d("CulturalWorkTaskInfoView", "Mostrando lista de tareas: ${tasks.size} tareas")
                        LazyColumn {
                            items(tasks) { task ->
                                CulturalWorkTaskCard(task = task, onEdit = {
                                    Log.d("CulturalWorkTaskInfoView", "Editar tarea: ${task.cultural_works_name}")
                                    // Navegar a la pantalla de edición, pasando el ID de la tarea
                                    navController.navigate(
                                        "${Routes.EditCulturalWorkView}/${task.cultural_work_task_id ?: 0}/${task.cultural_works_name ?: ""}/${task.collaborator_user_id ?: 0}/${task.collaborator_name ?: ""}/${task.task_date ?: ""}/$plotName/$plotId"
                                    )
                            })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

            }

            // Botón flotante para agregar una nueva tarea cultural
            if (errorMessage.isEmpty()) {
                Log.d("CulturalWorkTaskInfoView", "Mostrando botón flotante")
                CustomFloatingActionButton(
                    onAddClick = {
                        // Navegar a la pantalla para crear una nueva floración
                        navController.navigate("${Routes.AddCulturalWorkView1}/$plotId/$plotName")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * Vista previa de la función CulturalWorkTaskInformationView en Android Studio.
 * Utiliza un ViewModel simulado para mostrar cómo se verá la interfaz.
 */

@Preview(showBackground = true)
@Composable
fun CulturalWorkTaskInformationViewPreview() {
    CoffeTechTheme {
        // Simulación de un NavController para previsualización
        val navController = rememberNavController()

        // Crear una instancia del ViewModel con tareas predefinidas
        val previewViewModel = CulturalWorkTaskViewModel().apply {
            // Puedes cargar tareas predefinidas si es necesario
        }

        // Llamada a la vista de previsualización con el ViewModel predefinido
        CulturalWorkTaskInformationView(
            navController = navController,
            farmId = 1,
            farmName = "Finca Ejemplo",
            plotId = 1,
            plotName = "Plot A",
            viewModel = previewViewModel
        )
    }
}
