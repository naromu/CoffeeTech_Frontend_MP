package com.example.coffetech.common


import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffetech.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun ReusableAlertDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    isLoading: Boolean = false,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismissRequest: () -> Unit,
    image: Painter // El parámetro de la imagen se agrega aquí
) {
    AlertDialog(
        containerColor = Color.White,
        modifier = Modifier.background(Color.Transparent),
        onDismissRequest = { onDismissRequest() },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen en la parte superior
                Icon(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 207.dp, height = 160.dp)
                        .padding(bottom = 6.dp), // Espaciado debajo de la imagen
                    tint = Color.Unspecified // Evitar que se tiña la imagen
                )

                // Título
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB31D34), // Color rojo para el título
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            // Descripción del diálogo
            Text(
                text = description,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Espaciado interno del texto
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ReusableButton(
                        text = if (isLoading) "$confirmButtonText..." else confirmButtonText,
                        onClick = onConfirmClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.7f), // Ajusta el ancho del botón
                        buttonType = ButtonType.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre botones
                    ReusableButton(
                        text = cancelButtonText,
                        onClick = onCancelClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.7f), // Ajusta el ancho del botón
                        buttonType = ButtonType.Green
                    )
                }
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComposable(
    label: String,
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    onClearDate: (() -> Unit)? = null,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Formateador de fecha
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Convertir la fecha seleccionada a Calendar si no es nula
    selectedDate?.let {
        try {
            val date = dateFormat.parse(it)
            date?.let { parsedDate ->
                calendar.time = parsedDate
            }
        } catch (e: Exception) {
            // Manejar el error de parsing si es necesario
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    // Estilos similares a ReusableTextField
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate ?: "",
            onValueChange = {},
            label = { Text(text = label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { if (enabled) showDialog = true }, // Condicional
            enabled = false, // Deshabilitar la edición manual
            trailingIcon = {
                Row {
                    // Ícono de eliminar, visible solo si hay una fecha seleccionada y se ha proporcionado onClearDate
                    if (!selectedDate.isNullOrEmpty() && onClearDate != null) {
                        IconButton(
                            onClick = { onClearDate() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar fecha",
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)

                            )
                        }
                    }
                }
            },
            isError = errorMessage != null,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedBorderColor = if (errorMessage != null) Color.Red else Color(0xFF5D8032),
                unfocusedBorderColor = if (errorMessage != null) Color.Red else Color.Gray,
                disabledBorderColor = Color.Gray,
                containerColor = Color.White,
                errorBorderColor = Color.Red
            ),
            shape = RoundedCornerShape(4.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black,
                fontSize = 16.sp
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        if (showDialog) {
            android.app.DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    calendar.set(year, month, dayOfMonth)
                    val selected = dateFormat.format(calendar.time)
                    onDateSelected(selected)
                    showDialog = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setOnCancelListener {
                    showDialog = false
                }
                show()
            }
        }
    }
}


@Composable
fun TransactionTypeDropdown(
    selectedTransactionType: String,
    transactionTypes: List<String>,
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onTransactionTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showArrow: Boolean = true,
    placeholder: String = "Seleccione tipo de transacción"
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 5.dp)
                .padding(horizontal = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .height(56.dp)
        ) {
            OutlinedButton(
                onClick = { if (enabled) expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp),
                enabled = enabled
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(3.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTransactionType.isNotBlank()) selectedTransactionType else placeholder,
                        fontSize = 14.sp,
                        color = if (selectedTransactionType.isNotBlank()) Color.Black else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (showArrow) {
                        Icon(
                            painter = if (expanded) expandedArrowDropUp else arrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF5D8032)
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            transactionTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(
                        text = type,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onTransactionTypeChange(type)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 0.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp)
                )
            }
        }
    }
}

// TransactionCard.kt
@Composable
fun TransactionCard(
    transactionType: String, // "Ingreso" o "Gasto"
    transactionCategoryName: String, // Nuevo parámetro
    amount: String,
    description: String,
    date: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determinar el color basado en el tipo de transacción
    val isIncome = transactionType == "Ingreso"
    val textColor = if (isIncome) Color(0xFF8AA72A) else Color(0xFFB31D34)
    val backgroundColor = if (isIncome) Color(0xFF8AA72A) else Color(0xFFB31D34)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transactionType, // "Ingreso" o "Gasto"
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Categoría: $transactionCategoryName", // Mostrar la categoría
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Descripción: $description",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Fecha: $date",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "$ $amount",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(14.dp))

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(backgroundColor, shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "Editar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewIncomeCard() {
    TransactionCard(
        transactionType = "Ingreso",
        transactionCategoryName="Otros",
        amount = "40.000,00",
        description = "Descripción del ingreso",
        date = "01/11/2024",
        onEditClick = { /* Acción de edición */ }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewExpenseCard() {
    TransactionCard(
        transactionType = "Gasto",
        transactionCategoryName="Otros",
        amount = "20.000,00",
        description = "Descripción del gasto",
        date = "02/11/2024",
        onEditClick = { /* Acción de edición */ }
    )
}


@Composable
fun SaldoCard(
    balance: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Saldo",
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = "$ $balance",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSaldoCard() {
    SaldoCard(balance = "36.000,00")
}


@Composable
fun TransactionFilterDropdowns(
    selectedTypeFilter: String,
    onTypeFilterChange: (String) -> Unit,
    selectedOrderFilter: String,
    onOrderFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dropdown for Filtering by Transaction Type
        TransactionTypeFilterDropdown(
            selectedType = selectedTypeFilter,
            onSelectedTypeChange = onTypeFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp) // Spacing between the dropdowns
        )

        // Dropdown for Ordering Transactions
        TransactionOrderDropdown(
            selectedOrder = selectedOrderFilter,
            onSelectedOrderChange = onOrderFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp) // Spacing between the dropdowns
        )
    }
}

@Composable
fun TransactionTypeFilterDropdown(
    selectedType: String,
    onSelectedTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Todos", "Ingreso", "Gasto")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF49602D)
            ),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedType,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) painterResource(id = R.drawable.arrowdropup_icon) else painterResource(id = R.drawable.arrowdropdown_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF5D8032)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 150.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, color = Color.Black) },
                    onClick = {
                        onSelectedTypeChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionOrderDropdown(
    selectedOrder: String,
    onSelectedOrderChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Ordenar por", "Más antiguo", "Más reciente")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF49602D)
            ),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOrder,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) painterResource(id = R.drawable.arrowdropup_icon) else painterResource(id = R.drawable.arrowdropdown_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF5D8032)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 180.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, color = Color.Black) },
                    onClick = {
                        onSelectedOrderChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTransactionFilterDropdowns() {
    TransactionFilterDropdowns(
        selectedTypeFilter = "Todos",
        onTypeFilterChange = { /* Handle type filter change */ },
        selectedOrderFilter = "Ordenar por",
        onOrderFilterChange = { /* Handle order filter change */ }
    )
}


// TransactionCategoryDropdown.kt
@Composable
fun TransactionCategoryDropdown(
    selectedTransactionCategory: String,
    transactionCategories: List<String>,
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onTransactionCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showArrow: Boolean = true,
    placeholder: String = "Seleccione categoría de transacción"
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 5.dp)
                .padding(horizontal = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .height(56.dp)
        ) {
            OutlinedButton(
                onClick = { if (enabled) expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp),
                enabled = enabled
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(3.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTransactionCategory.isNotBlank()) selectedTransactionCategory else placeholder,
                        fontSize = 14.sp,
                        color = if (selectedTransactionCategory.isNotBlank()) Color.Black else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (showArrow) {
                        Icon(
                            painter = if (expanded) expandedArrowDropUp else arrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF5D8032)
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            transactionCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(
                        text = category,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onTransactionCategoryChange(category)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 0.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp)
                )
            }
        }
    }
}
