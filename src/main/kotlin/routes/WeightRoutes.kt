package com.ignaherner.routes

import com.ignaherner.extensions.vetId
import com.ignaherner.models.dto.CreateWeightRequest
import com.ignaherner.models.dto.UpdateWeightRequest
import com.ignaherner.services.WeightService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.weightRoutes() {
    val service = WeightService()

    authenticate("auth-jwt") {
        route("/api/weights") {
            post {
                val request = call.receive<CreateWeightRequest>()

                val weight = service.create(
                    petCode = request.petCode,
                    veterinarianId = call.vetId(),
                    peso = request.peso,
                    fecha = request.fecha,
                    notas = request.notas
                )

                call.respond(HttpStatusCode.Created, weight)
            }

            get("/pet/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val weights = service.getByPetCode(petCode)
                call.respond(weights)
            }

            patch("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpdateWeightRequest>()

                val weight = service.update(
                    id = id,
                    requestingVetId = call.vetId(),
                    peso = request.peso,
                    fecha = request.fecha,
                    notas = request.notas
                )

                call.respond(weight)
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                service.delete(id, call.vetId())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}