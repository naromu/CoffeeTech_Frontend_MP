// NewPasswordView.kt

package com.example.coffetech.view.Auth

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.coffetech.common.LogoImage
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Auth.NewPasswordViewModel


/**
 * Composable function that renders the screen for setting a new password.
 * The user enters and confirms their new password. The token received from the reset
 * password email is used to validate the request.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [NewPasswordViewModel] that manages the state and logic for resetting the password.
 * @param token The token used to validate the password reset request.
 */
@Composable
fun NewPasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NewPasswordViewModel = viewModel(),
    token: String // Token passed for password reset validation
) {

    val password by viewModel.password
    val confirmPassword by viewModel.confirmPassword
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading

    // Check if the token is valid and log it
    if (token.isBlank()) {
        Log.e("NewPasswordView", "Token is null or empty.")
        Toast.makeText(context, "Error: Invalid token", Toast.LENGTH_SHORT).show()
        return // Stop the composition if the token is invalid
    } else {
        Log.d("NewPasswordView", "Token received successfully: $token")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)),
        contentAlignment = Alignment.Center
        ,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(25.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Displays the app logo
            LogoImage()
            Spacer(modifier = Modifier.height(40.dp))

            // Instructional text prompting the user to enter a new password
            ReusableDescriptionText(text = "Ingrese su nueva contraseña")

            Spacer(modifier = Modifier.height(16.dp))

            // Input field for the new password, with password masking
            ReusableTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = "Nueva Contraseña",
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input field to confirm the new password, with password masking
            ReusableTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                placeholder = "Confirmar Contraseña",
                isPassword = true
                ,
                modifier = Modifier.fillMaxWidth()
            )

            // Displays an error message if there is any
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to submit the new password reset request
            ReusableButton(
                text = if (isLoading) "Restableciendo..." else "Restablecer",
                onClick = { viewModel.resetPassword(navController, context, token) },
                buttonType = ButtonType.Green,  // Botón verde
                enabled = !isLoading,
                modifier = Modifier.padding(bottom = 16.dp, top = 10.dp)
            )


            // Button to cancel the password reset process and return to the login screen
            ReusableTextButton(
                navController = navController,
                text = "Volver",
                destination = Routes.LoginView // Navigates to login screen on cancel
            )
        }
    }
}


/**
 * Preview function for the NewPasswordView.
 * It simulates the new password reset screen with a sample token to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun NewPasswordPreview() {
    CoffeTechTheme {
        // A sample token is provided for preview purposes
        NewPasswordView(navController = NavController(LocalContext.current), token = "sampleToken")
    }
}

