package com.ignaherner.exceptions

sealed class AppException(message: String) : RuntimeException(message)

class ValidationException(message: String) : AppException(message)

class EmailAlreadyExistsException(email: String) : AppException("El email '$email' ya esta registrado")

class NotFoundException(resource: String) : AppException("$resource no encontrado/a")

class UnauthorizedException(message: String = "No autorizado") : AppException(message)