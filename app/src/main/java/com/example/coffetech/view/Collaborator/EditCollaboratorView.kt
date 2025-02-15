package com.example.coffetech.view.Collaborator

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.RoleAddDropdown
import com.example.coffetech.common.UnitDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Collaborator.AddCollaboratorViewModel
import com.example.coffetech.viewmodel.Collaborator.EditCollaboratorViewModel
import com.example.coffetech.viewmodel.farm.CreateFarmViewModel

/**
 * Composable function that renders a view for editing an existing collaborator's details.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm to which the collaborator belongs.
 * @param collaboratorId The unique identifier of the collaborator to be edited.
 * @param collaboratorName The current name of the collaborator.
 * @param collaboratorEmail The current email of the collaborator.
 * @param selectedRole The current role assigned to the collaborator.
 * @param role The role of the current user, used to determine available actions.
 * @param viewModel The [EditCollaboratorViewModel] that manages the state and logic for editing a collaborator.
 */
@SuppressLint("RememberReturnType")
@Composable
fun EditCollaboratorView(
    navController: NavController,
    farmId: Int,
    collaboratorId: Int,
    collaboratorName: String,
    collaboratorEmail: String,
    selectedRole: String,
    role: String,
    viewModel: EditCollaboratorViewModel = viewModel()
) {
    val context = LocalContext.current
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRolesForEditing(context, role)
        viewModel.initializeValues(selectedRole)
    }

    val currentRole by viewModel.selectedRole.collectAsState()
    val collaboratorRoles by viewModel.collaboratorRole.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
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
                        modifier = Modifier.size(32.dp) // Tamaño más manejable
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Editar Colaborador",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF49602D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(45.dp))

                Text(
                    text = "Nombre",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))

                ReusableTextField(
                    value = collaboratorName,
                    onValueChange = {},
                    enabled = false,
                    placeholder = "Nombre colaborador",
                    modifier = Modifier.fillMaxWidth(), // Asegurar que ocupe todo el ancho disponible
                    isValid = true,
                    charLimit = 50,
                    errorMessage = ""
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Correo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                // Nombre de finca utilizando ReusableTextField
                ReusableTextField(
                    value = collaboratorEmail,
                    onValueChange = { },
                    enabled = false,
                    placeholder = "Correo de colaborador",
                    modifier = Modifier.fillMaxWidth(), // Asegurar que ocupe todo el ancho disponible
                    isValid = true,
                    charLimit = 50,
                    errorMessage =""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Rol Asignado",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))


                // Rol seleccionado
                RoleAddDropdown(
                    selectedRole = currentRole,
                    onCollaboratorRoleChange = viewModel::onCollaboratorRoleChange,
                    roles = collaboratorRoles,
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier.fillMaxWidth()
                )


                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Botón para guardar cambios
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        viewModel.editCollaborator(
                            context = context,
                            farmId = farmId,
                            collaboratorId = collaboratorId, // Reemplaza con el ID real del colaborador
                            navController = navController
                        )
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp) // Ajuste de tamaño del botón
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = hasChanges && !isLoading
                )

                // Botón para eliminar el colaborador
                Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre botones

                ReusableButton(
                    text = if (isLoading) "Cargando..." else "Eliminar",
                    onClick = {showDeleteConfirmation.value = true},
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Red,
                )

                //Confirmación para eliminar colaborador
                if (showDeleteConfirmation.value) {
                    Box(
                        modifier = Modifier
                    ) {
                        AlertDialog(
                            containerColor = Color.White,
                            modifier = Modifier
                                .background(Color.Transparent),
                            onDismissRequest = { showDeleteConfirmation.value = false },
                            title = {
                                Text(
                                    text = "¡Esta acción es irreversible!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            text = {
                                // Contenedor para el contenido del diálogo
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp), // Espacio alrededor del contenido
                                    horizontalAlignment = Alignment.CenterHorizontally // Centrar el contenido
                                ) {
                                    // Descripción centrada
                                    Text(
                                        text = "Este colaborador se eliminará permanentemente de tu lote. ¿Deseas continuar?",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },


                            confirmButton = {
                                // Botón para eliminar centrado
                                ReusableButton(
                                    text = if (isLoading) "Eliminando..." else "Eliminar",
                                    onClick = {
                                        viewModel.deleteCollaborator(
                                            context = context,
                                            farmId = farmId,
                                            collaboratorId = collaboratorId,
                                            navController = navController
                                        )
                                        showDeleteConfirmation.value = false
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(0.7f),
                                    buttonType = ButtonType.Red,
                                )
                            },
                            dismissButton = {
                                // Botón cancelar
                                ReusableButton(
                                    text = "Cancelar",
                                    onClick = { showDeleteConfirmation.value = false },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(0.7f),
                                    buttonType = ButtonType.Green,
                                )
                            },

                            shape = RoundedCornerShape(16.dp) // Esquinas redondeadas del diálogo
                        )
                    }
                }


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditCollaboratorViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        EditCollaboratorView(
            navController = mockNavController,
            farmId = 1, // Ejemplo de ID de la finca
            collaboratorId = 1,
            collaboratorName = "Juan Pérez", // Ejemplo de nombre de colaborador
            collaboratorEmail = "juan.perez@example.com", // Ejemplo de email de colaborador
            selectedRole = "Administrador",
            role = ""
        )
    }
}
