package com.mifica.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Guardrail #2: GlobalExceptionHandler
 *
 * Centraliza o tratamento de exceções em toda a aplicação.
 * Padroniza respostas de erro, melhorando observabilidade sem alterar lógica de negócio.
 *
 * Benefícios:
 * - Consistência: Todos os erros seguem o mesmo padrão.
 * - Observabilidade: Logs estruturados e mensagens auditáveis.
 * - Segurança: Detalhes técnicos não são expostos ao cliente em produção.
 * - Manutenibilidade: Tratamento centralizado reduz duplicação em controllers.
 *
 * Não altera: Lógica de negócio, fluxos de autenticação, processamento de dados.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Valida constraints de input (Jakarta Validation).
     * Retorna detalhes dos campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validação de entrada falhou: {}", ex.getBindingResult().getFieldErrors());
        
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Validação de entrada falhou");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));
        
        errors.put("campos", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Trata violações de integridade do banco (ex: email duplicado).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        
        log.warn("Violação de integridade de dados: {}", ex.getMostSpecificCause().getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Dados duplicados ou inválidos");
        error.put("detalhes", "Um ou mais campos violam restrições de unicidade ou integridade.");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata erros de argumentos inválidos (Ex: IllegalArgumentException).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {
        
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Argumento inválido");
        error.put("mensagem", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata acesso negado (Ex: Sem permissão para recurso).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {
        
        log.warn("Acesso negado: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Acesso negado");
        error.put("mensagem", "Você não tem permissão para acessar este recurso.");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Trata recurso não encontrado.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            NoHandlerFoundException ex) {
        
        log.warn("Endpoint não encontrado: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Endpoint não encontrado");
        error.put("caminho", ex.getRequestURL());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceção genérica (fallback).
     * Em produção, retorna mensagem genérica; em desenvolvimento, pode incluir detalhes.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        
        log.error("Erro inesperado", ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Erro interno do servidor");
        error.put("mensagem", "Ocorreu um erro inesperado. Entre em contato com o suporte se o problema persistir.");
        
        // Em desenvolvimento, pode incluir detalhes da exceção
        String profile = System.getProperty("spring.profiles.active", "default");
        if ("dev".equals(profile) || "test".equals(profile)) {
            error.put("detalhes", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
