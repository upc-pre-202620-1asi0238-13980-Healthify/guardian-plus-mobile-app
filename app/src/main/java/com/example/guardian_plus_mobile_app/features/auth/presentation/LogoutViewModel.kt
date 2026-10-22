package com.example.guardian_plus_mobile_app.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.example.guardian_plus_mobile_app.features.auth.application.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Signs out from wherever the shell offers it; the shell then goes back to the login. */
@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val logout: LogoutUseCase
) : ViewModel() {

    fun logout() = logout.invoke()
}
