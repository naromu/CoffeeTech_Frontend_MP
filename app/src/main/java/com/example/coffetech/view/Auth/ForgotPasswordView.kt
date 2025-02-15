// ForgotPasswordView.kt (View)

package com.example.coffetech.view.Auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.ReusableTittleLarge
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Auth.ForgotPasswordViewModel

/**
 * Composable function that renders the Forgot Password screen.
 * It allows the user to enter their email to receive instructions for resetting their password.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [ForgotPasswordViewModel] that manages the state and logic for the forgot password flow.
 */
@Composable
fun ForgotPasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val email by viewModel.email
    val isEmailValid by viewModel.isEmailValid
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val context =
        LocalContext.current // Obtain the current context for displaying toasts or handling actions

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(25.dp).verticalScroll(scrollState)
        ) {
            // Title of the screen
            ReusableTittleLarge(
                text = "Restablecer contraseña",
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 30.dp)
            )

            // Instructional text explaining the process of password reset
            ReusableDescriptionText(text = "Te enviaremos un correo con las instrucciones para restablecer tu contraseña")
            Spacer(modifier = Modifier.height(16.dp))

            // Input field for the user's email, with validation and error messages
            ReusableTextField(
                value = email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Correo Electrónico",
                isValid = isEmailValid,
                errorMessage = if (isEmailValid) "" else "Correo electrónico no válido"
            )

            // Button to send the password reset request, handling loading and validation states
            ReusableButton(
                text = if (isLoading) "Enviando..." else "Enviar correo",
                onClick = {
                    if (isEmailValid) {
                        viewModel.sendForgotPasswordRequest(navController, context)
                    }
                },
                buttonType = ButtonType.Green,  // Botón verde
                enabled = isEmailValid && !isLoading,
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp) // Padding similar al anterior
            )

            ReusableTextButton(
                navController = navController,
                destination = Routes.LoginView // Define la ruta a la que navegar al cancelar
            )
        }
    }
}

/**
 * Composable function that renders the button for sending the forgot password request.
 *
 * @param isEmailValid A Boolean indicating if the entered email is valid.
 * @param isLoading A Boolean indicating if the request is in progress.
 * @param onSendRequest A lambda function that triggers when the button is clicked, sending the password reset request.
 */
@Composable
fun ForgotButton(
    isEmailValid: Boolean,
    isLoading: Boolean,
    onSendRequest: () -> Unit
) {
    Button(
        onClick = { if (!isLoading) onSendRequest() }, // Only trigger if not loading
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF49602D),
            contentColor = Color.White
        ),
        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
        enabled = isEmailValid && !isLoading // Disable button if the email is invalid or request is loading
    ) {
        if (isLoading) {
            Text("Enviando...") // Display loading state
        } else {
            Text("Enviar correo") // Normal state
        }
    }
}

/**
 * Composable function that renders a button allowing the user to return to the login screen.
 *
 * @param navController The [NavController] used for navigating to the login screen.
 */
@Composable
fun ForgotBack(navController: NavController) {
    TextButton(
        onClick = {
            navController.navigate(Routes.LoginView) // Navigate to the login screen
        },
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text("Volver", color = Color(0xFF49602D)) // Button text and color
    }
}

/**
 * Preview function for the ForgotPasswordView.
 * It simulates the Forgot Password screen in a preview environment to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    CoffeTechTheme {
        ForgotPasswordView(navController = NavController(LocalContext.current))
    }
}
