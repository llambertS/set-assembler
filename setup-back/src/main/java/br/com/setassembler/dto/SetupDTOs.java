package br.com.setassembler.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Pedidos e respostas dos setups. */
public final class SetupDTOs {

    private SetupDTOs() {
    }

    /** Salvar/alterar setup: nome + lista de peças. Os totais são calculados no servidor. */
    public record SetupRequest(
            @NotBlank(message = "Dê um nome ao setup.") @Size(max = 120) String nome,
            @NotEmpty(message = "O setup precisa ter peças.") List<@Valid ItemRequest> itens
    ) {
    }

    public record ItemRequest(
            @NotNull(message = "Informe o componente.") Long componenteId,
            @Min(value = 1, message = "Quantidade mínima é 1.") Integer quantidade
    ) {
        public int qtd() {
            return quantidade == null ? 1 : quantidade;
        }
    }

    public record ItemResponse(String etapa, int quantidade, BigDecimal subtotal, ComponenteDTO componente) {
    }

    public record SetupResponse(
            Long id,
            String nome,
            BigDecimal precoTotal,
            int consumoW,
            int recomendadaW,
            int fonteW,
            boolean energiaAprovada,
            LocalDateTime criadoEm,
            LocalDateTime atualizadoEm,
            List<ItemResponse> itens
    ) {
    }
}
