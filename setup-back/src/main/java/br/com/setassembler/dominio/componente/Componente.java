package br.com.setassembler.dominio.componente;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Componente (diagrama de classes): modelo, marca, desc, preço, categoria.
 * Cada especialização (PlacaMae, Processador, ...) fica na mesma tabela; a coluna
 * "categoria" é o discriminador que diz qual subclasse o JPA deve instanciar.
 */
@Entity
@Table(name = "componente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categoria", length = 20)
@Getter
@Setter
public abstract class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 120)
    private String modelo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    public abstract Categoria getCategoria();

    /** Consumo em Watts usado no cálculo da fonte (0 para peças que não entram na conta). */
    public int consumoW() {
        return 0;
    }

    protected static int ou0(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
