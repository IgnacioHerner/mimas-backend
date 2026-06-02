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
data class UpdatePetRequest(
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val birthDate: String? = null
)

@Serializable
data class CreateVaccineRequest(
    val petCode: String,
    val tipoVacuna: String,
    val nombreComercial: String? = null,
    val fechaAplicacion: String,
    val tipoRecurrencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class UpdateVaccineRequest(
    val tipoVacuna: String? = null,
    val nombreComercial: String? = null,
    val fechaAplicacion: String? = null,
    val tipoRecurrencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class VaccineResponse(
    val id: Int,
    val petId: Int,
    val petCode: String,
    val veterinarianId: Int,
    val veterinarianName: String,
    val tipoVacuna: String,
    val nombreComercial: String? = null,
    val fechaAplicacion: String,
    val tipoRecurrencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class CreateDewormingRequest(
    val petCode: String,
    val producto: String,
    val tipo: String,
    val fechaAplicacion: String,
    val frecuencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class UpdateDewormingRequest(
    val producto: String? = null,
    val tipo: String? = null,
    val fechaAplicacion: String? = null,
    val frecuencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class DewormingResponse(
    val id: Int,
    val petId: Int,
    val petCode: String,
    val veterinarianId: Int,
    val veterinarianName: String,
    val producto: String,
    val tipo: String,
    val fechaAplicacion: String,
    val frecuencia: String? = null,
    val proximaDosis: String? = null,
    val notas: String? = null
)

@Serializable
data class CreateMedicationRequest(
    val petCode: String,
    val nombre: String,
    val dosisCantidad: String,
    val dosisUnidad: String,
    val viaAdministracion: String,
    val fechaInicio: String,
    val horaInicio: String? = null,
    val tipoRecurrencia: String? = null,
    val notas: String? = null
)

@Serializable
data class UpdateMedicationRequest(
    val nombre: String? = null,
    val dosisCantidad: String? = null,
    val dosisUnidad: String? = null,
    val viaAdministracion: String? = null,
    val fechaInicio: String? = null,
    val horaInicio: String? = null,
    val tipoRecurrencia: String? = null,
    val notas: String? = null
)

@Serializable
data class MedicationResponse(
    val id: Int,
    val petId: Int,
    val petCode: String,
    val veterinarianId: Int,
    val veterinarianName: String,
    val nombre: String,
    val dosisCantidad: String,
    val dosisUnidad: String,
    val viaAdministracion: String,
    val fechaInicio: String,
    val horaInicio: String? = null,
    val tipoRecurrencia: String? = null,
    val notas: String? = null
)