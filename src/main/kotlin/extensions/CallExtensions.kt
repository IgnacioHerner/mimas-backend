package com.ignaherner.extensions

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.vetId(): Int {
    val principal = principal<JWTPrincipal>()
        ?: error("vetId() llamado en ruta sin autenticación JWT")
    return principal.payload.getClaim("id").asInt()
}

fun ApplicationCall.vetEmail(): String {
    val principal = principal<JWTPrincipal>()
        ?: error("vetEmail() llamado en ruta sin autenticación JWT")
    return principal.payload.getClaim("email").asString()
}