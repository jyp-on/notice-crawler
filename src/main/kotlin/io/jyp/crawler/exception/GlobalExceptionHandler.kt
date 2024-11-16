package io.jyp.crawler.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleIllegalArgumentException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[ERROR] " + ex.message)
    }
}
