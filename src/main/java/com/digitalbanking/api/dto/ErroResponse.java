package com.digitalbanking.api.dto;

// Formato simples de resposta de erro da API.
public record ErroResponse(
        int status,
        String mensagem
) {
}
