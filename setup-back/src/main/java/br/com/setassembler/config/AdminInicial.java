package br.com.setassembler.config;

import br.com.setassembler.dominio.Papel;
import br.com.setassembler.dominio.Usuario;
import br.com.setassembler.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o administrador (ator "Admin") na primeira vez que a API sobe.
 * Fica aqui, e não numa migração SQL, porque a senha precisa ser gravada com hash BCrypt.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInicial implements ApplicationRunner {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    @Value("${app.admin.nome}")
    private String nome;
    @Value("${app.admin.email}")
    private String email;
    @Value("${app.admin.senha}")
    private String senha;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarios.existsByEmailIgnoreCase(email)) return;
        usuarios.save(new Usuario(nome, email.toLowerCase(), encoder.encode(senha), Papel.ADMIN));
        log.info("Administrador criado: {}", email);
    }
}
