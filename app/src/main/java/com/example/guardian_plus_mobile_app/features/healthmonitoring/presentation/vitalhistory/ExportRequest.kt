package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.LocalDate

/** Days a record is exported for, both ends included. */
data class ExportRange(val start: LocalDate, val end: LocalDate)

/** A report compiled for "Exportar expediente", and the signs the person chose to include in the PDF. */
data class ExportRequest(val report: HealthReport, val metrics: Set<VitalSignType>)
