package com.digitalbanking.api.dto.comum;

import java.time.Instant;
import java.util.List;

// Formato padrão de resposta de erro da API.
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> details
) {

    // Cria a resposta já com o horário atual.
    public static ApiErrorResponse of(int status, String error, String message, List<String> details) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                error,
                message,
                details == null ? List.of() : List.copyOf(details)
        );
    }

}
