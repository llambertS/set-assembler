package br.com.setassembler.web;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/** Erro de regra de negócio com o status HTTP que deve ir para o cliente. */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> detalhes;

    public ApiException(HttpStatus status, String mensagem) {
        this(status, mensagem, List.of());
    }

    public ApiException(HttpStatus status, String mensagem, List<String> detalhes) {
        super(mensagem);
        this.status = status;
        this.detalhes = detalhes;
    }

    public static ApiException naoEncontrado(String oque) {
        return new ApiException(HttpStatus.NOT_FOUND, oque + " não encontrado.");
    }
}
