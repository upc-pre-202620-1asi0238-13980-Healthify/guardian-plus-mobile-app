package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

/** The platform found no vital sign readings in the period a report was asked for. */
class NoReadingsInPeriodException(message: String) : Exception(message)
