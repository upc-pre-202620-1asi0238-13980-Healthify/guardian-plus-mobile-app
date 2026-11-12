package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.core.session.CareContext
import javax.inject.Inject

/**
 * Makes another person under care the one the app shows. The care context is loaded again for that person, so
 * the number to call follows them too.
 */
class SwitchCareRecipientUseCase @Inject constructor(
    private val loadCareContext: LoadCareContextUseCase
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<CareContext> =
        loadCareContext(preferredCareRecipientProfileId = careRecipientProfileId).mapCatching { load ->
            when (load) {
                is CareContextLoad.Ready -> load.careContext
                // Only reached if the account lost its profile meanwhile
                is CareContextLoad.OnboardingRequired -> error("No se pudo cambiar de persona")
            }
        }
}
