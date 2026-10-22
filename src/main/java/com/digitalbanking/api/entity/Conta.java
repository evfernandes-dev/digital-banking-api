package com.digitalbanking.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

// Entidade que representa uma conta bancária.
@Entity
@Table(
        name = "contas",
        uniqueConstraints = {
                // Garante número de conta único no banco.
                @UniqueConstraint(name = "uk_conta_numero", columnNames = "numero")
        }
)
public class Conta {

    // Chave primária gerada pelo banco.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número da conta com até 20 caracteres.
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    // Nome do titular da conta.
    @Column(name = "titular", nullable = false, length = 120)
    private String titular;

    // Saldo atual com duas casas decimais.
    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    // Construtor vazio exigido pelo JPA.
    public Conta() {
    }

    // Cria uma conta com número e titular.
    public Conta(String numero, String titular) {
        this.numero = numero;
        this.titular = titular;
    }

    // Devolve o id da conta.
    public Long getId() {
        return id;
    }

    // Define o id (usado em testes).
    public void setId(Long id) {
        this.id = id;
    }

    // Devolve o número da conta.
    public String getNumero() {
        return numero;
    }

    // Devolve o nome do titular.
    public String getTitular() {
        return titular;
    }

    // Devolve o saldo atual.
    public BigDecimal getSaldo() {
        return saldo;
    }

    // Atualiza o saldo da conta.
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

}
