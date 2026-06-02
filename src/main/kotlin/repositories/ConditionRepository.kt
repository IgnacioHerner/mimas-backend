package com.ignaherner.repositories

import com.ignaherner.models.dto.ConditionResponse
import com.ignaherner.models.tables.Conditions
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Veterinarians
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ConditionRepository {

    fun create(petId: Int, veterinarianId: Int, nombre: String, fechaDiagnostico: String, severidad: String, estado: String, notas: String?): Int {
        return transaction {
            Conditions.insert {
                it[Conditions.petId] = petId
                it[Conditions.veterinarianId] = veterinarianId
                it[Conditions.nombre] = nombre
                it[Conditions.fechaDiagnostico] = fechaDiagnostico
                it[Conditions.severidad] = severidad
                it[Conditions.estado] = estado
                it[Conditions.notas] = notas
            }[Conditions.id]
        }
    }

    fun findById(id: Int): ConditionResponse? {
        return transaction {
            Conditions
                .join(Pets, JoinType.INNER, onColumn = Conditions.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Conditions.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Conditions.id eq id) and (Conditions.deletedAt.isNull()) }
                .map { it.toConditionResponse() }
                .singleOrNull()
        }
    }

    fun findByPetId(petId: Int): List<ConditionResponse> {
        return transaction {
            Conditions
                .join(Pets, JoinType.INNER, onColumn = Conditions.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Conditions.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Conditions.petId eq petId) and (Conditions.deletedAt.isNull())}
                .orderBy(Conditions.fechaDiagnostico to SortOrder.DESC)
                .map { it.toConditionResponse() }
        }
    }

    fun update(id: Int, nombre: String?, fechaDiagnostico: String?, severidad: String?, estado: String?, notas: String?): Boolean {
        return transaction {
            val updated = Conditions.update({ (Conditions.id eq id) and (Conditions.deletedAt.isNull()) }) {
                if (nombre != null) it[Conditions.nombre] = nombre
                if (fechaDiagnostico != null) it[Conditions.fechaDiagnostico] = fechaDiagnostico
                if (severidad != null) it[Conditions.severidad] = severidad
                if (estado != null) it[Conditions.estado] = estado
                if (notas != null) it[Conditions.notas] = notas
            }
            updated > 0
        }
    }

    fun softDelete(id: Int): Boolean{
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Conditions.update({ (Conditions.id eq id) and (Conditions.deletedAt.isNull()) }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }

    fun findOwnerVetId(conditionId: Int): Int? {
        return transaction {
            Conditions.selectAll()
                .where { Conditions.id eq conditionId }
                .singleOrNull()
                ?.get(Conditions.veterinarianId)
        }
    }


    private fun ResultRow.toConditionResponse(): ConditionResponse {
        return ConditionResponse(
            id = this[Conditions.id],
            petId = this[Conditions.petId],
            petCode = this[Pets.uniqueCode],
            veterinarianId = this[Conditions.veterinarianId],
            veterinarianName = "${this[Veterinarians.firstName]} ${this[Veterinarians.lastName]}",
            nombre = this[Conditions.nombre],
            fechaDiagnostico = this[Conditions.fechaDiagnostico],
            severidad = this[Conditions.severidad],
            estado = this[Conditions.estado],
            notas = this[Conditions.notas],
        )
    }
}