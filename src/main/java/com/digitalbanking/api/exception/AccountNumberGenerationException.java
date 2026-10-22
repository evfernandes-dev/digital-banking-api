package com.digitalbanking.api.exception;

import org.springframework.http.HttpStatus;

// Erro quando não é possível gerar um número de conta único.
public class AccountNumberGenerationException extends BusinessException {

    // Responde 409 Conflict.
    public AccountNumberGenerationException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

}
