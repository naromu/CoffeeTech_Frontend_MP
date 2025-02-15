package com.example.coffetech.view.farm

import FarmViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.coffetech.common.FarmItemCard
import com.example.coffetech.common.FloatingActionButtonGroup
import com.example.coffetech.common.ReusableSearchBar
import com.example.coffetech.common.RoleDropdown
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.common.HeaderFooterView

/**
 * Composable function that renders the farm management screen.
 * This screen allows the user to view, search, and filter farms by role, as well as navigate to create a new farm or view details of an existing one.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [FarmViewModel] used to manage the state and logic for the farm view.
 */
@Composable
fun FarmView(
    navController: NavController,
    viewModel: FarmViewModel = viewModel() // Injects the ViewModel here
) {
    val context = LocalContext.current

    // Load the farms and roles when the composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadFarms(context)
        viewModel.loadRolesFromSharedPreferences(context) // Loads roles from SharedPreferences
    }

    // Retrieve the current state from the ViewModel
    val farms by viewModel.farms.collectAsState()
    val query by viewModel.searchQuery
    val selectedRole by viewModel.selectedRole
    val expanded by viewModel.isDropdownExpanded
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val roles by viewModel.roles.collectAsState()

    // Header and Footer layout with content in between
    HeaderFooterView(
        title = "Mis Fincas",
        currentView = "Fincas",
        navController = navController
    ) {
        // Main content box with the list of farms and floating action button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search bar for filtering farms by name
                ReusableSearchBar(
                    query = query,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    text = "Buscar finca por nombre",
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown menu for selecting user role
                RoleDropdown(
                    selectedRole = selectedRole,
                    onRoleChange = { viewModel.selectRole(it) },
                    roles = roles,
                    expanded = expanded,
                    onExpandedChange = { viewModel.setDropdownExpanded(it) },
                    expandedArrowDropUp = painterResource(id = R.drawable.arrowdropup_icon),
                    arrowDropDown = painterResource(id = R.drawable.arrowdropdown_icon)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Conditional UI based on the state of loading or error
                if (isLoading) {
                    Text("Cargando fincas...") // Show loading message
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red) // Show error message if any
                } else {
                    // LazyColumn to display the list of farms
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(farms) { farm ->
                            Column {
                                val cleanedFarmName = farm.name.replace(Regex("\\s+"), " ")
                                val cleanedFarmRole = farm.role.replace(Regex("\\s+"), " ")

                                // Card for each farm in the list
                                FarmItemCard(
                                    farmName = cleanedFarmName,
                                    farmRole = cleanedFarmRole,
                                    onClick = {
                                        viewModel.onFarmClick(farm, navController)
                                    }
                                )

                                Spacer(modifier = Modifier.height(8.dp)) // Space between cards
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Ajusta la altura aquí según el tamaño de tu botón
                        }

                    }
                }
            }

            // Floating action button for creating a new farm, only if there's no error
            if (errorMessage.isEmpty()) {
                FloatingActionButtonGroup(
                    onMainButtonClick = { navController.navigate("CreateFarmView") },
                    mainButtonIcon = painterResource(id = R.drawable.plus_icon),
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align to the bottom right
                )
            }

        }
    }
}

/**
 * Preview function for the FarmView.
 * It simulates the farm management screen in a preview environment to visualize the layout.
 */

@Preview(showBackground = true)
@Composable
fun FarmViewPreview() {
    CoffeTechTheme {
        FarmView(navController = NavController(LocalContext.current))
    }
}
