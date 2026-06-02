package com.ignaherner.repositories

import com.ignaherner.models.dto.MedicationResponse
import com.ignaherner.models.tables.Medications
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Veterinarians
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MedicationRepository {

    fun create(petId: Int, veterinarianId: Int, nombre: String, dosisCantidad: String, dosisUnidad: String, viaAdministracion: String, fechaInicio: String, horaInicio: String?, tipoRecurrencia: String?, notas: String?): Int {
        return transaction {
            Medications.insert {
                it[Medications.petId] = petId
                it[Medications.veterinarianId] = veterinarianId
                it[Medications.nombre] = nombre
                it[Medications.dosisCantidad] = dosisCantidad
                it[Medications.dosisUnidad] = dosisUnidad
                it[Medications.viaAdministracion] = viaAdministracion
                it[Medications.fechaInicio] = fechaInicio
                it[Medications.horaInicio] = horaInicio
                it[Medications.tipoRecurrencia] = tipoRecurrencia
                it[Medications.notas] = notas
            }[Medications.id]
        }
    }

    fun findById(id: Int): MedicationResponse? {
        return transaction {
            Medications
                .join(Pets, JoinType.INNER, onColumn = Medications.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Medications.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Medications.id eq id) and (Medications.deletedAt.isNull()) }
                .map { it.toMedicationResponse() }
                .singleOrNull()
        }
    }

    fun findByPetId(petId: Int): List<MedicationResponse> {
        return transaction {
            Medications
                .join(Pets, JoinType.INNER, onColumn = Medications.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Medications.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Medications.petId eq petId) and (Medications.deletedAt.isNull()) }
                .orderBy(Medications.fechaInicio to SortOrder.DESC)
                .map { it.toMedicationResponse() }
        }
    }

    fun update(id: Int, nombre: String?, dosisCantidad: String?, dosisUnidad: String?, viaAdministracion: String?, fechaInicio: String?, horaInicio: String?, tipoRecurrencia: String?, notas: String?): Boolean {
        return transaction {
            val updated = Medications.update({ (Medications.id eq id) and (Medications.deletedAt.isNull()) }) {
                if (nombre != null) it[Medications.nombre] = nombre
                if (dosisCantidad != null) it[Medications.dosisCantidad] = dosisCantidad
                if (dosisUnidad != null) it[Medications.dosisUnidad] = dosisUnidad
                if (viaAdministracion != null) it[Medications.viaAdministracion] = viaAdministracion
                if (fechaInicio != null) it[Medications.fechaInicio] = fechaInicio
                if (horaInicio != null) it[Medications.horaInicio] = horaInicio
                if (tipoRecurrencia != null) it[Medications.tipoRecurrencia] = tipoRecurrencia
                if (notas != null) it[Medications.notas] = notas
            }
            updated > 0
        }
    }

    fun softDelete(id: Int): Boolean {
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Medications.update({ (Medications.id eq id) and (Medications.deletedAt.isNull()) }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }

    fun findOwnerVetId(medicationId: Int): Int? {
        return transaction {
            Medications.selectAll()
                .where { Medications.id eq medicationId }
                .singleOrNull()
                ?.get(Medications.veterinarianId)
        }
    }

    private fun ResultRow.toMedicationResponse(): MedicationResponse {
        return MedicationResponse(
            id = this[Medications.id],
            petId = this[Medications.petId],
            petCode = this[Pets.uniqueCode],
            veterinarianId = this[Medications.veterinarianId],
            veterinarianName = "${this[Veterinarians.firstName]} ${this[Veterinarians.lastName]}",
            nombre = this[Medications.nombre],
            dosisCantidad = this[Medications.dosisCantidad],
            dosisUnidad = this[Medications.dosisUnidad],
            viaAdministracion = this[Medications.viaAdministracion],
            fechaInicio = this[Medications.fechaInicio],
            horaInicio = this[Medications.horaInicio],
            tipoRecurrencia = this[Medications.tipoRecurrencia],
            notas = this[Medications.notas],
        )
    }
}