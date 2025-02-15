// ProfileView.kt
package com.example.coffetech.view.Auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Auth.ProfileViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableFieldLabel
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.common.TopBarWithBackArrow

/**
 * Composable function that renders the profile editing screen.
 * It allows the user to view and edit their profile information, such as their name,
 * and navigate to change their password.
 *
 * @param modifier A [Modifier] to adjust the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [ProfileViewModel] used to manage the state and logic for the profile view.
 */
@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val name by viewModel.name
    val email by viewModel.email
    val errorMessage by viewModel.errorMessage
    val isProfileUpdated by viewModel.isProfileUpdated
    val isLoading by viewModel.isLoading
    val scrollState = rememberScrollState()
    val ErrorMessage by viewModel.nameErrorMessage

    // Load user data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadUserData(context)
    }

    Column(modifier = modifier.fillMaxSize()
        .statusBarsPadding() // Ajusta el padding para la barra de estado (notificaciones)
        .navigationBarsPadding(), // Ajusta el padding para la barra de navegación
    ) {
        // Top bar with a back arrow for navigation
        TopBarWithBackArrow(
            onBackClick = { navController.navigate(Routes.StartView) },
            title = "Editar Perfil"
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Fields for editing the profile
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                // Name field
                ReusableDescriptionText(
                    text = "Nombre",
                    textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
                )
                Spacer(modifier = Modifier.height(10.dp))

                ReusableTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChange(it) },
                    placeholder = "Nombre:",
                    charLimit = 50,
                    modifier = Modifier.fillMaxWidth(),
                            margin = 0.dp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Email field (disabled)
                ReusableDescriptionText(
                    text = "Correo",
                    textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
                )
                Spacer(modifier = Modifier.height(5.dp))

                ReusableTextField(
                    value = email,
                    onValueChange = { },
                    placeholder = "Correo",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    margin = 0.dp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Change password button
                ReusableDescriptionText(
                    text = "Contraseña",
                    textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
                )
                Spacer(modifier = Modifier.height(5.dp))


                ReusableTextButton(
                    navController = navController,
                    text = "Cambiar contraseña",

                    destination = Routes.ChangePasswordView // Navigates to login screen on cancel
                )

                // Display error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }
            }



            ReusableButton(
                text = if (isLoading) "Guardando..." else "Guardar",
                onClick = { viewModel.saveProfile(context) { /* Success action */ }},
                buttonType = ButtonType.Green,  // Verde si actualizado, rojo si no
                enabled = isProfileUpdated && name.isNotBlank() && !isLoading,

            )
        }
    }
}


/**
 * Preview function for the ProfileView.
 * It simulates the profile editing screen in a preview environment to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    // Wrapping in your app's theme
    CoffeTechTheme {
        // Creating a mock NavController for the preview
        val navController = NavController(LocalContext.current)

        // Creating a mock ViewModel with some sample data
        val viewModel = ProfileViewModel().apply {
            onNameChange("Daniela Beltrán")
        }

        // Rendering the ProfileView with the mock data
        ProfileView(
            navController = navController,
            viewModel = viewModel
        )
    }
}
