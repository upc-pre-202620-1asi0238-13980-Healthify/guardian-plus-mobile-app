package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services.AuthService
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote.services.UserAccountService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserAccountService(retrofit: Retrofit): UserAccountService {
        return retrofit.create(UserAccountService::class.java)
    }
}
