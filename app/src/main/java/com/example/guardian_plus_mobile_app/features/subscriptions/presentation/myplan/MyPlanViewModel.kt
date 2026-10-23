package com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MyPlanViewModel @Inject constructor() : ViewModel() {

    /*
     * Subscriptions todavía no cuenta con endpoints implementados
     * en guardian-plus-platform.
     *
     * Cuando se implemente el bounded context, este ViewModel
     * consumirá el repositorio real y poblará el mismo UiState.
     */
    private val _uiState = MutableStateFlow(
        MyPlanUiState(
            hasSubscription = false,
            isLoading = false
        )
    )

    val uiState: StateFlow<MyPlanUiState> =
        _uiState.asStateFlow()
}