package com.digitalbanking.api.dto.conta;

import java.math.BigDecimal;

// Resposta da consulta de saldo.
public record SaldoResponse(
        Long id,
        String numero,
        BigDecimal saldo
) {
}
