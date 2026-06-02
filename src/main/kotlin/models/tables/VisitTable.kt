package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Visits : Table("visits") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val date = varchar("date", 10)
    val type = varchar("type", 50)
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}