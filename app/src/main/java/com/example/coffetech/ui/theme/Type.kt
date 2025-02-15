package com.example.coffetech.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import com.example.coffetech.R

// Definir la familia de fuentes (Roboto)
val RobotoFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black)
)

// Definir los estilos de texto personalizados
val TitleLarge = TextStyle(
    fontFamily = RobotoFamily,
    fontWeight = FontWeight.Black,
    fontSize = 40.sp
)

val TitleMedium = TextStyle(
    fontFamily = RobotoFamily,
    fontWeight = FontWeight.Black,
    fontSize = 34.sp
)

val TitleSmall = TextStyle(
    fontFamily = RobotoFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 25.sp
)

val Description = TextStyle(
    fontFamily = RobotoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp
)

// Set de Material typography para Compose
val Typography = Typography(
    // Sobrescribir los estilos predeterminados con los personalizados
    titleLarge = TitleLarge, // Usa TitleLarge definido arriba
    titleMedium = TitleMedium,
    titleSmall = TitleSmall,  // Usa TitleSmall definido arriba
    bodyLarge = Description,  // Usa Description para textos grandes
    bodyMedium = TextStyle(   // Puedes usarlo para otro estilo intermedio
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(   // Estilo para etiquetas pequeñas
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
