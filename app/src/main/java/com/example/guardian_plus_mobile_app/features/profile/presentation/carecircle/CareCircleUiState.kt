package com.example.guardian_plus_mobile_app.features.profile.presentation.carecircle

enum class CareCircleFilter {
    ALL,
    FAMILY,
    CAREGIVER
}

data class CareCircleMemberUi(
    val userId: String,
    val fullName: String,
    val relationshipLabel: String,
    val isActive: Boolean
) {
    val initials: String
        get() {
            val parts = fullName
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }

            return parts
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
        }
}

data class CareCircleUiState(
    val careRecipientName: String = "",
    val members: List<CareCircleMemberUi> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: CareCircleFilter = CareCircleFilter.ALL,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredMembers: List<CareCircleMemberUi>
        get() = members.filter { member ->
            val matchesSearch =
                searchQuery.isBlank() ||
                        member.fullName.contains(
                            searchQuery,
                            ignoreCase = true
                        )

            val matchesFilter = when (selectedFilter) {
                CareCircleFilter.ALL -> true
                CareCircleFilter.FAMILY ->
                    member.relationshipLabel == "Familiar"

                CareCircleFilter.CAREGIVER ->
                    member.relationshipLabel == "Cuidador"
            }

            matchesSearch && matchesFilter
        }
}