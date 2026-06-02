package com.ignaherner.repositories

import com.ignaherner.models.dto.VaccineResponse
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Vaccines
import com.ignaherner.models.tables.Vaccines.petId
import com.ignaherner.models.tables.Veterinarians
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class VaccineRepository {

    fun create(petId: Int, veterinarianId: Int, tipoVacuna: String, nombreComercial: String?, fechaAplicacion: String, tipoRecurrencia: String?, proximaDosis: String?, notas: String?): Int {
        return transaction {
            Vaccines.insert {
                it[Vaccines.petId] = petId
                it[Vaccines.veterinarianId] = veterinarianId
                it[Vaccines.tipoVacuna] = tipoVacuna
                it[Vaccines.nombreComercial] = nombreComercial
                it[Vaccines.fechaAplicacion] = fechaAplicacion
                it[Vaccines.tipoRecurrencia] = tipoRecurrencia
                it[Vaccines.proximaDosis] = proximaDosis
                it[Vaccines.notas] = notas
            }[Vaccines.id]
        }
    }

    fun findById(id: Int): VaccineResponse? {
        return transaction {
            Vaccines
                .join(Pets, JoinType.INNER, onColumn = Vaccines.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Vaccines.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Vaccines.id eq id) and (Vaccines.deletedAt.isNull()) }
                .map { it.toVaccineResponse() }
                .singleOrNull()
        }
    }

    fun findByPetId(petId: Int): List<VaccineResponse> {
        return transaction {
            Vaccines
                .join(Pets, JoinType.INNER, onColumn = Vaccines.petId, otherColumn = Pets.id)
                .join(Veterinarians, JoinType.INNER, onColumn = Vaccines.veterinarianId, otherColumn = Veterinarians.id)
                .selectAll()
                .where { (Vaccines.petId eq petId) and (Vaccines.deletedAt.isNull()) }
                .orderBy(Vaccines.fechaAplicacion to SortOrder.DESC)
                .map { it.toVaccineResponse() }
        }
    }

    fun update(id: Int, tipoVacuna: String?, nombreComercial: String?, fechaAplicacion: String?, tipoRecurrencia: String?, proximaDosis: String?, notas: String?): Boolean {
        return transaction {
            val updated = Vaccines.update({ (Vaccines.id eq id) and (Vaccines.deletedAt.isNull()) }) {
                if (tipoVacuna != null) it[Vaccines.tipoVacuna] = tipoVacuna
                if (fechaAplicacion != null) it[Vaccines.fechaAplicacion] = fechaAplicacion
                if (nombreComercial != null) it[Vaccines.nombreComercial] = nombreComercial
                if (tipoRecurrencia != null) it[Vaccines.tipoRecurrencia] = tipoRecurrencia
                if (proximaDosis != null) it[Vaccines.proximaDosis] = proximaDosis
                if (notas != null) it[Vaccines.notas] = notas
            }
            updated > 0
        }
    }

    fun softDelete(id: Int): Boolean {
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Vaccines.update({ (Vaccines.id eq id) and (Vaccines.deletedAt.isNull()) }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }

    fun findOwnerVetId(vaccineId: Int): Int? {
        return transaction {
            Vaccines.selectAll()
                .where { Vaccines.id eq vaccineId }
                .singleOrNull()
                ?.get(Vaccines.veterinarianId)
        }
    }

    private fun ResultRow.toVaccineResponse(): VaccineResponse {
        return VaccineResponse(
            id = this[Vaccines.id],
            petId = this[Vaccines.petId],
            petCode = this[Pets.uniqueCode],
            veterinarianId = this[Vaccines.veterinarianId],
            veterinarianName = "${this[Veterinarians.firstName]} ${this[Veterinarians.lastName]}",
            tipoVacuna = this[Vaccines.tipoVacuna],
            nombreComercial = this[Vaccines.nombreComercial],
            fechaAplicacion = this[Vaccines.fechaAplicacion],
            tipoRecurrencia = this[Vaccines.tipoRecurrencia],
            proximaDosis = this[Vaccines.proximaDosis],
            notas = this[Vaccines.notas],
        )
    }
}