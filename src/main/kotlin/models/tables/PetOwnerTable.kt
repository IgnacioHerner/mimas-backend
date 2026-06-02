package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object PetOwners : Table("pet_owners") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val ownerName = varchar("owner_name", 150)
    val ownerPhone = varchar("owner_phone", 50).nullable()
    val ownerEmail = varchar("owner_email", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}