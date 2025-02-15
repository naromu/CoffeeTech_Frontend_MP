package com.example.coffetech.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definir los colores personalizados para el tema claro

import androidx.compose.material3.lightColorScheme

// Definir los colores personalizados para el tema claro
private val LightColorScheme = lightColorScheme(

    // Color principal, usado en botones primarios, barras de acción principales, etc.
    primary = Color(0xFFB31D34),  // Rojo para Botones 1

    // Color secundario, utilizado para botones secundarios, detalles adicionales.
    secondary = Color(0xFF49602D),  // Verde oscuro para Botones 2

    // Color de fondo general para la aplicación
    background = Color(0xFFF2F2F2),  // Gris claro para el fondo

    // Superficie principal, aplicado a tarjetas, contenedores, etc.
    surface = Color(0xFF95A94B),  // Verde claro para card1

    // Colores de texto sobre el fondo primario (botones, etc.)
    onPrimary = Color.White,  // Texto blanco sobre Botones 1 (rojo)

    // Colores de texto sobre el fondo secundario (botones secundarios, etc.)
    onSecondary = Color.White,  // Texto blanco sobre Botones 2 (verde oscuro)

    // Color del texto sobre superficies (como tarjetas, diálogos, etc.)
    onSurface = Color.Black,  // Texto negro sobre card1 (verde claro)

    // Otros colores adicionales
    primaryContainer = Color(0xFFB3C896), // Verde suave para card2
    onPrimaryContainer = Color.Black,     // Texto negro sobre card2 (verde suave)

    secondaryContainer = Color(0xFF77AF44), // Verde vivo para card3
    onSecondaryContainer = Color.Black,     // Texto blanco sobre card3 (verde vivo)

    // Colores para diferentes tipos de texto
    tertiary = Color(0xFF777438),  // Amarillo oscuro para Texto 2
    onTertiary = Color.Black,      // Texto negro sobre texto terciario
)


@Composable
fun CoffeTechTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
