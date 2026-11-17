package br.com.setassembler.repositorio;

import br.com.setassembler.dominio.Setup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetupRepository extends JpaRepository<Setup, Long> {

    List<Setup> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);

    /** Garante que um usuário só enxerga/altera os próprios setups. */
    Optional<Setup> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByItensComponenteId(Long componenteId);
}
