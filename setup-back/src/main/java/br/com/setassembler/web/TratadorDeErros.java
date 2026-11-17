package br.com.setassembler.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Transforma exceções em JSON { status, erro, detalhes } que o front mostra na tela. */
@RestControllerAdvice
public class TratadorDeErros {

    public record Erro(int status, String erro, List<String> detalhes) {
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Erro> api(ApiException e) {
        return resposta(e.getStatus(), e.getMessage(), e.getDetalhes());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Erro> validacao(MethodArgumentNotValidException e) {
        List<String> msgs = e.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage()).toList();
        return resposta(HttpStatus.BAD_REQUEST, msgs.isEmpty() ? "Dados inválidos." : msgs.get(0), msgs);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Erro> invalido(Exception e) {
        String msg = e instanceof IllegalArgumentException ? e.getMessage() : "JSON inválido.";
        return resposta(HttpStatus.BAD_REQUEST, msg, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Erro> integridade(DataIntegrityViolationException e) {
        return resposta(HttpStatus.CONFLICT, "Operação viola uma regra do banco de dados.", List.of());
    }

    private ResponseEntity<Erro> resposta(HttpStatus status, String msg, List<String> detalhes) {
        return ResponseEntity.status(status).body(new Erro(status.value(), msg, detalhes));
    }
}
