package com.example.coffetech.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.common.TopBarWithBackArrow
import com.example.coffetech.model.FinancialReportData
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.view.components.MPBarChartComparison
import com.example.coffetech.view.components.MPPieChart
import com.example.coffetech.view.components.PdfFloatingActionButton
import com.example.coffetech.view.components.generatePdf
import com.example.coffetech.viewmodel.reports.FinanceReportViewModel
import com.example.coffetech.viewmodel.reports.LoteRecommendation
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.coffetech.model.FarmSummary
import com.example.coffetech.model.TransactionHistory
import android.util.Log // Importación añadida para Logcat
import com.example.coffetech.view.components.CsvFloatingActionButton
import com.example.coffetech.view.components.generateCsv

@Composable
fun FinanceReportView(
    modifier: Modifier = Modifier,
    navController: NavController,
    plotIds: List<Int>,
    startDate: String,
    endDate: String,
    includeTransactionHistory: Boolean,
    viewModel: FinanceReportViewModel = viewModel()
) {
    val TAG = "FinanceReportView"

    Log.d(TAG, "FinanceReportView Composable iniciado con includeTransactionHistory=$includeTransactionHistory")

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val reportData by viewModel.reportData.collectAsState()

    var chartBitmaps by remember { mutableStateOf<List<Pair<String, Bitmap>>>(emptyList()) }
    val username by viewModel.username.collectAsState()

    fun handleChartsCaptured(bitmaps: List<Pair<String, Bitmap>>) {
        Log.d(TAG, "Capturando gráficas: ${bitmaps.size} imágenes")
        chartBitmaps = bitmaps
    }

    // Generar las recomendaciones después de cargar los datos
    val recomendaciones = remember(reportData) {
        Log.d(TAG, "Generando recomendaciones")
        reportData?.let { viewModel.generarRecomendaciones() } ?: emptyList()
    }

    // Estados para manejar permisos
    var showPdfPermissionRationale by remember { mutableStateOf(false) }
    var showCsvPermissionRationale by remember { mutableStateOf(false) }

    // Launchers para solicitar permisos
    val pdfPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Permiso de almacenamiento concedido para PDF")
            if (reportData != null && recomendaciones.isNotEmpty()) {
                generatePdf(context, reportData!!, recomendaciones, chartBitmaps)
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
            if (reportData != null) {
                generateCsv(context, reportData!!)
            } else {
                Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e(TAG, "Permiso de almacenamiento denegado para CSV")
            showCsvPermissionRationale = true
        }
    }

    // Función para iniciar la generación del PDF
    fun initiatePdfGeneration() {
        Log.d(TAG, "Iniciando generación de PDF")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Verificar si el permiso ya está concedido
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "Permiso WRITE_EXTERNAL_STORAGE concedido: $hasPermission")

            if (hasPermission) {
                if (reportData != null && recomendaciones.isNotEmpty()) {
                    generatePdf(context, reportData!!, recomendaciones, chartBitmaps)
                } else {
                    Toast.makeText(context, "No hay datos para generar el PDF", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Solicitar el permiso
                Log.d(TAG, "Solicitando permiso WRITE_EXTERNAL_STORAGE para PDF")
                pdfPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // Para Android 10 y superiores, no se necesita permiso para usar MediaStore
            Log.d(TAG, "Android versión >= Q, no se requiere permiso WRITE_EXTERNAL_STORAGE para PDF")
            if (reportData != null && recomendaciones.isNotEmpty()) {
                generatePdf(context, reportData!!, recomendaciones, chartBitmaps)
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
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "Permiso WRITE_EXTERNAL_STORAGE concedido: $hasPermission")

            if (hasPermission) {
                if (reportData != null) {
                    generateCsv(context, reportData!!)
                } else {
                    Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Solicitar el permiso
                Log.d(TAG, "Solicitando permiso WRITE_EXTERNAL_STORAGE para CSV")
                csvPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // Para Android 10 y superiores, no se necesita permiso para usar MediaStore
            Log.d(TAG, "Android versión >= Q, no se requiere permiso WRITE_EXTERNAL_STORAGE para CSV")
            if (reportData != null) {
                generateCsv(context, reportData!!)
            } else {
                Toast.makeText(context, "No hay datos para generar el CSV", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Llamando a getFinancialReport")
        viewModel.getFinancialReport(
            context = context,
            plotIds = plotIds,
            fechaInicio = startDate,
            fechaFin = endDate,
            includeTransactionHistory = includeTransactionHistory // Pasar el parámetro a la llamada de carga
        )
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
            title = "Reporte financiero"
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
                reportData != null -> {
                    Log.d(TAG, "Mostrando ReportContent")
                    ReportContent(
                        reportData = reportData!!,
                        recomendaciones = recomendaciones,
                        onChartsCaptured = { bitmaps ->
                            handleChartsCaptured(bitmaps)
                        },
                        username = username // Pasando el username aquí

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
                verticalArrangement = Arrangement.spacedBy(40.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (includeTransactionHistory) {
                    CsvFloatingActionButton(
                        onButtonClick = {
                            initiateCsvGeneration()
                        }
                    )
                }

                PdfFloatingActionButton(
                    onButtonClick = {
                        initiatePdfGeneration()
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
            text = { Text("Esta aplicación necesita acceso al almacenamiento para guardar el PDF del reporte.") },
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
            text = { Text("Esta aplicación necesita acceso al almacenamiento para guardar el CSV del reporte.") },
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
    reportData: FinancialReportData,
    recomendaciones: List<LoteRecommendation>,
    username: String, // Nuevo parámetro

    onChartsCaptured: (List<Pair<String, Bitmap>>) -> Unit // Callback para recibir las gráficas con su sección
) {
    val TAG = "ReportContent"
    Log.d(TAG, "Renderizando ReportContent")

    // Lista para almacenar pares de sección y bitmap
    val chartBitmaps = remember { mutableStateListOf<Pair<String, Bitmap>>() }

    // Función para agregar un par sección-bitmap
    fun addBitmap(section: String, bitmap: Bitmap) {
        Log.d(TAG, "Agregando bitmap para sección: $section")
        chartBitmaps.add(Pair(section, bitmap))
        // Verificar si todas las gráficas han sido capturadas antes de llamar al callback
        // Esto depende de cuántas gráficas esperas
        val expectedChartCount = 1 + (reportData.plot_financials.size * 2) + 2 // Ejemplo basado en tu ReportContent
        Log.d(TAG, "Bitmap agregado. Total actual: ${chartBitmaps.size}, Esperado: $expectedChartCount")
        if (chartBitmaps.size == expectedChartCount) {
            Log.d(TAG, "Se han capturado todas las gráficas. Llamando a onChartsCaptured")
            onChartsCaptured(chartBitmaps.toList())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título del reporte
        Text(
            text = "Reporte Financiero de la Finca: ${reportData.finca_nombre}",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF49602D),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Reporte Generado Por: $username",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )
        // Periodo
        Text(
            text = "Periodo: ${reportData.periodo}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )

        // Lotes incluidos
        Text(
            text = "Lotes Incluidos: ${reportData.lotes_incluidos.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Introducción
        Text(
            text = "Introducción:",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Este reporte, elaborado por $username, está dirigido a las partes interesadas. El reporte financiero de la finca: ${reportData.finca_nombre} incluye los lotes: ${reportData.lotes_incluidos.joinToString(", ")}, en el periodo ${reportData.periodo}. A continuación, se presenta un análisis de los ingresos y gastos por lote y para la finca en su conjunto.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Comparación de Ingresos y Gastos por Lote
        Text(
            text = "1. Comparación de Ingresos y Gastos por Lote",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gráfico de Barras usando MPAndroidChart
        MPBarChartComparison(
            plotFinancials = reportData.plot_financials,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            onBitmapReady = { bitmap ->
                addBitmap("Comparación de Ingresos y Gastos por Lote", bitmap)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Distribución de Categorías de Ingresos y Gastos por Lote
        Text(
            text = "2. Distribución de Categorías de Ingresos y Gastos por Lote",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        reportData.plot_financials.forEach { plotFinancial ->
            Text(
                text = "Lote: ${plotFinancial.plot_name}",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF3F3D3D),
                modifier = Modifier.fillMaxWidth()
            )

            // Ingresos por Categoría
            MPPieChart(
                title = "Ingresos por Categoría",
                categories = plotFinancial.ingresos_por_categoria,
                modifier = Modifier.fillMaxWidth(),
                onBitmapReady = { bitmap ->
                    addBitmap("Ingresos por Categoría - ${plotFinancial.plot_name}", bitmap)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gastos por Categoría
            MPPieChart(
                title = "Gastos por Categoría",
                categories = plotFinancial.gastos_por_categoria,
                modifier = Modifier.fillMaxWidth(),
                onBitmapReady = { bitmap ->
                    addBitmap("Gastos por Categoría - ${plotFinancial.plot_name}", bitmap)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Resumen Financiero de la Finca XYZ
        Text(
            text = "3. Resumen Financiero de la Finca: ${reportData.finca_nombre}",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar los totales
        Text(
            text = "Total Ingresos: \$${reportData.farm_summary.total_ingresos}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Total Gastos: \$${reportData.farm_summary.total_gastos}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Balance Financiero: \$${reportData.farm_summary.balance_financiero}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Distribución de Ingresos y Gastos de la Finca
        Text(
            text = "Distribución de Ingresos y Gastos de la Finca",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Distribución de Ingresos de la Finca
        MPPieChart(
            title = "Distribución de Ingresos de la Finca",
            categories = reportData.farm_summary.ingresos_por_categoria,
            modifier = Modifier.fillMaxWidth(),
            onBitmapReady = { bitmap ->
                addBitmap("Distribución de Ingresos de la Finca", bitmap)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Distribución de Gastos de la Finca
        MPPieChart(
            title = "Distribución de Gastos de la Finca",
            categories = reportData.farm_summary.gastos_por_categoria,
            modifier = Modifier.fillMaxWidth(),
            onBitmapReady = { bitmap ->
                addBitmap("Distribución de Gastos de la Finca", bitmap)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Análisis y Recomendaciones
        Text(
            text = "4. Análisis y Recomendaciones",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar las recomendaciones
        recomendaciones.forEach { recomendacion ->
            Text(
                text = "Lote: ${recomendacion.loteNombre} - ${recomendacion.rendimiento}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F3D3D),
                modifier = Modifier.fillMaxWidth()
            )
            recomendacion.recomendaciones.forEach { texto ->
                Text(
                    text = "- $texto",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF3F3D3D),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Aquí puedes agregar los textos de análisis y recomendaciones

        // 5. Conclusiones
        Text(
            text = "5. Conclusiones",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF49602D),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Texto de conclusiones
        Text(
            text = "Este reporte financiero proporciona una visión de la situación económica de la finca ${reportData.finca_nombre} y sus lotes seleccionados en el periodo ${reportData.periodo}. Con base en los análisis realizados, se recomienda seguir las acciones propuestas para mejorar el rendimiento financiero y asegurar la sostenibilidad y crecimiento de la finca.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val transactionHistory = reportData.transaction_history ?: emptyList()

        // 6. Historial de Transacciones
        if (transactionHistory.isNotEmpty()) {
            Text(
                text = "6. Historial de Transacciones",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF49602D),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                transactionHistory.forEach { transaction ->
                    TransactionItem(transaction = transaction)
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Log.d(TAG, "No hay historial de transacciones para mostrar.")
        }
        Spacer(modifier = Modifier.height(50.dp))


    }

}
@Composable
fun TransactionItem(transaction: TransactionHistory) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Fecha: ${transaction.date}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Lote: ${transaction.plot_name}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Tipo: ${transaction.transaction_type}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Categoría: ${transaction.transaction_category}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Creador: ${transaction.creator_name}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
        Text(
            text = "Valor: \$${transaction.value}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF3F3D3D)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FinanceReportPreview() {
    val TAG = "FinanceReportPreview"
    Log.d(TAG, "Mostrando Preview de FinanceReport")
    val navController = NavController(LocalContext.current)
    val sampleData = FinancialReportData(
        finca_nombre = "Finca Modelo",
        lotes_incluidos = listOf("Lote 1", "Lote 2"),
        periodo = "2024-01-01 al 2024-03-31",
        plot_financials = listOf(
            // Agrega datos de plot_financials de ejemplo aquí
        ),
        farm_summary = FarmSummary(
            total_ingresos = 100000,
            total_gastos = 50000,
            balance_financiero = 50000,
            ingresos_por_categoria = listOf(
                // Agrega categorías de ingresos de ejemplo aquí
            ),
            gastos_por_categoria = listOf(
                // Agrega categorías de gastos de ejemplo aquí
            )
        ),
        transaction_history = listOf(
            TransactionHistory(
                date = "2024-01-15",
                plot_name = "Lote 1",
                farm_name = "Finca Modelo",
                transaction_type = "Ingreso",
                transaction_category = "Venta de Café",
                creator_name = "Juan Pérez",
                value = 20000
            ),
            TransactionHistory(
                date = "2024-02-10",
                plot_name = "Lote 2",
                farm_name = "Finca Modelo",
                transaction_type = "Gasto",
                transaction_category = "Fertilizantes",
                creator_name = "Ana Gómez",
                value = 5000
            )
            // Agrega más transacciones de ejemplo según sea necesario
        )
    )

    CoffeTechTheme {
        ReportContent(
            reportData = sampleData,
            recomendaciones = listOf(
                // Agrega recomendaciones de ejemplo aquí
            ),
            username ="",
            onChartsCaptured = {}
        )
    }
}
