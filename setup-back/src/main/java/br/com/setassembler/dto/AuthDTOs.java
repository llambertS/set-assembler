package br.com.setassembler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Pedidos e respostas de login/cadastro. */
public final class AuthDTOs {

    private AuthDTOs() {
    }

    public record CadastroRequest(
            @NotBlank(message = "Informe o nome.") @Size(max = 120) String nome,
            @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail inválido.") String email,
            @NotBlank @Size(min = 6, max = 72, message = "A senha precisa ter pelo menos 6 caracteres.") String senha
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "Informe o e-mail.") String email,
            @NotBlank(message = "Informe a senha.") String senha
    ) {
    }

    /** Devolvido no login: o front guarda o token e manda em "Authorization: Bearer ...". */
    public record TokenResponse(String token, String nome, String email, String papel) {
    }
}
