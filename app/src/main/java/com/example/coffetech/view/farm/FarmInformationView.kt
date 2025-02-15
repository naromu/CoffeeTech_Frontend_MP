package com.example.coffetech.view.farm

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.*
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.farm.FarmInformationViewModel
import kotlinx.coroutines.flow.map
/**
 * Composable function that renders a view displaying detailed information about a specific farm.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm whose information is to be displayed.
 * @param viewModel The [FarmInformationViewModel] that manages the state and logic for displaying farm information.
 */
@Composable
fun FarmInformationView(
    navController: NavController,
    farmId: Int,
    viewModel: FarmInformationViewModel = viewModel() // Inyecta el ViewModel aquí
) {
    // Obtener el contexto para acceder a SharedPreferences o cualquier otra fuente del sessionToken
    val context = LocalContext.current
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }

    // Llamar a loadFarmData y loadPlots cuando la vista se cargue
    LaunchedEffect(farmId) {
        sessionToken?.let {
            viewModel.loadFarmData(farmId, it, context)
            // Verificar permiso antes de cargar lotes
            if (viewModel.hasPermission("read_plots")) {
                viewModel.loadPlots(farmId, it)
            }
        } ?: run {
            viewModel.setErrorMessage("Session token no encontrado. Por favor, inicia sesión.")
        }
    }

    // Obtener los estados del ViewModel
    val farmName by viewModel.farmName.collectAsState()
    val farmArea by viewModel.farmArea.collectAsState()
    val unitOfMeasure by viewModel.unitOfMeasure.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val collaboratorName by viewModel.collaboratorName.collectAsState()
    val lotes by viewModel.lotes.collectAsState() // Aquí están los lotes filtrados
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery // Obtener la consulta de búsqueda como TextFieldValue

    // Verificar permisos del usuario
    val userHasPermissionToEditFarm = viewModel.hasPermission("edit_farm")
    val userHasPermissionToDeleteFarm = viewModel.hasPermission("delete_farm")
    val userHasPermissionReadCollaborators = viewModel.hasPermission("read_collaborators") || viewModel.hasPermission("read_collaborators")
    val userHasPermissionReadReports = viewModel.hasPermission("read_report") || viewModel.hasPermission("read_report")
    val userHasPermissionReadPlots = viewModel.hasPermission("read_plots") || viewModel.hasPermission("read_plots")
    val userHasPermissionAddPlots = viewModel.hasPermission("add_plot") || viewModel.hasPermission("add_plot")

    val displayedFarmName = if (farmName.length > 21) {
        farmName.take(17) + "..." // Si tiene más de 21 caracteres, corta y añade "..."
    } else {
        farmName // Si es menor o igual a 21 caracteres, lo dejamos como está
    }

    // Vista principal
    HeaderFooterSubView(
        title = "Mi Finca",
        currentView = "Fincas",
        navController = navController,
        onBackClick = { navController.navigate(Routes.FarmView) },
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
                if (isLoading) {
                    // Mostrar un indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cargando datos de la finca...",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar el error si ocurrió algún problema
                    Text(text = errorMessage, color = Color.Red)
                } else {

                    // Mostrar el rol seleccionado
                    Text(text = "Su rol es: ${selectedRole.ifEmpty { "Sin rol" }}", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar para filtrar lotes por nombre
                    if (userHasPermissionReadPlots) {
                        ReusableSearchBar(
                            query = searchQuery,
                            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                            text = "Buscar lote por nombre",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    val farmAreaInt by viewModel.farmArea.map { it.toInt().toString() }.collectAsState("")

                    // Componente reutilizable de Información General
                    GeneralInfoCard(
                        farmName = displayedFarmName,
                        farmArea = farmAreaInt,
                        farmUnitMeasure = unitOfMeasure,
                        onEditClick = { viewModel.onEditFarm(navController, farmId, farmName, farmArea, unitOfMeasure) },
                        showEditButton = userHasPermissionToEditFarm // Solo muestra el botón si tiene el permiso
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                        if (userHasPermissionReadCollaborators) {
                            ActionCard(
                                buttonText = "Colaboradores", // Texto para el primer botón
                                onClick = {
                                    val roleToSend = viewModel.selectedRole.value
                                    Log.d("FarmInformationView", "Navigating to CollaboratorView with role: $roleToSend")

                                    navController.navigate("CollaboratorView/$farmId/$farmName/$roleToSend")
                                }
                            )
                        }
                        if (userHasPermissionReadReports) {

                            ActionCard(
                                buttonText = "Reportes", // Texto para el segundo botón
                                onClick = {
                                    navController.navigate("${Routes.ReportsSelectionView}/$farmId/$farmName")
                                }

                            )
                        }


                    Spacer(modifier = Modifier.height(16.dp))
                    if (userHasPermissionReadPlots) {
                        // Lista de Lotes usando el LotesList personalizado
                        LotesList(
                            lotes = lotes, // Utilizar la lista filtrada de lotes
                            modifier = Modifier.fillMaxWidth(),
                            onLoteClick = { lote ->
                                navController.navigate(
                                    "PlotInformationView/${lote.plot_id}/$farmName/$farmId"
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // Ajusta la altura según el tamaño del botón flotante
                }
            }
            if (userHasPermissionAddPlots && errorMessage.isEmpty()){
            CustomFloatingActionButton(
                onAddClick = {
                    // Navegamos a CreatePlotInformationView con el farmId
                    navController.navigate("${Routes.CreatePlotInformationView}/$farmId")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmInformationViewPreview() {
    CoffeTechTheme {
        FarmInformationView(
            navController = NavController(LocalContext.current),
            farmId = 1 // Valor simulado de farmId para la previsualización
        )
    }
}
