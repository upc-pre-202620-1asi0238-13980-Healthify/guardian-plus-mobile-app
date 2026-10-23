package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.di

import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.remote.MobilityGeofencingApi
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.infrastructure.repository.MobilityGeofencingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MobilityGeofencingModule {

    @Provides
    @Singleton
    fun provideMobilityGeofencingApi(retrofit: Retrofit): MobilityGeofencingApi {
        return retrofit.create(MobilityGeofencingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMobilityGeofencingRepository(
        api: MobilityGeofencingApi
    ): MobilityGeofencingRepository {
        return MobilityGeofencingRepositoryImpl(api)
    }
}