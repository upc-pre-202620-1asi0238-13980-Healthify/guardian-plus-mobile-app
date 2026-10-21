package com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects

data class Email(val value: String) {
    init {
        require(EMAIL_REGEX.matches(value)) { "Invalid email format: $value" }
    }
    fun matches(other: String): Boolean = value.equals(other.trim(), ignoreCase = true)
    companion object {
        private val EMAIL_REGEX = Regex(
            """[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*""" +
                    """@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?"""
        )
    }
}
