package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.mapper.LancamentoMapper;
import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final LancamentoMapper lancamentoMapper;

    @Autowired
    public LancamentoService(LancamentoRepository lancamentoRepository, LancamentoMapper lancamentoMapper) {
        this.lancamentoRepository = lancamentoRepository;
        this.lancamentoMapper = lancamentoMapper;
    }

    public LancamentoResponseDTO criarLancamento(LancamentoRequestDTO requestDTO, Usuario usuarioLogado) {
        Lancamento novoLancamento = lancamentoMapper.toEntity(requestDTO);

        novoLancamento.setUsuario(usuarioLogado);

        Lancamento lancamentoSalvo = lancamentoRepository.save(novoLancamento);

        return lancamentoMapper.toResponseDTO(lancamentoSalvo);
    }
}