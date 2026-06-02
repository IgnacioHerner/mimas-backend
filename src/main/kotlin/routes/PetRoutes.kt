package com.ignaherner.routes

import com.ignaherner.models.dto.CreatePetRequest
import com.ignaherner.services.PetService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.models.dto.UpdatePetRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.petRoutes() {
    val service = PetService()

    authenticate("auth-jwt") {
        route("/api/pets") {

            post {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val request = call.receive<CreatePetRequest>()

                val pet = service.create(
                    name = request.name,
                    species = request.species,
                    breed = request.breed,
                    birthDate = request.birthDate,
                    ownerName = request.ownerName,
                    ownerPhone = request.ownerPhone,
                    ownerEmail = request.ownerEmail,
                    vetId = vetId
                )
                call.respond(HttpStatusCode.Created, pet)
            }

            get("/{code}") {
                val code = call.parameters["code"]!!
                val pet = service.findByCode(code)
                    ?: throw NotFoundException("Mascota con código '$code'")

                call.respond(pet)
            }

            patch("/{code}") {
                val code = call.parameters["code"]!!
                val request = call.receive<UpdatePetRequest>()

                val pet = service.update(
                    uniqueCode = code,
                    name = request.name,
                    species = request.species,
                    breed = request.breed,
                    birthDate = request.birthDate
                )

                call.respond(pet)
            }

            delete("/{code}") {
                val code = call.parameters["code"]!!
                service.delete(code)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}