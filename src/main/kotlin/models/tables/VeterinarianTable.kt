package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Veterinarians : Table("veterinarians") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("passwordHash", 255)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val licenseNumber = varchar("license_number", 50)
    val phone = varchar("phone", 50).nullable()
    val clinicName = varchar("clinic_name", 150).nullable()

    override val primaryKey = PrimaryKey(id)
}