package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** Fonte de alimentação: a potência precisa cobrir o consumo + margem de segurança. */
@Entity
@DiscriminatorValue("fonte")
@Getter
@Setter
public class Fonte extends Componente {

    @Column(name = "potencia_w")
    private Integer potenciaW;

    @Override
    public Categoria getCategoria() {
        return Categoria.FONTE;
    }
}
