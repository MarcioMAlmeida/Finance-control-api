package br.com.marcio.financecontrol.dto;

import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        TipoLancamento tipo
) {
    public LancamentoResponseDTO(Lancamento lancamento) {
        this(lancamento.getId(), lancamento.getDescricao(), lancamento.getValor(), lancamento.getData(), lancamento.getTipo());
    }
}