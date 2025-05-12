// Navigation.kt

package com.example.coffetech.navigation

import NotificationView
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffetech.routes.Routes
import com.example.coffetech.utils.GlobalEventBus
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.FinanceReportView
import com.example.coffetech.view.FormFinanceReportView
import com.example.coffetech.view.auth.ChangePasswordView
import com.example.coffetech.view.auth.ConfirmTokenForgotPasswordView
import com.example.coffetech.view.auth.ForgotPasswordView
import com.example.coffetech.view.auth.LoginView
import com.example.coffetech.view.auth.NewPasswordView
import com.example.coffetech.view.auth.ProfileView
import com.example.coffetech.view.auth.RegisterPasswordView
import com.example.coffetech.view.auth.RegisterView
import com.example.coffetech.view.auth.StartView
import com.example.coffetech.view.auth.VerifyAccountView
import com.example.coffetech.view.collaborator.AddCollaboratorView
import com.example.coffetech.view.collaborator.CollaboratorView
import com.example.coffetech.view.collaborator.EditCollaboratorView
import com.example.coffetech.view.farm.CreateFarmView
import com.example.coffetech.view.farm.FarmEditView
import com.example.coffetech.view.farm.FarmInformationView
import com.example.coffetech.view.farm.FarmView
import com.example.coffetech.view.plot.CreateMapPlotView
import com.example.coffetech.view.plot.CreatePlotInformationView
import com.example.coffetech.view.plot.EditMapPlotView
import com.example.coffetech.view.plot.EditPlotInformationView
import com.example.coffetech.view.plot.PlotInformationView
import com.example.coffetech.view.reports.ReportsSelectionView
import com.example.coffetech.view.transaction.AddTransactionView
import com.example.coffetech.view.transaction.EditTransactionView
import com.example.coffetech.view.transaction.TransactionInformationView
import com.example.coffetech.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLDecoder


/**
 * Composable function that sets up the app's navigation using the Navigation component in Jetpack Compose.
 * This function defines the navigation graph, handling different routes for various views in the app.
 *
 * @param context The [Context] used for accessing SharedPreferences and other system resources.
 */
@Composable
fun AppNavHost(context: Context) {
    // Create the NavController to handle navigation between screens
    val navController = rememberNavController()

    // Helper for managing SharedPreferences (e.g., login state)
    val sharedPreferencesHelper = SharedPreferencesHelper(context)

    // Check if the user is logged in based on saved session data
    val isLoggedIn = sharedPreferencesHelper.isLoggedIn()

    val sharedViewModel: SharedViewModel = viewModel()

    // BackHandler to disable the default back navigation behavior
    BackHandler {
        // No action performed, back gesture is disabled
    }

    LaunchedEffect(Unit) {
        launch {
            GlobalEventBus.logoutEvent.collectLatest {
                // Limpiar datos de sesión
                sharedPreferencesHelper.clearSession()

                // Navegar a LoginView y limpiar la pila de navegación
                navController.navigate(Routes.LoginView)

                // Mostrar un Toast indicando el cierre de sesión
                Toast.makeText(
                    context,
                    "Credenciales expiradas.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    // Navigation host for defining the navigation graph and initial destination
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.StartView else Routes.LoginView // Start at StartView if logged in, otherwise LoginView
    ) {

        /**
         * Composable destination for the LoginView.
         */
        composable(Routes.LoginView) {
            LoginView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the RegisterView.
         */


        composable(
            route = "${Routes.RegisterView}?name={name}&email={email}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterView(navController = navController, name = name, email = email)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the ForgotPasswordView.
         */
        composable(Routes.ForgotPasswordView) {
            ForgotPasswordView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the ConfirmTokenForgotPasswordView.
         */
        composable(Routes.ConfirmTokenForgotPasswordView) {
            ConfirmTokenForgotPasswordView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the VerifyAccountView.
         */
        composable(Routes.VerifyAccountView) {
            VerifyAccountView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the NewPasswordView with a token as an argument.
         * This view handles the password reset process.
         *
         * @param token The token required for resetting the password.
         */
        composable(
            route = "${Routes.NewPasswordView}/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            NewPasswordView(navController = navController, token = token)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the FarmView, which displays a list of farms.
         */
        composable(Routes.FarmView) {
            FarmView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }


        /**
         * Composable destination for the FarmEditView, allowing users to edit farm details.
         */
        composable(
            route = "${Routes.FarmEditView}/{farmId}/{farmName}/{farmArea}/{unitOfMeasure}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("farmName") { type = NavType.StringType },
                navArgument("farmArea") { type = NavType.StringType },
                navArgument("unitOfMeasure") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""
            val farmArea = backStackEntry.arguments?.getString("farmArea") ?: ""
            val unitOfMeasure = backStackEntry.arguments?.getString("unitOfMeasure") ?: ""
            BackHandler {
                // No action performed, back gesture is disabled
            }
            FarmEditView(
                navController = navController,
                farmId = farmId,
                farmName = farmName,
                farmArea = farmArea,
                unitOfMeasure = unitOfMeasure
            )
        }

        /**
         * Composable destination for the FarmInformationView, which displays detailed information about a farm.
         */
        composable("FarmInformationView/{farmId}") { backStackEntry ->
            val farmId = backStackEntry.arguments?.getString("farmId")?.toIntOrNull() ?: 0
            FarmInformationView(navController = navController, farmId = farmId)
            BackHandler {
                // No action performed, back gesture is disabled
            }
        }


        /**
         * Composable destination for the CreateFarmView, allowing users to create a new farm.
         */
        composable(Routes.CreateFarmView) {
            CreateFarmView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the StartView, the main screen that users see after logging in.
         */
        composable(Routes.StartView) {
            StartView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the ProfileView, which allows users to view and edit their profile.
         */
        composable(Routes.ProfileView) {
            ProfileView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        /**
         * Composable destination for the ChangePasswordView, which allows users to change their password.
         */
        composable(Routes.ChangePasswordView) {
            ChangePasswordView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        composable(
            route = "${Routes.RegisterPasswordView}/{name}/{email}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterPasswordView(navController = navController, name = name, email = email)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }


        composable(Routes.NotificationView) {
            NotificationView(navController = navController)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        composable(
            route = "${Routes.CollaboratorView}/{farmId}/{farmName}/{role}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("farmName") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }

            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: ""

            CollaboratorView(navController = navController, farmId = farmId, farmName = farmName, role = role)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }

        composable(
            route = "${Routes.AddCollaboratorView}/{farmId}/{farmName}/{role}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("farmName") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }

            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: ""

            AddCollaboratorView(navController = navController, farmId = farmId, farmName = farmName, role = role)
            BackHandler {
                // Prevents back navigation gesture here
            }
        }
        composable(
            route = "${Routes.EditCollaboratorView}/{farmId}/{collaboratorId}/{collaboratorName}/{collaboratorEmail}/{selectedRole}/{role}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("collaboratorId") { type = NavType.IntType },
                navArgument("collaboratorName") { type = NavType.StringType },
                navArgument("collaboratorEmail") { type = NavType.StringType },
                navArgument("selectedRole") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }

            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val collaboratorId = backStackEntry.arguments?.getInt("collaboratorId") ?: 0
            val collaboratorName = backStackEntry.arguments?.getString("collaboratorName") ?: ""
            val collaboratorEmail = backStackEntry.arguments?.getString("collaboratorEmail") ?: ""
            val selectedRole = backStackEntry.arguments?.getString("selectedRole") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: ""

            BackHandler {
                // No action performed, back gesture is disabled
            }
            // Llama a la vista `EditCollaboratorView` con los parámetros necesarios
            EditCollaboratorView(
                navController = navController,
                farmId = farmId,
                collaboratorId = collaboratorId,
                collaboratorName = collaboratorName,
                collaboratorEmail= collaboratorEmail,
                selectedRole = selectedRole,
                role = role
            )
        }


        ///PLOTS

        composable(
            route = "createPlotInformationView/{farmId}?plotName={plotName}&selectedVariety={selectedVariety}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("plotName") { type = NavType.StringType; defaultValue = "" },
                navArgument("selectedVariety") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val plotName = backStackEntry.arguments?.getString("plotName") ?: ""
            val selectedVariety = backStackEntry.arguments?.getString("selectedVariety") ?: ""
            BackHandler {
                // No action performed, back gesture is disabled
            }
            CreatePlotInformationView(
                navController = navController,
                farmId = farmId,
                plotName = plotName,
                selectedVariety = selectedVariety
            )
        }

        composable(
            route = "createMapPlotView/{farmId}/{plotName}/{selectedVariety}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("plotName") { type = NavType.StringType },
                navArgument("selectedVariety") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val plotName = backStackEntry.arguments?.getString("plotName") ?: ""
            val selectedVariety = backStackEntry.arguments?.getString("selectedVariety") ?: ""
            BackHandler {
                // No action performed, back gesture is disabled
            }
            CreateMapPlotView(
                navController = navController,
                farmId = farmId,
                plotName = plotName,
                selectedVariety = selectedVariety
            )
        }


        composable(
            route = "PlotInformationView/{plotId}/{farmName}/{farmId}",
            arguments = listOf(
                navArgument("plotId") { type = NavType.IntType },

                navArgument("farmName") { type = NavType.StringType },
                navArgument("farmId") { type = NavType.IntType },


                )
        ) { backStackEntry ->
            val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0

            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""

            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            BackHandler {
            }
            PlotInformationView(
                navController = navController,
                plotId = plotId,
                farmId = farmId,
                farmName = farmName
            )
        }

        composable(
            route = "${Routes.EditPlotInformationView}/{plotId}/{plotName}/{selectedVariety}",
            arguments = listOf(
                navArgument("plotId") { type = NavType.IntType },
                navArgument("plotName") { type = NavType.StringType },
                navArgument("selectedVariety") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0
            val plotName = backStackEntry.arguments?.getString("plotName") ?: ""
            val selectedVariety = backStackEntry.arguments?.getString("selectedVariety") ?: ""
            BackHandler {
            }
            EditPlotInformationView(
                navController = navController,
                plotId = plotId,
                plotName = Uri.decode(plotName),
                selectedVariety = Uri.decode(selectedVariety)
            )
        }

        // Agrega esta ruta en Navigation.kt
        composable(
            route = "${Routes.EditMapPlotView}/{plotId}/{latitude}/{longitude}/{altitude}",
            arguments = listOf(
                navArgument("plotId") { type = NavType.IntType },
                navArgument("latitude") { type = NavType.StringType },
                navArgument("longitude") { type = NavType.StringType },
                navArgument("altitude") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            val altitude = backStackEntry.arguments?.getString("altitude")?.toDoubleOrNull() ?: 0.0

            EditMapPlotView(
                navController = navController,
                plotId = plotId,
                initialLatitude = latitude,
                initialLongitude = longitude,
                initialAltitude = altitude
            )
        }

        ///TRANSACTIONS
        composable(
            route = "${Routes.TransactionInformationView}/{plotId}/{plotName}/{farmName}/{farmId}",
            arguments = listOf(
                navArgument("plotId") { type = NavType.IntType },
                navArgument("plotName") { type = NavType.StringType },
                navArgument("farmName") { type = NavType.StringType },
                navArgument("farmId") { type = NavType.IntType },


                )
        ) { backStackEntry ->
            val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0
            val plotName = backStackEntry.arguments?.getString("plotName") ?: ""

            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""

            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            BackHandler {
                // No action performed, back gesture is disabled
            }
            TransactionInformationView(
                navController = navController,
                plotId = plotId,
                plotName = plotName,
                farmId = farmId,
                farmName = farmName
            )
        }

        composable(
            route = "${Routes.AddTransactionView}/{plotId}",
            arguments = listOf(
                navArgument("plotId") { type = NavType.IntType },


                )
        ) { backStackEntry ->
            val plotId = backStackEntry.arguments?.getInt("plotId") ?: 0

            BackHandler {
                // No action performed, back gesture is disabled
            }
            AddTransactionView(
                navController = navController,
                plotId = plotId,
            )
        }

        composable(
            route = "${Routes.EditTransactionView}/{transaction_id}/{transaction_type_name}/{transaction_category_name}/{description}/{value}/{transaction_date}",
            arguments = listOf(
                navArgument("transaction_id") { type = NavType.IntType },
                navArgument("transaction_type_name") { type = NavType.StringType },
                navArgument("transaction_category_name") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("value") { type = NavType.LongType },
                navArgument("transaction_date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transaction_id") ?: 0
            val transactionTypeName = backStackEntry.arguments?.getString("transaction_type_name")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
            val transactionCategoryName = backStackEntry.arguments?.getString("transaction_category_name")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
            val description = backStackEntry.arguments?.getString("description")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
            val value = backStackEntry.arguments?.getLong("value") ?: 0
            val transactionDate = backStackEntry.arguments?.getString("transaction_date")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""

            // Reemplazar el placeholder con una cadena vacía
            val finalDescription = if (description == "NoDescription") "" else description

            // Pasa los parámetros a EditTransactionView
            EditTransactionView(
                navController = navController,
                transactionId = transactionId,
                transactionTypeName = transactionTypeName,
                transactionCategoryName = transactionCategoryName,
                description = finalDescription, // Usar finalDescription
                value = value,
                transactionDate = transactionDate
            )
        }

        //Reports

        composable(
            route = "${Routes.ReportsSelectionView}/{farmId}/{farmName}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("farmName") { type = NavType.StringType },
                )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""

            BackHandler {
                // No action performed, back gesture is disabled
            }
            ReportsSelectionView(
                navController = navController,
                farmId = farmId,
                farmName = farmName
            )
        }

        composable(
            route = "${Routes.FormFinanceReportView}/{farmId}/{farmName}",
            arguments = listOf(
                navArgument("farmId") { type = NavType.IntType },
                navArgument("farmName") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getInt("farmId") ?: 0
            val farmName = backStackEntry.arguments?.getString("farmName") ?: ""

            BackHandler {
                // No action performed, back gesture is disabled
            }
            FormFinanceReportView(
                navController = navController,
                farmId = farmId,
                farmName = farmName
            )
        }

        composable(
            route = "financeReport/{plotIds}/{startDate}/{endDate}/{historyParam}",
            arguments = listOf(
                navArgument("plotIds") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.StringType },
                navArgument("endDate") { type = NavType.StringType },
                navArgument("historyParam") { type = NavType.StringType } // Nuevo argumento
            )
        ) { backStackEntry ->
            val plotIdsParam = backStackEntry.arguments?.getString("plotIds") ?: ""
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""
            val historyParam = backStackEntry.arguments?.getString("historyParam") ?: "0" // Valor por defecto

            val plotIds = plotIdsParam.split(",").mapNotNull { it.toIntOrNull() }
            val includeTransactionHistory = historyParam == "1" // Interpretación del parámetro

            FinanceReportView(
                navController = navController,
                plotIds = plotIds,
                startDate = startDate,
                endDate = endDate,
                includeTransactionHistory = includeTransactionHistory // Pasar el valor booleano
            )
        }


    }
}
