package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** PlacadeVideo: usa o slot PCIe x16; o comprimento é conferido com o gabinete. */
@Entity
@DiscriminatorValue("gpu")
@Getter
@Setter
public class PlacaDeVideo extends Componente {

    @Column(name = "tdp_w")
    private Integer tdpW;

    @Column(name = "comprimento_mm")
    private Integer comprimentoMm;

    @Override
    public Categoria getCategoria() {
        return Categoria.GPU;
    }

    @Override
    public int consumoW() {
        return ou0(tdpW);
    }
}
