package com.example.coffetech.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.zIndex
import com.example.coffetech.R
import com.example.coffetech.model.Plot

//FARM INFORMATION COMMONS COMPOSABLES---------------------------------
//FARM INFORMATION COMMONS COMPOSABLES---------------------------------
//FARM INFORMATION COMMONS COMPOSABLES---------------------------------
//FARM INFORMATION COMMONS COMPOSABLES---------------------------------

@Composable
fun SelectedRoleDisplay(
    roleName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Su rol es: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = roleName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF49602D), // Subrayado azul o puedes cambiar el color
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}


@Composable
fun GeneralInfoCard(
    farmName: String,
    farmArea: String,
    farmUnitMeasure: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    showEditButton: Boolean = false // Nuevo parámetro opcional para mostrar el botón
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
                modifier = Modifier.weight(1f) // Permite que el texto ocupe todo el espacio disponible
            ) {
                Text(
                    text = "Información General",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = farmName,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = farmArea,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = farmUnitMeasure,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Mostrar el botón de edición solo si showEditButton es true
            if (showEditButton) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .offset(x = -10.dp)
                        .size(20.dp)
                        .background(Color(0xFFB31D34), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "Editar Información General",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    buttonText: String, // Nuevo parámetro para el texto del botón
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(8.dp)
            .background(Color(0xFFE52542), shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick) // Hacer el botón clickable
    ) {

        Icon(
            painter = painterResource(id = R.drawable.arrow_forward_icon),
            contentDescription = "Icono de acción",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        )


        Text(
            text = buttonText,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun CustomFloatingActionButton(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButtonGroup(
        onMainButtonClick = onAddClick, // Navegar a la vista de agregar lote
        mainButtonIcon = painterResource(id = R.drawable.plus_icon),
    )
}

@Composable
fun LoteItemCard(
    loteName: String,
    loteDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF86B049), RoundedCornerShape(12.dp)) // Fondo verde con bordes redondeados
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = loteName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = loteDescription,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LotesList(
    lotes: List<Plot>, // Lista de objetos Lote
    modifier: Modifier = Modifier,
    onLoteClick: (Plot) -> Unit // Nueva función para manejar el clic en cada lote
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Lotes",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        lotes.forEach { lote ->
            Spacer(modifier = Modifier.height(8.dp))
            LoteItemCard(
                loteName = lote.name,
                loteDescription = lote.coffee_variety_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoteClick(lote) } // Pasa el objeto Lote al hacer clic
            )
        }
    }
}



@Composable
fun FarmItemCard(
    farmName: String,
    farmRole: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val cleanedFarmName = farmName.replace(Regex("\\s+"), " ")
    val cleanedFarmRole = farmRole.replace(Regex("\\s+"), " ")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF86B049), RoundedCornerShape(12.dp)) // Fondo verde con bordes redondeados
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = cleanedFarmName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cleanedFarmRole,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}


//FARM EDIT COMMON COMPOSABLES-----
//FARM EDIT COMMON COMPOSABLES-----
//FARM EDIT COMMON COMPOSABLES-----
//FARM EDIT COMMON COMPOSABLES-----

@Composable
fun UnitDropdown(
    selectedUnit: String,
    units: List<String>,  // Parámetro para unidades dinámicas
    expandedArrowDropUp: Painter,
    arrowDropDown: Painter,
    onUnitChange: (String) -> Unit,
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
                        text = selectedUnit,
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
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(
                        text = unit,
                        fontSize = 14.sp,
                        color = Color.Black,
                    ) },
                    onClick = {
                        onUnitChange(unit)
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
fun RoleDropdown(
    selectedRole: String?,  // Cambiado a String? para aceptar valores nulos
    onRoleChange: (String?) -> Unit, // onRoleChange ahora acepta String? para manejar la deselección
    roles: List<String>,
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
            .background(Color.White, shape = RoundedCornerShape(10.dp)) // Fondo blanco con esquinas redondeadas
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
                    text = selectedRole ?: "Todos los roles", // Muestra "Todos los roles" si es null
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
                    onRoleChange(null) // Pasa null para quitar el filtro de roles
                    onExpandedChange(false)
                }
            )

            // Opciones de roles reales
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(
                        role,
                        color = Color.Black) },
                    onClick = {
                        onRoleChange(role)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null // Parámetro onClick opcional
) {
    // Estado para manejar si el botón ya fue presionado
    var isClicked by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            if (!isClicked) {
                isClicked = true
                onClick?.invoke() ?: navController.popBackStack() // Ejecuta onClick si está definido, sino usa popBackStack
            }
        },
        modifier = modifier.padding(1.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}



@Composable
fun ReusableDeleteButton(
    deleteIcon: Painter = painterResource(id = R.drawable.delete_icon),
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
    ) {
        Icon(
            painter = deleteIcon,
            contentDescription = contentDescription,
            tint = Color.Unspecified,
            modifier = Modifier.size(42.dp)
        )
    }
}
