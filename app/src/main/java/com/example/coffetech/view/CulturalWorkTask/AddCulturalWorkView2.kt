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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.CulturalWorkTask.AddCulturalWorkViewModel2

@OptIn(ExperimentalMaterial3Api::class)

/**
 * Vista para añadir detalles adicionales a una tarea de labor cultural, como seleccionar un colaborador.
 *
 * @param navController Controlador de navegación para manejar la navegación entre vistas.
 * @param plotId ID del lote en el que se realiza la labor cultural.
 * @param plotName Nombre del lote donde se ejecuta la labor.
 * @param culturalWorkType Tipo de labor cultural seleccionada (e.g., "Chequeo de Salud").
 * @param date Fecha seleccionada para la labor cultural.
 * @param viewModel ViewModel que gestiona el estado y lógica de la vista.
 */

@Composable
fun AddCulturalWorkView2(
    navController: NavController,
    plotId: Int,
    plotName: String = "",
    culturalWorkType: String,
    date: String,
    viewModel: AddCulturalWorkViewModel2 = viewModel()
) {
    val context = LocalContext.current

    // Obtener los estados del ViewModel
    val collaborators by viewModel.collaborators.collectAsState()
    val selectedCollaboratorId by viewModel.selectedCollaboratorId.collectAsState()
    val buttonText by viewModel.buttonText.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFetchingCollaborators by viewModel.isFetchingCollaborators.collectAsState()
    val isSendingRequest by viewModel.isSendingRequest.collectAsState()

    // Estado de nombre del colaborador seleccionado en el dropdown
    var selectedCollaboratorName by remember { mutableStateOf("Seleccione un colaborador") }

    // Efecto lanzado para cargar colaboradores y determinar el texto del botón basado en la fecha
    LaunchedEffect(Unit) {
        viewModel.fetchCollaborators(plotId, context)
        viewModel.determineButtonText(date)
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

    // Caja contenedora principal con fondo oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenedor blanco donde se encuentra el formulario de la tarea
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
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            // Retrocede dos veces en la pila de navegación
                            navController.popBackStack()
                            navController.popBackStack()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título de la vista
                Text(
                    text = "Añadir Labor",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Información del lote, tipo de labor cultural y fecha
                Text(
                    text = "Lote: $plotName",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    textAlign = TextAlign.Center,

                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$culturalWorkType",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Fecha: $date",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A84B)
                    ),
                    textAlign = TextAlign.Center,

                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible

                )

                Spacer(modifier = Modifier.height(22.dp))

                // Etiqueta para seleccionar colaborador
                Text(
                    text = "Colaborador",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF3F3D3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Dropdown de colaboradores o mensaje si no hay colaboradores
                when {
                    isFetchingCollaborators -> {
                        // Indicador de carga mientras se obtienen los colaboradores
                        CircularProgressIndicator(
                            color = Color(0xFF5D8032),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    collaborators.isEmpty() -> {
                        // Mostrar mensaje si no hay colaboradores
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
                        // Dropdown para seleccionar colaborador
                        CollaboratorDropdown(
                            selectedCollaboratorName = selectedCollaboratorName,
                            collaborators = collaborators,
                            expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                            arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                            onCollaboratorChange = { collaborator ->
                                selectedCollaboratorName = collaborator.name.trim()
                                viewModel.setSelectedCollaboratorId(collaborator.user_id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Botón dinámico para guardar la tarea de labor cultural
                ReusableButton(
                    text = if (isSendingRequest) "Guardando" else buttonText,
                    onClick = {
                        viewModel.onButtonClick(
                            plotId = plotId,
                            culturalWorkType = culturalWorkType,
                            date = date,
                            plotName = plotName,
                            navController = navController,
                            context = context

                        )
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green, // Siempre verde
                    enabled = selectedCollaboratorId != null && !isSendingRequest && collaborators.isNotEmpty()
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
 * Vista previa de la función AddCulturalWorkView2 para ver cómo se muestra en Android Studio.
 */

@Preview(showBackground = true)
@Composable
fun AddCulturalWorkView2Preview() {
    val navController = rememberNavController() // Usar rememberNavController para la vista previa

    CoffeTechTheme {
        AddCulturalWorkView2(
            navController = navController,
            plotId = 1,
            plotName = "Lote 1",
            culturalWorkType = "Chequeo de Salud",
            date = "2024-10-22" // Ajusta la fecha para probar
            )
        }
    }

