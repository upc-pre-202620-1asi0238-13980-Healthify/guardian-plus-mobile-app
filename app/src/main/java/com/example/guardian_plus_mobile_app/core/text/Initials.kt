package com.example.guardian_plus_mobile_app.core.text

/** "María Rojas" → "MR". */
fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
