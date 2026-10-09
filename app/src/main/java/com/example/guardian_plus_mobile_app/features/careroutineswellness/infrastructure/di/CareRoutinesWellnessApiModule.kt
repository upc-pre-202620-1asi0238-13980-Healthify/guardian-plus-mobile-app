package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.di

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.AdherenceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object CareRoutinesWellnessApiModule {

    @Provides
    @Singleton
    fun provideAdherenceService(retrofit: Retrofit): AdherenceService {
        return retrofit.create(AdherenceService::class.java)
    }
}
