package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.di

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.ActivityMonitorService
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.AdherenceService
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.HydrationPlanService
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.MedicationStockService
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.ReminderService
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.SleepCycleRecordService
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
    fun provideReminderService(retrofit: Retrofit): ReminderService {
        return retrofit.create(ReminderService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdherenceService(retrofit: Retrofit): AdherenceService {
        return retrofit.create(AdherenceService::class.java)
    }

    @Provides
    @Singleton
    fun provideHydrationPlanService(retrofit: Retrofit): HydrationPlanService {
        return retrofit.create(HydrationPlanService::class.java)
    }

    @Provides
    @Singleton
    fun provideActivityMonitorService(retrofit: Retrofit): ActivityMonitorService {
        return retrofit.create(ActivityMonitorService::class.java)
    }

    @Provides
    @Singleton
    fun provideMedicationStockService(retrofit: Retrofit): MedicationStockService {
        return retrofit.create(MedicationStockService::class.java)
    }

    @Provides
    @Singleton
    fun provideSleepCycleRecordService(retrofit: Retrofit): SleepCycleRecordService {
        return retrofit.create(SleepCycleRecordService::class.java)
    }
}
