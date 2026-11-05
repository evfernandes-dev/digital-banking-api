package com.digitalbanking.api.service;

import com.digitalbanking.api.dto.ContaRequest;
import com.digitalbanking.api.dto.ContaResponse;
import com.digitalbanking.api.entity.Conta;
import com.digitalbanking.api.repository.ContaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// Service com as regras de negócio das contas.
@Service
public class ContaService {

    private static final int LIMITE_NUMERO_CONTA = 100_000_000;

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    // Cria uma conta com número aleatório e saldo zerado.
    @Transactional
    public ContaResponse criar(ContaRequest request) {
        Conta conta = new Conta(gerarNumero(), request.titular().trim());
        return ContaResponse.from(contaRepository.save(conta));
    }

    // Lista as contas em ordem de id.
    @Transactional(readOnly = true)
    public List<ContaResponse> listar() {
        return contaRepository.findAll(Sort.by("id")).stream()
                .map(ContaResponse::from)
                .toList();
    }

    // Busca uma conta pelo id.
    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        return ContaResponse.from(buscarConta(id));
    }

    // Soma o valor ao saldo com bloqueio da linha.
    @Transactional
    public ContaResponse depositar(Long contaId, BigDecimal valor) {
        Conta conta = buscarComBloqueio(contaId);
        conta.setSaldo(conta.getSaldo().add(valor));
        return ContaResponse.from(conta);
    }

    // Subtrai o valor do saldo com bloqueio da linha.
    @Transactional
    public ContaResponse sacar(Long contaId, BigDecimal valor) {
        Conta conta = buscarComBloqueio(contaId);

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo insuficiente para realizar o saque.");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        return ContaResponse.from(conta);
    }

    // Busca a conta ou lança 404.
    private Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta não encontrada: id " + id));
    }

    // Busca a conta com lock para escrita.
    private Conta buscarComBloqueio(Long id) {
        return contaRepository.findByIdComBloqueio(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conta não encontrada: id " + id));
    }

    // Sorteia um número com 8 dígitos.
    private String gerarNumero() {
        return String.format("%08d", ThreadLocalRandom.current().nextInt(LIMITE_NUMERO_CONTA));
    }

}
