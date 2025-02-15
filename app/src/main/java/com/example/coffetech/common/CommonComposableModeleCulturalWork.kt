// GenericDropdown.kt
package com.example.coffetech.view.CulturalWorkTask

import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.coffetech.R
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.model.Collaborator
import com.example.coffetech.model.CulturalWorkTask
import com.example.coffetech.model.GeneralCulturalWorkTask
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// GenericDropdown.kt
@Composable
fun GenericDropdown(
    modifier: Modifier = Modifier, // Añadido
    selectedOption: String?, // Valor seleccionado, puede ser null
    onOptionSelected: (String?) -> Unit, // Callback cuando se selecciona una opción
    options: List<String>, // Lista de opciones
    expanded: Boolean, // Estado de expansión del dropdown
    onExpandedChange: (Boolean) -> Unit, // Callback para cambiar el estado de expansión
    label: String, // Texto de etiqueta o placeholder
    expandedArrowDropUp: Painter, // Icono cuando el dropdown está expandido
    arrowDropDown: Painter // Icono cuando el dropdown está cerrado
) {
    Box(
        modifier = modifier // Aplicar el modifier aquí
            .wrapContentWidth()
            .padding(bottom = 15.dp)
            .padding(horizontal = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
    ) {
        OutlinedButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = selectedOption ?: label,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) expandedArrowDropUp else arrowDropDown,
                    contentDescription = if (expanded) "Collapse dropdown" else "Expand dropdown",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF5D8032)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(Color.White)
        ) {
            // Opción para deseleccionar (mostrar todas las opciones)
            DropdownMenuItem(
                text = { Text(text = "Todos") },
                onClick = {
                    onOptionSelected(null)
                    onExpandedChange(false)
                }
            )

            // Opciones dinámicas
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, color = Color.Black) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun CulturalWorkTaskGeneralCard(
    task: GeneralCulturalWorkTask,
    farmName: String,
    plotName: String,
    onClick: () -> Unit // Agregamos el parámetro onClick
) {
    val context = LocalContext.current

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Intenta parsear y formatear la fecha
    val formattedDate = try {
        inputFormat.parse(task.task_date)?.let { outputFormat.format(it) }
    } catch (e: ParseException) {
        task.task_date
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Nombre de la tarea
            Text(
                text = task.cultural_works_name,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Asignado a
            Text(
                text = "Asignada por: ${task.owner_name}",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Estado
            Text(
                text = "Estado: ${task.status}",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Fecha formateada
            Text(
                text = "Fecha: $formattedDate",
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Información de la finca y lote
            Text(text = "Finca: $farmName", color = Color.Black, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(2.dp))

            Text(text = "Lote: $plotName", color = Color.Black, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Comenzar, usando el onClick recibido
            ReusableButton(
                text = "Comenzar",
                onClick = onClick, // Usamos el onClick pasado como parámetro
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                buttonType = ButtonType.Green
            )
        }
    }
}


@Composable
fun CulturalWorkTaskCard(
    task: CulturalWorkTask,
    onEdit: (() -> Unit)? = null
) {
    // Define el formato de entrada y el formato deseado
    val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Intenta parsear la fecha de la tarea
    val parsedDate: Date? = try {
        inputFormat.parse(task.task_date)
    } catch (e: ParseException) {
        null
    }

    // Formatea la fecha si el parseo fue exitoso
    val formattedDate = parsedDate?.let { outputFormat.format(it) } ?: task.task_date

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f) .padding(end = 40.dp)
            ) {
                // Nombre de la tarea
                Text(
                    text = task.cultural_works_name,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Asignado a
                Text(
                    text = "Asignado a: ${task.collaborator_name}",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Asignado por: ${task.owner_name}",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Estado
                Text(
                    text = "Estado: ${task.status}",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Fecha formateada
                Text(
                    text = "Fecha: $formattedDate",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Icono de edición
            if (task.status == "Por hacer") {
                IconButton(
                    onClick = { onEdit?.invoke() },
                    modifier = Modifier
                        .offset(x = -10.dp)
                        .size(20.dp)
                        .background(Color(0xFFB31D34), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "Editar Información Tarea Cultural",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}






@Composable
fun TypeCulturalWorkDropdown(
    selectedCulturalWork: String,
    cultural_work: List<String>,  // Parámetro para unidades dinámicas
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    placeholder: String = "Selecciona un tipo", // Añadido
    onTypeCulturalWorkChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp)
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
                        text = if (selectedCulturalWork.isEmpty()) placeholder else selectedCulturalWork, // Modificado
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
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            cultural_work.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(
                        text = unit,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onTypeCulturalWorkChange(unit)
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
fun DateDropdown(
    selectedDate: String,
    dates: List<String>,  // Parámetro para unidades dinámicas
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp)
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
                        text = selectedDate,
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
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            dates.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(
                        text = unit,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onDateChange(unit)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 0.dp), // Puedes ajustar este valor para reducir el espaciado
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp)
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



@Composable
fun CulturalTaskFilterDropdowns(
    selectedStatusFilter: String,
    onStatusFilterChange: (String) -> Unit,
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
        // Dropdown para Filtrar por Estado
        StatusFilterDropdown(
            selectedStatus = selectedStatusFilter,
            onSelectedStatusChange = onStatusFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp) // Espaciado entre los dropdowns
        )

        // Dropdown para Ordenar las Tareas
        OrderFilterDropdown(
            selectedOrder = selectedOrderFilter,
            onSelectedOrderChange = onOrderFilterChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp) // Espaciado entre los dropdowns
        )
    }
}

@Composable
fun StatusFilterDropdown(
    selectedStatus: String,
    onSelectedStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Todos", "Por hacer", "Terminado")
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
                    text = selectedStatus,
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
                        onSelectedStatusChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OrderFilterDropdown(
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


@Composable
fun CollaboratorDropdownWithId(
    selectedCollaboratorId: Int?,
    collaborators: List<Collaborator>,
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onCollaboratorChange: (Collaborator) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedCollaborator = collaborators.find { it.user_id == selectedCollaboratorId }
    val selectedCollaboratorName = selectedCollaborator?.name ?: "Seleccione un colaborador"

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
                .size(width = 300.dp, height = 56.dp)
        ) {
            OutlinedButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp)
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
                        text = selectedCollaboratorName,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            collaborators.forEach { collaborator ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = collaborator.name.trim(),
                            fontSize = 14.sp,
                            color = Color.Black,
                        )
                    },
                    onClick = {
                        onCollaboratorChange(collaborator)
                        expanded = false
                    },
                    modifier = Modifier.padding(vertical = 0.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp)
                )
            }
        }
    }
}

@Composable
fun CollaboratorDropdown(
    selectedCollaboratorName: String,
    collaborators: List<Collaborator>,
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onCollaboratorChange: (Collaborator) -> Unit,
    modifier: Modifier = Modifier
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
                .size(width = 300.dp, height = 56.dp)
        ) {
            OutlinedButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF49602D)
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 4.dp)
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
                        text = selectedCollaboratorName,
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
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(max = 200.dp)
        ) {
            collaborators.forEach { collaborator ->
                DropdownMenuItem(
                    text = { Text(
                        text = collaborator.name.trim(),
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onCollaboratorChange(collaborator)
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
fun CulturalTaskFilterDropdowns(
    farmOptions: List<String>,
    selectedFarm: String,
    onFarmSelected: (String) -> Unit,
    plotOptions: List<String>,
    selectedPlot: String,
    onPlotSelected: (String) -> Unit,
    selectedOrder: String,
    onOrderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Dropdown para Finca
            FarmFilterDropdown(
                options = farmOptions,
                selectedFarm = selectedFarm,
                onSelectedFarmChange = onFarmSelected,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            // Dropdown para Lote
            PlotFilterDropdown(
                options = plotOptions,
                selectedPlot = selectedPlot,
                onSelectedPlotChange = onPlotSelected,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dropdown para Ordenar
            OrderFilterDropdown(
                selectedOrder = selectedOrder,
                onSelectedOrderChange = onOrderSelected,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            // Espacio vacío para igualar el ancho
            Spacer(modifier = Modifier.weight(1f))
        }

    }
}
@Composable
fun FarmFilterDropdown(
    options: List<String>,
    selectedFarm: String,
    onSelectedFarmChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF49602D)),
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
                    text = selectedFarm,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) painterResource(id = R.drawable.arrowdropup_icon)
                    else painterResource(id = R.drawable.arrowdropdown_icon),
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
                        onSelectedFarmChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PlotFilterDropdown(
    options: List<String>,
    selectedPlot: String,
    onSelectedPlotChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(20.dp))
    ) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF49602D)),
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
                    text = selectedPlot,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) painterResource(id = R.drawable.arrowdropup_icon)
                    else painterResource(id = R.drawable.arrowdropdown_icon),
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
                        onSelectedPlotChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
