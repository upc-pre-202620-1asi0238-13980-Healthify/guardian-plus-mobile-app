package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** Same rule as the platform's PhoneNumber value object: E.164, e.g. +51987654321. */
object PhoneNumber {
    private val E164 = Regex("^\\+[1-9]\\d{7,14}$")

    fun isValid(value: String): Boolean = E164.matches(value)
}
