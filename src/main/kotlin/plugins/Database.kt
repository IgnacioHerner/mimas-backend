package com.ignaherner.plugins

import com.ignaherner.database.DatabaseFactory
import io.ktor.server.application.Application

fun Application.configureDatabase() {

    val url = environment.config.property("database.url").getString()
    val driver = environment.config.property("database.driver").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

    DatabaseFactory.init(url, driver, user, password)
}