package com.ignaherner.plugins

import com.ignaherner.exceptions.EmailAlreadyExistsException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import com.ignaherner.exceptions.NotFoundException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseBody(
    val error: String,
    val message: String
)

fun Application.configureStatusPages() {
    install(StatusPages) {

        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponseBody(error = "VALIDATION_ERROR", message = cause.message ?: "Datos inválidos")
            )
        }

        exception<EmailAlreadyExistsException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponseBody(error = "EMAIL_ALREADY_EXISTS", message = cause.message ?: "Email duplicado")
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponseBody(error = "NOT_FOUND", message = cause.message ?: "Recurso no encontrado")
            )
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponseBody(error = "UNAUTHORIZED", message = cause.message ?: "No autorizado")
            )
        }

        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponseBody(error = "BAD_REQUEST", message = "El cuerpo del request es inválido o está mal formado")
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Error no manejado", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponseBody(error = "INTERNAL_ERROR", message = "Ocurrió un error interno")
            )
        }
    }
}
