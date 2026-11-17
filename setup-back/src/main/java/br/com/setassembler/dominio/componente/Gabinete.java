package br.com.setassembler.dominio.componente;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/** Aceita certos formatos de placa e placas de vídeo até um comprimento máximo. */
@Entity
@DiscriminatorValue("gabinete")
@Getter
@Setter
public class Gabinete extends Componente {

    /** Lista separada por vírgula, ex.: "mATX,ATX". */
    @Column(length = 60)
    private String formatos;

    @Column(name = "max_gpu_mm")
    private Integer maxGpuMm;

    public List<String> listaFormatos() {
        if (formatos == null || formatos.isBlank()) return List.of();
        return Arrays.stream(formatos.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.GABINETE;
    }
}
