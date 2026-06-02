package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Dewormings : Table("dewormings") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val producto = varchar("producto",100)
    val tipo = varchar("tipo", 30)
    val fechaAplicacion = varchar("fecha_aplicacion", 10)
    val frecuencia = varchar("frecuencia", 30).nullable()
    val proximaDosis = varchar("proxima_dosis",10).nullable()
    val notas = text("notas").nullable()
    val deletedAt = varchar("deleted_at", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}