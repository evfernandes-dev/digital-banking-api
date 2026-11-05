package com.digitalbanking.api.handler;

import com.digitalbanking.api.dto.ErroResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Trata os erros de negócio e de validação da API.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Converte erros de negócio no status definido por eles.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> tratarErroDeNegocio(ResponseStatusException ex) {
        String mensagem = ex.getReason() != null ? ex.getReason() : "Não foi possível concluir a operação.";
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErroResponse(ex.getStatusCode().value(), mensagem));
    }

    // Converte erros de validação em 400.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        var erro = ex.getBindingResult().getFieldError();
        String mensagem = erro != null ? erro.getDefaultMessage() : "Dados de entrada inválidos.";
        return ResponseEntity.badRequest().body(new ErroResponse(400, mensagem));
    }

}
