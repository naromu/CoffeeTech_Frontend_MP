// MultiSelectDropdown.kt
package com.example.coffetech.view.components

import android.content.ContentValues
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coffetech.model.Plot
import com.example.coffetech.model.PlotFinancial


import android.graphics.Color as AndroidColor
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.example.coffetech.model.CategoryAmount
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import com.github.mikephil.charting.formatter.ValueFormatter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.core.content.FileProvider
import com.example.coffetech.model.DetectionHistory
import com.example.coffetech.model.FinancialReportData
import com.example.coffetech.viewmodel.reports.LoteRecommendation
import com.github.mikephil.charting.charts.LineChart
import java.io.File
import java.io.FileOutputStream

@Composable
fun PlotsList(
    plots: List<Plot>,
    selectedPlotIds: List<Int>,
    onPlotToggle: (Int) -> Unit,
    onSelectAllToggle: () -> Unit,
    allSelected: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp) // Ajusta la altura según tus necesidades
    ) {
        // Opción "Todos"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSelectAllToggle() }, // Maneja la selección de todos al hacer clic en la fila completa
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = { onSelectAllToggle() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Divider(color = Color.Gray, thickness = 0.5.dp)
        }

        // Lista de lotes
        items(plots) { plot ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onPlotToggle(plot.plot_id) }, // Permite seleccionar al hacer clic en la fila completa
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedPlotIds.contains(plot.plot_id),
                    onCheckedChange = { onPlotToggle(plot.plot_id) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plot.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f) // Ocupa el espacio restante
                )
            }
            Divider(color = Color.Gray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun PdfFloatingActionButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onButtonClick,
        modifier = modifier,
        containerColor = Color(0xFFB31D34),
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = "PDF",
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}


// MPBarChartComparison.kt
@Composable
fun MPBarChartComparison(
    plotFinancials: List<PlotFinancial>,
    modifier: Modifier = Modifier,
    onBitmapReady: (Bitmap) -> Unit // Callback para recibir el Bitmap
) {
    var chartBitmap by remember { mutableStateOf<Bitmap?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setFitBars(true)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                legend.isEnabled = true
            }
        },
        update = { barChart ->
            val ingresosEntries = plotFinancials.mapIndexed { index, plot ->
                BarEntry(index.toFloat(), plot.ingresos.toFloat())
            }

            val gastosEntries = plotFinancials.mapIndexed { index, plot ->
                BarEntry(index.toFloat(), plot.gastos.toFloat())
            }

            val dataSetIngresos = BarDataSet(ingresosEntries, "Ingresos").apply {
                color = AndroidColor.parseColor("#4CAF50") // Verde
            }

            val dataSetGastos = BarDataSet(gastosEntries, "Gastos").apply {
                color = AndroidColor.parseColor("#F44336") // Rojo
            }

            val data = BarData(dataSetIngresos, dataSetGastos).apply {
                barWidth = 0.45f
            }

            barChart.data = data

            // Configurar el espacio entre grupos
            barChart.groupBars(0f, 0.1f, 0.05f)

            // Configurar etiquetas del eje X
            val labels = plotFinancials.map { it.plot_name }
            val xAxis = barChart.xAxis
            xAxis.granularity = 1f
            xAxis.setLabelCount(labels.size)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            barChart.invalidate() // Refresh

            // Capturar el Bitmap después de que la gráfica se haya renderizado
            barChart.post {
                chartBitmap = barChart.chartBitmap
                chartBitmap?.let { onBitmapReady(it) }
            }
        }
    )
}

class IndexAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < labels.size) {
            labels[index]
        } else {
            ""
        }
    }
}


// MPPieChart.kt
@Composable
fun MPPieChart(
    title: String,
    categories: List<CategoryAmount>,
    modifier: Modifier = Modifier,
    onBitmapReady: (Bitmap) -> Unit // Callback para recibir el Bitmap
) {
    var chartBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF49602D),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    setUsePercentValues(true)
                    setEntryLabelTextSize(12f)
                    setEntryLabelColor(AndroidColor.BLACK)
                    centerText = title
                    legend.isEnabled = true
                }
            },
            update = { pieChart ->
                val entries = categories.map { category ->
                    PieEntry(category.monto.toFloat(), category.category_name)
                }

                val dataSet = PieDataSet(entries, "").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    sliceSpace = 2f
                    selectionShift = 5f
                }

                val data = PieData(dataSet).apply {
                    setDrawValues(true)
                    setValueFormatter(PercentFormatter(pieChart))
                    setValueTextSize(12f)
                    setValueTextColor(AndroidColor.BLACK)
                }

                pieChart.data = data
                pieChart.invalidate()

                // Capturar el Bitmap después de que la gráfica se haya renderizado
                pieChart.post {
                    chartBitmap = pieChart.chartBitmap
                    chartBitmap?.let { onBitmapReady(it) }
                }
            }
        )
    }
}


fun generatePdf(
    context: Context,
    reportData: FinancialReportData,
    recomendaciones: List<LoteRecommendation>,
    chartBitmaps: List<Pair<String, Bitmap>> // Recibir las gráficas con su sección
) {
    val document = PdfDocument()

    // Definir las dimensiones de una página A4 en puntos (595 x 842)
    val pageWidth = 595
    val pageHeight = 842

    var y = 25f // Posición Y inicial
    val lineHeight = 20f // Altura de línea ajustada para más líneas por página
    val margin = 25f // Margen lateral

    var pageNumber = 1
    var currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page: PdfDocument.Page = document.startPage(currentPageInfo)
    var canvas: Canvas = page.canvas
    val paint = Paint().apply {
        textSize = 12f
        color = android.graphics.Color.BLACK
        isAntiAlias = true
    }

    // Función auxiliar para dibujar texto y manejar saltos de página
    fun drawText(text: String, x: Float, isCentered: Boolean = false) {
        val textWidth = paint.measureText(text)
        val adjustedX = if (isCentered) (pageWidth - textWidth) / 2 else x

        if (y + paint.textSize > pageHeight - margin) {
            // Finalizar la página actual
            document.finishPage(page)
            // Crear una nueva página
            pageNumber += 1
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            y = margin
        }
        canvas.drawText(text, adjustedX, y, paint)
        y += lineHeight
    }

    // Función auxiliar para dibujar texto multilínea
    fun drawMultilineText(text: String, x: Float, maxWidth: Float) {
        val words = text.split(" ")
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val textWidth = paint.measureText(testLine)
            if (textWidth > maxWidth) {
                if (currentLine.isNotEmpty()) {
                    drawText(currentLine, x)
                }
                currentLine = word
            } else {
                currentLine = testLine
            }
        }

        // Dibujar la última línea
        if (currentLine.isNotEmpty()) {
            drawText(currentLine, x)
        }
    }

    // Inicio de la escritura del contenido

    // 1. Título del reporte
    paint.textSize = 16f
    paint.isFakeBoldText = true
    drawText("Reporte Financiero de la Finca: ${reportData.finca_nombre}", pageWidth / 2f, isCentered = true)
    y += lineHeight / 2 // Ajuste para separación
    paint.isFakeBoldText = false

    // 2. Periodo y lotes incluidos
    paint.textSize = 12f
    drawText("Periodo: ${reportData.periodo}", margin)
    drawText("Lotes Incluidos: ${reportData.lotes_incluidos.joinToString(", ")}", margin)
    y += lineHeight

    // 3. Introducción
    paint.textSize = 14f
    drawText("Introducción:", margin)
    paint.textSize = 12f
    drawMultilineText(
        "Este es el reporte financiero de la finca: ${reportData.finca_nombre} que incluye los lotes: ${reportData.lotes_incluidos.joinToString(", ")}, en el periodo ${reportData.periodo}. A continuación, se presenta un análisis de los ingresos y gastos por lote y para la finca en su conjunto.",
        margin,
        pageWidth - 2 * margin
    )
    y += lineHeight

    // 4. Comparación de Ingresos y Gastos por Lote
    paint.textSize = 14f
    drawText("1. Comparación de Ingresos y Gastos por Lote", margin)
    paint.textSize = 12f
    reportData.plot_financials.forEach { plot ->
        drawText("Lote: ${plot.plot_name}", margin + 10f)
        drawText("Ingresos: \$${plot.ingresos}", margin + 20f)
        drawText("Gastos: \$${plot.gastos}", margin + 20f)
        y += lineHeight
    }
    y += lineHeight

    // Insertar la gráfica de Barras para "Comparación de Ingresos y Gastos por Lote"
    val comparacionBitmap = chartBitmaps.find { it.first == "Comparación de Ingresos y Gastos por Lote" }?.second
    comparacionBitmap?.let { bitmap ->
        // Escalar la gráfica
        val maxWidth = pageWidth - 2 * margin
        val scale = maxWidth / bitmap.width.toFloat()
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale

        // Verificar si cabe en la página, sino crear una nueva página
        if (y + scaledHeight > pageHeight - margin) {
            // Finalizar la página actual
            document.finishPage(page)
            // Crear una nueva página
            pageNumber += 1
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            y = margin
        }

        // Dibujar el Bitmap escalado centrado
        val left = (pageWidth - scaledWidth) / 2
        canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, y, left + scaledWidth, y + scaledHeight), paint)
        y += scaledHeight + lineHeight
    }

    // 5. Distribución de Categorías de Ingresos y Gastos por Lote
    paint.textSize = 14f
    drawText("2. Distribución de Categorías de Ingresos y Gastos por Lote", margin)
    paint.textSize = 12f
    reportData.plot_financials.forEach { plot ->
        drawText("Lote: ${plot.plot_name}", margin + 10f)
        drawText("Ingresos por Categoría:", margin + 10f)
        plot.ingresos_por_categoria.forEach { categoria ->
            drawText("- ${categoria.category_name}: \$${categoria.monto}", margin + 20f)
        }
        drawText("Gastos por Categoría:", margin + 10f)
        plot.gastos_por_categoria.forEach { categoria ->
            drawText("- ${categoria.category_name}: \$${categoria.monto}", margin + 20f)
        }
        y += lineHeight

        // Insertar las gráficas de Ingresos y Gastos por Categoría para cada lote
        val ingresosBitmap = chartBitmaps.find { it.first == "Ingresos por Categoría - ${plot.plot_name}" }?.second
        ingresosBitmap?.let { bitmap ->
            val maxWidth = pageWidth - 2 * margin
            val scale = maxWidth / bitmap.width.toFloat()
            val scaledWidth = bitmap.width * scale
            val scaledHeight = bitmap.height * scale

            if (y + scaledHeight > pageHeight - margin) {
                document.finishPage(page)
                pageNumber += 1
                currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(currentPageInfo)
                canvas = page.canvas
                y = margin
            }

            val left = (pageWidth - scaledWidth) / 2
            canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, y, left + scaledWidth, y + scaledHeight), paint)
            y += scaledHeight + lineHeight
        }

        val gastosBitmap = chartBitmaps.find { it.first == "Gastos por Categoría - ${plot.plot_name}" }?.second
        gastosBitmap?.let { bitmap ->
            val maxWidth = pageWidth - 2 * margin
            val scale = maxWidth / bitmap.width.toFloat()
            val scaledWidth = bitmap.width * scale
            val scaledHeight = bitmap.height * scale

            if (y + scaledHeight > pageHeight - margin) {
                document.finishPage(page)
                pageNumber += 1
                currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(currentPageInfo)
                canvas = page.canvas
                y = margin
            }

            val left = (pageWidth - scaledWidth) / 2
            canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, y, left + scaledWidth, y + scaledHeight), paint)
            y += scaledHeight + lineHeight
        }

    }
    y += lineHeight

    // 6. Resumen Financiero de la Finca
    paint.textSize = 14f
    drawText("3. Resumen Financiero de la Finca: ${reportData.finca_nombre}", margin)
    paint.textSize = 12f
    drawText("Total Ingresos: \$${reportData.farm_summary.total_ingresos}", margin + 10f)
    drawText("Total Gastos: \$${reportData.farm_summary.total_gastos}", margin + 10f)
    drawText("Balance Financiero: \$${reportData.farm_summary.balance_financiero}", margin + 10f)
    y += lineHeight

    // 7. Distribución de Ingresos y Gastos de la Finca
    paint.textSize = 14f
    drawText("Distribución de Ingresos y Gastos de la Finca", margin)
    paint.textSize = 12f
    drawText("Distribución de Ingresos de la Finca:", margin + 10f)
    reportData.farm_summary.ingresos_por_categoria.forEach { categoria ->
        drawText("- ${categoria.category_name}: \$${categoria.monto}", margin + 20f)
    }
    drawText("Distribución de Gastos de la Finca:", margin + 10f)
    reportData.farm_summary.gastos_por_categoria.forEach { categoria ->
        drawText("- ${categoria.category_name}: \$${categoria.monto}", margin + 20f)
    }
    y += lineHeight

    // Insertar las gráficas de Distribución de Ingresos y Gastos de la Finca
    val distribucionIngresosBitmap = chartBitmaps.find { it.first == "Distribución de Ingresos de la Finca" }?.second
    distribucionIngresosBitmap?.let { bitmap ->
        val maxWidth = pageWidth - 2 * margin
        val scale = maxWidth / bitmap.width.toFloat()
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale

        if (y + scaledHeight > pageHeight - margin) {
            document.finishPage(page)
            pageNumber += 1
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            y = margin
        }

        val left = (pageWidth - scaledWidth) / 2
        canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, y, left + scaledWidth, y + scaledHeight), paint)
        y += scaledHeight + lineHeight
    }

    val distribucionGastosBitmap = chartBitmaps.find { it.first == "Distribución de Gastos de la Finca" }?.second
    distribucionGastosBitmap?.let { bitmap ->
        val maxWidth = pageWidth - 2 * margin
        val scale = maxWidth / bitmap.width.toFloat()
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale

        if (y + scaledHeight > pageHeight - margin) {
            document.finishPage(page)
            pageNumber += 1
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            y = margin
        }

        val left = (pageWidth - scaledWidth) / 2
        canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, y, left + scaledWidth, y + scaledHeight), paint)
        y += scaledHeight + lineHeight
    }

    // 8. Análisis y Recomendaciones
    paint.textSize = 14f
    drawText("4. Análisis y Recomendaciones", margin)
    paint.textSize = 12f
    recomendaciones.forEach { recomendacion ->
        drawText("Lote: ${recomendacion.loteNombre} - ${recomendacion.rendimiento}", margin + 10f)
        recomendacion.recomendaciones.forEach { texto ->
            drawText("- $texto", margin + 20f)
        }
        y += lineHeight
    }
    y += lineHeight

    // 9. Conclusiones
    paint.textSize = 14f
    drawText("5. Conclusiones", margin)
    paint.textSize = 12f
    drawMultilineText(
        "Este reporte financiero proporciona una visión de la situación económica de la finca ${reportData.finca_nombre} y sus lotes seleccionados en el periodo ${reportData.periodo}. Con base en los análisis realizados, se recomienda seguir las acciones propuestas para mejorar el rendimiento financiero y asegurar la sostenibilidad y crecimiento de la finca.",
        margin,
        pageWidth - 2 * margin
    )
    y += lineHeight

    // Finalizar la última página
    document.finishPage(page)

    // Guardar el PDF en la carpeta de Descargas utilizando MediaStore
    val pdfUri: Uri? = savePdfToDownloads(context, document, "Reporte_Financiero_${reportData.finca_nombre}_${reportData.periodo}.pdf")

    // Cerrar el documento
    document.close()

    if (pdfUri != null) {
        Toast.makeText(context, "PDF generado correctamente.", Toast.LENGTH_LONG).show()
        abrirPdf(context, pdfUri)
    } else {
        Toast.makeText(context, "Error al guardar el PDF.", Toast.LENGTH_LONG).show()
    }
}


fun savePdfToDownloads(context: Context, document: PdfDocument, fileName: String): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Para Android 10 y superiores, usar MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                document.writeTo(outputStream)
            }
            it
        }
    } else {
        // Para versiones anteriores de Android
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val pdfFile = File(downloadsDir, fileName)
        try {
            document.writeTo(FileOutputStream(pdfFile))
            Uri.fromFile(pdfFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// Función para abrir el PDF generado
fun abrirPdf(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    // Crear un chooser para que el usuario seleccione la aplicación
    val chooser = Intent.createChooser(intent, "Abrir PDF con")

    // Verificar si hay alguna aplicación que pueda manejar este intent
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        // No hay aplicaciones disponibles para abrir PDFs
        Toast.makeText(context, "No hay una aplicación para abrir el PDF.", Toast.LENGTH_LONG).show()

        // Opcional: Abrir la ubicación del archivo en el explorador de archivos
        abrirCarpetaDescargas(context)
    }
}

fun abrirCarpetaDescargas(context: Context) {
    val downloadIntent = Intent(Intent.ACTION_VIEW).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setDataAndType(MediaStore.Downloads.EXTERNAL_CONTENT_URI, "resource/folder")
        } else {
            val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            val uri = Uri.parse(downloadsPath)
            setDataAndType(uri, "resource/folder")
        }
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    if (downloadIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(downloadIntent)
    } else {
        // Si no se puede abrir la carpeta, notificar al usuario
        Toast.makeText(context, "Guardado en Downloads o Descargas.", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun CsvFloatingActionButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onButtonClick,
        modifier = modifier,
        containerColor = Color(0xFF4CAF50),
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = ".CSV",
            color = Color.White,
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun generateCsv(
    context: Context,
    reportData: FinancialReportData
) {
    try {
        // Crear el contenido del CSV
        val csvBuilder = StringBuilder()

        // Encabezados
        csvBuilder.append("Finca,Periodo,Total Ingresos,Total Gastos,Balance Financiero\n")
        csvBuilder.append("${reportData.finca_nombre},${reportData.periodo},${reportData.farm_summary.total_ingresos},${reportData.farm_summary.total_gastos},${reportData.farm_summary.balance_financiero}\n\n")

        // Ingresos por Categoría
        csvBuilder.append("Ingresos por Categoría\n")
        csvBuilder.append("Categoría,Monto\n")
        reportData.farm_summary.ingresos_por_categoria.forEach { categoria ->
            csvBuilder.append("${categoria.category_name},${categoria.monto}\n")
        }
        csvBuilder.append("\n")

        // Gastos por Categoría
        csvBuilder.append("Gastos por Categoría\n")
        csvBuilder.append("Categoría,Monto\n")
        reportData.farm_summary.gastos_por_categoria.forEach { categoria ->
            csvBuilder.append("${categoria.category_name},${categoria.monto}\n")
        }
        csvBuilder.append("\n")

        // Detalles por Lote
        csvBuilder.append("Detalles por Lote\n")
        csvBuilder.append("Lote,Ingresos,Gastos,Balance\n")
        reportData.plot_financials.forEach { plot ->
            csvBuilder.append("${plot.plot_name},${plot.ingresos},${plot.gastos},${plot.balance}\n")
        }
        csvBuilder.append("\n")

        // Historial de Transacciones (si está incluido)
        reportData.transaction_history?.let { transactions ->
            if (transactions.isNotEmpty()) {
                csvBuilder.append("Historial de Transacciones\n")
                csvBuilder.append("Fecha,Lote,Tipo,Categoría,Creador,Valor\n")
                transactions.forEach { transaction ->
                    csvBuilder.append("${transaction.date},${transaction.plot_name},${transaction.transaction_type},${transaction.transaction_category},${transaction.creator_name},${transaction.value}\n")
                }
                csvBuilder.append("\n")
            }
        }

        // Convertir el contenido a bytes
        val csvBytes = csvBuilder.toString().toByteArray()

        // Nombre del archivo
        val fileName = "Reporte_Financiero_${reportData.finca_nombre}_${reportData.periodo}.csv"

        // Guardar el CSV en la carpeta de Descargas utilizando MediaStore
        val csvUri: Uri? = saveCsvToDownloads(context, csvBytes, fileName)

        if (csvUri != null) {
            Toast.makeText(context, "CSV generado correctamente.", Toast.LENGTH_LONG).show()
            abrirCsv(context, csvUri)
        } else {
            Toast.makeText(context, "Error al guardar el CSV.", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("generateCsv", "Error al generar el CSV: ${e.message}")
        Toast.makeText(context, "Error al generar el CSV.", Toast.LENGTH_LONG).show()
    }
}

fun saveCsvToDownloads(context: Context, csvBytes: ByteArray, fileName: String): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Para Android 10 y superiores, usar MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(csvBytes)
            }
            it
        }
    } else {
        // Para versiones anteriores de Android
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val csvFile = File(downloadsDir, fileName)
        try {
            FileOutputStream(csvFile).use { outputStream ->
                outputStream.write(csvBytes)
            }
            Uri.fromFile(csvFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun abrirCsv(context: Context, csvUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(csvUri, "text/csv")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    // Crear un chooser para que el usuario seleccione la aplicación
    val chooser = Intent.createChooser(intent, "Abrir CSV con")

    // Verificar si hay alguna aplicación que pueda manejar este intent
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        // No hay aplicaciones disponibles para abrir CSVs
        Toast.makeText(context, "No hay una aplicación para abrir el CSV.", Toast.LENGTH_LONG).show()

        // Opcional: Abrir la ubicación del archivo en el explorador de archivos
        abrirCarpetaDescargas(context)
    }
}
