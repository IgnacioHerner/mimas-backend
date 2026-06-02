package com.ignaherner.routes

import com.ignaherner.models.dto.CreateMedicationRequest
import com.ignaherner.models.dto.UpdateMedicationRequest
import com.ignaherner.services.MedicationService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.medicationRoutes() {
    val service = MedicationService()

    authenticate("auth-jwt") {
        route("/api/medications") {

            post {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val request = call.receive<CreateMedicationRequest>()

                val medication = service.create(
                    petCode = request.petCode,
                    veterinarianId = vetId,
                    nombre = request.nombre,
                    dosisCantidad = request.dosisCantidad,
                    dosisUnidad = request.dosisUnidad,
                    viaAdministracion = request.viaAdministracion,
                    fechaInicio = request.fechaInicio,
                    horaInicio = request.horaInicio,
                    tipoRecurrencia = request.tipoRecurrencia,
                    notas = request.notas
                )

                call.respond(HttpStatusCode.Created, medication)
            }

            get("/pet/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val medications = service.getByPetCode(petCode)
                call.respond(medications)
            }

            patch("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpdateMedicationRequest>()

                val medication = service.update(
                    id = id,
                    requestingVetId = vetId,
                    nombre = request.nombre,
                    dosisCantidad = request.dosisCantidad,
                    dosisUnidad = request.dosisUnidad,
                    viaAdministracion = request.viaAdministracion,
                    fechaInicio = request.fechaInicio,
                    horaInicio = request.horaInicio,
                    tipoRecurrencia = request.tipoRecurrencia,
                    notas = request.notas
                )

                call.respond(medication)
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val id = call.parameters["id"]!!.toInt()

                service.delete(id, vetId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}