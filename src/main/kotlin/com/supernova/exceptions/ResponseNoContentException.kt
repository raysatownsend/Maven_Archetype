package com.supernova.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NO_CONTENT)
class ResponseNoContentException (exception: String?): RuntimeException(exception)
