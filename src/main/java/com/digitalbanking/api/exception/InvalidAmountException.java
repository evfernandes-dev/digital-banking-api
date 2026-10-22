package com.digitalbanking.api.exception;

import org.springframework.http.HttpStatus;

// Erro quando o valor da operação é inválido.
public class InvalidAmountException extends BusinessException {

    // Responde 400 Bad Request.
    public InvalidAmountException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
