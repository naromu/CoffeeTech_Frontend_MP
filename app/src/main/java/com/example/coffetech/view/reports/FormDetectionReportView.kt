package com.example.coffetech.view.reports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.DatePickerComposable
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.components.PlotsList
import com.example.coffetech.viewmodel.FormFinanceReportViewModel
import com.example.coffetech.viewmodel.reports.FormDetectionReportViewModel

@Composable
fun FormDetectionReportView(
    navController: NavController,
    farmId: Int,
    farmName: String,
    viewModel: FormDetectionReportViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val allPlots by viewModel.allPlots.collectAsState()
    val selectedPlotIds by viewModel.selectedPlotIds.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    val allSelected = allPlots.isNotEmpty() && selectedPlotIds.size == allPlots.size

    val context = LocalContext.current
    var includeTransactionHistory by remember { mutableStateOf(false) } // Nuevo estado para el checkbox

    LaunchedEffect(Unit) {
        viewModel.loadPlots(farmId, context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                // Botón de retroceso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp)
                    )
                }
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF5D8032),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }else {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Título
                    Text(
                        text = "Reporte general de detecciones",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF49602D)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre de la finca
                    Text(
                        text = "Finca: $farmName",
                        style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF94A84B)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Dropdown de lotes
                    Text(
                        text = "Lotes",
                        textAlign = TextAlign.Center,

                        style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3F3D3D)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PlotsList(
                        plots = allPlots,
                        selectedPlotIds = selectedPlotIds,
                        onPlotToggle = { plotId ->
                            viewModel.togglePlotSelection(plotId)
                        },
                        onSelectAllToggle = {
                            if (allSelected) {
                                viewModel.deselectAllPlots()
                            } else {
                                viewModel.selectAllPlots()
                            }
                        },
                        allSelected = allSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fecha de inicio
                    Text(
                        text = "Fecha de Inicio",
                        textAlign = TextAlign.Center,

                        style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3F3D3D)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerComposable(
                        label = "Fecha de inicio",
                        selectedDate = startDate ?: "",
                        onDateSelected = { date ->
                            viewModel.updateStartDate(date)
                        },
                        errorMessage = if (!isFormValid && startDate.isNullOrBlank()) "La fecha de inicio es requerida." else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fecha de fin
                    Text(
                        text = "Fecha de Fin",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3F3D3D)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerComposable(
                        label = "Fecha de fin",
                        selectedDate = endDate ?: "",
                        onDateSelected = { date ->
                            viewModel.updateEndDate(date)
                        },
                        errorMessage = if (!isFormValid && endDate.isNullOrBlank()) "La fecha de fin es requerida." else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    // Mostrar mensaje de error si existe
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de generación de reporte
                    ReusableButton(
                        text = "Generar Reporte",
                        onClick = {
                            viewModel.onSubmit(navController) // Paso el estado del checkbox
                            // Implementa la lógica para generar el reporte
                            // Por ejemplo, navegar a otra pantalla o mostrar un diálogo de éxito
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        buttonType = com.example.coffetech.common.ButtonType.Green
                    )
                }


                // Mostrar indicador de carga

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormFinanceReportPreview() {
    val navController = rememberNavController()
    CoffeTechTheme {
        FormDetectionReportView(
            navController = navController,
            farmId = 1,
            farmName = "Finca El Paraíso"
        )
    }
}
