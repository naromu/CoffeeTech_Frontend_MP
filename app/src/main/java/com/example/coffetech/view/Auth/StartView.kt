package com.example.coffetech.view.Auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.ui.theme.White
import com.example.coffetech.view.common.HeaderFooterView
import com.example.coffetech.viewmodel.Auth.StartViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun StartView(
    navController: NavController,
    viewModel: StartViewModel = viewModel()
) {
    HeaderFooterView(
        title = "CoffeeTech",
        currentView = "Inicio",
        navController = navController
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Hacer todo el contenido desplazable
            ) {
                // Mensaje de bienvenida
                Text(
                    text = "Bienvenido a CoffeeTech",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally) // Cambiado a Alignment.CenterHorizontally
                        .padding(bottom = 8.dp)
                )


                // Tarjeta de link para descargar el manual
                LinkCard(
                    text = "Para descargar el manual oprime aquí",
                    linkText = "Descargar manual",
                    url = "https://prueba-deploy--coffeetech.netlify.app/manual.pdf",
                    backgroundColor = Color(152, 172, 76)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tarjeta de link para más información
                LinkCard(
                    text = "Para más información oprime aquí",
                    linkText = "Más información",
                    url = "https://prueba-deploy--coffeetech.netlify.app",
                    backgroundColor = Color(152, 172, 76)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título de subsección
                Text(
                    text = "¿Qué puedes hacer en CoffeeTech?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                // Column para las tarjetas de funcionalidades
                Column {
                    getCardsData().forEach { cardData ->
                        InfoItemCard(
                            title = cardData.title,
                            description = cardData.description,
                            backgroundColor = cardData.backgroundColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LinkCard(
    text: String,
    linkText: String,
    url: String,
    backgroundColor: Color
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Altura fija como en ActionCard
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
    ) {
        // Icono en la esquina superior derecha
        Icon(
            painter = painterResource(id = R.drawable.arrow_forward_icon),
            contentDescription = "Icono de acción",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        )

        // Texto alineado a la izquierda
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart) // Alineación a la izquierda y centrado verticalmente
                .padding(start = 16.dp) // Espaciado desde el borde izquierdo
        ) {
            Text(
                text = linkText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}



@Composable
fun InfoItemCard(title: String, description: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

            // Column que ocupa el resto del ancho disponible
            Column(
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

    }
}


data class CardData(val title: String, val description: String, val backgroundColor: Color)

fun getCardsData(): List<CardData> {
    return listOf(
        CardData(
            title = "Chequeos de salud con IA",
            description = "Detecta plagas, deficiencias nutricionales y maduración con IA de forma precisa.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Gestiona tus fincas y lotes",
            description = "Organiza tus fincas y lotes con colaboradores, floraciones, tareas culturales y chequeos.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Añade colaboradores",
            description = "Añade colaboradores del rol administrador de finca y operador de campo para optimizar la gestión de tu equipo en fincas.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Control de floraciones",
            description = "Registra floraciones y sigue el progreso hasta la cosecha.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Tareas culturales optimizadas",
            description = "Organiza labores y asegura cumplimiento en cada lote.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Reportes de rendimiento",
            description = "Consulta informes para evaluar y mejorar tu rendimiento.",
            backgroundColor = Color.White
        ),
        CardData(
            title = "Autenticación y seguridad",
            description = "Regístrate, inicia sesión y protege tu acceso fácilmente.",
            backgroundColor = Color.White
        )
    )
}



@Preview(showBackground = true)
@Composable
fun StartViewPreview() {
    StartView(navController = rememberNavController())
}
