package com.example.coffetech.view.Transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.TransactionCategoryDropdown
import com.example.coffetech.common.TransactionTypeDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Transaction.AddTransactionViewModel

@Composable
fun AddTransactionView(
    navController: NavController,
    plotId: Int,
    viewModel: AddTransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedTransactionType by viewModel.selectedTransactionType.collectAsState()
    val transactionTypes by viewModel.transactionTypes.collectAsState()
    val selectedTransactionCategory by viewModel.selectedTransactionCategory.collectAsState()
    val transactionCategories by viewModel.transactionCategories.collectAsState()
    val valor by viewModel.valor.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFormSubmitted by viewModel.isFormSubmitted.collectAsState()

    val transactionTypeError by viewModel.transactionTypeError.collectAsState()
    val transactionCategoryError by viewModel.transactionCategoryError.collectAsState()
    val valorError by viewModel.valorError.collectAsState()
    val descripcionError by viewModel.descripcionError.collectAsState()
    val fechaError by viewModel.fechaError.collectAsState()

    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .windowInsetsPadding(WindowInsets.systemBars)
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
                    text = "Agregar Transacción",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(45.dp))

                // Tipo de Transacción
                Text(
                    text = "Tipo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TransactionTypeDropdown(
                    selectedTransactionType = selectedTransactionType,
                    transactionTypes = transactionTypes,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    onTransactionTypeChange = { viewModel.onTransactionTypeChange(it) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = true,
                    showArrow = true,
                    placeholder = "Seleccione tipo de transacción"
                )

                // Mensaje de error para el tipo de transacción
                if (transactionTypeError != null) {
                    Text(
                        text = transactionTypeError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Categoría de Transacción
                Text(
                    text = "Categoría",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TransactionCategoryDropdown(
                    selectedTransactionCategory = selectedTransactionCategory,
                    transactionCategories = transactionCategories,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    onTransactionCategoryChange = { viewModel.onTransactionCategoryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = selectedTransactionType.isNotBlank(), // Habilitar solo si se seleccionó el tipo
                    showArrow = true,
                    placeholder = "Seleccione categoría de transacción"
                )

                // Mensaje de error para la categoría de transacción
                if (transactionCategoryError != null) {
                    Text(
                        text = transactionCategoryError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Valor
                Text(
                    text = "Valor",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))

                ReusableTextField(
                    value = valor,
                    onValueChange = { viewModel.onValorChange(it) },
                    placeholder = "Ingrese el valor",
                    modifier = Modifier.fillMaxWidth(),
                    isValid = valorError == null,
                    charLimit = 10,
                    isNumeric = true,
                    errorMessage = valorError
                        ?: "", // Proporciona una cadena vacía si valorError es null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))

                ReusableTextField(
                    value = descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    placeholder = "Ingrese la descripción",
                    modifier = Modifier.fillMaxWidth(),
                    isValid = descripcionError == null,
                    charLimit = 50, // Asumiendo un límite de 255 caracteres
                    isNumeric = false,
                    errorMessage = descripcionError ?: "",
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fecha
                Text(
                    text = "Fecha",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(2.dp))

                DatePickerComposable(
                    label = "Fecha de transacción",
                    selectedDate = fecha,
                    onDateSelected = { viewModel.onFechaChange(it) },
                    errorMessage = when {
                        isFormSubmitted && fecha.isBlank() -> "La fecha no puede estar vacía."
                        fechaError != null -> fechaError
                        else -> ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje de error general si existe
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Botón "Crear"
                ReusableButton(
                    text = if (isLoading) "Creando..." else "Crear",
                    onClick = {
                        viewModel.onCreate(navController, context, plotId)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = isButtonEnabled && !isLoading // Habilitado solo si el formulario es válido y no está cargando
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddFloweringViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        AddTransactionView(
            navController = mockNavController,
            plotId = 1, // Ejemplo de ID de la finca
        )
    }
}
