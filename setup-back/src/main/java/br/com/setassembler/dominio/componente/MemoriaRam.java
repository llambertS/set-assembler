package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** MemoriaRAM: tipo (DDR4/DDR5) igual ao da placa e frequência até o máximo suportado. Preço e consumo por pente. */
@Entity
@DiscriminatorValue("ram")
@Getter
@Setter
public class MemoriaRam extends Componente {

    @Column(length = 10)
    private String tipo;

    @Column(name = "frequencia_mhz")
    private Integer frequenciaMhz;

    @Column(name = "tdp_w")
    private Integer tdpW;

    @Override
    public Categoria getCategoria() {
        return Categoria.RAM;
    }

    @Override
    public int consumoW() {
        return ou0(tdpW);
    }
}
