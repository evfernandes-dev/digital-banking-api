package com.digitalbanking.api.service;

import com.digitalbanking.api.dto.conta.ContaRequest;
import com.digitalbanking.api.dto.conta.ContaResponse;
import com.digitalbanking.api.dto.conta.SaldoResponse;
import com.digitalbanking.api.entity.Conta;
import com.digitalbanking.api.exception.AccountNumberGenerationException;
import com.digitalbanking.api.exception.InsufficientBalanceException;
import com.digitalbanking.api.exception.InvalidAmountException;
import com.digitalbanking.api.exception.ResourceNotFoundException;
import com.digitalbanking.api.repository.ContaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service com as regras de negócio das contas.
@Service
public class ContaService {

    // Quantidade de dígitos do número da conta.
    private static final int TAMANHO_NUMERO_CONTA = 8;
    // Tentativas para evitar número repetido.
    private static final int MAX_TENTATIVAS_NUMERO = 20;
    // Casas decimais dos valores monetários.
    private static final int ESCALA_MONETARIA = 2;
    // Maior valor aceito em uma operação.
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("99999999999999999.99");

    // Acesso ao banco de dados.
    private final ContaRepository contaRepository;

    // Recebe o repositório pelo construtor.
    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    // Cria uma conta com número único e saldo zerado.
    @Transactional
    public ContaResponse criar(ContaRequest request) {
        Conta conta = new Conta(gerarNumeroUnico(), request.titular().trim());
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

    // Devolve o saldo de uma conta.
    @Transactional(readOnly = true)
    public SaldoResponse consultarSaldo(Long id) {
        Conta conta = buscarConta(id);
        return new SaldoResponse(conta.getId(), conta.getNumero(), conta.getSaldo());
    }

    // Soma o valor ao saldo com bloqueio da linha.
    @Transactional
    public ContaResponse depositar(Long contaId, BigDecimal valor) {
        BigDecimal valorNormalizado = validarValor(valor);
        Conta conta = buscarComBloqueio(contaId);
        conta.setSaldo(conta.getSaldo().add(valorNormalizado));
        return ContaResponse.from(conta);
    }

    // Subtrai o valor do saldo com bloqueio da linha.
    @Transactional
    public ContaResponse sacar(Long contaId, BigDecimal valor) {
        BigDecimal valorNormalizado = validarValor(valor);
        Conta conta = buscarComBloqueio(contaId);

        // Impede saldo negativo.
        if (conta.getSaldo().compareTo(valorNormalizado) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar o saque.");
        }

        conta.setSaldo(conta.getSaldo().subtract(valorNormalizado));
        return ContaResponse.from(conta);
    }

    // Busca a conta ou lança 404.
    private Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: id " + id));
    }

    // Busca a conta com lock para escrita.
    private Conta buscarComBloqueio(Long id) {
        return contaRepository.findByIdComBloqueio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: id " + id));
    }

    // Valida o valor e ajusta para duas casas decimais.
    private BigDecimal validarValor(BigDecimal valor) {
        // Rejeita valor nulo ou não positivo.
        if (valor == null || valor.signum() <= 0) {
            throw new InvalidAmountException("O valor da operação deve ser maior que zero.");
        }
        // Rejeita mais de duas casas decimais.
        if (valor.stripTrailingZeros().scale() > ESCALA_MONETARIA) {
            throw new InvalidAmountException("O valor da operação deve ter no máximo duas casas decimais.");
        }
        BigDecimal valorNormalizado = valor.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);
        // Rejeita valores acima do limite.
        if (valorNormalizado.compareTo(VALOR_MAXIMO) > 0) {
            throw new InvalidAmountException("O valor da operação excede o limite máximo permitido.");
        }
        return valorNormalizado;
    }

    // Gera um número de conta que ainda não existe.
    private String gerarNumeroUnico() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS_NUMERO; tentativa++) {
            String numero = gerarNumero();
            // Aceita o primeiro número livre.
            if (!contaRepository.existsByNumero(numero)) {
                return numero;
            }
        }
        throw new AccountNumberGenerationException(
                "Não foi possível gerar um número de conta único. Tente novamente.");
    }

    // Sorteia um número com 8 dígitos.
    private String gerarNumero() {
        int limite = (int) Math.pow(10, TAMANHO_NUMERO_CONTA);
        int numero = ThreadLocalRandom.current().nextInt(limite);
        return String.format("%0" + TAMANHO_NUMERO_CONTA + "d", numero);
    }

}
