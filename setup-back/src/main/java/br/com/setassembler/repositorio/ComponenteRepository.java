package br.com.setassembler.repositorio;

import br.com.setassembler.dominio.componente.Componente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComponenteRepository extends JpaRepository<Componente, Long> {

    /** Busca por marca + modelo (sem diferenciar maiúsculas). Termo vazio devolve tudo. */
    @Query("""
            select c from Componente c
            where lower(concat(c.marca, ' ', c.modelo, ' ', c.descricao)) like lower(concat('%', :termo, '%'))
            order by c.preco
            """)
    List<Componente> buscar(@Param("termo") String termo);
}
