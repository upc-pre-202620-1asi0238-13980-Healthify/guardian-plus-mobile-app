package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

/** Every person the account cares for, one per active care relationship, in the order they were added. */
class GetCareRecipientsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userAccountId: String): Result<List<CareRecipient>> = runCatching {
        profileRepository.getCareRelationships(userAccountId).getOrThrow()
            .filter { it.active }
            .map { profileRepository.getCareRecipient(it.careRecipientProfileId).getOrThrow() }
    }
}
