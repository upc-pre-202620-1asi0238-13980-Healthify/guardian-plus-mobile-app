package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import java.time.LocalDate
import javax.inject.Inject

/** What the onboarding collected; a part is null when the account already had it. */
data class OnboardingData(
    val userProfile: UserProfileData?,
    val careRecipient: CareRecipientData?
)

data class UserProfileData(val firstName: String, val lastName: String, val phoneNumber: String)

data class CareRecipientData(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val relationshipType: RelationshipType
)

/**
 * Creates in Profile what the account was missing (its profile, the person under care and the care relationship
 * that links them) and then loads the care context with it.
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val loadCareContext: LoadCareContextUseCase
) {
    suspend operator fun invoke(userAccountId: String, data: OnboardingData): Result<CareContextLoad> = runCatching {
        data.userProfile?.let {
            profileRepository.createUserProfile(userAccountId, it.firstName, it.lastName, it.phoneNumber).getOrThrow()
        }
        data.careRecipient?.let {
            val careRecipient = profileRepository
                .createCareRecipient(userAccountId, it.firstName, it.lastName, it.birthDate).getOrThrow()
            profileRepository.establishCareRelationship(userAccountId, careRecipient.id, it.relationshipType).getOrThrow()
        }
        loadCareContext().getOrThrow()
    }
}
