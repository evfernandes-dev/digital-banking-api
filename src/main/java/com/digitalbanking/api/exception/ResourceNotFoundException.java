package com.digitalbanking.api.exception;

import org.springframework.http.HttpStatus;

// Erro quando o recurso pedido não existe.
public class ResourceNotFoundException extends BusinessException {

    // Responde 404 Not Found.
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
