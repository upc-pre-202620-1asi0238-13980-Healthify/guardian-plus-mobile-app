package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Border
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Destructive
import com.example.guardian_plus_mobile_app.core.designsystem.theme.MutedForeground
import com.example.guardian_plus_mobile_app.core.designsystem.theme.NeutralForeground
import com.example.guardian_plus_mobile_app.core.designsystem.theme.NeutralMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.PastelYellowText
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Primary
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.StabilityIndex
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.displayUnit
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.title
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.generatedAtText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.labelRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.periodText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.summaryRows
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb

// A4 in PostScript points, the unit PdfDocument draws in
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 40f
private const val CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN

// Widths of "Signo vital · Promedio · Mín · Máx · Lecturas · Fuera de rango · Estabilidad", adding up to CONTENT_WIDTH
private val columnWidths = floatArrayOf(130f, 95f, 60f, 60f, 55f, 60f, 55f)

/**
 * Draws a health report on one A4 page, in the app's palette, and saves it in the cache where only the
 * share sheet can reach it. Blocking: call it off the main thread.
 */
fun writeHealthReportPdf(context: Context, report: HealthReport, careRecipientName: String): File {
    val document = PdfDocument()
    val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create())
    page.canvas.drawReport(context, report, careRecipientName)
    document.finishPage(page)

    val directory = File(context.cacheDir, REPORTS_DIRECTORY).apply { mkdirs() }
    val file = File(directory, "reporte-salud-${report.periodStart}-${report.periodEnd}.pdf")
    try {
        file.outputStream().use { document.writeTo(it) }
    } finally {
        document.close()
    }
    return file
}

/** Writes the PDF off the main thread and opens the share sheet with it; false when the file could not be made. */
suspend fun Context.exportHealthReport(report: HealthReport, careRecipientName: String): Boolean {
    val file = try {
        withContext(Dispatchers.IO) { writeHealthReportPdf(this@exportHealthReport, report, careRecipientName) }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        return false
    }
    sharePdf(file)
    return true
}

/** Opens the share sheet with the PDF, so it can be sent, saved to Drive or opened in a viewer. */
fun Context.sharePdf(file: File) {
    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(send, getString(R.string.report_share_title)))
}

// Same folder as res/xml/file_paths.xml
private const val REPORTS_DIRECTORY = "reports"

private fun Canvas.drawReport(context: Context, report: HealthReport, careRecipientName: String) {
    val sans = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    val sansBold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    val mono = Typeface.MONOSPACE
    fun paint(color: ComposeColor, size: Float, typeface: Typeface = sans) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        textSize = size
        this.typeface = typeface
    }

    // Header band
    drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 118f, paint(Primary, 0f))
    drawText(context.getString(R.string.pdf_brand), MARGIN, 38f, paint(ComposeColor.White.copy(alpha = 0.8f), 10f, sansBold))
    drawText(context.getString(R.string.report_title), MARGIN, 68f, paint(ComposeColor.White, 24f, sansBold))
    drawText("$careRecipientName · ${report.periodText()}", MARGIN, 94f, paint(ComposeColor.White.copy(alpha = 0.85f), 12f))

    // Kind, date and overall state
    var y = 156f
    val infoWidth = CONTENT_WIDTH / 3
    val overall = context.getString(if (report.clinicallyStable) R.string.report_stable else R.string.reading_observation)
    listOf(
        context.getString(R.string.pdf_type) to context.getString(report.reportType.labelRes),
        context.getString(R.string.pdf_generated) to report.generatedAtText(),
        context.getString(R.string.pdf_overall) to overall
    ).forEachIndexed { index, (label, value) ->
        val x = MARGIN + index * infoWidth
        drawText(label.uppercase(), x, y, paint(MutedForeground, 8.5f, sansBold))
        val valueColor = if (index == 2 && !report.clinicallyStable) PastelYellowText else NeutralForeground
        drawText(value, x, y + 18f, paint(valueColor, 12f, sansBold))
    }

    // Totals of the period
    y = 200f
    val gap = 12f
    val boxWidth = (CONTENT_WIDTH - 2 * gap) / 3
    listOf(
        report.readingsCount to context.getString(R.string.report_readings),
        report.outOfRangeCount to context.getString(R.string.report_out_of_range),
        report.recurrentAnomaliesCount to context.getString(R.string.report_recurrent_anomalies)
    ).forEachIndexed { index, (value, label) ->
        val x = MARGIN + index * (boxWidth + gap)
        val box = RectF(x, y, x + boxWidth, y + 64f)
        drawRoundRect(box, 10f, 10f, paint(Border, 0f).apply { style = Paint.Style.STROKE; strokeWidth = 1f })
        drawText(value.toString(), x + 14f, y + 32f, paint(NeutralForeground, 20f, mono))
        drawText(label, x + 14f, y + 50f, paint(MutedForeground, 9.5f))
    }

    // One row per vital sign
    y = 296f
    drawText(context.getString(R.string.report_vital_signs), MARGIN, y, paint(NeutralForeground, 14f, sansBold))
    y += 14f
    val headers = listOf(
        R.string.pdf_col_sign, R.string.pdf_col_average, R.string.pdf_col_min, R.string.pdf_col_max,
        R.string.pdf_col_readings, R.string.pdf_col_out_of_range, R.string.pdf_col_stability
    ).map { context.getString(it) }
    drawRoundRect(RectF(MARGIN, y, MARGIN + CONTENT_WIDTH, y + 26f), 6f, 6f, paint(NeutralMuted, 0f))
    drawRow(headers, y + 17f, paint(MutedForeground, 8.5f, sansBold))
    y += 26f

    val rowText = paint(NeutralForeground, 10.5f)
    val rowNumbers = paint(NeutralForeground, 10.5f, mono)
    val divider = paint(Border, 0f).apply { strokeWidth = 0.75f }
    report.summaryRows().forEach { row ->
        val cells = listOf(
            row.type.title,
            "${row.averageText} ${row.type.displayUnit}",
            row.minText,
            row.maxText,
            row.readingsCount.toString(),
            row.outOfRangeCount.toString(),
            context.getString(row.stability.labelRes)
        )
        val baseline = y + 21f
        drawCell(cells[0], 0, baseline, rowText)
        for (column in 1..5) drawCell(cells[column], column, baseline, rowNumbers)
        val stabilityColor = when (row.stability) {
            StabilityIndex.STABLE -> Primary
            StabilityIndex.UNSTABLE -> PastelYellowText
            StabilityIndex.RECURRENT -> Destructive
        }
        drawCell(cells[6], 6, baseline, paint(stabilityColor, 10.5f, sansBold))
        y += 32f
        drawLine(MARGIN, y, MARGIN + CONTENT_WIDTH, y, divider)
    }

    // Legend and disclaimer
    val legend = paint(MutedForeground, 8.5f)
    drawText(context.getString(R.string.pdf_legend), MARGIN, y + 22f, legend)
    drawText(context.getString(R.string.pdf_footer_source), MARGIN, PAGE_HEIGHT - 52f, legend)
    drawText(context.getString(R.string.pdf_footer_disclaimer), MARGIN, PAGE_HEIGHT - 38f, legend)
}

private fun Canvas.drawRow(cells: List<String>, baseline: Float, paint: Paint) {
    cells.forEachIndexed { column, text -> drawCell(text, column, baseline, paint) }
}

// Long texts shrink to their column instead of running into the next one
private fun Canvas.drawCell(text: String, column: Int, baseline: Float, paint: Paint) {
    val x = MARGIN + columnWidths.take(column).sum() + 8f
    val available = columnWidths[column] - 12f
    val fitted = Paint(paint)
    while (fitted.measureText(text) > available && fitted.textSize > 6f) {
        fitted.textSize -= 0.5f
    }
    drawText(text, x, baseline, fitted)
}
