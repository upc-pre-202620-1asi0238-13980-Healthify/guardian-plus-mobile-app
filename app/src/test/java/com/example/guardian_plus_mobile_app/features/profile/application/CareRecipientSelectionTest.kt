package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRelationship
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CareRecipientSelectionTest {

    private fun relationship(careRecipientProfileId: String, active: Boolean = true) = CareRelationship(
        id = "rel-$careRecipientProfileId",
        userId = "maria",
        careRecipientProfileId = careRecipientProfileId,
        relationshipType = RelationshipType.FAMILY,
        active = active
    )

    private val relationships = listOf(relationship("elena"), relationship("jorge"), relationship("rosa", active = false))

    @Test
    fun `the person just picked wins`() {
        val chosen = chooseCareRelationship(relationships, preferredCareRecipientProfileId = "jorge", currentCareRecipientProfileId = "elena")

        assertEquals("jorge", chosen?.careRecipientProfileId)
    }

    @Test
    fun `the person already shown is kept when nobody was picked`() {
        val chosen = chooseCareRelationship(relationships, preferredCareRecipientProfileId = null, currentCareRecipientProfileId = "jorge")

        assertEquals("jorge", chosen?.careRecipientProfileId)
    }

    @Test
    fun `an ended relationship falls back to the first active one`() {
        val chosen = chooseCareRelationship(relationships, preferredCareRecipientProfileId = "rosa", currentCareRecipientProfileId = null)

        assertEquals("elena", chosen?.careRecipientProfileId)
    }

    @Test
    fun `no active relationship means nobody to show`() {
        assertNull(chooseCareRelationship(listOf(relationship("rosa", active = false)), null, null))
    }
}
