package com.ignaherner.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponde(
    val status: String,
    val service: String,
    val version: String,
)

@Serializable
data class VeterinarianResponse(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
    val phone: String? = null,
    val clinicName: String? = null
)

data class VetWithPassword(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
    val licenseNumber: String
)

@Serializable
data class CreatePetRequest(
    val name: String,
    val species: String,
    val breed: String? = null,
    val birthDate: String? = null,
    val ownerName: String,
    val ownerPhone: String? = null,
    val ownerEmail: String? = null
)

@Serializable
data class PetResponse(
    val id: Int,
    val uniqueCode: String,
    val name: String,
    val species: String,
    val breed: String? = null,
    val birthDate: String? = null,
    val createdByVetId: Int? = null,
    val owners: List<PetOwnerResponse> = emptyList()
)

@Serializable
data class PetOwnerResponse(
    val id: Int,
    val ownerName: String,
    val ownerPhone: String? = null,
    val ownerEmail: String? = null
)

@Serializable
data class CreateVisitRequest(
    val petCode: String,
    val date: String,
    val type: String,
    val notes: String? = null
)

@Serializable
data class VisitResponse(
    val id: Int,
    val petId: Int,
    val petName: String,
    val veterinarianId: Int,
    val veterinarianName: String,
    val date: String,
    val type: String,
    val notes: String? = null
)

@Serializable
data class UpdatePetRequest(
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val birthDate: String? = null
)