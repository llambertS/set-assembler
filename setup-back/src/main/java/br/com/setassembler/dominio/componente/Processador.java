package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** Precisa ter o mesmo socket da placa-mãe. */
@Entity
@DiscriminatorValue("cpu")
@Getter
@Setter
public class Processador extends Componente {

    @Column(length = 20)
    private String socket;

    @Column(name = "tdp_w")
    private Integer tdpW;

    @Override
    public Categoria getCategoria() {
        return Categoria.CPU;
    }

    @Override
    public int consumoW() {
        return ou0(tdpW);
    }
}
