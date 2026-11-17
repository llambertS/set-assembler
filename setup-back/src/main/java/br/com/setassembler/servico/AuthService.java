package br.com.setassembler.servico;

import br.com.setassembler.dominio.Papel;
import br.com.setassembler.dominio.Usuario;
import br.com.setassembler.dto.AuthDTOs.CadastroRequest;
import br.com.setassembler.dto.AuthDTOs.LoginRequest;
import br.com.setassembler.dto.AuthDTOs.TokenResponse;
import br.com.setassembler.repositorio.UsuarioRepository;
import br.com.setassembler.seguranca.JwtService;
import br.com.setassembler.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** "Realizar cadastro" e "Efetuar login" (diagrama de atividades). */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public TokenResponse cadastrar(CadastroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        }
        Usuario u = usuarios.save(new Usuario(req.nome().trim(), email, encoder.encode(req.senha()), Papel.USUARIO));
        return token(u);
    }

    @Transactional(readOnly = true)
    public TokenResponse entrar(LoginRequest req) {
        Usuario u = usuarios.findByEmailIgnoreCase(req.email().trim())
                .filter(x -> encoder.matches(req.senha(), x.getSenhaHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos."));
        return token(u);
    }

    public TokenResponse token(Usuario u) {
        return new TokenResponse(jwt.gerar(u), u.getNome(), u.getEmail(), u.getPapel().name());
    }
}
