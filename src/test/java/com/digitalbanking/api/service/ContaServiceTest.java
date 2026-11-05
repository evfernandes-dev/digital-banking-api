package com.digitalbanking.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.digitalbanking.api.dto.ContaRequest;
import com.digitalbanking.api.dto.ContaResponse;
import com.digitalbanking.api.entity.Conta;
import com.digitalbanking.api.repository.ContaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Testes unitários das regras do ContaService.
@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta contaComSaldo(Long id, String numero, String saldo) {
        Conta conta = new Conta(numero, "Ana Silva");
        conta.setId(id);
        conta.setSaldo(new BigDecimal(saldo));
        return conta;
    }

    // Conta criada deve começar com saldo zero.
    @Test
    void deveCriarContaComSaldoZero() {
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta conta = invocation.getArgument(0);
            conta.setId(5L);
            return conta;
        });

        ContaResponse response = contaService.criar(new ContaRequest("Ana Silva"));

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.numero()).hasSize(8);
        assertThat(response.titular()).isEqualTo("Ana Silva");
        assertThat(response.saldo()).isEqualByComparingTo("0.00");
    }

    // Listagem deve manter a ordem por id.
    @Test
    void deveListarContasOrdenadasPorId() {
        when(contaRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(contaComSaldo(1L, "00000001", "10.00"),
                        contaComSaldo(2L, "00000002", "20.00")));

        List<ContaResponse> contas = contaService.listar();

        assertThat(contas).hasSize(2);
        assertThat(contas.get(0).id()).isEqualTo(1L);
        assertThat(contas.get(1).id()).isEqualTo(2L);
    }

    // Busca por id deve devolver os dados da conta.
    @Test
    void deveBuscarContaPorId() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaComSaldo(1L, "00000001", "100.00")));

        ContaResponse response = contaService.buscarPorId(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.saldo()).isEqualByComparingTo("100.00");
    }

    // Conta inexistente deve gerar 404.
    @Test
    void deveLancarExcecaoQuandoContaNaoExistir() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaService.buscarPorId(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    // Depósito deve somar o valor ao saldo.
    @Test
    void deveDepositarSomandoAoSaldo() {
        Conta conta = contaComSaldo(1L, "00000001", "100.00");
        when(contaRepository.findByIdComBloqueio(1L)).thenReturn(Optional.of(conta));

        ContaResponse response = contaService.depositar(1L, new BigDecimal("50.00"));

        assertThat(conta.getSaldo()).isEqualByComparingTo("150.00");
        assertThat(response.saldo()).isEqualByComparingTo("150.00");
    }

    // Saque deve subtrair o valor do saldo.
    @Test
    void deveSacarSubtraindoDoSaldo() {
        Conta conta = contaComSaldo(1L, "00000001", "100.00");
        when(contaRepository.findByIdComBloqueio(1L)).thenReturn(Optional.of(conta));

        ContaResponse response = contaService.sacar(1L, new BigDecimal("40.00"));

        assertThat(conta.getSaldo()).isEqualByComparingTo("60.00");
        assertThat(response.saldo()).isEqualByComparingTo("60.00");
    }

    // Saque acima do saldo deve ser recusado.
    @Test
    void deveRejeitarSaqueComSaldoInsuficiente() {
        Conta conta = contaComSaldo(1L, "00000001", "10.00");
        when(contaRepository.findByIdComBloqueio(1L)).thenReturn(Optional.of(conta));

        assertThatThrownBy(() -> contaService.sacar(1L, new BigDecimal("50.00")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        assertThat(conta.getSaldo()).isEqualByComparingTo("10.00");
    }

}
