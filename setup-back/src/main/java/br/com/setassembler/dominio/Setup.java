package br.com.setassembler.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Setup (diagrama de classes): nome, id, preço_total. Pertence a um usuário e tem 1..* componentes. */
@Entity
@Table(name = "setup")
@Getter
@Setter
@NoArgsConstructor
public class Setup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "preco_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoTotal;

    @Column(name = "consumo_w", nullable = false)
    private Integer consumoW;

    @Column(name = "recomendada_w", nullable = false)
    private Integer recomendadaW;

    @OneToMany(mappedBy = "setup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<SetupItem> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    /** Troca todas as peças (usado ao criar e ao alterar o setup). */
    public void substituirItens(List<SetupItem> novos) {
        itens.clear();
        novos.forEach(i -> {
            i.setSetup(this);
            itens.add(i);
        });
    }
}
