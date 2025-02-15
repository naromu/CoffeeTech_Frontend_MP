package com.example.coffetech.view.Auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.coffetech.viewmodel.Auth.ConfirmTokenForgotPasswordViewModel

/**
 * Composable function that renders the view for confirming the token to reset the password.
 * It prompts the user to enter the token received for password reset.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [ConfirmTokenForgotPasswordViewModel] that manages the state of the token confirmation.
 */
@Composable
fun ConfirmTokenForgotPasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ConfirmTokenForgotPasswordViewModel = viewModel()
) {
    val token by viewModel.token
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current // Obtener el contexto aquí
    val isLoading by viewModel.isLoading
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(5.dp)
            .statusBarsPadding() // Ajusta el padding para la barra de estado (notificaciones)
        .navigationBarsPadding(), // Ajusta el padding para la barra de navegación

        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(25.dp).verticalScroll(scrollState)

        ) {
            ReusableTittleLarge(text = "Restablecer Contraseña", modifier = Modifier.padding(top = 30.dp, bottom = 30.dp))

            ReusableDescriptionText(text = "Por favor, introduce el código para restablecer tu contraseña")

            Spacer(modifier = Modifier.height(16.dp))

            ReusableTextField(
                value = token,
                onValueChange = {
                    viewModel.onTokenChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Código"
            )
            Spacer(modifier = Modifier.height(16.dp))


            ReusableButton(
                text = if (isLoading) "Confirmando..." else "Confirmar",
                onClick = {
                    if (token.isNotEmpty()) {
                        viewModel.confirmToken(navController, context)
                    }
                },
                buttonType = ButtonType.Green,  // Botón verde
                enabled = !isLoading,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            ReusableTextButton(
                navController = navController,
                destination = Routes.LoginView // Define la ruta a la que navegar al cancelar
            )
        }
    }
}


/**
 * Preview function for the ConfirmTokenForgotPasswordView.
 * Simulates the view in a preview environment to visualize its layout and content.
 */
@Preview(showBackground = true)
@Composable
fun ConfirmTokenForgotPasswordPreview() {
    CoffeTechTheme {
        ConfirmTokenForgotPasswordView(navController = NavController(LocalContext.current))
    }
}
