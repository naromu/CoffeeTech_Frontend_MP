// ResultHealthCheckView.kt
package com.example.coffetech.view.healthcheck

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.R
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.DetectionResultInfoCard
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.SharedViewModel
import com.example.coffetech.viewmodel.healthcheck.ResultHealthCheckViewModel
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch

@Composable
fun ResultHealthCheckView(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: ResultHealthCheckViewModel = viewModel()
) {
    val context = LocalContext.current

    // Obtener los datos del SharedViewModel
    val culturalWorkTaskId by sharedViewModel.culturalWorkTaskId.collectAsState()
    val culturalWorksName by sharedViewModel.culturalWorksName.collectAsState()
    val imagesBase64 by sharedViewModel.imagesBase64.collectAsState()

    // Asignar a variables locales para evitar errores de smart cast
    val culturalWorkTaskIdLocal = culturalWorkTaskId
    val culturalWorksNameLocal = culturalWorksName

    // Trigger API call cuando la composable entra a la vista
    LaunchedEffect(Unit) {
        if (culturalWorkTaskIdLocal != null && culturalWorksNameLocal != null && imagesBase64.isNotEmpty()) {
            viewModel.fetchDetectionResults(
                culturalWorksName = culturalWorksNameLocal,
                culturalWorkTasksId = culturalWorkTaskIdLocal,
                imagesBase64 = imagesBase64,
                context = context
            )
        } else {
            viewModel.setErrorMessage("Datos incompletos para realizar la detección.")
            Toast.makeText(
                context,
                "Datos incompletos para realizar la detección.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Obtener estados del ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val detectionResults by viewModel.detectionResults.collectAsState()

    // Decodificar todas las imágenes una vez y almacenarlas en una lista
    val decodedImages: List<ImageBitmap?> = remember(imagesBase64) {
        imagesBase64.map { base64Str ->
            try {
                val decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Función para convertir un Vector Drawable a ImageBitmap
    fun vectorDrawableToBitmap(@DrawableRes id: Int, context: android.content.Context): ImageBitmap? {
        val drawable = AppCompatResources.getDrawable(context, id) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }

    // Cargar la imagen placeholder una sola vez
    val placeholderBitmap: ImageBitmap? = remember {
        vectorDrawableToBitmap(R.drawable.placeholder_image, context)
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(Color(0xFF101010)) // Fondo oscuro
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Contenedor ocupa el 95% del ancho de la pantalla
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón de cerrar o volver (BackButton)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            navController.popBackStack()
                            // navController.popBackStack() // Elimina este segundo llamado si no es necesario
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Resultado Chequeo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje y spinner de carga
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No se salga por favor...",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar mensaje de error
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (detectionResults.isNotEmpty()) {
                    // Mostrar los resultados de la detección con imágenes
                    detectionResults.forEach { result ->
                        val imageIndex =
                            result.imagenNumero - 1 // Asumiendo que imagenNumero comienza en 1
                        val image =
                            if (imageIndex in decodedImages.indices) decodedImages[imageIndex] else null
                        if (image != null) {
                            DetectionResultInfoCard(
                                imagen_numero = result.imagenNumero,
                                prediction = result.prediccion,
                                recommendation = result.recomendacion,
                                image = image, // Pasar la imagen al card
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            // Mostrar un placeholder si la imagen no está disponible
                            if (placeholderBitmap != null) {
                                DetectionResultInfoCard(
                                    imagen_numero = result.imagenNumero,
                                    prediction = result.prediccion,
                                    recommendation = result.recomendacion,
                                    image = placeholderBitmap, // Usar el bitmap decodificado
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                // Mostrar un cuadro vacío o algún otro indicador
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Imagen no disponible",
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Mostrar las imágenes recibidas si no hay resultados de detección
                    if (imagesBase64.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(decodedImages.size) { index ->
                                val image = decodedImages[index]
                                if (image != null) {
                                    Image(
                                        bitmap = image,
                                        contentDescription = "Imagen de la tarea",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(Color.Gray, RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Mostrar placeholder si la imagen no se pudo decodificar
                                    if (placeholderBitmap != null) {
                                        Image(
                                            bitmap = placeholderBitmap,
                                            contentDescription = "Imagen de la tarea",
                                            modifier = Modifier
                                                .size(100.dp)
                                                .background(Color.Gray, RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // Mostrar un cuadro vacío o algún otro indicador
                                        Box(
                                            modifier = Modifier
                                                .size(100.dp)
                                                .background(Color.Gray, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No Image",
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No se recibieron imágenes.",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje de error si lo hay
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Guardar
                ReusableButton(
                    text = if (isLoading) "Guardando..." else "Guardar",
                    onClick = {
                        // Mostrar un Toast o iniciar un proceso de guardado
                        Toast.makeText(context, "Guardando resultados...", Toast.LENGTH_SHORT).show()
                        viewModel.acceptPredictions(context) {
                            // Navegar antes de limpiar los datos
                            navController.popBackStack()
                            navController.popBackStack()

                            // Ahora puedes limpiar los datos
                            sharedViewModel.clearData()
                            Toast.makeText(context, "Predicciones aceptadas correctamente.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Descartar
                ReusableButton(
                    text = if (isLoading) "Descartando..." else "Descartar",
                    onClick = {
                        Log.d("ResultHealthCheckView", "Descartar botón presionado")
                        Toast.makeText(context, "Descartando resultados...", Toast.LENGTH_SHORT).show()
                        viewModel.discardPredictions(context) {
                            coroutineScope.launch {
                                try {
                                    Log.d("ResultHealthCheckView", "Descartando: Navegación iniciada")
                                    navController.popBackStack()
                                    navController.popBackStack()
                                    sharedViewModel.clearData()
                                    Toast.makeText(context, "Predicciones descartadas correctamente.", Toast.LENGTH_SHORT).show()
                                    Log.d("ResultHealthCheckView", "Descartando: Operación exitosa")
                                } catch (e: Exception) {
                                    Log.e("ResultHealthCheckView", "Error al descartar: ${e.message}", e)
                                    Toast.makeText(context, "Error al descartar: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(width = 160.dp, height = 48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Red,
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultHealthCheckViewPreview() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()
    CoffeTechTheme {
        ResultHealthCheckView(
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}
