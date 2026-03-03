package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.mapper.LancamentoMapper;
import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.TipoLancamento;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.LancamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private LancamentoMapper lancamentoMapper;

    @InjectMocks
    private LancamentoService lancamentoService;

    private Usuario usuarioLogado;
    private LancamentoRequestDTO requestDTO;
    private Lancamento lancamentoMapeado;
    private Lancamento lancamentoExistente;
    private LancamentoResponseDTO responseEsperado;
    private final Long LANCAMENTO_ID = 1L;

    @BeforeEach
    void setUp() {
        usuarioLogado = new Usuario(1L, "Usuário Logado", "logado@email.com", "senha1", null);
        Usuario usuarioDono = new Usuario(2L, "Dono do Lançamento", "dono@email.com", "senha2", null);

        requestDTO = new LancamentoRequestDTO("Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), TipoLancamento.DESPESA);

        lancamentoMapeado = new Lancamento(null, "Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), TipoLancamento.DESPESA, usuarioLogado);

        lancamentoExistente = new Lancamento(LANCAMENTO_ID, "Lançamento Original", BigDecimal.ONE, LocalDate.now(), TipoLancamento.DESPESA, usuarioDono);

        responseEsperado = new LancamentoResponseDTO(LANCAMENTO_ID, "Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), TipoLancamento.DESPESA);
    }

    @Test
    @DisplayName("Deve criar um lançamento com sucesso quando os dados forem válidos")
    void criarLancamento_ComDadosValidos_DeveRetornarLancamentoResponseDTO() {
        when(lancamentoMapper.toEntity(requestDTO)).thenReturn(lancamentoMapeado);
        when(lancamentoMapper.toResponseDTO(any(Lancamento.class))).thenReturn(responseEsperado);
        when(lancamentoRepository.save(any(Lancamento.class))).thenAnswer(invocation -> {
            Lancamento l = invocation.getArgument(0);
            l.setId(LANCAMENTO_ID);
            return l;
        });

        LancamentoResponseDTO responseDTO = lancamentoService.criarLancamento(requestDTO, usuarioLogado);

        assertNotNull(responseDTO.id());
        assertEquals(requestDTO.descricao(), responseDTO.descricao());
        assertEquals(requestDTO.valor(), responseDTO.valor());
        assertEquals(requestDTO.data(), responseDTO.data());
        assertEquals(requestDTO.tipo(), responseDTO.tipo());

        verify(lancamentoMapper, times(1)).toEntity(requestDTO);
        verify(lancamentoRepository, times(1)).save(any(Lancamento.class));
        verify(lancamentoMapper, times(1)).toResponseDTO(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar um lançamento que não existe")
    void atualizarLancamento_ComIdInexistente_DeveLancarExcecao() {
        Long idInexistente = 99L;
        when(lancamentoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            lancamentoService.atualizarLancamento(idInexistente, requestDTO, usuarioLogado);
        });

        verify(lancamentoRepository, times(1)).findById(idInexistente);
        verify(lancamentoRepository, never()).save(any(Lancamento.class)); // Garante que não salvou
    }

    @Test
    @DisplayName("Deve lançar SecurityException ao tentar atualizar um lançamento de outro usuário")
    void atualizarLancamento_ComLancamentoDeOutroUsuario_DeveLancarExcecao() {
        when(lancamentoRepository.findById(LANCAMENTO_ID)).thenReturn(Optional.of(lancamentoExistente));

        assertThrows(SecurityException.class, () -> {
            lancamentoService.atualizarLancamento(LANCAMENTO_ID, requestDTO, usuarioLogado);
        });

        verify(lancamentoRepository, times(1)).findById(LANCAMENTO_ID);
        verify(lancamentoRepository, never()).save(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve lançar SecurityException ao tentar deletar um lançamento de outro usuário")
    void deletarLancamento_ComLancamentoDeOutroUsuario_DeveLancarExcecao() {
        when(lancamentoRepository.findById(LANCAMENTO_ID)).thenReturn(Optional.of(lancamentoExistente));

        assertThrows(SecurityException.class, () -> {
            lancamentoService.deletarLancamento(LANCAMENTO_ID, usuarioLogado);
        });

        verify(lancamentoRepository, times(1)).findById(LANCAMENTO_ID);
        verify(lancamentoRepository, never()).delete(any(Lancamento.class)); // Garante que a exclusão foi barrada
    }
}