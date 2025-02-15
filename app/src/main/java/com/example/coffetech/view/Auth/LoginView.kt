package com.example.coffetech.view.Auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.LogoImage
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.ReusableTittleLarge
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Auth.LoginViewModel

/**
 * Composable function that renders the login screen.
 * This screen allows the user to input their email and password, and log into the app.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [LoginViewModel] used to manage the state and logic for login.
 */
@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val email by viewModel.email
    val password by viewModel.password
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by viewModel.isLoading

    BoxWithConstraints( // Detectamos el tamaño disponible
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        val logoSize =
            if (maxHeight < 800.dp) 80.dp else 150.dp // Ajuste dinámico del tamaño del logo

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = 25.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Displays the app logo
            LogoImage(modifier = Modifier.size(logoSize))

            // Displays a welcome message
            ReusableTittleLarge(
                text = "!Bienvenido!",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )


                // Contenedor que envuelve tanto los TextFields
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Ajusta esto para controlar el ancho de todo el contenido
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ReusableDescriptionText(text = "Por favor ingresa tus datos")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Input fields for email and password
                        ReusableTextField(
                            value = email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            placeholder = "Correo Electrónico"
                            ,
                            modifier = Modifier.fillMaxWidth()
                        )

                        ReusableTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholder = "Contraseña",
                            isPassword = true
                            ,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Display an error message if one exists
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(10.dp)
                            )
                        }
                        // Botón para recuperación de contraseña
                        ReusableTextButton(
                            navController = navController,
                            text = "Olvidé la contraseña",
                            destination = Routes.ForgotPasswordView
                        )

                        // Botón de inicio de sesión
                        ReusableButton(
                            text = if (isLoading) "Iniciando sesión..." else "Iniciar sesión",
                            onClick = { viewModel.loginUser(navController, context) },
                            buttonType = ButtonType.Green,
                            enabled = !isLoading,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )



                        // Botón para registrarse
                        ReusableTextButton(
                            navController = navController,
                            text = "¿No tienes cuenta? Registrate",
                            destination = Routes.RegisterView
                        )
                    }
                }
            }
        }
    }


/**
 * Preview function for the LoginView.
 * It simulates the login screen in a preview environment to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CoffeTechTheme {
        LoginView(navController = NavController(LocalContext.current))
    }
}
