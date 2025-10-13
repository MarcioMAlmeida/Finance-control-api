package br.com.marcio.financecontrol.mapper;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.model.Lancamento;
import org.springframework.stereotype.Component;

@Component
public class LancamentoMapper {

    public Lancamento toEntity(LancamentoRequestDTO requestDTO) {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(requestDTO.descricao());
        lancamento.setValor(requestDTO.valor());
        lancamento.setData(requestDTO.data());
        lancamento.setTipo(requestDTO.tipo());

        return lancamento;
    }

    public LancamentoResponseDTO toResponseDTO(Lancamento lancamento) {
        return new LancamentoResponseDTO(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getData(),
                lancamento.getTipo()
        );
    }
}