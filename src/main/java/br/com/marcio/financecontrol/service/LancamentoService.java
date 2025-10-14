package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.mapper.LancamentoMapper;
import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.LancamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<LancamentoResponseDTO> listarLancamentosPorUsuario(Usuario usuarioLogado) {
        List<Lancamento> lancamentosDoUsuario = lancamentoRepository.findByUsuario(usuarioLogado);

        return lancamentosDoUsuario.stream()
                .map(lancamentoMapper::toResponseDTO)
                .toList();
    }

    public LancamentoResponseDTO atualizarLancamento(Long id, LancamentoRequestDTO requestDTO, Usuario usuarioLogado) {

            Lancamento lancamento = lancamentoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado"));

            if (!lancamento.getUsuario().getId().equals(usuarioLogado.getId())) {
                throw new SecurityException("Acesso negado: Este lançamento não pertence ao usuário.");
            }

            lancamento.setDescricao(requestDTO.descricao());
            lancamento.setValor(requestDTO.valor());
            lancamento.setData(requestDTO.data());
            lancamento.setTipo(requestDTO.tipo());

            Lancamento lancamentoAtualizado = lancamentoRepository.save(lancamento);

            return lancamentoMapper.toResponseDTO(lancamentoAtualizado);
    }

    public LancamentoResponseDTO buscarLancamentoPorId(Long id, Usuario usuarioLogado) {
        Lancamento lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lançamento não encontrado"));

        if (!lancamento.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new SecurityException("Acesso negado: Este lançamento não pertence ao usuário.");
        }

        return lancamentoMapper.toResponseDTO(lancamento);
    }
}
