package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Weights : Table("weights") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val peso = decimal("peso", precision = 5, scale = 2)
    val fecha = varchar("fecha", 10)
    val notas = text("notas").nullable()
    val deletedAt = varchar("deleted_at", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}