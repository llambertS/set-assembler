package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** SSD/HD. Tipo "M.2" ocupa um slot M.2 da placa; "SATA" usa as portas SATA. */
@Entity
@DiscriminatorValue("armazenamento")
@Getter
@Setter
public class Armazenamento extends Componente {

    @Column(length = 10)
    private String tipo;

    @Column(name = "tdp_w")
    private Integer tdpW;

    public boolean isM2() {
        return "M.2".equalsIgnoreCase(tipo);
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.ARMAZENAMENTO;
    }

    @Override
    public int consumoW() {
        return ou0(tdpW);
    }
}
