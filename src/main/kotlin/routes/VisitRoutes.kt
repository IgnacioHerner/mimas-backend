package com.ignaherner.routes

import com.ignaherner.models.dto.CreateVisitRequest
import com.ignaherner.services.VisitService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.ignaherner.exceptions.NotFoundException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.visitRoutes() {
    val service = VisitService()

    authenticate("auth-jwt") {
        route("/api/visits") {

            post {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val request = call.receive<CreateVisitRequest>()

                val visit = service.create(
                    petCode = request.petCode,
                    veterinarianId = vetId,
                    date = request.date,
                    type = request.type,
                    notes = request.notes
                ) ?: throw NotFoundException("Mascota con código '${request.petCode}'")

                call.respond(HttpStatusCode.Created, visit)
            }

            get("/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val visits = service.getByPetCode(petCode)
                call.respond(visits)
            }
        }
    }
}