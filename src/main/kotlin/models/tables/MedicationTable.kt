package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Medications : Table("medications") {
    val id = integer("id").autoIncrement()
    val petId = integer("pet_id").references(Pets.id)
    val veterinarianId = integer("veterinarian_id").references(Veterinarians.id)
    val nombre = varchar("nombre", 150)
    val dosisCantidad = varchar("dosis_cantidad", 20)
    val dosisUnidad = varchar("dosis_unidad", 30)
    val viaAdministracion = varchar("via_administracion", 30)
    val fechaInicio = varchar("fecha_inicio", 10)
    val horaInicio = varchar("hora_inicio", 5).nullable()
    val tipoRecurrencia = varchar("tipo_recurrencia", 30).nullable()
    val notas = text("notas").nullable()
    val deletedAt = varchar("deleted_at", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}