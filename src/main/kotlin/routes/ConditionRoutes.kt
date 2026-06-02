package com.ignaherner.routes

import com.ignaherner.extensions.vetId
import com.ignaherner.models.dto.CreateConditionRequest
import com.ignaherner.models.dto.UpdateConditionRequest
import com.ignaherner.services.ConditionService
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
import io.ktor.server.routing.path
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.conditionRoutes() {
    val service = ConditionService()

    authenticate("auth-jwt") {
        route("/api/conditions"){
            post {
                val request = call.receive<CreateConditionRequest>()

                val condition = service.create(
                    petCode = request.petCode,
                    veterinarianId = call.vetId(),
                    nombre = request.nombre,
                    fechaDiagnostico = request.fechaDiagnostico,
                    severidad = request.severidad,
                    estado = request.estado,
                    notas = request.notas
                )

                call.respond(HttpStatusCode.Created, condition)
            }

            get("/pet/{petCode}") {
                val petCode = call.parameters["petCode"]!!
                val conditions = service.getByPetCode(petCode)
                call.respond(conditions)
            }

            patch("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val request = call.receive<UpdateConditionRequest>()

                val condition = service.update(
                    id = id,
                    requestingVetId = call.vetId(),
                    nombre = request.nombre,
                    fechaDiagnostico = request.fechaDiagnostico,
                    severidad = request.severidad,
                    estado = request.estado,
                    notas = request.notas
                )
                call.respond(condition)
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                service.delete(id, call.vetId())
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}