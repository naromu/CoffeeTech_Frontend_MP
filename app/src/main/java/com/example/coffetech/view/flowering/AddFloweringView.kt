package com.example.coffetech.view.flowering

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.coffetech.common.DatePickerComposable
import com.example.coffetech.common.FloweringNameDropdown
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.flowering.AddFloweringViewModel


/**
 * Función composable que muestra la vista de agregar floración, permitiendo a los usuarios añadir información de floración.
 *
 * @param navController El controlador de navegación para manejar acciones de navegación.
 * @param plotId El ID de la parcela donde se está añadiendo la floración.
 * @param viewModel El ViewModel que gestiona el estado y lógica para agregar datos de floración.
 */

@Composable
fun AddFloweringView(
    navController: NavController,
    plotId: Int,
    viewModel: AddFloweringViewModel = viewModel()
) {

    // Variables para obtener el contexto y observar los valores de estado del ViewModel
    val context = LocalContext.current
    val selectedFloweringName by viewModel.selectedFloweringName.collectAsState()
    val floweringName by viewModel.floweringName.collectAsState()
    val flowering_date by viewModel.flowering_date.collectAsState()
    val harvest_date by viewModel.harvest_date.collectAsState() // Corregido aquí
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFormSubmitted by viewModel.isFormSubmitted.collectAsState()

    // Cargar los tipos de floración en el primer lanzamiento
    LaunchedEffect(Unit) {
        viewModel.loadFloweringTypes()
    }

    // Contenedor principal para el diseño del formulario de Agregar Floración
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

                // Título del formulario

                Text(
                    text = "Agregar Floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(45.dp))
                Text(
                    text = "Tipo de floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))


                // Desplegable para seleccionar el nombre de la floración
                FloweringNameDropdown(
                    selectedFloweringName = selectedFloweringName,
                    onFloweringNameChange = { viewModel.onFloweringNameChange(it) },
                    flowerings = floweringName,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier.fillMaxWidth()
                )


                // Etiqueta para la fecha de floración
                Text(
                    text = "Fecha de Floración",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Selector de fecha para la fecha de floración
                DatePickerComposable(
                    label = "Fecha de floración",
                    selectedDate = flowering_date,
                    onDateSelected = { viewModel.onFloweringDateChange(it) },
                    errorMessage = if (isFormSubmitted && flowering_date.isBlank()) "La fecha de floración no puede estar vacía." else null
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Selector de fecha para la fecha de floración
                Text(
                    text = "Fecha de Cosecha (Opcional)",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Selector de fecha para la fecha de cosecha, con opción de limpiar la fecha
                DatePickerComposable(
                    label = "Fecha de cosecha",
                    selectedDate = harvest_date,
                    onDateSelected = { viewModel.onHarvestDateChange(it) },
                    onClearDate = { viewModel.clearHarvestDate() }, // Pasar el callback para limpiar la fecha
                    errorMessage = null // Opcional, ya que es opcional
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                }

                // Botón para enviar el formulario, desactivado si está cargando o hay un error
                ReusableButton(
                    text = if (isLoading) "Creando..." else "Crear",
                    onClick = {
                        viewModel.onCreate(navController, context, plotId)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = !isLoading && errorMessage.isEmpty() // Activado solo si no está cargando y no hay error
                )
            }
        }
    }
}


/**
 * Función de vista previa para mostrar la vista de Agregar Floración en el modo de vista previa de Android Studio.
 */

@Preview(showBackground = true)
@Composable
fun AddFloweringViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        AddFloweringView(
            navController = mockNavController,
            plotId = 1, // Ejemplo de ID de la finca
        )
    }
}
