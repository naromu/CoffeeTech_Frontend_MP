package com.example.coffetech.view.Auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.LogoImage
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableDescriptionMediumText
import com.example.coffetech.common.ReusableDescriptionText
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.common.ReusableTextField
import com.example.coffetech.common.ReusableTittleLarge
import com.example.coffetech.common.TermsAndConditionsText
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.Auth.RegisterViewModel

@Composable
fun RegisterView(
    modifier: Modifier = Modifier,
    navController: NavController,
    name: String = "",  // Recibe el nombre
    email: String = "", // Recibe el email
    viewModel: RegisterViewModel = viewModel()
) {
    // Solo inicializar los valores si no están vacíos
    LaunchedEffect(Unit) {
        if (name.isNotEmpty() && viewModel.name.value.isEmpty()) {
            viewModel.onNameChange(name)
        }
        if (email.isNotEmpty() && viewModel.email.value.isEmpty()) {
            viewModel.onEmailChange(email)
        }
    }

    val name by viewModel.name
    val email by viewModel.email
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading
    var acceptTerms by remember { mutableStateOf(false) } // Estado del checkbox

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        val logoSize = if (maxHeight < 800.dp) 80.dp else 150.dp // Ajuste dinámico del tamaño del logo

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
                text = "Crea tu Cuenta",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
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

                    // Input fields for name and email
                    ReusableTextField(
                        value = name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = "Nombre o apodo",
                        charLimit = 40,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ReusableTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholder = "Correo Electrónico",
                        charLimit = 80,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Checkbox para aceptar términos y condiciones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = acceptTerms,
                            onCheckedChange = { acceptTerms = it }
                        )

                        TermsAndConditionsText()
                    }

                    // Mostrar mensaje de error si es necesario
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(10.dp)
                        )
                    }

                    // Botón de siguiente
                    ReusableButton(
                        text = if (isLoading) "Siguiendo..." else "Siguiente",
                        onClick = {
                            // Llama a la función de validación y navegación
                            viewModel.nextButton(navController, context)
                        },
                        buttonType = ButtonType.Green,
                        enabled = !isLoading && acceptTerms,  // Habilitar solo si se aceptan los términos
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
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
fun RegisterScreenPreview() {
    CoffeTechTheme {
        RegisterView(navController = NavController(LocalContext.current))
    }
}
