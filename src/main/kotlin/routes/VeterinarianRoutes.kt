package com.ignaherner.routes

import com.ignaherner.services.VeterinarianService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
)
@Serializable
data class RegisterResponse(
    val id: Int,
    val message: String
)

fun Route.veterinarianRoutes() {
    val service = VeterinarianService()

    route("/api/veterinarians") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val id = service.register(
                email = request.email,
                password = request.password,
                firstName = request.firstName,
                lastName = request.lastName,
                licenseNumber = request.licenseNumber
            )
            call.respond(
                HttpStatusCode.Created,
                RegisterResponse(id = id, message = "Veterinarian registered successfully")
            )
        }

        authenticate("auth-jwt") {
            get {
                val veterinarians = service.getAll()
                call.respond(veterinarians)
            }
        }
    }
}