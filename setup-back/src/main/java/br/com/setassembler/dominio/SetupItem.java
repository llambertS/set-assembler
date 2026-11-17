package br.com.setassembler.dominio;

import br.com.setassembler.dominio.componente.Componente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Liga Setup e Componente (associação 1..* / 0..*) guardando a quantidade (ex.: 2 pentes de RAM). */
@Entity
@Table(name = "setup_item")
@Getter
@Setter
@NoArgsConstructor
public class SetupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "setup_id", nullable = false)
    private Setup setup;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "componente_id", nullable = false)
    private Componente componente;

    @Column(nullable = false)
    private Integer quantidade = 1;

    public SetupItem(Componente componente, int quantidade) {
        this.componente = componente;
        this.quantidade = quantidade;
    }

    public BigDecimal subtotal() {
        return componente.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }
}
