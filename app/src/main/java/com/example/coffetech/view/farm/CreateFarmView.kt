package com.example.coffetech.view.farm

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.UnitDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.farm.CreateFarmViewModel
/**
 * Composable function that renders a view for creating a new farm.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [CreateFarmViewModel] that manages the state and logic for creating a farm.
 */
@Composable
fun CreateFarmView(
    navController: NavController,
    viewModel: CreateFarmViewModel = viewModel()
) {
    val context = LocalContext.current
    val farmName by viewModel.farmName.collectAsState()
    val farmArea by viewModel.farmArea.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val areaUnits by viewModel.areaUnits.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isFormSubmitted = remember { mutableStateOf(false) }


    // Cargar unidades de medida al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUnitMeasuresFromSharedPreferences(context)
    }

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
                    .verticalScroll(rememberScrollState()) // Hace que el contenido sea scrolleable

            ) {
                // Botón de cerrar o volver (BackButton)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp) // Tamaño más manejable
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crear Finca",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
              // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF49602D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(45.dp))

                Text(
                    text = "Nombre",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Nombre de finca utilizando ReusableTextField
                ReusableTextField(
                    value = farmName,
                    onValueChange = { viewModel.onFarmNameChange(it) },
                    placeholder = "Nombre de finca",
                    modifier = Modifier.fillMaxWidth(), // Asegurar que ocupe todo el ancho disponible
                    isValid = farmName.isNotEmpty() || !isFormSubmitted.value,
                    charLimit = 50,
                    errorMessage = if (farmName.isEmpty() && isFormSubmitted.value) "El nombre de la finca no puede estar vacío" else ""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Área",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Área de la finca utilizando ReusableTextField
                ReusableTextField(
                    value = farmArea,
                    onValueChange = { viewModel.onFarmAreaChange(it) },
                    placeholder = "Área de finca",
                    modifier = Modifier.fillMaxWidth(), // Asegurar que ocupe todo el ancho disponible
                    isValid = farmArea.isNotEmpty() || !isFormSubmitted.value,
                    charLimit = 4,
                    isNumeric = true,
                    errorMessage = if (farmArea.isEmpty() && isFormSubmitted.value) "El área de la finca no puede estar vacía" else ""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Unidad de Medida",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))


                // Unidad de medida
                UnitDropdown(
                    selectedUnit = selectedUnit,
                    onUnitChange = { viewModel.onUnitChange(it) },
                    units = areaUnits,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier.fillMaxWidth()
                )


                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para crear finca
                ReusableButton(
                    text = if (isLoading) "Creando..." else "Crear",
                    onClick = {
                        isFormSubmitted.value = true
                        val trimmedFarmName = farmName.trim()  // Eliminar espacios antes de enviar
                        val trimmedFarmArea = farmArea.trim()  // Eliminar espacios antes de enviar

                        if (trimmedFarmName.isNotEmpty() && trimmedFarmArea.isNotEmpty()) {
                            viewModel.onFarmNameChange(trimmedFarmName)  // Guardar la versión recortada
                            viewModel.onFarmAreaChange(trimmedFarmArea)  // Guardar la versión recortada
                            viewModel.onCreate(navController, context)   // Enviar los datos
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,  // Usar el botón con color rojo
                    enabled = !isLoading
                )

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CreateFarmViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        CreateFarmView(navController = mockNavController)
    }
}