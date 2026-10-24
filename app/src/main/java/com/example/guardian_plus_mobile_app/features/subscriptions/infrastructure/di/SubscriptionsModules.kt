package com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.di

import com.example.guardian_plus_mobile_app.features.subscriptions.domain.repositories.SubscriptionRepository
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.remote.services.SubscriptionService
import com.example.guardian_plus_mobile_app.features.subscriptions.infrastructure.repositories.SubscriptionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionsApiModule {

    @Provides
    @Singleton
    fun provideSubscriptionService(
        retrofit: Retrofit
    ): SubscriptionService =
        retrofit.create(SubscriptionService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface SubscriptionsRepositoryModule {

    @Binds
    fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository
}