package com.ignaherner.repositories

import com.ignaherner.models.dto.DewormingResponse
import com.ignaherner.models.tables.Dewormings
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Veterinarians
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DewormingRepository {

    fun create(petId: Int, veterinarianId: Int, producto: String, tipo: String, fechaAplicacion: String, frecuencia: String?, proximaDosis: String?, notas: String?): Int {
        return transaction {
            Dewormings.insert {
                it[Dewormings.petId] = petId
                it[Dewormings.veterinarianId] = veterinarianId
                it[Dewormings.producto] = producto
                it[Dewormings.tipo] = tipo
                it[Dewormings.fechaAplicacion] = fechaAplicacion
                it[Dewormings.frecuencia] = frecuencia
                it[Dewormings.proximaDosis] = proximaDosis
                it[Dewormings.notas] = notas
            }[Dewormings.id]
        }
    }

    fun findById(id: Int): DewormingResponse? {
        return transaction {
            Dewormings
                .join(Pets, JoinType.INNER, onColumn = Dewormings.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Dewormings.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Dewormings.id eq id) and (Dewormings.deletedAt.isNull()) }
                .map { it.toDewormingResponse() }
                .singleOrNull()
        }
    }

    fun findByPetId(petId: Int): List<DewormingResponse> {
        return transaction {
            Dewormings
                .join(Pets, JoinType.INNER, onColumn = Dewormings.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Dewormings.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Dewormings.petId eq petId) and (Dewormings.deletedAt.isNull()) }
                .orderBy(Dewormings.fechaAplicacion to SortOrder.DESC)
                .map { it.toDewormingResponse() }
        }
    }

    fun update(id: Int, producto: String?, tipo: String?, fechaAplicacion: String?, frecuencia: String?, proximaDosis: String?, notas: String?): Boolean {
        return transaction {
            val updated = Dewormings.update({ (Dewormings.id eq id) and (Dewormings.deletedAt.isNull()) }) {
                if (producto != null) it[Dewormings.producto] = producto
                if (tipo != null) it[Dewormings.tipo] = tipo
                if (fechaAplicacion != null) it[Dewormings.fechaAplicacion] = fechaAplicacion
                if (frecuencia != null) it[Dewormings.frecuencia] = frecuencia
                if (proximaDosis != null) it[Dewormings.proximaDosis] = proximaDosis
                if (notas != null) it[Dewormings.notas] = notas
            }
            updated > 0
        }
    }

    fun softDelete(id: Int): Boolean {
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Dewormings.update({ (Dewormings.id eq id) and (Dewormings.deletedAt.isNull()) }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }

    fun findOwnerVetId(dewormingId: Int): Int? {
        return transaction {
            Dewormings.selectAll()
                .where { Dewormings.id eq dewormingId }
                .singleOrNull()
                ?.get(Dewormings.veterinarianId)
        }
    }


    private fun ResultRow.toDewormingResponse(): DewormingResponse {
        return DewormingResponse(
            id = this[Dewormings.id],
            petId = this[Dewormings.petId],
            petCode = this[Pets.uniqueCode],
            veterinarianId = this[Dewormings.veterinarianId],
            veterinarianName = "${this[Veterinarians.firstName]} ${this[Veterinarians.lastName]}",
            producto = this[Dewormings.producto],
            tipo = this[Dewormings.tipo],
            fechaAplicacion = this[Dewormings.fechaAplicacion],
            frecuencia = this[Dewormings.frecuencia],
            proximaDosis = this[Dewormings.proximaDosis],
            notas = this[Dewormings.notas],
        )
    }
}