package com.ignaherner.services

import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.VisitResponse
import com.ignaherner.repositories.PetRepository
import com.ignaherner.repositories.VisitRepository

class VisitService {

    private val visitRepository = VisitRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, date: String, type: String, notes: String?): VisitResponse? {
        validateCreate(petCode, date, type)

        val pet = petRepository.findByCode(petCode) ?: return null

        val visitId = visitRepository.create(pet.id, veterinarianId, date, type, notes)

        val visits = visitRepository.findByPet(pet.id)
        return visits.find { it.id == visitId }
    }

    fun getByPetCode(petCode: String): List<VisitResponse> {
        val pet = petRepository.findByCode(petCode) ?: return emptyList()
        return visitRepository.findByPet(pet.id)
    }

    private fun validateCreate(petCode: String, date: String, type: String) {
        if (petCode.isBlank()) throw ValidationException("El código de la mascota es obligatorio")
        if (date.isBlank()) throw ValidationException("La fecha es obligatoria")
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La fecha debe tener formato YYYY-MM-DD")
        }
        if (type.isBlank()) throw ValidationException("El tipo de visita es obligatorio")
    }
}