package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.di

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContextRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSettingsRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContactRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.AlertRepositoryImpl
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.AlertSettingsRepositoryImpl
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.EmergencyContactRepositoryImpl
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.repositories.IncidentRepositoryImpl
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.simulated.SimulatedAlertContextRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface EmergencyAlertingRepositoryModule {

    @Binds
    fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds
    fun bindAlertSettingsRepository(impl: AlertSettingsRepositoryImpl): AlertSettingsRepository

    @Binds
    fun bindIncidentRepository(impl: IncidentRepositoryImpl): IncidentRepository

    @Binds
    fun bindEmergencyContactRepository(impl: EmergencyContactRepositoryImpl): EmergencyContactRepository

    // Simulated until Health Monitoring and Mobility & Geofencing publish their APIs
    @Binds
    fun bindAlertContextRepository(impl: SimulatedAlertContextRepository): AlertContextRepository
}
