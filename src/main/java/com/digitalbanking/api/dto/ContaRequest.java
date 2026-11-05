package com.digitalbanking.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados de entrada para criar uma conta.
public record ContaRequest(

        @NotBlank(message = "O nome do titular é obrigatório.")
        @Size(max = 120, message = "O nome do titular deve ter no máximo 120 caracteres.")
        String titular

) {
}
