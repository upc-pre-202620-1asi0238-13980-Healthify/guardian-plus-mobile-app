package com.example.guardian_plus_mobile_app.features.profile.infrastructure.di

import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.CareRecipientProfileService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.CareRelationshipService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.UserProfileService
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.repositories.ProfileRepositoryImpl
import com.example.guardian_plus_mobile_app.features.profile.infrastructure.remote.services.UserPreferencesService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ProfileApiModule {

    @Provides
    @Singleton
    fun provideUserProfileService(retrofit: Retrofit): UserProfileService = retrofit.create(UserProfileService::class.java)

    @Provides
    @Singleton
    fun provideCareRecipientProfileService(retrofit: Retrofit): CareRecipientProfileService =
        retrofit.create(CareRecipientProfileService::class.java)

    @Provides
    @Singleton
    fun provideCareRelationshipService(retrofit: Retrofit): CareRelationshipService =
        retrofit.create(CareRelationshipService::class.java)

    @Provides
    @Singleton
    fun provideUserPreferencesService(
        retrofit: Retrofit
    ): UserPreferencesService =
        retrofit.create(UserPreferencesService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface ProfileRepositoryModule {

    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
