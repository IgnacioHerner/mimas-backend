package com.ignaherner.routes

import com.ignaherner.models.dto.CreateVaccineRequest
import com.ignaherner.models.dto.UpdateVaccineRequest
import com.ignaherner.services.VaccineService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.vaccineRoutes() {
    val service = VaccineService()

    authenticate("auth-jwt") {
        route("/api/vaccines") {
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val request = call.receive<CreateVaccineRequest>()

                val vaccine = service.create(
                    petCode = request.petCode,
                    veterinarianId = vetId,
                    tipoVacuna = request.tipoVacuna,
                    nombreComercial = request.nombreComercial,
                    fechaAplicacion = request.fechaAplicacion,
                    tipoRecurrencia = request.tipoRecurrencia,
                    proximaDosis = request.proximaDosis,
                    notas = request.notas
                )

                call.respond(HttpStatusCode.Created, vaccine)
            }

            get("/pet/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val vaccines = service.getByPetCode(petCode)
                call.respond(vaccines)
            }

            patch ("/{id}"){
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpdateVaccineRequest>()

                val vaccine = service.update(
                    id = id,
                    requestingVetId = vetId,
                    tipoVacuna = request.tipoVacuna,
                    nombreComercial = request.nombreComercial,
                    fechaAplicacion = request.fechaAplicacion,
                    tipoRecurrencia = request.tipoRecurrencia,
                    proximaDosis = request.proximaDosis,
                    notas = request.notas
                )

                call.respond(vaccine)
            }

            delete ("/{id}"){
                val principal = call.principal<JWTPrincipal>()!!
                val vetId = principal.payload.getClaim("id").asInt()
                val id = call.parameters["id"]!!.toInt()

                service.delete(id, vetId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}