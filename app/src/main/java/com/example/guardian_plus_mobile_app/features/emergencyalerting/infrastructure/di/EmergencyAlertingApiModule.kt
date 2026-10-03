package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.di

import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertChannelSettingService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.AlertSettingsService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.EmergencyContactService
import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.IncidentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object EmergencyAlertingApiModule {

    @Provides
    @Singleton
    fun provideAlertService(retrofit: Retrofit): AlertService {
        return retrofit.create(AlertService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlertSettingsService(retrofit: Retrofit): AlertSettingsService {
        return retrofit.create(AlertSettingsService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlertChannelSettingService(retrofit: Retrofit): AlertChannelSettingService {
        return retrofit.create(AlertChannelSettingService::class.java)
    }

    @Provides
    @Singleton
    fun provideIncidentService(retrofit: Retrofit): IncidentService {
        return retrofit.create(IncidentService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmergencyContactService(retrofit: Retrofit): EmergencyContactService {
        return retrofit.create(EmergencyContactService::class.java)
    }
}
