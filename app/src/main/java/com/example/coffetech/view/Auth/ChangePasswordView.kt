package com.example.coffetech.view.Auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableFieldLabel
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.TopBarWithBackArrow
import com.example.coffetech.viewmodel.Auth.ChangePasswordViewModel

/**
 * Composable function that renders a view for changing the user's password.
 *
 * @param modifier A [Modifier] for adjusting the layout or appearance of the view.
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [ChangePasswordViewModel] that manages the state of the password change process.
 */
@Composable
fun ChangePasswordView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    val currentPassword by viewModel.currentPassword
    val newPassword by viewModel.newPassword
    val confirmPassword by viewModel.confirmPassword
    val errorMessage by viewModel.errorMessage
    val isPasswordChanged by viewModel.isPasswordChanged
    val context = LocalContext.current
    val isLoading by viewModel.isLoading

    LaunchedEffect(isPasswordChanged) {
        if (isPasswordChanged) {
            navController.popBackStack() // Navegar a la pantalla anterior
        }
    }

    // Ajusta el padding para la barra de estado (notificaciones)
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding() // Ajusta el padding para la barra de estado (notificaciones)
            .navigationBarsPadding(), // Ajusta el padding para la barra de navegación
    ) {
        TopBarWithBackArrow(
            onBackClick = { navController.navigate(Routes.ProfileView) },
            title = "Actualizar contraseña"
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            ReusableDescriptionText(
                text = "Contraseña actual",
                modifier = Modifier.padding(start = 25.dp),
                textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
            )
            Spacer(modifier = Modifier.height(5.dp))

            ReusableTextField(
                value = currentPassword,
                onValueChange = { viewModel.onCurrentPasswordChange(it) },
                placeholder = "Contraseña actual",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReusableDescriptionText(
                text = "Nueva contraseña",
                modifier = Modifier.padding(start = 25.dp),
                textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
            )
            Spacer(modifier = Modifier.height(5.dp))

            ReusableTextField(
                value = newPassword,
                onValueChange = { viewModel.onNewPasswordChange(it) },
                placeholder = "Nueva contraseña",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReusableDescriptionText(
                text = "Confirme nueva contraseña",
                modifier = Modifier.padding(start = 25.dp),
                textAlign = TextAlign.Left // Cambia el alineamiento a la izquierda
            )
            Spacer(modifier = Modifier.height(5.dp))

            ReusableTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                placeholder = "Confirme nueva contraseña",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Mostrar el mensaje de error si las contraseñas no cumplen con los requisitos de seguridad
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ReusableButton(
                text = if (isLoading) "Guardando..." else "Guardar",
                onClick = {
                    if (viewModel.validatePasswordRequirements()) {
                        viewModel.changePassword(context)
                    }
                },
                buttonType = ButtonType.Green,  // Botón verde
                enabled = !isLoading && errorMessage.isEmpty(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReusableTextButton(
                navController = navController,
                destination = Routes.ProfileView // Define a dónde navegar cuando presionas "Cancelar"
            )
        }
    }
}




/**
 * A preview composable function to simulate and display the [ChangePasswordView] in a preview window.
 */
@Preview(showBackground = true)
@Composable
fun ChangePasswordViewPreview() {
    ChangePasswordView(
        navController = rememberNavController() // Simula un NavController para la vista previa
    )
}
