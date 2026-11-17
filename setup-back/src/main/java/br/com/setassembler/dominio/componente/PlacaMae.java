package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/** Base do setup: define socket, padrão/frequência de RAM, formato e slots M.2. */
@Entity
@DiscriminatorValue("placa")
@Getter
@Setter
public class PlacaMae extends Componente {

    @Column(length = 20)
    private String socket;

    @Column(name = "tipo_memoria", length = 10)
    private String tipoMemoria;

    @Column(length = 10)
    private String formato;

    @Column(name = "slots_ram")
    private Integer slotsRam;

    @Column(name = "ram_max_mhz")
    private Integer ramMaxMhz;

    @Column(name = "slots_m2")
    private Integer slotsM2;

    @Override
    public Categoria getCategoria() {
        return Categoria.PLACA;
    }
}
