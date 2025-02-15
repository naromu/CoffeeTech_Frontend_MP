package com.example.coffetech.view.flowering

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.DatePickerComposable
import com.example.coffetech.common.FloweringNameDropdown
import com.example.coffetech.common.ReusableAlertDialog
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.RoleAddDropdown
import com.example.coffetech.common.VarietyCoffeeDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.Collaborator.AddCollaboratorView
import com.example.coffetech.viewmodel.flowering.AddFloweringViewModel
import com.example.coffetech.viewmodel.flowering.EditFloweringViewModel

/**
 * Función composable que muestra la vista para editar una floración, permitiendo a los usuarios actualizar
 * o eliminar una floración existente.
 *
 * @param navController Controlador de navegación para manejar acciones de navegación.
 * @param floweringId ID de la floración a editar.
 * @param floweringTypeName Nombre del tipo de floración.
 * @param floweringDate Fecha de la floración.
 * @param harvestDate Fecha de cosecha.
 * @param plotId ID de la parcela asociada con la floración.
 * @param viewModel ViewModel que gestiona el estado y lógica para editar una floración.
 */

@Composable
fun EditFloweringView(
    navController: NavController,
    floweringId: Int,
    floweringTypeName: String,
    floweringDate: String,
    harvestDate: String,
    plotId: Int,
    viewModel: EditFloweringViewModel = viewModel()
) {

    Log.d("EditFloweringView", "Iniciando EditFloweringView con ID: $floweringId")

    // Variables para obtener el contexto y observar los valores de estado del ViewModel
    val context = LocalContext.current
    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val currentFloweringName by viewModel.selectedFloweringName.collectAsState()
    val floweringName by viewModel.floweringName.collectAsState()
    val currentFlowering_date by viewModel.flowering_date.collectAsState()
    val currentHarvest_date by viewModel.harvest_date.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()


    // Inicializar la vista con datos existentes de la floración
    LaunchedEffect(Unit) {
        viewModel.initialize(
            floweringId = floweringId,
            floweringTypeName = floweringTypeName,
            floweringDate = floweringDate,
            harvestDate = harvestDate
        )
    }

    // Contenedor principal de la interfaz de edición de floración
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Haz que el contenedor ocupe el 95% del ancho de la pantalla
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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

                // Título de la pantalla de edición
                Text(
                    text = "Editar Floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(45.dp))

                // Etiqueta y desplegable para el tipo de floración, solo para visualización
                Text(
                    text = "Tipo de floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))
                FloweringNameDropdown(
                    selectedFloweringName = currentFloweringName,
                    onFloweringNameChange = {}, // No permitir cambios
                    flowerings = floweringName,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(vertical = 4.dp),
                    enabled = false,
                    showArrow = false
                )

                // Etiqueta para la fecha de floración, solo para visualización
                Text(
                    text = "Fecha de Floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))


                DatePickerComposable(
                    label = "Fecha de Floración",
                    selectedDate = currentFlowering_date,
                    onDateSelected = {}, // No permitir cambios
                    onClearDate = null, // No permitir limpiar
                    errorMessage = null,
                    enabled = false // Deshabilitar interacción
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Etiqueta para la fecha de cosecha, permitiendo cambios
                Text(
                    text = "Fecha de Cosecha",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))


                DatePickerComposable(
                    label = "Fecha de cosecha",
                    selectedDate = currentHarvest_date.ifEmpty { "" },
                    onDateSelected = { viewModel.onHarvestDateChange(it) },
                    errorMessage = null // Opcional, ya que es opcional
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                }
                // Botón para guardar cambios en la floración
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        viewModel.editFlowering(
                            context = context,
                            navController = navController
                        )
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp) // Ajuste de tamaño del botón
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = hasChanges && !isLoading && errorMessage.isEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre botones

                // Botón para eliminar la floracion
                ReusableButton(
                    text = if (isLoading) "Cargando..." else "Eliminar",
                    onClick = {showDeleteConfirmation.value = true},
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Red,
                )

                val image = painterResource(id = R.drawable.delete_confirmation_icon)

                // Diálogo de confirmación para eliminar la floración
                if (showDeleteConfirmation.value) {
                    ReusableAlertDialog(
                        title = "¡ESTA ACCIÓN\nES IRREVERSIBLE!",
                        description = "Esta floración se eliminará permanentemente. ¿Deseas continuar?",
                        confirmButtonText = "Eliminar",
                        cancelButtonText = "Cancelar",
                        isLoading = isLoading,
                        onConfirmClick = {
                            viewModel.deleteFlowering(
                                context = context,
                                navController = navController
                            )
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

/**
 * Función de vista previa para mostrar la vista de Editar Floración en el modo de vista previa de Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun EditFloweringViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        EditFloweringView(
            navController = mockNavController,
            floweringId = 1, // Ejemplo de ID de la finca
            floweringTypeName= "Principal",
            floweringDate= "15-08-2024",
            harvestDate= "",
            plotId = 1,
        )
    }
}
