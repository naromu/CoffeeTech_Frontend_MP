// VerifyAccountView.kt

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
import com.example.coffetech.viewmodel.Auth.VerifyAccountViewModel

/**
 * Composable function that renders the account verification screen.
 * The user inputs the verification code received via email to verify their account.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [VerifyAccountViewModel] that manages the state and logic for the account verification process.
 */
@Composable
fun VerifyAccountView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: VerifyAccountViewModel = viewModel() // Using ViewModel for handling state
) {
    val token by viewModel.token
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val isLoading by viewModel.isLoading
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding() // Ajusta el padding para la barra de estado (notificaciones)
            .navigationBarsPadding(), // Ajusta el padding para la barra de navegación

        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(25.dp).verticalScroll(scrollState)
        ) {
            // Title for the verification screen
            ReusableTittleLarge(text = "Verifica tu cuenta", modifier = Modifier.padding(top = 30.dp, bottom = 30.dp))

            // Description for entering the verification code
            ReusableDescriptionText(text = "Por favor, introduce el código que te acabamos de enviar al correo para verificar tu cuenta. Tambien revisa el SPAM si no lo encuentras.")

            Spacer(modifier = Modifier.height(16.dp))
            // Input field for the user to enter the verification token
            ReusableTextField(
                value = token,
                onValueChange = { viewModel.onTokenChange(it) },
                placeholder = "Código",
                modifier = Modifier.fillMaxWidth()
            )

            // Button to verify the account
            ReusableButton(
                text = if (isLoading) "Verificando..." else "Verificar Correo",
                onClick = { viewModel.verifyUser(navController, context) },
                buttonType = ButtonType.Green,  // Botón verde
                enabled = !isLoading,  // Deshabilitar mientras está cargando
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
            )
            ReusableTextButton(
                navController = navController,
                destination = "${Routes.LoginView}",  // Pasar el nombre y email como parámetros en la ruta
                text = "volver",
                maxWidth = 400.dp
            )

            // Display an error message if one exists
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(10.dp))
            }
        }
    }
}



/**
 * Preview function for the VerifyAccountView.
 * It simulates the account verification screen in a preview environment to visualize the layout.
 */
@Preview(showBackground = true)
@Composable
fun VerifyAccountPreview() {
    CoffeTechTheme {
        VerifyAccountView(navController = NavController(LocalContext.current))
    }
}
