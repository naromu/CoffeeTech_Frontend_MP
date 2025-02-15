package com.example.coffetech.view.CulturalWorkTask


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ReusableSearchBar
import com.example.coffetech.model.GeneralCulturalWorkTask
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.common.HeaderFooterView
import com.example.coffetech.viewmodel.CulturalWorkTask.GeneralCulturalWorkTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Vista principal para gestionar y visualizar las tareas de labores culturales asignadas al usuario.
 * Permite filtrar, buscar y ordenar las tareas de labor cultural en función de distintos criterios.
 *
 * @param navController Controlador de navegación para manejar las rutas de navegación.
 * @param viewModel ViewModel que gestiona el estado y la lógica de esta vista.
 */
@Composable
fun CulturalWorkTaskGeneralView(
    navController: NavController,
    viewModel: GeneralCulturalWorkTaskViewModel = viewModel()
) {
    val context = LocalContext.current

    // Efecto lanzado para cargar las tareas de labores culturales al montarse la vista
    LaunchedEffect(Unit) {
        viewModel.loadTasks(context)
    }

    // Obtener los estados del ViewModel
    val tasks by viewModel.filteredTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Estados de los filtros
    val farmOptions by viewModel.farmOptions.collectAsState()
    val selectedFarm by viewModel.selectedFarm.collectAsState()
    val plotOptions by viewModel.plotOptions.collectAsState()
    val selectedPlot by viewModel.selectedPlot.collectAsState()
    val selectedOrder by viewModel.selectedOrder.collectAsState()

    // Contenedor principal con encabezado y pie de página
    HeaderFooterView(
        title = "Mis Tareas",
        currentView = "Labores",
        navController = navController
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
                    // Mostrar indicador de carga si los datos están cargando
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
                } else if (errorMessage != null) {
                    // Mostrar mensaje de error
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {

                    // Barra de búsqueda para filtrar tareas por colaborador
                    ReusableSearchBar(
                        query = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        text = "Buscar tarea por 'Asignado a'",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdowns para seleccionar filtros y orden
                    CulturalTaskFilterDropdowns(
                        farmOptions = farmOptions,
                        selectedFarm = selectedFarm,
                        onFarmSelected = { viewModel.selectFarm(it) },
                        plotOptions = plotOptions,
                        selectedPlot = selectedPlot,
                        onPlotSelected = { viewModel.selectPlot(it) },
                        selectedOrder = selectedOrder,
                        onOrderSelected = { viewModel.selectOrder(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de tareas culturales
                    if (tasks.isEmpty()) {
                        // Mensaje si no hay tareas disponibles
                        Text(
                            text = "No hay tareas de labor cultural para mostrar",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        LazyColumn {
                            items(tasks) { task ->
                                CulturalWorkTaskGeneralCard(
                                    task = task,
                                    farmName = task.farm_name,
                                    plotName = task.plot_name,
                                    onClick = {
                                        navController.navigate("${Routes.SendDectectionView}/${task.cultural_work_task_id}/${task.cultural_works_name}")
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Función de vista previa de CulturalWorkTaskGeneralView en Android Studio.
 * Utiliza un ViewModel simulado con datos de prueba para mostrar cómo se verá la interfaz.
 */
@Preview(showBackground = true)
@Composable
fun CulturalWorkTaskGeneralViewPreview() {
    CoffeTechTheme {
        val navController = rememberNavController()
        val viewModel: GeneralCulturalWorkTaskViewModel = viewModel()

        // Simulación de tareas predefinidas para la vista previa
        LaunchedEffect(Unit) {
            viewModel.addTestTasks(
                listOf(
                    GeneralCulturalWorkTask(
                        cultural_work_task_id = 1,
                        cultural_works_name = "Recolección de Café",
                        collaborator_id = 1,
                        collaborator_name = "Daniel Pruebas",
                        owner_name = "Natalia Rodríguez Mu",
                        status = "Por hacer",
                        task_date = "2024-10-29",
                        farm_name = "Finca 1",
                        plot_name = "Lote 1"
                    ),
                    GeneralCulturalWorkTask(
                        cultural_work_task_id = 2,
                        cultural_works_name = "Poda de Árboles",
                        collaborator_id = 2,
                        collaborator_name = "Otros Colaboradores",
                        owner_name = "María García",
                        status = "Terminado",
                        task_date = "2024-10-27",
                        farm_name = "Finca 2",
                        plot_name = "Lote 2"
                    )
                )
            )
        }

        CulturalWorkTaskGeneralView(
            navController = navController,
            viewModel = viewModel
        )
    }
}