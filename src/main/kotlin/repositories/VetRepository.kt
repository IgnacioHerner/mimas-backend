package com.ignaherner.repositories

import com.ignaherner.models.dto.VisitResponse
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Veterinarians
import com.ignaherner.models.tables.Visits
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class VisitRepository {

    fun create(petId: Int, veterinarianId: Int, date: String, type: String, notes: String?): Int {
        return transaction {
            Visits.insert {
                it[Visits.petId] = petId
                it[Visits.veterinarianId] = veterinarianId
                it[Visits.date] = date
                it[Visits.type] = type
                it[Visits.notes] = notes
            }[Visits.id]
        }
    }

    fun findByPet(petId: Int): List<VisitResponse> {
        return transaction {
            (Visits innerJoin Veterinarians)
                .selectAll()
                .where { Visits.petId eq petId }
                .map { row ->
                    val petName = Pets.selectAll()
                        .where { Pets.id eq petId }
                        .single()[Pets.name]

                    VisitResponse(
                        id = row[Visits.id],
                        petId = petId,
                        petName = petName,
                        veterinarianId = row[Veterinarians.id],
                        veterinarianName = "${row[Veterinarians.firstName]} ${row[Veterinarians.lastName]}",
                        date = row[Visits.date],
                        type = row[Visits.type],
                        notes = row[Visits.notes]
                    )
                }
        }
    }
}