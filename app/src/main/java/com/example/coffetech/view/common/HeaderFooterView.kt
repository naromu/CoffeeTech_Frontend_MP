package com.example.coffetech.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.viewmodel.common.HeaderFooterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffetech.utils.SharedPreferencesHelper



import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.coffetech.common.ReusableTittleSmall

@Composable
fun HeaderFooterView(
    modifier: Modifier = Modifier,
    title: String,
    navController: NavController,
    currentView: String = "",
    content: @Composable () -> Unit
) {
    val headerFooterViewModel: HeaderFooterViewModel = viewModel()
    val isMenuVisible by headerFooterViewModel.isMenuVisible.collectAsState()
    val isLoading by headerFooterViewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val sharedPreferencesHelper = SharedPreferencesHelper(context)
    val userName = sharedPreferencesHelper.getUserName()

    Scaffold(
        modifier = Modifier
            .systemBarsPadding() // Aplica padding en las áreas de barras del sistema (notificaciones y navegación)
            .navigationBarsPadding() // Ajusta el padding para la barra de navegación
            .statusBarsPadding(), // Ajusta el padding para la barra de estado (notificaciones)
        topBar = {
            TopBarWithHamburger(
                onHamburgerClick = headerFooterViewModel::toggleMenu,
                title = title,
                backgroundColor = Color.White
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentView = currentView,
                navController = navController,
                onHomeClick = { headerFooterViewModel.onHomeClick(navController) },
                onFincasClick = { headerFooterViewModel.onFincasClick(navController) },
                onCentralButtonClick = { headerFooterViewModel.onCentralButtonClick(context) },
                onLaborClick = { headerFooterViewModel.onLaborClick(navController, context) },
                onReportsClick = { headerFooterViewModel.onReportsClick(navController, context) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Respetar el padding total de las barras del sistema
        ) {
            content()

            if (isMenuVisible) {
                HamburgerMenu(
                    profileImage = painterResource(id = R.drawable.menu_icon),
                    profileName = userName,
                    onProfileClick = { headerFooterViewModel.onProfileClick(navController) },
                    onNotificationsClick = { headerFooterViewModel.onNotificationsClick(navController) },
                    onHelpClick = headerFooterViewModel::onHelpClick,
                    onLogoutClick = { headerFooterViewModel.onLogoutClick(context, navController) },
                    isLoading = isLoading,
                    onCloseClick = headerFooterViewModel::toggleMenu,
                    modifier = Modifier.systemBarsPadding() // Respeta las áreas seguras del sistema
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HeaderFooterViewPreview() {
    val navController = rememberNavController()
    HeaderFooterView(
        title = "Mi App",
        currentView = "Costos",
        navController = navController
    ) {
        // Contenido de ejemplo
        Box(modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Contenido Principal")
        }
    }
}


@Composable
fun TopBarWithHamburger(
    onHamburgerClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(width = 360.dp, height = 74.dp)
            .background(Color.White)
            .padding(10.dp)

    ) {
        // El texto centrado
        ReusableTittleSmall(
            text = title,

            modifier = Modifier.align(Alignment.Center)
        )

        // El ícono alineado a la izquierda
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            IconButton(onClick = onHamburgerClick) {
                Icon(
                    painter = painterResource(R.drawable.menu_icon),
                    contentDescription = "Menu",
                    tint = Color(0xFF2B2B2B) ,
                            modifier = Modifier.size(56.dp)
                )
            }
        }
    }


}

@Composable
fun HamburgerMenu(
    profileImage: Painter,
    profileName: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isLoading: Boolean, // Nuevo parámetro para rastrear el estado de carga
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color.White)
            .border(1.dp, Color.LightGray)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Botón de cierre
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Cerrar menú",
                    tint = Color(0xFF2B2B2B)
                )
            }

            // Imagen de perfil y nombre
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = profileName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider()

            // Opciones del menú
            MenuOption(
                icon = painterResource(R.drawable.user),
                text = "Perfil",
                onClick = onProfileClick
            )
            MenuOption(
                icon = painterResource(R.drawable.bell),
                text = "Notificaciones",
                onClick = onNotificationsClick
            )
           /* MenuOption(
                icon = painterResource(R.drawable.circle_question_regular),
                text = "Ayuda",
                onClick = onHelpClick
            )*/

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB31D34),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),

                enabled = !isLoading // Deshabilitar si está en estado de carga
            ) {
                if (isLoading) {
                    Text("Cerrando sesión...") // Texto mientras se está cerrando sesión
                } else {
                    Icon(
                        painter = painterResource(R.drawable.logout),
                        contentDescription = "Cerrar sesión",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión", color = Color.White)
                }
            }
        }
    }
}


@Composable
private fun MenuOption(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color(0xFF2B2B2B),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentView: String,
    navController: NavController,
    onHomeClick: () -> Unit,
    onFincasClick: () -> Unit,
    onCentralButtonClick: () -> Unit,
    onReportsClick: () -> Unit,
    onLaborClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    BottomAppBar(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        containerColor = Color.White,
        contentPadding = PaddingValues(horizontal = 6.dp)
    ) {
        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 9.dp)
                .size(90.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(R.drawable.home_icon),
                    contentDescription = "Inicio",
                    tint = if (currentView == "Inicio") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Inicio",
                    color = if (currentView == "Inicio") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = onFincasClick,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .size(90.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.central_icon),
                    contentDescription = "Fincas",
                    tint = if (currentView == "Fincas") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Fincas",
                    color = if (currentView == "Fincas") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }



        IconButton(
            onClick = onLaborClick,
            modifier = Modifier
                .weight(1f)
                .size(90.dp)
                .padding(vertical = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.labor_icon),
                    contentDescription = "Labores",
                    tint = if (currentView == "Labores") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Labores",
                    color = if (currentView == "Labores") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        /*
        IconButton(
            onClick = onReportsClick,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .size(90.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.reports_icon),
                    contentDescription = "Reportes",
                    tint = if (currentView == "Reportes") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Reportes",
                    color = if (currentView == "Reportes") Color(0xFFB31D34) else Color(0xFF9A9A9A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }*/
    }
}



    @Preview(showBackground = true)
@Composable
fun TopBarWithHamburgerPreview() {
    TopBarWithHamburger(
        onHamburgerClick = {},
        title = "Mi App",
        backgroundColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun HamburgerMenuPreview() {
    HamburgerMenu(
        profileImage = painterResource(id = R.drawable.user),
        profileName = "Usuario de Ejemplo",
        onProfileClick = {},
        onNotificationsClick = {},
        onHelpClick = {},
        onLogoutClick = {},
        onCloseClick = {},
        isLoading = false
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(
        currentView= "Home",
        navController = navController,
        onHomeClick = {},
        onFincasClick = {},
        onCentralButtonClick = {},
        onReportsClick = {},
        onLaborClick = {}
    )
}
