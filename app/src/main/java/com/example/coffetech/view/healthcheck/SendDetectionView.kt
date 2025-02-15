// SendDectectionView.kt
package com.example.coffetech.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.viewmodel.SharedViewModel
import com.example.coffetech.viewmodel.healthcheck.SendDectectionViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun SendDectectionView(modifier: Modifier = Modifier,
                       navController: NavController,
                       culturalWorkTaskId: Int,
                       culturalWorksName: String,
                       sharedViewModel: SharedViewModel,
                       viewModel: SendDectectionViewModel = viewModel()
) {
    val context = LocalContext.current

    // Collect states from ViewModel
    val imageUri by viewModel.imageUri.collectAsState()
    val currentTaskId by viewModel.currentTaskId.collectAsState()
    val showPermissionRationale by viewModel.showPermissionRationale.collectAsState()
    val taskPhotos by viewModel.taskPhotos.collectAsState()


    // SendDectectionView.kt

    LaunchedEffect(taskPhotos) {
        Log.d("SendDectectionView", "Current taskPhotos: ${taskPhotos[culturalWorkTaskId]}")
    }

    // Obtener el número total de fotos para verificar el límite
    val totalPhotos = taskPhotos[culturalWorkTaskId]?.size ?: 0
    val hasReachedLimit = totalPhotos >= SendDectectionViewModel.MAX_PHOTOS_PER_TASK
    val hasImages = taskPhotos[culturalWorkTaskId]?.isNotEmpty() == true

    // Launcher para capturar una foto
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            viewModel.onPictureTaken(success)
            if (success && imageUri != null && currentTaskId != null) {
                Toast.makeText(context, "Foto tomada exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Launcher para importar fotos desde la galería
    val pickMultipleImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        // En SendDectectionView.kt, dentro de pickMultipleImagesLauncher
        onResult = { uris ->
            var totalPhotos = taskPhotos[culturalWorkTaskId]?.size ?: 0
            if (uris.isNotEmpty()) {
                for (uri in uris) {
                    if (totalPhotos >= SendDectectionViewModel.MAX_PHOTOS_PER_TASK) {
                        Toast.makeText(context, "Se alcanzó el límite de 10 fotos.", Toast.LENGTH_SHORT).show()
                        break
                    }
                    totalPhotos += 1
                    viewModel.setImageUri(uri)
                    viewModel.onPictureTaken(true)
                }
                Toast.makeText(context, "Fotos importadas exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se seleccionaron fotos.", Toast.LENGTH_SHORT).show()
            }
            // Ahora puedes restablecer _currentTaskId aquí
            viewModel.endPhotoAction()
}
    )
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openCamera(
        viewModel: SendDectectionViewModel,
        context: Context,
        takePictureLauncher: androidx.activity.result.ActivityResultLauncher<Uri>
    ) {
        val taskId = viewModel.currentTaskId.value
        if (taskId != null) {
            val photoFile = viewModel.createImageFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            viewModel.setImageUri(uri)
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, "No hay una tarea seleccionada para tomar fotos.", Toast.LENGTH_SHORT).show()
        }
    }
    // Launcher para solicitar permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.showPermissionRationale(false)
            openCamera(viewModel, context, takePictureLauncher)
        } else {
            viewModel.showPermissionRationale(true)
        }
    }

    // Función para abrir la cámara


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
                    .padding(10.dp) // Añadir padding interno
                    .verticalScroll(rememberScrollState())
            ) {
                // Botón de cerrar o volver (BackButton)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BackButton(
                        navController = navController,
                        modifier = Modifier.size(32.dp) // Tamaño manejable
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Imágenes Cargadas",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF49602D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar las imágenes capturadas
                taskPhotos[culturalWorkTaskId]?.let { photos ->
                    if (photos.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp), // Puedes ajustar la altura máxima según tus necesidades
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(photos) { uri ->
                                Log.d("SendDectectionView", "Mostrando imagen URI: $uri")
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Foto de la tarea",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No hay imágenes cargadas.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } ?: run {
                    Text(
                        text = "No hay imágenes cargadas.",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensaje cuando se ha alcanzado el límite de fotos
                if (hasReachedLimit) {
                    Text(
                        text = "Ya has seleccionado diez fotos.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botón para tomar una foto
                ReusableButton(
                    text = "Tomar Foto",
                    onClick = {
                        viewModel.startPhotoAction(culturalWorkTaskId)
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                        if (hasPermission && !hasReachedLimit) {
                            openCamera(viewModel, context, takePictureLauncher)
                        } else if (!hasReachedLimit) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = !hasReachedLimit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para importar fotos desde la galería
                ReusableButton(
                    text = "Importar Fotos",
                    onClick = {
                        if (!hasReachedLimit) {
                            viewModel.startPhotoAction(culturalWorkTaskId) // Establecer currentTaskId
                            pickMultipleImagesLauncher.launch("image/*")
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green, // Asumiendo que ButtonType.Blue existe
                    enabled = !hasReachedLimit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar las imágenes
                ReusableButton(
                    text = "Continuar",
                    onClick = {

                            // Obtener las imágenes para la tarea actual
                            val photos = taskPhotos[culturalWorkTaskId] ?: emptyList()

                            // Convertir cada URI a Base64
                            val base64List = photos.mapNotNull { uriToBase64(context, it) }

                            // Establecer los datos en el SharedViewModel
                            sharedViewModel.setImagesBase64(base64List)

                            // Navegar a ResultHealthCheckView
                            navController.navigate(Routes.ResultHealthCheckView)
                        },
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    buttonType = ButtonType.Green,
                    enabled = hasImages  // Habilitado solo si hay imágenes y no está cargando
                )


            }
        }
    }

    // Mostrar diálogo de razón para el permiso de cámara
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { viewModel.showPermissionRationale(false) },
            title = { Text(text = "Permiso de Cámara") },
            text = { Text("Esta aplicación necesita acceso a la cámara para capturar fotos de las tareas.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.showPermissionRationale(false)
                    // Abrir la configuración de la aplicación para que el usuario pueda habilitar el permiso manualmente
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Abrir Configuración")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showPermissionRationale(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun previewSendDectectionView() {
    val navController = NavController(LocalContext.current)
    val sharedViewModel: SharedViewModel = viewModel()

    CoffeTechTheme {
        SendDectectionView(
            navController = navController,
            culturalWorkTaskId = 1,
            culturalWorksName = "Chequeo de estado de maduración",
            sharedViewModel = sharedViewModel

        )
    }
}
