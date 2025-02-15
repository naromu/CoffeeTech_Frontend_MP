package com.example.coffetech.view.Collaborator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.RoleAddDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Collaborator.AddCollaboratorViewModel

/**
 * Composable function that renders a view for adding a new collaborator to a farm.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm to which the collaborator is being added.
 * @param farmName The name of the farm to which the collaborator is being added.
 * @param role The role of the current user, used to determine available roles for the new collaborator.
 * @param viewModel The [AddCollaboratorViewModel] that manages the state and logic for adding a collaborator.
 */
@Composable
fun AddCollaboratorView(
    navController: NavController,
    farmId: Int,  // Añadir farmId
    farmName: String,
    role: String,
    viewModel: AddCollaboratorViewModel = viewModel()
) {
    val context = LocalContext.current
    val collaboratorEmail by viewModel.collaboratorEmail.collectAsState()
    val collaboratorRole by viewModel.collaboratorRole.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()


    val isFormSubmitted = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadRolesForCollaborator(context, role)
    }


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
                    text = "Agregar Colaborador",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF49602D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(45.dp))

                Text(
                    text = "Correo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall.copy( // Usamos el estilo predefinido y sobreescribimos algunas propiedades
                        // Sobrescribir el tamaño de la fuente
                        color = Color(0xFF3F3D3D)      // Sobrescribir el color
                    ),
                    modifier = Modifier.fillMaxWidth()  // Ocupa todo el ancho disponible
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Nombre de finca utilizando ReusableTextField
                ReusableTextField(
                    value = collaboratorEmail,
                    onValueChange = { viewModel.onCollaboratorEmailChange(it) },
                    placeholder = "Correo de colaborador",
                    modifier = Modifier.fillMaxWidth(), // Asegurar que ocupe todo el ancho disponible
                    isValid = collaboratorEmail.isNotEmpty() || !isFormSubmitted.value,
                    charLimit = 50,
                    errorMessage = if (collaboratorEmail.isEmpty() && isFormSubmitted.value) "El nombre del colaborador no puede estar vacío" else ""
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
                    selectedRole = selectedRole,
                    onCollaboratorRoleChange = { viewModel.onCollaboratorRoleChange(it) },
                    roles = collaboratorRole,  // Lista de roles obtenida del ViewModel
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon),
                    modifier = Modifier.fillMaxWidth()
                )

                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para agregar colaborador

                ReusableButton(
                    text = if (isLoading) "Creando..." else "Crear",
                    onClick = {
                        if (viewModel.validateInputs()) {
                            viewModel.onCreate(navController, context, farmId)  // Enviar farmId
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = !isLoading
                )





            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCollaboratorViewPreview() {
    val mockNavController = rememberNavController() // MockNavController
    CoffeTechTheme {
        AddCollaboratorView(
            navController = mockNavController,
            farmId = 1, // Ejemplo de ID de la finca
            farmName = "Finca 2" ,
            role = ""
        )
    }
}



