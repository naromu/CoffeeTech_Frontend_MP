package com.example.coffetech.view.Plot

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.BackButton
import com.example.coffetech.common.ButtonType
import com.example.coffetech.common.ReusableButton
import com.example.coffetech.common.ReusableTextButton
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.coffetech.viewmodel.Plot.PlotViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.location.LocationManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner



/**
 * Composable function that renders a Google Map view.
 * Allows users to select a location by clicking on the map, updating the selected location accordingly.
 *
 * @param location The initial [LatLng] representing the latitude and longitude to center the map.
 * @param onLocationSelected Callback function invoked when a new location is selected on the map.
 */
@Composable
fun GoogleMapView(
    location: LatLng, // Latitud y longitud inicial
    onLocationSelected: (LatLng) -> Unit // Callback para manejar el cambio de ubicación
) {
    // Estado de la posición de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(location, 15f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng: LatLng ->
            onLocationSelected(latLng) // Actualizar la ubicación seleccionada
        }
    ) {
        Marker(
            state = MarkerState(position = location)
        )
    }
}



/**
 * Composable function that renders a view for creating a new plot on the map.
 * This view handles location permissions, displays the map for selecting a plot location, and captures altitude data.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param farmId The unique identifier of the farm to which the plot belongs.
 * @param plotName The name of the plot being created.
 * @param selectedVariety The coffee variety selected for the plot.
 * @param viewModel The [PlotViewModel] that manages the state and logic for creating a plot.
 */
@Composable
fun CreateMapPlotView(
    navController: NavController,
    farmId: Int,
    plotName: String,
    selectedVariety: String,
    viewModel: PlotViewModel = viewModel()
) {
    val context = LocalContext.current
    val location by viewModel.location.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val altitude by viewModel.altitude.collectAsState()
    val isAltitudeLoading by viewModel.isAltitudeLoading.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estados para gestionar permisos y servicios de ubicación
    var isPermissionDeniedPermanently by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(true) }
    var showLocationDialog by remember { mutableStateOf(false) }

    // Obtener el LocationManager
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Observer para detectar cambios en el ciclo de vida de la actividad
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Verificar si los servicios de ubicación están habilitados
                val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (isEnabled != isLocationEnabled) {
                    isLocationEnabled = isEnabled
                    if (!isEnabled) {
                        showLocationDialog = true
                    } else {
                        // Si los servicios de ubicación están habilitados, verificar permisos
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission != locationPermissionGranted) {
                            // Actualizar el estado a través del ViewModel
                            viewModel.updateLocationPermissionStatus(hasPermission)
                            if (hasPermission) {
                                viewModel.fetchLocation(context)
                            } else {
                                coroutineScope.launch {
                                    navController.popBackStack() // Regresar a FarmInformationView
                                }
                            }
                        } else if (isEnabled && hasPermission) {
                            // Si los servicios y permisos están habilitados, obtener la ubicación
                            viewModel.fetchLocation(context)
                        }
                    }
                } else {
                    // Si el estado de los servicios no ha cambiado, aún debemos verificar los permisos
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission != locationPermissionGranted) {
                        viewModel.updateLocationPermissionStatus(hasPermission)
                        if (hasPermission) {
                            viewModel.fetchLocation(context)
                        } else {
                            coroutineScope.launch {
                                navController.popBackStack() // Regresar a FarmInformationView
                            }
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    // Verificar si los servicios de ubicación están habilitados al iniciar
    LaunchedEffect(Unit) {
        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isLocationEnabled) {
            showLocationDialog = true
        }
    }

    // Crear el lanzador de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.updateLocationPermissionStatus(isGranted)
            viewModel.fetchLocation(context)
        } else {
            val activity = context as? Activity
            if (activity != null) {
                isPermissionDeniedPermanently = !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            coroutineScope.launch {
                snackbarHostState.showSnackbar("Se necesitan permisos de localización")
                if (!isPermissionDeniedPermanently) {
                    navController.popBackStack() // Regresa a FarmInformationView
                }
            }
        }
    }

    // Solicitar permisos al iniciar si no están concedidos
    LaunchedEffect(Unit) {
        if (!viewModel.checkLocationPermission(context)) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.updateLocationPermissionStatus(true)
            viewModel.fetchLocation(context)
        }
    }

    // Detectar cambios en los estados de permisos y servicios de ubicación
    LaunchedEffect(locationPermissionGranted, isLocationEnabled) {
        if (locationPermissionGranted && isLocationEnabled) {
            viewModel.fetchLocation(context)
        }
    }

    // Interceptar el botón de back para navegar con los datos
    BackHandler {
        navController.navigate(
            "createPlotInformationView/$farmId?plotName=${Uri.encode(plotName)}&selectedVariety=${Uri.encode(selectedVariety)}"
        )
    }

    // Estructura de la UI usando Scaffold para manejar el Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when {
                    locationPermissionGranted && isLocationEnabled -> {
                        // Contenido principal cuando los permisos y servicios de ubicación están habilitados
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
                                    .fillMaxWidth(0.95f) // Haz que el contenedor ocupe el 95% del ancho de la pantalla
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 20.dp, vertical = 30.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())// Hace que el contenido sea scrolleable

                                ) {
                                    // Botón de cerrar o volver (BackButton)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        BackButton(
                                            navController = navController,
                                            onClick = { navController.navigate("FarmInformationView/${farmId}") },
                                            modifier = Modifier.size(32.dp)


                                        )
                                    }


                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Título de la pantalla
                                    Text(
                                        text = "Crear Lote",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.W600,
                                        fontSize = 30.sp,
                                        color = Color(0xFF49602D),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(30.dp))

                                    // Título para ubicación
                                    Text(
                                        text = "Ubicación",
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.W500,
                                        fontSize = 22.sp,
                                        color = Color(0xFF49602D),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (location != null) {
                                        GoogleMapView(
                                            location = location!!,
                                            onLocationSelected = { latLng ->
                                                viewModel.onLocationChange(latLng)
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Mostrar latitud, longitud y altitud debajo del mapa
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Latitud: ${latitude.take(10)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Longitud: ${longitude.take(10)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = if (isAltitudeLoading) "Obteniendo altitud..." else "Altitud: ${altitude ?: "No disponible"} metros",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                        }

                                    } else {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (errorMessage.isNotEmpty()) {
                                        Text(
                                            text = errorMessage,
                                            color = Color.Red,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    ReusableButton(
                                        text = if (isLoading) "Guardando..." else "Guardar",
                                        onClick = {
                                            viewModel.onCreatePlot(
                                                navController = navController,
                                                context = context,
                                                farmId = farmId,
                                                plotName = plotName,
                                                coffeeVarietyName = selectedVariety
                                            )
                                        },
                                        modifier = Modifier
                                            .size(width = 160.dp, height = 48.dp)
                                            .align(Alignment.CenterHorizontally),
                                        buttonType = ButtonType.Green,
                                        enabled = altitude != null && !isAltitudeLoading && !isLoading // Deshabilitar si la altitud no está lista
                                    )

                                    ReusableTextButton(
                                        navController = navController,
                                        destination = "createPlotInformationView/$farmId?plotName=${Uri.encode(plotName)}&selectedVariety=${Uri.encode(selectedVariety)}",
                                        text = "Volver",
                                        modifier = Modifier
                                            .size(width = 160.dp, height = 54.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }

                    isPermissionDeniedPermanently -> {
                        // Mostrar el botón para abrir la configuración si los permisos están denegados permanentemente
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Los permisos de localización fueron denegados. Habilítalos manualmente desde la configuración.",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            ReusableButton(
                                text = "Abrir Configuración",
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ReusableTextButton(
                                navController = navController,
                                text = "Volver",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                destination = "FarmInformationView/$farmId"
                            )
                        }
                    }

                    else -> {



                        // Mostrar diálogo para activar los servicios de ubicación
                        if (showLocationDialog) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Los servicios de ubicación están desactivados. Habilítalos manualmente desde la configuración.",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                ReusableButton(
                                    text = "Abrir Configuración",
                                    onClick = {
                                        showLocationDialog = false
                                        // Abrir la configuración de ubicación
                                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ReusableTextButton(
                                    navController = navController,
                                    text = "Volver",
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    destination = "FarmInformationView/$farmId"
                                )
                            }

                        } else {
                            // Mostrar una interfaz de carga o mensaje adecuado
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF101010))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddLocationPlotPreview() {
    CoffeTechTheme {
        CreateMapPlotView(
            navController = NavController(LocalContext.current),
            farmId = 1,
            plotName = "",
            selectedVariety = ""
        )
    }
}

