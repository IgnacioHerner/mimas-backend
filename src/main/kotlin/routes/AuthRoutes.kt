package com.ignaherner.routes

import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.models.dto.VeterinarianResponse
import com.ignaherner.services.JwtService
import com.ignaherner.services.PasswordService
import com.ignaherner.services.VeterinarianService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import com.ignaherner.exceptions.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
)

fun Route.authRoutes(jwtService: JwtService) {
    val veterinarianService = VeterinarianService()

    route("/api/auth") {

        post("/login") {
            val request = call.receive<LoginRequest>()

            val vet = veterinarianService.findByEmail(request.email)
                ?: throw UnauthorizedException("Credenciales inválidas")

            if (!PasswordService.verify(request.password, vet.passwordHash)) {
                throw UnauthorizedException("Credenciales inválidas")
            }

            val token = jwtService.generateToken(vet.id, vet.email)
            call.respond(LoginResponse(token = token))
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val email = principal.payload.getClaim("email").asString()

                val vet = veterinarianService.findByEmail(email)
                    ?: throw NotFoundException("Veterinario")

                call.respond(VeterinarianResponse(
                    id = vet.id,
                    email = vet.email,
                    firstName = vet.firstName,
                    lastName = vet.lastName,
                    licenseNumber = vet.licenseNumber
                ))
            }
        }
    }
}