package com.ignaherner.repositories

import com.ignaherner.models.dto.PetOwnerResponse
import com.ignaherner.models.dto.PetResponse
import com.ignaherner.models.tables.PetOwners
import com.ignaherner.models.tables.Pets
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PetRepository {

    fun create(uniqueCode: String, name: String, species: String, breed: String?, birthDate: String?, createdByVetId: Int?): Int {
        return transaction {
            Pets.insert {
                it[Pets.uniqueCode] = uniqueCode
                it[Pets.name] = name
                it[Pets.species] = species
                it[Pets.breed] = breed
                it[Pets.birthDate] = birthDate
                it[Pets.createdByVetId] = createdByVetId
            }[Pets.id]
        }
    }

    fun addOwner(petId: Int, ownerName: String, ownerPhone: String?, ownerEmail: String?): Int {
        return transaction {
            PetOwners.insert {
                it[PetOwners.petId] = petId
                it[PetOwners.ownerName] = ownerName
                it[PetOwners.ownerPhone] = ownerPhone
                it[PetOwners.ownerEmail] = ownerEmail
            }[PetOwners.id]
        }
    }

    fun findByCode(uniqueCode: String): PetResponse? {
        return transaction {
            val pet = Pets.selectAll()
                .where { (Pets.uniqueCode eq uniqueCode) and (Pets.deletedAt.isNull()) }
                .singleOrNull() ?: return@transaction null

            val owners = PetOwners.selectAll()
                .where { PetOwners.petId eq pet[Pets.id] }
                .map { row ->
                    PetOwnerResponse(
                        id = row[PetOwners.id],
                        ownerName = row[PetOwners.ownerName],
                        ownerPhone = row[PetOwners.ownerPhone],
                        ownerEmail = row[PetOwners.ownerEmail]
                    )
                }

            PetResponse(
                id = pet[Pets.id],
                uniqueCode = pet[Pets.uniqueCode],
                name = pet[Pets.name],
                species = pet[Pets.species],
                breed = pet[Pets.breed],
                birthDate = pet[Pets.birthDate],
                createdByVetId = pet[Pets.createdByVetId],
                owners = owners
            )
        }
    }

    fun update(uniqueCode: String, name: String?, species: String?, breed: String?, birthDate: String?): Boolean {
        return transaction {
            val updated = Pets.update({ Pets.uniqueCode eq uniqueCode }) {
                if (name != null) it[Pets.name] = name
                if (species != null) it[Pets.species] = species
                it[Pets.breed] = breed
                it[Pets.birthDate] = birthDate
            }
            updated > 0
        }
    }

    fun softDelete(uniqueCode: String): Boolean {
        return transaction {
            val now = java.time.LocalDateTime.now().toString()
            val updated = Pets.update({ Pets.uniqueCode eq uniqueCode }) {
                it[deletedAt] = now
            }
            updated > 0
        }
    }
}