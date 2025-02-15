package com.example.coffetech.view.CulturalWorkTask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.CulturalWorkTask.EditCulturalWorkViewModel

/**
 * Vista para editar una tarea de labor cultural. Permite al usuario modificar detalles de la tarea,
 * como el tipo de labor, la fecha, y el colaborador asignado. También incluye opciones para guardar
 * o eliminar la tarea.
 *
 * @param navController Controlador de navegación para manejar el flujo entre vistas.
 * @param culturalWorkTaskId ID de la tarea de labor cultural.
 * @param culturalWorksName Nombre de la labor cultural.
 * @param collaboratorUserId ID del colaborador asignado.
 * @param collaborator_name Nombre del colaborador asignado.
 * @param taskDate Fecha de la tarea.
 * @param plotName Nombre del lote asociado.
 * @param plotId ID del lote asociado.
 * @param viewModel ViewModel que gestiona el estado y lógica de esta vista.
 */
@Composable
fun EditCulturalWorkView(
    navController: NavController,
    culturalWorkTaskId: Int,
    culturalWorksName: String,
    collaboratorUserId: Int,
    collaborator_name: String,
    taskDate: String,
    plotName: String,
    plotId: Int, // Añade plotId como parámetro
    viewModel: EditCulturalWorkViewModel = viewModel()
) {
    val context = LocalContext.current

    // Obtener los estados del ViewModel
    val collaborators by viewModel.collaborators.collectAsState()
    val selectedCollaboratorId by viewModel.selectedCollaboratorId.collectAsState()
    val isFetchingCollaborators by viewModel.isFetchingCollaborators.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsState()
    val showDeleteConfirmation by viewModel.showDeleteConfirmation.collectAsState()
    val isFormSubmitted by viewModel.isFormSubmitted.collectAsState()

    // Obtener otros estados necesarios
    val selectedCulturalWork by viewModel.selectedCulturalWork.collectAsState()
    val selectedDate by viewModel.taskDate.collectAsState()

    // Inicializa los datos en el ViewModel cuando se monta el composable
    LaunchedEffect(Unit) {
        viewModel.initialize(
            culturalWorkTaskId,
            culturalWorksName,
            collaboratorUserId,
            collaborator_name,
            taskDate,
            plotId,
            context
        )
    }


    // Mostrar mensajes de error si los hay
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            // Aquí podrías mostrar un Snackbar o cualquier otro mecanismo para notificar el error
        }
        Text(
            text = errorMessage ?: "",
            color = Color.Red,
            modifier = Modifier.padding(8.dp)
        )
    }

    // Caja principal con fondo oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenedor del formulario de edición de tarea
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Haz que el contenedor ocupe el 95% del ancho de la pantalla
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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

                // Título de la vista
                Text(
                    text = "Editar Labor",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Información del lote
                Text(
                    text = "Lote: $plotName",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )

                Spacer(modifier = Modifier.height(22.dp))

                // Dropdown para seleccionar el tipo de labor cultural
                Text(
                    text = "Tipo Labor Cultural",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                TypeCulturalWorkDropdown(
                    selectedCulturalWork = selectedCulturalWork,
                    onTypeCulturalWorkChange = { selected ->
                        viewModel.onTypeCulturalWorkChange(selected)
                    },
                    cultural_work = listOf("Chequeo de Salud", "Chequeo de estado de maduración"),
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de fecha
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
                    label = "Fecha completación",
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        viewModel.onTaskDateChange(date)
                    },
                    errorMessage = if (isFormSubmitted && selectedDate.isBlank()) "La fecha de floración no puede estar vacía." else null
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dropdown para seleccionar colaborador
                Text(
                    text = "Colaborador",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))


                when {
                    isFetchingCollaborators -> {
                        // Indicador de carga para colaboradores
                        CircularProgressIndicator(
                            color = Color(0xFF5D8032),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    collaborators.isEmpty() -> {
                        // Mensaje si no hay colaboradores disponibles
                        Text(
                            text = "Usted no tiene colaboradores operadores de campo en su finca, agréguelos para continuar.",
                            color = Color.Red,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    else -> {
                        // Dropdown de colaboradores
                        CollaboratorDropdownWithId(
                            selectedCollaboratorId = selectedCollaboratorId,
                            collaborators = collaborators,
                            expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                            arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                            onCollaboratorChange = { collaborator ->
                                viewModel.setSelectedCollaboratorId(collaborator.user_id, collaborator.name)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Botón para guardar cambios
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        viewModel.saveChanges(context, navController)
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = isSaveEnabled && !isLoading && selectedCollaboratorId != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para eliminar la tarea
                ReusableButton(
                    text = if (isLoading) "Cargando..." else "Eliminar",
                    onClick = {
                        viewModel.showDeleteConfirmation.value = true
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Red,
                )

                val image = painterResource(id = R.drawable.delete_confirmation_icon)
                // Dialogo de Confirmación para eliminar tarea
                if (showDeleteConfirmation) {
                    ReusableAlertDialog(
                        title = "¡ESTA ACCIÓN\nES IRREVERSIBLE!",
                        description = "Todos tus datos relacionados a esta labor cultural se perderán. ¿Deseas continuar?",
                        confirmButtonText = "Eliminar labor",
                        cancelButtonText = "Cancelar",
                        isLoading = isLoading,
                        onConfirmClick = {
                            viewModel.deleteTask(context, navController)
                            viewModel.showDeleteConfirmation.value = false
                        },
                        onCancelClick = { viewModel.showDeleteConfirmation.value = false },
                        onDismissRequest = { viewModel.showDeleteConfirmation.value = false },
                        image = image
                    )
                }

            }
        }
    }
}

/**
 * Vista previa de EditCulturalWorkView para Android Studio.
 */
// Mueve la función Preview fuera de la función CreatePlotView
@Preview(showBackground = true)
@Composable
fun EditCulturalWorkViewPreview() {
    val navController = rememberNavController() // Usar rememberNavController para la vista previa

    CoffeTechTheme {
        EditCulturalWorkView(
            navController = navController,
            culturalWorkTaskId = 1 ,
            culturalWorksName = "Chequeo ",
            collaboratorUserId = 1,
            collaborator_name= "Jose",
            taskDate= "",
            plotName = " lote 1",
            plotId = 1

        )
    }
}
