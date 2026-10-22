package com.digitalbanking.api.dto.conta;

import com.digitalbanking.api.entity.Conta;
import java.math.BigDecimal;

// Dados de uma conta devolvidos pela API.
public record ContaResponse(
        Long id,
        String numero,
        String titular,
        BigDecimal saldo
) {

    // Converte a entidade para o DTO de resposta.
    public static ContaResponse from(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getTitular(),
                conta.getSaldo()
        );
    }

}
