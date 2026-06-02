package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Conditions : Table("conditions") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val nombre = varchar("nombre", 150)
    val fechaDiagnostico = varchar("fecha_diagnostico", 10)
    val severidad = varchar("severidad", 30)
    val estado = varchar("estado", 30)
    val notas = text("notas").nullable()
    val deletedAt = varchar("deleted_at", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}