package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.di

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.AdherenceRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlanRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecordRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.ActivityMonitorRepositoryImpl
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.AdherenceRepositoryImpl
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.HydrationPlanRepositoryImpl
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.MedicationStockRepositoryImpl
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.ReminderRepositoryImpl
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories.SleepCycleRecordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Every routine is read from and written to the platform. */
@Module
@InstallIn(SingletonComponent::class)
interface CareRoutinesWellnessRepositoryModule {

    @Binds
    fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    fun bindHydrationPlanRepository(impl: HydrationPlanRepositoryImpl): HydrationPlanRepository

    @Binds
    fun bindSleepCycleRecordRepository(impl: SleepCycleRecordRepositoryImpl): SleepCycleRecordRepository

    @Binds
    fun bindActivityMonitorRepository(impl: ActivityMonitorRepositoryImpl): ActivityMonitorRepository

    @Binds
    fun bindMedicationStockRepository(impl: MedicationStockRepositoryImpl): MedicationStockRepository

    @Binds
    fun bindAdherenceRepository(impl: AdherenceRepositoryImpl): AdherenceRepository
}
