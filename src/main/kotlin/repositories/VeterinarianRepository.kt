package com.ignaherner.repositories

import com.ignaherner.models.dto.VetWithPassword
import com.ignaherner.models.dto.VeterinarianResponse
import com.ignaherner.models.tables.Veterinarians
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class VeterinarianRepository {

    fun create(email: String, passwordHash: String, firstName: String, lastName: String, licenseNumber: String): Int {
        return transaction {
            Veterinarians.insert {
                it[Veterinarians.email] = email
                it[Veterinarians.passwordHash] = passwordHash
                it[Veterinarians.firstName] = firstName
                it[Veterinarians.lastName] = lastName
                it[Veterinarians.licenseNumber] = licenseNumber
            }[Veterinarians.id]
        }
    }

    fun findAll(): List<VeterinarianResponse> {
        return transaction {
            Veterinarians.selectAll().map { row ->
                VeterinarianResponse(
                    id = row[Veterinarians.id],
                    email = row[Veterinarians.email],
                    firstName = row[Veterinarians.firstName],
                    lastName = row[Veterinarians.lastName],
                    licenseNumber = row[Veterinarians.licenseNumber],
                    phone = row[Veterinarians.phone],
                    clinicName = row[Veterinarians.clinicName]
                )
            }
        }
    }

    fun findByEmail(email: String): VetWithPassword? {
        return transaction {
            Veterinarians.selectAll()
                .where(Veterinarians.email eq email)
                .map { row ->
                    VetWithPassword(
                        id = row[Veterinarians.id],
                        email = row[Veterinarians.email],
                        passwordHash = row[Veterinarians.passwordHash],
                        firstName = row[Veterinarians.firstName],
                        lastName = row[Veterinarians.lastName],
                        licenseNumber = row[Veterinarians.licenseNumber]
                    )
                }
                .singleOrNull()
        }
    }
}