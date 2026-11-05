package com.digitalbanking.api.controller;

import com.digitalbanking.api.dto.ContaRequest;
import com.digitalbanking.api.dto.ContaResponse;
import com.digitalbanking.api.dto.ValorRequest;
import com.digitalbanking.api.service.ContaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// Controller REST dos endpoints de contas.
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Cria uma conta e responde 201 Created.
    @PostMapping
    public ResponseEntity<ContaResponse> criar(@Valid @RequestBody ContaRequest request) {
        ContaResponse response = contaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    // Lista todas as contas cadastradas.
    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar());
    }

    // Busca uma conta pelo id.
    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    // Deposita um valor na conta.
    @PostMapping("/{id}/deposito")
    public ResponseEntity<ContaResponse> depositar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.depositar(id, request.valor()));
    }

    // Saca um valor da conta.
    @PostMapping("/{id}/saque")
    public ResponseEntity<ContaResponse> sacar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.sacar(id, request.valor()));
    }

}
