package com.supernova.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class RequiredObjectIsNullException : RuntimeException {
    constructor(): super("It's not allowed persist null objects")
    constructor(exception: String?): super(exception)
}
