package com.digitalbanking.api.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados de entrada para criar uma conta.
public record ContaRequest(

        // Nome do titular, obrigatório e com até 120 caracteres.
        @NotBlank(message = "O nome do titular é obrigatório.")
        @Size(max = 120, message = "O nome do titular deve ter no máximo 120 caracteres.")
        String titular

) {
}
