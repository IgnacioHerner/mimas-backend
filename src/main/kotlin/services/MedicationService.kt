package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.MedicationResponse
import com.ignaherner.repositories.MedicationRepository
import com.ignaherner.repositories.PetRepository

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
        if (nombre.isBlank()) throw ValidationException("El nombre del medicamento es obligatorio")
        if (dosisCantidad.isBlank()) throw ValidationException("La cantidad de dosis es obligatoria")
        if (dosisUnidad.isBlank()) throw ValidationException("La unidad de dosis es obligatoria")
        if (viaAdministracion.isBlank()) throw ValidationException("La vía de administración es obligatoria")
        if (fechaInicio.isBlank()) throw ValidationException("La fecha de inicio es obligatoria")
        validateDateFormat(fechaInicio, "fecha de inicio")
        if (horaInicio != null && horaInicio.isNotBlank()) {
            validateTimeFormat(horaInicio)
        }
    }

    private fun validateUpdate(nombre: String?, dosisCantidad: String?, dosisUnidad: String?, viaAdministracion: String?, fechaInicio: String?, horaInicio: String?) {
        if (nombre != null && nombre.isBlank()) throw ValidationException("El nombre no puede estar vacío")
        if (dosisCantidad != null && dosisCantidad.isBlank()) throw ValidationException("La cantidad de dosis no puede estar vacía")
        if (dosisUnidad != null && dosisUnidad.isBlank()) throw ValidationException("La unidad de dosis no puede estar vacía")
        if (viaAdministracion != null && viaAdministracion.isBlank()) throw ValidationException("La vía de administración no puede estar vacía")
        if (fechaInicio != null) {
            if (fechaInicio.isBlank()) throw ValidationException("La fecha de inicio no puede estar vacía")
            validateDateFormat(fechaInicio, "fecha de inicio")
        }
        if (horaInicio != null && horaInicio.isNotBlank()) {
            validateTimeFormat(horaInicio)
        }
    }

    private fun validateDateFormat(date: String, fieldName: String) {
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La $fieldName debe tener formato YYYY-MM-DD")
        }
    }

    private fun validateTimeFormat(time: String) {
        if (!time.matches(Regex("\\d{2}:\\d{2}"))) {
            throw ValidationException("La hora de inicio debe tener formato HH:MM")
        }
    }
}