package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.di

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.VitalSignService
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.WearableDeviceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HealthMonitoringApiModule {
    
    @Provides
    @Singleton
    fun provideVitalSignService(retrofit: Retrofit): VitalSignService {
        return retrofit.create(VitalSignService::class.java)
    }

    @Provides
    @Singleton
    fun provideWearableDeviceService(retrofit: Retrofit): WearableDeviceService {
        return retrofit.create(WearableDeviceService::class.java)
    }
}
