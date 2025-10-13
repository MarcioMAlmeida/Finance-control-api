package br.com.marcio.financecontrol.dto;

import br.com.marcio.financecontrol.model.TipoLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequestDTO(
        @NotBlank
        String descricao,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal valor,

        @NotNull
        LocalDate data,

        @NotNull
        TipoLancamento tipo
) {
}