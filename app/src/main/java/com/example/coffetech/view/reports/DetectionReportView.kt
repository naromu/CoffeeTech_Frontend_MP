// DetectionReportView.kt
package com.example.coffetech.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.common.TopBarWithBackArrow
import com.example.coffetech.model.Detection
import com.example.coffetech.model.DetectionHistory
import com.example.coffetech.view.components.CsvFloatingActionButton
import com.example.coffetech.view.components.HealthStatusBarChart
import com.example.coffetech.view.components.LineChartDetectionStatus
import com.example.coffetech.view.components.NutrientPieChart
import com.example.coffetech.view.components.PdfFloatingActionButton
import com.example.coffetech.viewmodel.reports.DetectionReportViewModel
import com.example.coffetech.view.components.generateCsvDetection
import com.example.coffetech.view.components.generatePdfDetection
import com.example.coffetech.viewmodel.reports.HealthStatus
import com.example.coffetech.viewmodel.reports.MaturityStatusPerDate
import com.example.coffetech.viewmodel.reports.NutrientDeficiency
@Composable
fun DetectionReportView(
    modifier: Modifier = Modifier,
    navController: NavController,
    plotIds: List<Int>,
    startDate: String,
    endDate: String,
    viewModel: DetectionReportViewModel = viewModel()
) {
    val TAG = "DetectionReportView"

    Log.d(TAG, "DetectionReportView Composable iniciado")

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val detectionData by viewModel.detectionData.collectAsState()
    Log.d(TAG, "Detection Data Loaded: ${detectionData != null}, Content: $detectionData")

    val username by viewModel.username.collectAsState()



    // Estados para manejar permisos
    var showPdfPermissionRationale by remember { mutableStateOf(false) }
    var showCsvPermissionRationale by remember { mutableStateOf(false) }

    val maturityStatusPerDate by viewModel.maturityStatusPerDate.collectAsState()
    val nutrientDeficiencies by viewModel.nutrientDeficiencies.collectAsState()
    val healthStatuses by viewModel.healthStatuses.collectAsState()
    val totalCharts = nutrientDeficiencies.size + healthStatuses.size + if (maturityStatusPerDate.isNotEmpty()) 1 else 0

    // Lista para almacenar los bitmaps de los gráficos
    val chartBitmaps = remember { mutableStateListOf<Pair<String, Bitmap>>() }

    // Launchers para solicitar permisos
    val pdfPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Permiso de almacenamiento concedido para PDF")
            if (detectionData != null) {
                // Una vez que los gráficos sean capturados, el PDF se generará automáticamente
                // No se necesita hacer nada aquí
            } else {
                Toast.makeText(context, "No hay datos para generar el PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e(TAG, "Permiso de almacenamiento denegado para PDF")
            showPdfPermissionRationale = true
        }
    }

    val csvPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Permiso de almacenamiento concedido para CSV")
            if (detectionData != null) {
                generateCsvDetection(context, detectionData!!, "Historial_Detecciones.csv")
            } else {
                Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e(TAG, "Permiso de almacenamiento denegado para CSV")
            showCsvPermissionRationale = true
        }
    }

    // Función para manejar la generación del PDF
    fun handlePdfGeneration() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Verificar permisos
        } else {
            // Para Android Q y superiores, no se necesita permiso
            if (detectionData != null) {
                val includedLots = detectionData!!.map { it.lote_name }.distinct()

                val introduction = "Este reporte, elaborado por $username, está dirigido a las partes interesadas. Contiene gráficas y resumen para el entendimiento del usuario sobre las detecciones realizadas entre $startDate y $endDate. Los lotes que se incluyen son: ${includedLots.joinToString(", ")}. A continuación, se presentan las deficiencias nutricionales identificadas, las plagas identificadas, el conteo de estados de maduración de los granos de café en cada lote y el historial de las detecciones efectuadas durante este periodo."

                generatePdfDetection(
                    context = context,
                    detections = detectionData!!,
                    fileName = "Historial_Detecciones.pdf",
                    chartBitmaps = chartBitmaps,
                    generatedBy = username, // Variable que contiene el nombre del usuario que generó el reporte
                    introduction = introduction,
                    nutrientDeficiencies = nutrientDeficiencies, // Datos obtenidos del ViewModel
                    healthStatuses = healthStatuses, // Datos obtenidos del ViewModel
                    includedLots = detectionData!!.map { it.lote_name }.distinct(), // Lista de lotes incluidos
                    startDate = startDate, // Fecha de inicio del reporte
                    endDate = endDate, // Fecha de fin del reporte
                    maturityStatusPerDate = maturityStatusPerDate // Datos de maduración obtenidos del ViewModel
                )


            } else {
                Toast.makeText(context, "No hay datos para generar el PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Función para iniciar la generación del CSV
    fun initiateCsvGeneration() {
        Log.d(TAG, "Iniciando generación de CSV")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Verificar si el permiso ya está concedido
            val hasPermission = context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "Permiso WRITE_EXTERNAL_STORAGE concedido: $hasPermission")

            if (hasPermission) {
                if (detectionData != null) {
                    generateCsvDetection(context, detectionData!!, "Historial_Detecciones.csv")
                } else {
                    Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Solicitar el permiso
                Log.d(TAG, "Solicitando permiso WRITE_EXTERNAL_STORAGE para CSV")
                csvPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // Para Android 10 y superiores, no se necesita permiso para usar MediaStore
            Log.d(TAG, "Android versión >= Q, no se requiere permiso WRITE_EXTERNAL_STORAGE para CSV")
            if (detectionData != null) {
                generateCsvDetection(context, detectionData!!, "Historial_Detecciones.csv")
            } else {
                Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            Log.d(TAG, "Fetching detection history for plot IDs: $plotIds")
            viewModel.getDetectionHistory(
                context = context,
                plotIds = plotIds,
                fechaInicio = startDate,
                fechaFin = endDate
            )
            Log.d(TAG, "Detection history fetched successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching detection history: ${e.message}", e)
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBarWithBackArrow(
            onBackClick = {
                Log.d(TAG, "Botón de retroceso clickeado")
                navController.popBackStack()
                navController.popBackStack()
            },
            title = "Reporte Detecciones"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            when {
                isLoading -> {
                    Log.d(TAG, "Mostrando CircularProgressIndicator")
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Log.e(TAG, "Mostrando mensaje de error: $errorMessage")
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

                detectionData != null -> {
                    val includedLots = detectionData!!.map { it.lote_name }.distinct()

                    // Crear una introducción dinámica
                    val introduction = "Este reporte, elaborado por $username, está dirigido a las partes interesadas. Contiene gráficas y resumen para el entendimiento del usuario sobre las detecciones realizadas entre $startDate y $endDate. Los lotes que se incluyen son: ${includedLots.joinToString(", ")}. A continuación, se presentan las deficiencias nutricionales identificadas, las plagas identificadas, el conteo de estados de maduración de los granos de café en cada lote y el historial de las detecciones efectuadas durante este periodo."

                    ReportContent(
                        detections = detectionData!!,
                        nutrientDeficiencies = nutrientDeficiencies,
                        healthStatuses = healthStatuses,
                        startDate = startDate,
                        endDate = endDate,
                        maturityStatusPerDate = maturityStatusPerDate,
                        includedLots = includedLots,
                        introduction = introduction,
                        generatedBy = username,
                        chartBitmaps = chartBitmaps
                    )
                }
                else -> {
                    Log.e(TAG, "No se pudo generar el reporte.")
                    Text(
                        text = "No se pudo generar el reporte.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                CsvFloatingActionButton(
                    onButtonClick = {
                        initiateCsvGeneration()
                    }
                )

                PdfFloatingActionButton(
                    onButtonClick = {
                        handlePdfGeneration()
                    }
                )
            }
        }
    }

    // Mostrar diálogos de razón para los permisos
    if (showPdfPermissionRationale) {
        Log.d(TAG, "Mostrando diálogo de razón para permiso de almacenamiento para PDF")
        AlertDialog(
            onDismissRequest = { showPdfPermissionRationale = false },
            title = { Text(text = "Permiso de almacenamiento") },
            text = { Text("Esta aplicación necesita acceso al almacenamiento para guardar el PDF del historial de detecciones.") },
            confirmButton = {
                TextButton(onClick = {
                    Log.d(TAG, "Usuario decidió abrir configuración para habilitar permisos para PDF")
                    showPdfPermissionRationale = false
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
                TextButton(onClick = {
                    Log.d(TAG, "Usuario canceló el diálogo de permiso para PDF")
                    showPdfPermissionRationale = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showCsvPermissionRationale) {
        Log.d(TAG, "Mostrando diálogo de razón para permiso de almacenamiento para CSV")
        AlertDialog(
            onDismissRequest = { showCsvPermissionRationale = false },
            title = { Text(text = "Permiso de almacenamiento") },
            text = { Text("Esta aplicación necesita acceso al almacenamiento para guardar el CSV del historial de detecciones.") },
            confirmButton = {
                TextButton(onClick = {
                    Log.d(TAG, "Usuario decidió abrir configuración para habilitar permisos para CSV")
                    showCsvPermissionRationale = false
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
                TextButton(onClick = {
                    Log.d(TAG, "Usuario canceló el diálogo de permiso para CSV")
                    showCsvPermissionRationale = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ReportContent(
    detections: List<DetectionHistory>,
    nutrientDeficiencies: List<NutrientDeficiency>,
    healthStatuses: List<HealthStatus>,
    startDate: String,
    endDate: String,
    maturityStatusPerDate: List<MaturityStatusPerDate>,
    includedLots: List<String>,
    introduction: String,
    generatedBy: String,
    chartBitmaps: MutableList<Pair<String, Bitmap>>
)
{

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)

            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título del reporte
        Text(
            text = "Reporte General de Detecciones",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF49602D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Reporte Generado Por: $generatedBy",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )
        // Fecha de Inicio y Fin
        Text(
            text = "Fecha de Inicio: $startDate",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Fecha de Fin: $endDate",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )

        // Lotes Incluidos
        Text(
            text = "Lotes Incluidos: ${includedLots.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )



        Spacer(modifier = Modifier.height(16.dp))

        // Introducción
        Text(
            text = "1. Introducción",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = introduction,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Deficiencias de Nutrientes
        Text(
            text = "2. Deficiencias de Nutrientes",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        nutrientDeficiencies.forEach { deficiency ->
            NutrientPieChart(
                nutrientDeficiency = deficiency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onBitmapReady = { bitmap ->
                    Log.d(TAG, "Chart Captured: $bitmap")

                    chartBitmaps.add("Deficiencias de Nutrientes - ${deficiency.loteName}" to bitmap)
                    // Verificar si todos los gráficos han sido capturados antes de generar el PDF
                    if (chartBitmaps.size == (nutrientDeficiencies.size + healthStatuses.size + maturityStatusPerDate.size)) {
                        Log.d(TAG, "All charts captured, proceeding with PDF generation")

                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Texto Explicativo del Gráfico
            Text(
                text = "Lote: ${deficiency.loteName}\n" +
                        "Deficiencia de Nitrógeno (N): ${deficiency.nitrogenN}\n" +
                        "Deficiencia de Fósforo (P): ${deficiency.phosphorusP}\n" +
                        "Deficiencia de Potasio (K): ${deficiency.potassiumK}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Resumen de Deficiencias de Nutrientes
        Text(
            text = "Resumen de Deficiencias de Nutrientes por Lote",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Spacer(modifier = Modifier.height(8.dp))
        nutrientDeficiencies.forEach { deficiency ->
            Text(
                text = "Lote ${deficiency.loteName}: N=${deficiency.nitrogenN}, P=${deficiency.phosphorusP}, K=${deficiency.potassiumK}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráficos de Estado de Salud
        Text(
            text = "3. Estado de Salud por Lote",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        healthStatuses.forEach { status ->
            HealthStatusBarChart(
                healthStatus = status,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onBitmapReady = { bitmap ->
                    chartBitmaps.add("Estado de Salud - ${status.loteName}" to bitmap)
                    if (chartBitmaps.size == (nutrientDeficiencies.size + healthStatuses.size + maturityStatusPerDate.size)) {
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Texto Explicativo del Gráfico
            Text(
                text = "Lote: ${status.loteName}\n" +
                        "Cercospora: ${status.cercospora}\n" +
                        "Mancha de Hierro: ${status.ferrugem}\n" +
                        "Roya: ${status.leafRust}\n" +
                        "Hoja Sana: ${status.hojaSana}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Resumen de Estado de Salud por Lote
        Text(
            text = "Resumen de Estado de Salud por Lote",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Spacer(modifier = Modifier.height(8.dp))
        healthStatuses.forEach { status ->
            Text(
                text = "Lote ${status.loteName}: Cercospora=${status.cercospora}, Mancha de Hierro=${status.ferrugem}, Roya=${status.leafRust}, Hoja Sana=${status.hojaSana}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfica de Estado de Maduración
        Text(
            text = "4. Estado de Maduración",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LineChartDetectionStatus(
            maturityStatusPerDate = maturityStatusPerDate,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            onBitmapReady = { bitmap ->
                chartBitmaps.add("Estado de Maduración por Fecha" to bitmap)
                if (chartBitmaps.size == (nutrientDeficiencies.size + healthStatuses.size + maturityStatusPerDate.size)) {

                }
            }
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Texto Explicativo del Gráfico de Maduración
        Text(
            text = "El gráfico anterior muestra el estado de maduración de los lotes en las fechas. A continuación se presenta un resumen:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Resumen de Estado de Maduración por Fecha
        Text(
            text = "5. Resumen de Estado de Maduración por Fecha",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Spacer(modifier = Modifier.height(8.dp))
        maturityStatusPerDate.forEach { maturityStatus ->
            Text(
                text = "Fecha: ${maturityStatus.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D)
            )
            maturityStatus.counts.forEach { (status, count) ->
                Text(
                    text = "$status: $count",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3F3D3D)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de detecciones
        Text(
            text = "6. Lista de Detecciones",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        detections.forEach { detection ->
            DetectionItem(detection = detection)
            Divider(color = Color.LightGray, thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apartado "Reporte Generado Por:"
        Text(
            text = "7. Reporte Generado Por:",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = generatedBy,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DetectionItem(detection: DetectionHistory) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Fecha: ${detection.date}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Colaborador: ${detection.person_name}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Detección: ${detection.detection}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Recomendación: ${detection.recommendation}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Trabajo Cultural: ${detection.cultural_work}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Lote: ${detection.lote_name}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Finca: ${detection.farm_name}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
    }
}

