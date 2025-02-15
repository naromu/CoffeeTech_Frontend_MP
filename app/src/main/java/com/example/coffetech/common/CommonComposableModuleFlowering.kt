package com.example.coffetech.common

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.model.Flowering
import com.example.coffetech.model.Plot
import com.example.coffetech.model.Task
import com.example.coffetech.ui.theme.CoffeTechTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FloweringGeneralInfoCard(
    flowering_type_name: String,
    status: String,
    flowering_date: String,
    onEditClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determinar si se debe mostrar el botón de información
    val showInfoButton = flowering_type_name != "Mitaca"

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
                modifier = Modifier.weight(1f) // Permite que el texto ocupe todo el espacio disponible
            ) {
                Text(
                    text = flowering_type_name,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = status,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = flowering_date,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )

            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Espaciado entre los botones
            ) {

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .offset(-9.dp)
                        .size(36.dp) // Tamaño del botón
                        .background(Color(0xFFB31D34), shape = CircleShape) // Rojo
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon), // Usa el icono de edición
                        contentDescription = "Editar Información General",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Mostrar el botón de información solo si showInfoButton es true
                if (showInfoButton) {
                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier
                            .size(36.dp) // Tamaño del botón
                            .background(Color(0xFF8AA72A), shape = CircleShape) // Verde
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.info_icon), // Usa el icono de información
                            contentDescription = "Información",
                            tint = Color.White,
                            modifier = Modifier.size(29.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun FloweringItemCard(
    flowering_type_name: String,
    status: String,
    harvest_date: String,
    onEditClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF5F5F5),
                RoundedCornerShape(12.dp)
            ) // Fondo gris claro con bordes redondeados
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f) // Permite que el texto ocupe todo el espacio disponible
            ) {
                Text(
                    text = flowering_type_name,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = status,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = harvest_date,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )

            }
        }
    }
}

@Composable
fun FloweringList(
    flowerings: List<Flowering>, // Lista de objetos Flowering
    modifier: Modifier = Modifier,
    onEditClick: (Flowering) -> Unit, // Lambda para manejar clic en editar
    onInfoClick: (Flowering) -> Unit // Lambda para manejar clic en info
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        flowerings.forEach { flowering ->
            Spacer(modifier = Modifier.height(8.dp))
            FloweringItemCard(
                flowering_type_name = flowering.flowering_type_name,
                status = flowering.status,
                harvest_date = flowering.harvest_date ?: "No hay fecha de cosecha",
                onEditClick = { onEditClick(flowering) },
                onInfoClick = { onInfoClick(flowering) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { } // Pasa el objeto Lote al hacer clic si es necesario
            )
        }
    }
}

@Composable
fun FloweringNameDropdown(
    selectedFloweringName: String?,  // Cambiado a String? para aceptar valores nulos
    onFloweringNameChange: (String?) -> Unit, // onRoleChange ahora acepta String? para manejar la deselección
    flowerings: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter
) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .padding(bottom = 15.dp)
            .padding(horizontal = 8.dp)
            .background(
                Color.White,
                shape = RoundedCornerShape(20.dp)
            ) // Fondo blanco con esquinas redondeadas
            .size(width = 200.dp, height = 32.dp) // Tamaño del área del botón
    ) {
        OutlinedButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .fillMaxWidth(), // Ajusta el tamaño para llenar el área blanca
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(Color.White),
            ) {
                Text(
                    text = selectedFloweringName ?: "General", // Muestra "Todos los roles" si es null
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // Manejo del desbordamiento con puntos suspensivos
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) expandedArrowDropUp else arrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF5D8032)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(Color.White),
        ) {
            // Opción para mostrar todas las fincas
            DropdownMenuItem(
                text = { Text("Todos los roles") },
                onClick = {
                    onFloweringNameChange(null) // Pasa null para quitar el filtro de roles
                    onExpandedChange(false)
                }
            )

            // Opciones de roles reales
            flowerings.forEach { flowering ->
                DropdownMenuItem(
                    text = { Text(
                        flowering,
                        color = Color.Black) },
                    onClick = {
                        onFloweringNameChange(flowering)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun FloweringNameDropdown(
    selectedFloweringName: String,
    flowerings: List<String>,  // Parámetro para unidades dinámicas
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onFloweringNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showArrow: Boolean = true

) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 5.dp)
                .padding(horizontal = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .size(width = 300.dp, height = 56.dp)
        ) {
            OutlinedButton(
                onClick = { if (enabled) expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp),
                enabled = enabled  // Aplicar el parámetro enabled
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
                        text = selectedFloweringName,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis, // Manejo del desbordamiento con puntos suspensivos
                        modifier = Modifier.weight(1f)
                    )
                    if (showArrow) { // Mostrar ícono solo si showArrow es true
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
            flowerings.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(
                        text = unit,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onFloweringNameChange(unit)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 0.dp), // Puedes ajustar este valor para reducir el espaciado
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp)
                )
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: String,
    start_date: String,
    end_date: String,
    programar: String,
    modifier: Modifier = Modifier,
    onProgramClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF5F5F5),
                RoundedCornerShape(12.dp)
            ) // Fondo gris claro con bordes redondeados
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Inicio: $start_date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fin: $end_date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )
            }

            if (programar.equals("Sí", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(8.dp))
                ReusableButton(
                    text = "Programar", // Texto que aparece en el botón
                    onClick = onProgramClick,
                    modifier = Modifier.align(Alignment.CenterVertically), // Centrado vertical y alineado a la izquierda
                    buttonType = ButtonType.Green, // Tipo de botón verde
                    minHeight = 48.dp, // Puedes ajustar los tamaños según sea necesario
                    minWidth = 120.dp
                )
            }
        }
    }
}


@Composable
fun TasksList(
    tasks: List<Task>, // Lista de objetos Task
    modifier: Modifier = Modifier,
    onProgramClick: (Task) -> Unit // Recibe la tarea para programar
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Recomendaciones",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        tasks.forEach { task ->
            Spacer(modifier = Modifier.height(8.dp))
            TaskItemCard(
                task = task.task,
                start_date = task.start_date,
                end_date = task.end_date,
                programar = task.programar,
                onProgramClick = { onProgramClick(task) }
            )
        }
    }
}

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
                            .fillMaxWidth(0.9f), // Ajusta el ancho del botón
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

@Composable
fun HistoryFilterDropdowns(
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
        // Dropdown para Filtrar por Tipo de Floración
        FloweringTypeFilterDropdown(
            selectedType = selectedTypeFilter,
            onSelectedTypeChange = onTypeFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp) // Espaciado entre los dropdowns
        )

        // Dropdown para Ordenar el Historial de Floraciones
        FloweringOrderDropdown(
            selectedOrder = selectedOrderFilter,
            onSelectedOrderChange = onOrderFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp) // Espaciado entre los dropdowns
        )
    }
}

@Composable
fun FloweringTypeFilterDropdown(
    selectedType: String,
    onSelectedTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Todos los tipos", "Principal", "Mitaca")
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
fun FloweringOrderDropdown(
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
            DatePickerDialog(
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



@Preview(showBackground = true)
@Composable
fun FloweringGeneralInfoCardPreview() {
    CoffeTechTheme {
        FloweringGeneralInfoCard(
            flowering_type_name = "Principal",
            status= "Activa",
            flowering_date = "15-08-2024",
            onEditClick = {},
            onInfoClick = {}
        )
    }
}