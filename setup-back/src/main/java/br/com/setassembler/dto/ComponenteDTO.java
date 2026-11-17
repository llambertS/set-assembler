package br.com.setassembler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Formato "achatado" que o front-end usa (mesmos nomes de src/dados.js).
 * nome = modelo e info = descrição no banco. Campos que não se aplicam à categoria não aparecem no JSON.
 */
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ComponenteDTO(
        Long id,
        @NotBlank(message = "Informe a categoria.") String categoria,
        @NotBlank(message = "Informe a marca.") String marca,
        @NotBlank(message = "Informe o modelo.") String nome,
        @NotBlank(message = "Informe a descrição.") String info,
        @NotNull(message = "Informe o preço.") @Positive(message = "O preço deve ser maior que zero.") BigDecimal preco,

        // Placa-mãe / processador
        String socket,
        String memoria,
        String formato,
        @PositiveOrZero Integer slotsRam,
        @PositiveOrZero Integer ramMax,
        @PositiveOrZero Integer m2,

        // RAM / armazenamento / processador / placa de vídeo
        String tipo,
        @PositiveOrZero Integer freq,
        @PositiveOrZero Integer tdp,
        @PositiveOrZero Integer comp,

        // Fonte / gabinete
        @PositiveOrZero Integer watts,
        List<String> formatos,
        @PositiveOrZero Integer maxGpu
) {
}
