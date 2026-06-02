package com.ignaherner.plugins

import com.ignaherner.models.dto.HealthResponde
import com.ignaherner.routes.authRoutes
import com.ignaherner.routes.conditionRoutes
import com.ignaherner.routes.dewormingRoutes
import com.ignaherner.routes.medicationRoutes
import com.ignaherner.routes.petRoutes
import com.ignaherner.routes.vaccineRoutes
import com.ignaherner.routes.veterinarianRoutes
import com.ignaherner.routes.weightRoutes
import com.ignaherner.services.JwtService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val jwtService = JwtService(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expirationMs = environment.config.property("jwt.expiration").getString().toLong()
    )
    routing {
        get("/api/health") {
            call.respond(
                HealthResponde(
                    status = "ok",
                    service = "mimas-backend",
                    version = "0.0.1"
                )
            )
        }
        authRoutes(jwtService)
        veterinarianRoutes()
        petRoutes()
        vaccineRoutes()
        dewormingRoutes()
        medicationRoutes()
        conditionRoutes()
        weightRoutes()
    }
}