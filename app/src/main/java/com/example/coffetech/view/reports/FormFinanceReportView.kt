// FormFinanceReport.kt
package com.example.coffetech.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.DatePickerComposable
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.components.PlotsList
import com.example.coffetech.viewmodel.FormFinanceReportViewModel

@Composable
fun FormFinanceReportView(
    navController: NavController,
    farmId: Int,
    farmName: String,
    viewModel: FormFinanceReportViewModel = viewModel()
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
                        text = "Reporte de Costos",
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

                    // Checkbox para historial de transacciones
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = includeTransactionHistory,
                            onCheckedChange = { includeTransactionHistory = it },
                            colors = CheckboxDefaults.colors(Color(0xFF5D8032))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Desea el historial de las transacciones para poderlo descargar en .csv y en el pdf?",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF3F3D3D)),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

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
                            viewModel.onSubmit(navController, includeTransactionHistory) // Paso el estado del checkbox
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
        FormFinanceReportView(
            navController = navController,
            farmId = 1,
            farmName = "Finca El Paraíso"
        )
    }
}
