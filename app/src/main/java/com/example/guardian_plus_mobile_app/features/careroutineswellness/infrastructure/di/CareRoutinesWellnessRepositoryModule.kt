package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.di

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ActivityMonitorRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.HydrationPlanRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStockRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.SleepCycleRecordRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated.SimulatedActivityMonitorRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated.SimulatedHydrationPlanRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated.SimulatedMedicationStockRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated.SimulatedReminderRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.simulated.SimulatedSleepCycleRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Every routine comes from local sample data for now. Connecting the platform means adding the remote
 * implementations under infrastructure/repositories and binding them here; the screens stay the same.
 */
@Module
@InstallIn(SingletonComponent::class)
interface CareRoutinesWellnessRepositoryModule {

    @Binds
    fun bindReminderRepository(impl: SimulatedReminderRepository): ReminderRepository

    @Binds
    fun bindHydrationPlanRepository(impl: SimulatedHydrationPlanRepository): HydrationPlanRepository

    @Binds
    fun bindSleepCycleRecordRepository(impl: SimulatedSleepCycleRecordRepository): SleepCycleRecordRepository

    @Binds
    fun bindActivityMonitorRepository(impl: SimulatedActivityMonitorRepository): ActivityMonitorRepository

    @Binds
    fun bindMedicationStockRepository(impl: SimulatedMedicationStockRepository): MedicationStockRepository
}
