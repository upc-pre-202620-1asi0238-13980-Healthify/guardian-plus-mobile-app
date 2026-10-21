package com.example.guardian_plus_mobile_app.features.auth.infrastructure.di

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.AuthRepository
import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.UserAccountRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedAuthRepository
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.simulated.SimulatedUserAccountRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * IAM is not deployed on the platform yet, so accounts come from SimulatedIamStore. Once /auth and
 * /user-accounts exist, binding AuthRepositoryImpl and UserAccountRepositoryImpl here is the only change.
 */
@Module
@InstallIn(SingletonComponent::class)
interface AuthRepositoryModule {

    @Binds
    fun bindAuthRepository(impl: SimulatedAuthRepository): AuthRepository

    @Binds
    fun bindUserAccountRepository(impl: SimulatedUserAccountRepository): UserAccountRepository
}
