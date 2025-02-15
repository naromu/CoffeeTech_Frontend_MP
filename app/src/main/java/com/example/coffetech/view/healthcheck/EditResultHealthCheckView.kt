package com.example.coffetech.view.healthcheck

import android.content.Context
import com.example.coffetech.common.ReusableAlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.DetectionResultInfoCard
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.viewmodel.Collaborator.EditCollaboratorViewModel
import com.example.coffetech.viewmodel.healthcheck.EditResultHealthCheckViewModel


// EditResultHealthCheckView.kt
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun EditResultHealthCheckView(
    navController: NavController,
    farmName: String,
    plotName: String,
    detectionId: Int,
    prediction: String,
    recommendation: String,
    date: String,
    performedBy: String,
    viewModel: EditResultHealthCheckViewModel = viewModel()
) {
    val context = LocalContext.current
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    // Obtener el token de sesión desde SharedPreferences u otro método
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() ?: "" }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    // Decodificar los parámetros de tipo String
    val decodedFarmName = URLDecoder.decode(farmName, StandardCharsets.UTF_8.toString())
    val decodedPlotName = URLDecoder.decode(plotName, StandardCharsets.UTF_8.toString())
    val decodedPrediction = URLDecoder.decode(prediction, StandardCharsets.UTF_8.toString())
    val decodedRecommendation = URLDecoder.decode(recommendation, StandardCharsets.UTF_8.toString())
    val decodedDate = URLDecoder.decode(date, StandardCharsets.UTF_8.toString())
    val decodedPerformedBy = URLDecoder.decode(performedBy, StandardCharsets.UTF_8.toString())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Contenedor ocupa el 95% del ancho
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(scrollState)
            ) {
                // Botón de cerrar o volver (BackButton)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Editar Resultado de Salud",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Finca: $farmName",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )
                Text(
                    text = "Lote: $plotName",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar los parámetros como título y contenido
                ParameterDisplay(title = "Predicción", content = decodedPrediction)
                ParameterDisplay(title = "Recomendación", content = decodedRecommendation)
                ParameterDisplay(title = "Fecha", content = decodedDate)
                ParameterDisplay(title = "Realizado por", content = decodedPerformedBy)

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReusableButton(
                    text = if (isLoading) "Regresando..." else "Volver",
                    onClick = { navController.popBackStack() }, // Acción de volver
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReusableButton(
                    text = if (isLoading) "Cargando..." else "Eliminar",
                    onClick = { showDeleteConfirmation.value = true },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Red,
                )

                val image = painterResource(id = R.drawable.delete_confirmation_icon)
                // Confirmación para eliminar detección
                if (showDeleteConfirmation.value) {
                    ReusableAlertDialog(
                        title = "¡ESTA ACCIÓN\nES IRREVERSIBLE!",
                        description = "Todos tus datos relacionados a esta detección se perderán. ¿Deseas continuar?",
                        confirmButtonText = "Descartar detección",
                        cancelButtonText = "Cancelar",
                        isLoading = isLoading,
                        onConfirmClick = {
                            viewModel.deleteDetection(context = context, navController = navController, sessionToken = sessionToken, detectionId = detectionId)
                            showDeleteConfirmation.value = false
                        },
                        onCancelClick = { showDeleteConfirmation.value = false },
                        onDismissRequest = { showDeleteConfirmation.value = false },
                        image = image
                    )
                }
            }
        }
    }
}

@Composable
fun ParameterDisplay(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color(0xFF3F3D3D)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

}


@Preview(showBackground = true)
@Composable
fun EditResultHealthCheckViewPreview() {
    val mockNavController = rememberNavController()
    CoffeTechTheme {
        EditResultHealthCheckView(
            navController = mockNavController,
            farmName = URLEncoder.encode("Finca Ejemplo", StandardCharsets.UTF_8.toString()),
            plotName = URLEncoder.encode("Lote 1", StandardCharsets.UTF_8.toString()),
            detectionId = 1,
            prediction = URLEncoder.encode("Positivo", StandardCharsets.UTF_8.toString()),
            recommendation = URLEncoder.encode("Aplicar tratamiento X", StandardCharsets.UTF_8.toString()),
            date = URLEncoder.encode("2024-04-27", StandardCharsets.UTF_8.toString()),
            performedBy = URLEncoder.encode("Juan Pérez", StandardCharsets.UTF_8.toString())
        )
    }
}

