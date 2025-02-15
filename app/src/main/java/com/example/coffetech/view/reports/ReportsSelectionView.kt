package com.example.coffetech.view.reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.reports.ReportsSelectionViewModel
import com.example.coffetech.viewmodel.farm.FarmInformationViewModel

/**
 * Composable function that renders a view displaying detailed information about a specific farm.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm whose information is to be displayed.
 * @param viewModel The [FarmInformationViewModel] that manages the state and logic for displaying farm information.
 */
@Composable
fun ReportsSelectionView(
    navController: NavController,
    farmId: Int,
    farmName: String,
    viewModel: ReportsSelectionViewModel = viewModel() // Inyecta el ViewModel aquí
) {
    // Obtener el contexto para acceder a SharedPreferences o cualquier otra fuente del sessionToken
    val context = LocalContext.current

    // Vista principal
    HeaderFooterSubView(
        title = "Mi Finca",
        currentView = "Fincas",
        navController = navController,
        onBackClick = {navController.navigate("FarmInformationView/${farmId}") },
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Finca: $farmName",
                        color = Color.Black,
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }

                ActionCard(
                    buttonText = "Reporte financiero", // Texto para el primer botón
                    onClick = {

                        navController.navigate("${Routes.FormFinanceReportView}/$farmId/$farmName")

                       /* navController.navigate("CollaboratorView/$farmId/$farmName/$roleToSend")*/
                    }
                )

                ActionCard(
                    buttonText = "Reporte general de detecciones", // Texto para el primer botón
                    onClick = {
                        navController.navigate("${Routes.FormDetectionReportView}/$farmId/$farmName")

                    }
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmInformationViewPreview() {
    CoffeTechTheme {
        ReportsSelectionView(
            navController = NavController(LocalContext.current),
            farmId = 1 ,// Valor simulado de farmId para la previsualización
            farmName= "Finquita"
        )
    }
}
