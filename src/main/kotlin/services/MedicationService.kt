package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.MedicationResponse
import com.ignaherner.repositories.MedicationRepository
import com.ignaherner.repositories.PetRepository
import com.ignaherner.validators.CommonValidators

class MedicationService {

    private val medicationRepository = MedicationRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, nombre: String, dosisCantidad: String, dosisUnidad: String, viaAdministracion: String, fechaInicio: String, horaInicio: String?, tipoRecurrencia: String?, notas: String?): MedicationResponse {
        validateCreate(nombre, dosisCantidad, dosisUnidad, viaAdministracion, fechaInicio, horaInicio)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        val medicationId = medicationRepository.create(
            petId = pet.id,
            veterinarianId = veterinarianId,
            nombre = nombre,
            dosisCantidad = dosisCantidad,
            dosisUnidad = dosisUnidad,
            viaAdministracion = viaAdministracion,
            fechaInicio = fechaInicio,
            horaInicio = horaInicio,
            tipoRecurrencia = tipoRecurrencia,
            notas = notas
        )

        return medicationRepository.findById(medicationId)!!
    }

    fun getByPetCode(petCode: String): List<MedicationResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        return medicationRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, nombre: String?, dosisCantidad: String?, dosisUnidad: String?, viaAdministracion: String?, fechaInicio: String?, horaInicio: String?, tipoRecurrencia: String?, notas: String?): MedicationResponse {
        validateUpdate(nombre, dosisCantidad, dosisUnidad, viaAdministracion, fechaInicio, horaInicio)

        val ownerVetId = medicationRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Medicación con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la medicación puede modificarla")
        }

        val updated = medicationRepository.update(id, nombre, dosisCantidad, dosisUnidad, viaAdministracion, fechaInicio, horaInicio, tipoRecurrencia, notas)
        if (!updated) {
            throw NotFoundException("Medicación con id '$id'")
        }

        return medicationRepository.findById(id)!!
    }

    fun delete(id: Int, requestingVetId: Int) {
        val ownerVetId = medicationRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Medicación con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la medicación puede eliminarla")
        }

        val deleted = medicationRepository.softDelete(id)
        if (!deleted) {
            throw NotFoundException("Medicación con id '$id'")
        }
    }

    private fun validateCreate(nombre: String, dosisCantidad: String, dosisUnidad: String, viaAdministracion: String, fechaInicio: String, horaInicio: String?) {
        CommonValidators.validateNotBlank(nombre, "El nombre del medicamento")
        CommonValidators.validateNotBlank(dosisCantidad, "La cantidad de dosis")
        CommonValidators.validateNotBlank(dosisUnidad, "La unidad de dosis")
        CommonValidators.validateNotBlank(viaAdministracion, "La vía de administración")
        CommonValidators.validateNotBlank(fechaInicio, "La fecha de inicio")
        CommonValidators.validateDateFormat(fechaInicio, "fecha de inicio")
        if (!horaInicio.isNullOrBlank()) {
            CommonValidators.validateTimeFormat(horaInicio, "hora de inicio")
        }
    }

    private fun validateUpdate(nombre: String?, dosisCantidad: String?, dosisUnidad: String?, viaAdministracion: String?, fechaInicio: String?, horaInicio: String?) {
        CommonValidators.validateNotBlankIfPresent(nombre, "El nombre")
        CommonValidators.validateNotBlankIfPresent(dosisCantidad, "La cantidad de dosis")
        CommonValidators.validateNotBlankIfPresent(dosisUnidad, "La unidad de dosis")
        CommonValidators.validateNotBlankIfPresent(viaAdministracion, "La vía de administración")
        if (fechaInicio != null) {
            CommonValidators.validateNotBlankIfPresent(fechaInicio, "La fecha de inicio")
            CommonValidators.validateDateFormat(fechaInicio, "fecha de inicio")
        }
        if (horaInicio != null && horaInicio.isNotBlank()) {
            CommonValidators.validateTimeFormat(horaInicio, "hora de inicio")
        }
    }

}