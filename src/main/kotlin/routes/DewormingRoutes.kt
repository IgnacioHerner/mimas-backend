package com.ignaherner.routes

import com.ignaherner.extensions.vetId
import com.ignaherner.models.dto.CreateDewormingRequest
import com.ignaherner.models.dto.UpdateDewormingRequest
import com.ignaherner.services.DewormingService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.dewormingRoutes() {
    val service = DewormingService()

    authenticate("auth-jwt") {
        route("/api/dewormings") {

            post {
                val request = call.receive<CreateDewormingRequest>()

                val deworming = service.create(
                    petCode = request.petCode,
                    veterinarianId = call.vetId(),
                    producto = request.producto,
                    tipo = request.tipo,
                    fechaAplicacion = request.fechaAplicacion,
                    frecuencia = request.frecuencia,
                    proximaDosis = request.proximaDosis,
                    notas = request.notas
                )

                call.respond(HttpStatusCode.Created, deworming)
            }

            get("/pet/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val dewormings = service.getByPetCode(petCode)
                call.respond(dewormings)
            }

            patch("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpdateDewormingRequest>()

                val deworming = service.update(
                    id = id,
                    requestingVetId = call.vetId(),
                    producto = request.producto,
                    tipo = request.tipo,
                    fechaAplicacion = request.fechaAplicacion,
                    frecuencia = request.frecuencia,
                    proximaDosis = request.proximaDosis,
                    notas = request.notas
                )

                call.respond(deworming)
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                service.delete(id, call.vetId())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}