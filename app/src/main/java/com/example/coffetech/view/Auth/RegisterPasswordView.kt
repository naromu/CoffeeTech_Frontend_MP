// RegisterScreen.kt (View)

package com.example.coffetech.view.Auth

import ReusableInfoIcon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.coffetech.viewmodel.Auth.RegisterPasswordViewModel
import com.example.coffetech.viewmodel.Auth.RegisterViewModel

/**
 * Composable function that renders the registration screen.
 * It allows the user to input their name, email, and password to create an account.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [RegisterViewModel] used to manage the state and logic for the registration process.
 */
@Composable
fun RegisterPasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    name: String,
    email: String,
    viewModel: RegisterPasswordViewModel = viewModel()
) {

    val password by viewModel.password
    val confirmPassword by viewModel.confirmPassword
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val scrollState = rememberScrollState()
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
            // Displays the app logo con tamaño dinámico
            LogoImage(modifier = Modifier.size(logoSize))

            // Large header text for registration
            ReusableTittleLarge(
                text = "Crea tu contraseña",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )


                // Contenedor que envuelve tanto el Row como los TextFields
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
                        // Row para el texto y el ícono
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente al centro
                            horizontalArrangement = Arrangement.Center // Alinea horizontalmente al centro
                        ) {
                            ReusableDescriptionText(
                                text = "Crea tu contraseña",
                                modifier = Modifier.padding(end = 16.dp) // Opcional: Añade un pequeño espaciado entre el texto y el ícono
                            )
                            ReusableInfoIcon(modifier = Modifier.size(24.dp)) // Tamaño del ícono ajustado
                        }

                        // Campos de texto
                        ReusableTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholder = "Contraseña",
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth() // Ocupa el mismo ancho que el Row
                        )

                        ReusableTextField(
                            value = confirmPassword,
                            onValueChange = { viewModel.onConfirmPasswordChange(it) },
                            placeholder = "Confirmar Contraseña",
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth() // Ocupa el mismo ancho que el Row
                        )

                        // Display an error message if one exists
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f) // Asegura que el mensaje de error use el mismo ancho que los TextFields
                                    .padding(10.dp)
                            )
                        }

                        // Otros componentes, como el botón de registro...
                        ReusableButton(
                            text = if (isLoading) "Registrandose..." else "Registrarse",
                            onClick = { viewModel.registerUser(navController, context, name, email) },
                            buttonType = ButtonType.Green,
                            enabled = !isLoading,
                            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                        )

                        // Botón para volver
                        ReusableTextButton(
                            navController = navController,
                            destination = "${Routes.RegisterView}?name=$name&email=$email",  // Pasar el nombre y email como parámetros en la ruta
                            text = "volver",
                            maxWidth = 400.dp
                        )




                        // Botón para iniciar sesión
                        ReusableTextButton(
                            navController = navController,
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            maxWidth = 400.dp,
                            destination = Routes.LoginView
                        )
                    }
                }
            }
        }
    }



/**
 * Preview function for the RegisterView.
 * It simulates the registration screen in a preview environment to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun RegisterPasswordScreenPreview() {
    CoffeTechTheme {
        val name = "John Doe" // Ejemplo de nombre
        val email = "john.doe@example.com" // Ejemplo de correo
        RegisterPasswordView(
            navController = NavController(LocalContext.current),
            name = name,
            email = email
        )
    }
}

