package com.supernova.data.vo.v1

import java.util.*

class TokenVO (
    var username: String? = null,
    var authenticated: Boolean? = null,
    var createdAt: Date? = null,
    var expiresAt: Date? = null,
    var accessToken: String? = null,
    var refreshToken: String? = null
)
