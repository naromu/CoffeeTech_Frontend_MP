// EditTransactionView.kt
package com.example.coffetech.view.Transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.coffetech.common.*
import com.example.coffetech.model.Transaction
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Transaction.EditTransactionViewModel

@Composable
fun EditTransactionView(
    navController: NavController,
    transactionId: Int,
    transactionTypeName: String,
    transactionCategoryName: String,
    description: String,
    value: Long,
    transactionDate: String,
    viewModel: EditTransactionViewModel = viewModel()
) {
    val context = LocalContext.current

    // Cargar la transacción existente en el ViewModel
    LaunchedEffect(transactionId, transactionTypeName, transactionCategoryName, description, value, transactionDate) {
        viewModel.loadTransactionData(
            transactionId,
            transactionTypeName,
            transactionCategoryName,
            description,
            value,
            transactionDate
        )
    }

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

    val showDeleteConfirmation = remember { mutableStateOf(false) }

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
            .windowInsetsPadding(WindowInsets.systemBars) // Respeta los insets de la barra de estado y la barra de navegación
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
                    text = "Editar Transacción",
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
                    charLimit = 50, // Asumiendo un límite de 50 caracteres
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

                // Botón "Guardar"
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        viewModel.onSave(navController, context)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = isButtonEnabled && !isLoading // Habilitado solo si el formulario es válido y no está cargando
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

                // Icono de confirmación de eliminación
                val image = painterResource(id = R.drawable.delete_confirmation_icon)

                // Confirmación para eliminar la transacción
                if (showDeleteConfirmation.value) {
                    ReusableAlertDialog(
                        title = "¡ESTA ACCIÓN\nES IRREVERSIBLE!",
                        description = "Esta transacción se eliminará permanentemente. ¿Deseas continuar?",
                        confirmButtonText = "Eliminar",
                        cancelButtonText = "Cancelar",
                        isLoading = isLoading,
                        onConfirmClick = {
                            viewModel.deleteTransaction(
                                transactionId = transactionId,
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

    // Optional: Mostrar un indicador de carga sobre la vista
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTransactionViewPreview() {
    val mockNavController = rememberNavController()
    val sampleTransaction = Transaction(
        transaction_id = 16,
        plot_id = 6,
        transaction_type_name = "Ingreso",
        transaction_category_name = "Venta de café",
        description = "Venta cafécito",
        value = 36,
        transaction_date = "2024-11-03",
        status = "Activo"
    )
    CoffeTechTheme {
        EditTransactionView(
            navController = mockNavController,
            transactionId = 16,
            transactionTypeName = "Ingreso",
            transactionCategoryName = "Venta de café",
            description = "Venta cafécito",
            value = 36,
            transactionDate = "2024-11-03",

            )
    }
}
