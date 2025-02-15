package com.example.coffetech.view.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.ui.theme.CoffeTechTheme


@Composable
fun ConfirmationView(
    message: String, // El mensaje que quieres mostrar ("Invitación enviada correctamente")
    textButton: String = "Aceptar", // El mensaje para el boton
    onButtonClick: () -> Unit, // La acción que se ejecuta al presionar "Aceptar"
    navController: NavController? = null, // Opcional si necesitas usar el NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Ícono de confirmación (emoji)
                Icon(
                    painter = painterResource(id = R.drawable.emoji_message), // Icono de confirmación
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de confirmación
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Reutilizable
                ReusableButton(
                    text = textButton,
                    onClick = onButtonClick,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.7f),
                    buttonType = ButtonType.Green // Asume que el botón verde es el estilo correcto
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationViewPreview() {
    CoffeTechTheme {
        ConfirmationView(
            message = "Invitación enviada correctamente", // Mensaje de ejemplo para la preview
            onButtonClick = {} // No necesitas implementar nada en este callback para la preview
        )
    }
}

