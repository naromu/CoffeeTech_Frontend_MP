package com.example.coffetech.view.Collaborator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.CollaboratorInfoCard
import com.example.coffetech.common.FarmItemCard
import com.example.coffetech.common.FloatingActionButtonGroup
import com.example.coffetech.common.ReusableDeleteButton
import com.example.coffetech.common.ReusableSearchBar
import com.example.coffetech.common.RoleDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.Collaborator.Collaborator
import com.example.coffetech.viewmodel.Collaborator.CollaboratorViewModel

/**
 * Composable function that renders the collaborators management screen.
 * This screen allows the user to view, search, and filter collaborators by role, as well as navigate to add or edit collaborators.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm whose collaborators are being managed.
 * @param farmName The name of the farm whose collaborators are being managed.
 * @param role The role of the current user, used to determine permissions and available actions.
 * @param viewModel The [CollaboratorViewModel] that manages the state and logic for the collaborators view.
 */
@Composable
fun CollaboratorView(
    navController: NavController,
    farmId: Int,  // Añadir farmId
    farmName: String,  // Añadir farmName
    role: String,
    viewModel: CollaboratorViewModel = viewModel() // Injects the ViewModel here
) {
    val context = LocalContext.current

    // Load the farms and roles when the composable is first displayed
    LaunchedEffect(farmId) {
        viewModel.loadRolesFromSharedPreferences(context)
        viewModel.loadCollaborators(context, farmId)
        viewModel.loadRolePermissions(context, role) // Cargar permisos para el rol

    }

    // Retrieve the current state from the ViewModel
    val collaborators by viewModel.collaborators.collectAsState()
    val collaboratorName by viewModel.collaboratorName.collectAsState()
    val query by viewModel.searchQuery
    val selectedRole by viewModel.selectedRole
    val expanded by viewModel.isDropdownExpanded
    //val userHasPermissionToDelete = viewModel.hasPermission("delete_collaborator")
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val roles by viewModel.roles.collectAsState()
    val userHasPermissionAddCollaborators = viewModel.hasPermission("add_administrador_farm" ) ||viewModel.hasPermission("add_operador_farm" )
    val userHasPermissionReadCollaborators = viewModel.hasPermission("read_collaborators")

    val canEditAdministrador = viewModel.hasPermission("edit_administrador_farm")
    val canEditOperador = viewModel.hasPermission("edit_operador_farm")
    // Header and Footer layout with content in between

    HeaderFooterSubView(
        title = "Mis Colaboradores",
        currentView = "Fincas",
        navController = navController,
        onBackClick = { navController.navigate("${Routes.FarmInformationView}/$farmId") },
    ) {
        // Main content box with the list of farms and floating action button
        if (userHasPermissionReadCollaborators) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(19.dp)
            ) {
                Text(text = "Finca: $farmName", color = Color.Black)


               
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Su rol es: ${role.ifEmpty { "Sin rol" }}", color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                // Search bar for filtering farms by name
                ReusableSearchBar(
                    query = query,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    text = "Buscar colaborador por nombre",
                    modifier = Modifier.fillMaxWidth()

                )






                Spacer(modifier = Modifier.height(16.dp))


                // Dropdown menu for selecting user role
                RoleDropdown(
                    selectedRole = selectedRole, // Can be null
                    onRoleChange = { viewModel.selectRole(it) }, // Role change handler
                    roles = roles,
                    expanded = expanded,
                    onExpandedChange = { viewModel.setDropdownExpanded(it) },
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon)
                )



                Spacer(modifier = Modifier.height(16.dp))

                // Conditional UI based on the state of loading or error
                if (isLoading) {
                    Text("Cargando colaboradores...") // Show loading message
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red) // Show error message if any
                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(collaborators) { collaborator ->
                            val collaboratorRole = collaborator.role
                            val collaboratorId = collaborator.user_id
                            val collaboratorName = collaborator.name
                            val collaboratorEmail = collaborator.email

                            // Verificar permisos de edición basados en el rol del colaborador
                            val canEdit = when (collaboratorRole) {
                                "Administrador de finca" -> canEditAdministrador
                                "Operador de campo" -> canEditOperador
                                else -> false
                            }

                            CollaboratorInfoCard(
                                collaboratorName = collaboratorName,
                                collaboratorRole = collaboratorRole,
                                collaboratorEmail = collaboratorEmail,
                                onEditClick = {
                                    if (canEdit) {
                                        navController.navigate("EditCollaboratorView/$farmId/$collaboratorId/$collaboratorName/$collaboratorEmail/$collaboratorRole/$role")
                                    }
                                },
                                // Pasar una bandera para mostrar u ocultar el ícono de edición
                                showEditIcon = canEdit
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Ajusta la altura aquí según el tamaño de tu botón
                        }
                    }

                }
            }
            if (userHasPermissionAddCollaborators && errorMessage.isEmpty()) {
            // Floating action button for creating a new farm
            FloatingActionButtonGroup(
                onMainButtonClick = {
                    navController.navigate("AddCollaboratorView/$farmId/$farmName/$role")
                },
                mainButtonIcon = painterResource(id = R.drawable.plus_icon),
                modifier = Modifier
                    .padding(16.dp)
            )}

    }}
}

/**
 * Preview function for the FarmView.
 * It simulates the farm management screen in a preview environment to visualize the layout.
 */

@Preview(showBackground = true)
@Composable
fun CollaboratorViewPreview() {
    CoffeTechTheme {
        CollaboratorView(navController = NavController(LocalContext.current),
            farmName = "Finca Ejemplo",
            farmId= 1,
            role= "")
    }
}

