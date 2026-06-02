package com.ignaherner.repositories

import com.ignaherner.models.dto.WeightResponse
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Veterinarians
import com.ignaherner.models.tables.Weights
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal

class WeightRepository {

    fun create(petId: Int, veterinarianId: Int, peso: String, fecha: String, notas: String?): Int {
        return transaction {
            Weights.insert {
                it[Weights.petId] = petId
                it[Weights.veterinarianId] = veterinarianId
                it[Weights.peso] = BigDecimal(peso)
                it[Weights.fecha] = fecha
                it[Weights.notas] = notas
            }[Weights.id]
        }
    }

    fun findById(id: Int): WeightResponse? {
        return transaction {
            Weights
                .join(Pets, JoinType.INNER, onColumn = Weights.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Weights.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Weights.id eq id) and (Weights.deletedAt.isNull()) }
                .map { it.toWeightResponse() }
                .singleOrNull()
        }
    }

    fun findByPetId(petId: Int): List<WeightResponse> {
        return transaction {
            Weights
                .join(Pets, JoinType.INNER, onColumn = Weights.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Weights.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Weights.petId eq petId) and (Weights.deletedAt.isNull()) }
                .orderBy(Weights.fecha to SortOrder.DESC)
                .map { it.toWeightResponse() }
        }
    }

    fun update(id: Int, peso: String?, fecha: String?, notas: String?): Boolean {
        return transaction {
            val updated = Weights.update({ (Weights.id eq id) and (Weights.deletedAt.isNull()) }) {
                if (peso != null) it[Weights.peso] = BigDecimal(peso)
                if (fecha != null) it[Weights.fecha] = fecha
                if (notas != null) it[Weights.notas] = notas
            }
            updated > 0
        }
    }

    fun softDelete(id: Int): Boolean {
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Weights.update({ (Weights.id eq id) and (Weights.deletedAt.isNull()) }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }

    fun findOwnerVetId(weightId: Int): Int? {
        return transaction {
            Weights.selectAll()
                .where { Weights.id eq weightId }
                .singleOrNull()
                ?.get(Weights.veterinarianId)
        }
    }

    private fun ResultRow.toWeightResponse(): WeightResponse {
        return WeightResponse(
            id = this[Weights.id],
            petId = this[Weights.petId],
            petCode = this[Pets.uniqueCode],
            veterinarianId = this[Weights.veterinarianId],
            veterinarianName = "${this[Veterinarians.firstName]} ${this[Veterinarians.lastName]}",
            peso = this[Weights.peso].toPlainString(),
            fecha = this[Weights.fecha],
            notas = this[Weights.notas]
        )
    }
}