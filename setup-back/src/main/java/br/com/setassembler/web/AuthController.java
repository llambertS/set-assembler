package br.com.setassembler.web;

import br.com.setassembler.dominio.Usuario;
import br.com.setassembler.dto.AuthDTOs.CadastroRequest;
import br.com.setassembler.dto.AuthDTOs.LoginRequest;
import br.com.setassembler.dto.AuthDTOs.TokenResponse;
import br.com.setassembler.servico.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @Operation(summary = "Realizar cadastro (já devolve o token)")
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse cadastrar(@Valid @RequestBody CadastroRequest req) {
        return auth.cadastrar(req);
    }

    @Operation(summary = "Efetuar login")
    @PostMapping("/login")
    public TokenResponse entrar(@Valid @RequestBody LoginRequest req) {
        return auth.entrar(req);
    }

    @Operation(summary = "Dados do usuário logado (renova o token)")
    @GetMapping("/eu")
    public TokenResponse eu(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "Faça login.");
        return auth.token(usuario);
    }
}
