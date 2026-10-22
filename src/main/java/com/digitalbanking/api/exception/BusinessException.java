package com.digitalbanking.api.exception;

import org.springframework.http.HttpStatus;

// Base para erros de regra de negócio com status HTTP.
public abstract class BusinessException extends RuntimeException {

    // Status HTTP que será devolvido ao cliente.
    private final HttpStatus status;

    // Guarda o status e a mensagem do erro.
    protected BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // Devolve o status HTTP do erro.
    public HttpStatus getStatus() {
        return status;
    }

}
