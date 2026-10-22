package com.digitalbanking.api.exception;

import org.springframework.http.HttpStatus;

// Erro quando o saldo não cobre o saque.
public class InsufficientBalanceException extends BusinessException {

    // Responde 409 Conflict.
    public InsufficientBalanceException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

}
