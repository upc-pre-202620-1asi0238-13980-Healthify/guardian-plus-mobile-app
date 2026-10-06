package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.di

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.WearableDeviceRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories.VitalSignRepositoryImpl
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories.WearableDeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HealthMonitoringRepositoryModule {


    @Binds
    fun bindVitalSignRepository(impl: VitalSignRepositoryImpl): VitalSignRepository

    @Binds
    fun bindWearableDeviceRepository(impl: WearableDeviceRepositoryImpl) : WearableDeviceRepository
}
