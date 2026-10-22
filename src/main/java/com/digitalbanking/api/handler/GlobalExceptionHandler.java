package com.digitalbanking.api.handler;

import com.digitalbanking.api.dto.comum.ApiErrorResponse;
import com.digitalbanking.api.exception.BusinessException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Trata exceções de todos os controllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger para erros inesperados.
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Converte exceções de negócio no status definido por elas.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> tratarRegraDeNegocio(BusinessException ex) {
        return construir(ex.getStatus(), ex.getMessage(), List.of());
    }

    // Converte erros de validação em 400 com a lista de campos.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        // Junta campo e mensagem de cada erro.
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos.", detalhes);
    }

    // Trata JSON ausente ou malformado.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou inválido.", List.of());
    }

    // Último recurso: registra e devolve 500.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(Exception ex) {
        log.error("Erro interno inesperado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado.", List.of());
    }

    // Monta a resposta padrão de erro.
    private ResponseEntity<ApiErrorResponse> construir(HttpStatus status, String mensagem, List<String> detalhes) {
        ApiErrorResponse body = ApiErrorResponse.of(status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(body);
    }

}
