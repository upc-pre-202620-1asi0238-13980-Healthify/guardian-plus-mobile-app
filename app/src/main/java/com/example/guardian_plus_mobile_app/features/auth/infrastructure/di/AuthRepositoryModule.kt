package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.local.SharedPreferencesSessionRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories.AuthRepositoryImpl
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.repositories.UserAccountRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Accounts, sign-in and the emailed codes come from the platform's IAM (/auth and /user-accounts). */
@Module
@InstallIn(SingletonComponent::class)
interface AuthRepositoryModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindUserAccountRepository(impl: UserAccountRepositoryImpl): UserAccountRepository

    @Binds
    fun bindSessionRepository(impl: SharedPreferencesSessionRepository): SessionRepository
}
