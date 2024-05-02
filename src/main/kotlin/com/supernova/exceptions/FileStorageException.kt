package com.supernova.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class FileStorageException : RuntimeException{

    constructor(exception : String) : super(exception)
    constructor(exception : String, cause: Throwable) : super(exception, cause)
}
