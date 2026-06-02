package com.ignaherner.models.tables

import org.jetbrains.exposed.sql.Table

object Pets : Table("pets") {
    val id = integer("id").autoIncrement()
    val uniqueCode = varchar("unique_code", 20).uniqueIndex()
    val name = varchar("name", 100)
    val species = varchar("species", 50)
    val breed = varchar("breed", 100).nullable()
    val birthDate = varchar("birth_date", 10).nullable()
    val createdByVetId = integer("created_by_vet_id").references(Veterinarians.id).nullable()
    val deletedAt = varchar("deleted_at", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}