package com.ignaherner.database

import com.ignaherner.models.tables.Conditions
import com.ignaherner.models.tables.Dewormings
import com.ignaherner.models.tables.Medications
import com.ignaherner.models.tables.PetOwners
import com.ignaherner.models.tables.Pets
import com.ignaherner.models.tables.Vaccines
import com.ignaherner.models.tables.Veterinarians
import com.ignaherner.models.tables.Weights
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(url: String, driver: String, user: String, password: String) {
        Database.connect(createHikariDataSource(url, driver, user, password))

        transaction {
            SchemaUtils.create(Veterinarians, Pets, PetOwners, Vaccines, Dewormings, Medications, Conditions, Weights)
        }
    }

    private fun createHikariDataSource(url: String, driver: String, user: String, password: String): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            this.password = password
            maximumPoolSize = 10
            isAutoCommit = false
        }
        return HikariDataSource(config)
    }
}