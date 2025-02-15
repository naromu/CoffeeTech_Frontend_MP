package com.example.coffetech.view.CulturalWorkTask

import androidx.compose.ui.text.style.TextAlign
import com.example.coffetech.common.ButtonType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.CulturalWorkTask.ReminderViewModel

/**
 * Vista para configurar recordatorios de labores culturales. Permite al usuario establecer
 * recordatorios para sí mismo y/o para el colaborador asignado a la tarea.
 *
 * @param navController Controlador de navegación para manejar la navegación entre vistas.
 * @param viewModel ViewModel que gestiona el estado y lógica de la vista de recordatorios.
 * @param plotId ID del lote asociado a la tarea de labor cultural.
 * @param plotName Nombre del lote asociado a la tarea de labor cultural.
 * @param culturalWorkType Tipo de labor cultural para la cual se establece el recordatorio.
 * @param date Fecha de la tarea de labor cultural.
 * @param collaboratorUserId ID del colaborador asignado a la tarea.
 */
@Composable
fun ReminderCulturalWorkView(
    navController: NavController,
    viewModel: ReminderViewModel = viewModel(),
    plotId: Int,
    plotName: String = "",
    culturalWorkType: String,
    date: String,
    collaboratorUserId: Int,
) {

    // Obtener estados desde el ViewModel
    val isReminderForUser by viewModel.isReminderForUser.collectAsState()
    val isReminderForCollaborator by viewModel.isReminderForCollaborator.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current

    // Caja principal que contiene el diseño de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Caja blanca con esquinas redondeadas para el formulario de configuración de recordatorio
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón de cerrar o volver
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            navController.popBackStack()
                            navController.popBackStack()
                            navController.popBackStack()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título de la vista
                Text(
                    text = "Recordatorios",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Descripción de la funcionalidad de recordatorios
                Text(
                    text = "Seleccione los recordatorios que desea establecer y haga clic en guardar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Checkbox para establecer recordatorio al usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isReminderForUser,
                        onCheckedChange = { viewModel.setReminderForUser(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF5D8032),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "Enviarme un recordatorio",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF3F3D3D)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox para enviar recordatorio al colaborador asignado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isReminderForCollaborator,
                        onCheckedChange = { viewModel.setReminderForCollaborator(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF5D8032),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "Enviar recordatorio al colaborador asignado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF3F3D3D)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Mostrar mensaje de error si existe
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                // Botón de guardar para confirmar los recordatorios seleccionados
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        viewModel.saveReminders(
                            plotId = plotId,
                            culturalWorkType = culturalWorkType,
                            date = date,
                            collaboratorUserId = collaboratorUserId,
                            navController = navController,
                            context = context // Pasar el contexto aquí
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(48.dp),
                    buttonType = ButtonType.Green,
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón de texto para volver
                ReusableTextButton(
                    navController = navController,
                    destination = "", // Puedes pasar una cadena vacía o una ruta predeterminada
                    text = "Volver",
                    modifier = Modifier
                        .size(width = 160.dp, height = 54.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        // Realiza dos veces popBackStack para retroceder dos pantallas
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Vista previa de ReminderCulturalWorkView en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun ReminderCulturalWorkViewPreview() {
    val navController = rememberNavController() // Usar rememberNavController para la vista previa

    CoffeTechTheme {
        ReminderCulturalWorkView(
            navController = navController,
            plotId=1,
            plotName = "",
            culturalWorkType= "",
            date= "",
            collaboratorUserId=1,
        )
    }
}
