package com.example.coffetech.common

import androidx.compose.runtime.Composable
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffetech.R
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateDropdown(
    selectedDate: String?,  // Cambiado a String? para aceptar valores nulos
    onDateChange: (String?) -> Unit, // onRoleChange ahora acepta String? para manejar la deselección
    dates: List<String>,
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
                shape = RoundedCornerShape(10.dp)
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
                    text = selectedDate ?: "Fecha", // Muestra "Todos los roles" si es null
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
                    onDateChange(null) // Pasa null para quitar el filtro de roles
                    onExpandedChange(false)
                }
            )

            // Opciones de roles reales
            dates.forEach { date ->
                DropdownMenuItem(
                    text = { Text(
                        date,
                        color = Color.Black) },
                    onClick = {
                        onDateChange(date)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
@Composable
fun CheckingInfoCard(
    date: String,
    collaboratorName: String,
    prediction: String,        // Nuevo parámetro para la predicción
    recommendation: String,     // Nuevo parámetro para la recomendación
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = false
) {
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
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp) // Agrega espacio al final de la columna
            ) {
                // Sección para mostrar la predicción
                Text(
                    text = "Predicción: $prediction",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Sección para mostrar la recomendación
                Text(
                    text = "Recomendación: $recommendation",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Fecha: $date",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Hecha por: $collaboratorName",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            // Icono de edición
            if (showEditIcon) {
                Spacer(modifier = Modifier.width(8.dp)) // Agrega espacio entre la columna y el icono
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp) // Aumenta el tamaño del botón para que sea más fácil de presionar
                        .background(Color(0xFFB31D34), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "Editar Información Colaborador",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetectionResultInfoCard(
    imagen_numero: Int,
    prediction: String,
    recommendation: String,
    image: ImageBitmap? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF95A94B), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column( // Cambiamos Row por Column para que los elementos estén en filas separadas
            verticalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre filas
            horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mostrar la imagen en la primera fila
            if (image != null) {

                Image(
                    bitmap = image,
                    contentDescription = "Imagen de la detección",
                    modifier = Modifier
                        .fillMaxWidth() // La imagen ocupa todo el ancho disponible
                        .height(200.dp) // Altura fija para la imagen
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }
            // Mostrar los textos en filas separadas
            Text(
                text = "Número de la imagen: $imagen_numero",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth() // El texto ocupa todo el ancho disponible
            )

            Text(
                text = "Predicción: $prediction",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth() // El texto ocupa todo el ancho disponible
            )

            Text(
                text = "Recomendación: $recommendation",
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth() // El texto ocupa todo el ancho disponible
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CheckingInfoCardPreview() {
    CoffeTechTheme {
        CheckingInfoCard(
            date= "24-02-34",
            collaboratorName = "Nombre",
            prediction= " alta de nutrientes",
            recommendation= "Hacer una siembra con caca",
            onEditClick = {},
            showEditIcon = true,
        )
    }

}




