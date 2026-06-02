package com.ignaherner.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val expirationMs: Long
) {
    fun generateToken(veterinarianId: Int, email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", veterinarianId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(Algorithm.HMAC256(secret))
    }
}