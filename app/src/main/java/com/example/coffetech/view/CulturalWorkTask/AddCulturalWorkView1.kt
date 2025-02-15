package com.example.coffetech.view.CulturalWorkTask

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
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.CulturalWorkTask.AddCulturalWorkViewModel1


/**
 * Vista principal para añadir una nueva tarea de labor cultural.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre vistas.
 * @param plotId ID del lote en el que se añadirá la labor cultural.
 * @param plotName Nombre del lote asociado (opcional).
 * @param viewModel ViewModel que maneja el estado y la lógica para añadir una labor cultural.
 */

@Composable
fun AddCulturalWorkView1(
    navController: NavController,
    plotId: Int,
    plotName: String = "",
    viewModel: AddCulturalWorkViewModel1 = viewModel()
) {
    // Observación de los estados desde el ViewModel
    val flowering_date by viewModel.flowering_date.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val typeCulturalWorkList by viewModel.typeCulturalWorkList.collectAsState()
    val selectedTypeCulturalWork by viewModel.selectedTypeCulturalWork.collectAsState()
    val isFormSubmitted by viewModel.isFormSubmitted.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    // Efecto lanzado para inicializar la lista de tipos de labores culturales
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // Simula la carga de tipos de labores culturales
        viewModel.setTypeCulturalWorkList(listOf("Chequeo de Salud", "Chequeo de estado de maduración"))
    }

    // Efecto lanzado para actualizar el nombre del lote si está disponible
    LaunchedEffect(plotName) {
        if (plotName.isNotEmpty()) {
            viewModel.updatePlotName(plotName)
        }
    }

    // Caja contenedora principal con fondo oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenedor blanco donde se encuentra el formulario de la tarea
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Haz que el contenedor ocupe el 95% del ancho de la pantalla
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            // Columna que contiene los elementos del formulario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Botón para regresar a la vista anterior
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

                // Título de la vista
                Text(
                    text = "Añadir Labor",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF49602D)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Texto que muestra el nombre del lote
                Text(
                    text = "Lote: $plotName",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF94A84B)),
                    color = Color(0xFF94A84B),
                            modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(22.dp))

                // Etiqueta para el tipo de labor cultural
                Text(
                    text = "Tipo Labor Cultural",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3F3D3D)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Desplegable para seleccionar el tipo de labor cultural
                TypeCulturalWorkDropdown(
                    selectedCulturalWork = selectedTypeCulturalWork,
                    onTypeCulturalWorkChange = { selected ->
                        viewModel.setSelectedTypeCulturalWork(selected)
                    },
                    cultural_work = typeCulturalWorkList,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    placeholder = "Seleccione una labor cultural", // Añadido
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Etiqueta para seleccionar la fecha de la tarea
                Text(
                    text = "Fecha",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF3F3D3D)),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Selector de fecha para la tarea de labor cultural
                DatePickerComposable(
                    label = "Fecha de la tarea",
                    selectedDate = flowering_date,  // Pasa la fecha actual
                    onDateSelected = { date ->
                        // Actualiza la fecha seleccionada en el ViewModel
                        viewModel.updateFloweringDate(date)
                    },
                    errorMessage = if (isFormSubmitted && flowering_date.isBlank())
                        "La fecha de floración no puede estar vacía."
                    else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de "Siguiente" para proceder a la siguiente vista con los datos ingresados
                ReusableButton(
                    text = "Siguiente",
                    onClick = {
                        navController.navigate("${Routes.AddCulturalWorkView2}/$plotId/$plotName/${selectedTypeCulturalWork ?: ""}/${flowering_date}")

                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green
                )
            }
        }
    }
}




/**
 * Vista previa de la función AddCulturalWorkView1 para ver cómo se muestra en Android Studio.
 */

// Mueve la función Preview fuera de la función CreatePlotView
@Preview(showBackground = true)
@Composable
fun AddCulturalWorkView1Preview() {
    val navController = rememberNavController() // Usar rememberNavController para la vista previa

    CoffeTechTheme {
        AddCulturalWorkView1(
            navController = navController,
            plotId= 1
        )
    }
}
