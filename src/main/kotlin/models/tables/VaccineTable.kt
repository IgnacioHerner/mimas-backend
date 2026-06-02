package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Vaccines : Table("vaccines") {
    val id = integer("id").autoIncrement()
    val petId = integer("petId").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val tipoVacuna = varchar("tipo_vacuna", 50)
    val nombreComercial = varchar("nombre_comercial",100).nullable()
    val fechaAplicacion = varchar("fecha_aplicacion",10)
    val tipoRecurrencia = varchar("tipo_recurrencia", 30).nullable()
    val proximaDosis = varchar("proxima_dosis", 10).nullable()
    val notas = text("notas").nullable()
    val deletedAt = varchar("deleted_at",30).nullable()

    override val primaryKey = PrimaryKey(id)
}