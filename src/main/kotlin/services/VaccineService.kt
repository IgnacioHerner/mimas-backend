package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.VaccineResponse
import com.ignaherner.repositories.PetRepository
import com.ignaherner.repositories.VaccineRepository
import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date

class VaccineService {

    private val vaccineRepository = VaccineRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, tipoVacuna: String, nombreComercial: String?, fechaAplicacion: String, tipoRecurrencia: String?, proximaDosis: String?, notas: String?): VaccineResponse {
        validateCreate(tipoVacuna, fechaAplicacion, proximaDosis)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con codigo '$petCode'")

        val vaccineId = vaccineRepository.create(
            petId = pet.id,
            veterinarianId = veterinarianId,
            tipoVacuna = tipoVacuna,
            nombreComercial = nombreComercial,
            fechaAplicacion = fechaAplicacion,
            tipoRecurrencia = tipoRecurrencia,
            proximaDosis = proximaDosis,
            notas = notas
        )

        return vaccineRepository.findById(vaccineId)!!
    }

    fun getByPetCode(petCode: String): List<VaccineResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con codigo '$petCode'")
        return vaccineRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, tipoVacuna: String?, nombreComercial: String?, fechaAplicacion: String?, tipoRecurrencia: String?, proximaDosis: String?, notas: String?): VaccineResponse {
        validateUpdate(tipoVacuna, fechaAplicacion, proximaDosis)

        val ownerVetId = vaccineRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Vacuna con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la vacuna puede modificarla")
        }

        val updated = vaccineRepository.update(id, tipoVacuna, nombreComercial, fechaAplicacion, tipoRecurrencia, proximaDosis, notas)
        if (!updated) {
            throw NotFoundException("Vacuna con id '$id'")
        }

        return vaccineRepository.findById(id)!!
    }

    fun delete(id: Int, requestingVetId: Int) {
        val ownerVetId = vaccineRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Vacuna con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la vacuna puede eliminarla")
        }

        val deleted = vaccineRepository.softDelete(id)
        if (!deleted) {
            throw NotFoundException("Vacuna con id '$id'")
        }
    }

    private fun validateUpdate(tipoVacuna: String?, fechaAplicacion: String?, proximaDosis: String?) {
        if (tipoVacuna != null && tipoVacuna.isBlank()) {
            throw ValidationException("El tipo de vacuna no puede estar vacío")
        }
        if (fechaAplicacion != null) {
            if (fechaAplicacion.isBlank()) throw ValidationException("La fecha de aplicación no puede estar vacía")
            validateDateFormat(fechaAplicacion, "fecha de aplicación")
        }
        if (proximaDosis != null && proximaDosis.isNotBlank()) {
            validateDateFormat(proximaDosis, "próxima dosis")
        }
    }

    private fun validateCreate(tipoVacuna: String, fechaAplicacion: String, proximaDosis: String?) {
        if (tipoVacuna.isBlank()) throw ValidationException("El tipo de vacuna es obligatorio")
        if (fechaAplicacion.isBlank()) throw ValidationException("La fecha de aplicación es obligatoria")
        validateDateFormat(fechaAplicacion, "fecha de aplicación")
        if (proximaDosis != null && proximaDosis.isNotBlank()) {
            validateDateFormat(proximaDosis, "próxima dosis")
        }
    }

    private fun validateDateFormat(date: String, fieldName: String) {
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La $fieldName debe tener formato YYYY-MM-DD")
        }
    }

}