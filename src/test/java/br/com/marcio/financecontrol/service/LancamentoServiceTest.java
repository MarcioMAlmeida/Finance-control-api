package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.mapper.LancamentoMapper;
import br.com.marcio.financecontrol.model.Lancamento;
import br.com.marcio.financecontrol.model.TipoLancamento;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.LancamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.marcio.financecontrol.model.TipoLancamento.DESPESA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private LancamentoMapper lancamentoMapper;

    @InjectMocks
    private LancamentoService lancamentoService;

    @Test
    void deveCriarLancamento_QuandoDadosValidos() {
        LancamentoRequestDTO requestDTO = new LancamentoRequestDTO("Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), DESPESA);
        Usuario usuarioLogado = new Usuario(1L, "Teste", "teste@email.com", "senhaCriptografada");
        Lancamento lancamentoMapeado = new Lancamento(null, "Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), DESPESA, usuarioLogado);
        LancamentoResponseDTO responseEsperado = new LancamentoResponseDTO(1L, "Teste", new BigDecimal("0.1"), LocalDate.parse("2025-10-25"), DESPESA);

        when(lancamentoMapper.toEntity(requestDTO)).thenReturn(lancamentoMapeado);
        when(lancamentoMapper.toResponseDTO(any(Lancamento.class))).thenReturn(responseEsperado);
        when(lancamentoRepository.save(any(Lancamento.class))).thenAnswer(invocation -> {
            Lancamento u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        LancamentoResponseDTO responseDTO = lancamentoService.criarLancamento(requestDTO, usuarioLogado);

        assertNotNull(responseDTO.id());
        assertEquals(requestDTO.descricao(), responseDTO.descricao());
        assertEquals(requestDTO.valor(), responseDTO.valor());
        assertEquals(requestDTO.data(), responseDTO.data());
        assertEquals(requestDTO.tipo(), responseDTO.tipo());

        verify(lancamentoRepository).save(any(Lancamento.class));
    }

    @Test
    void deveLancarExcecao_QuandoAtualizarLancamentoNaoEncontrado() {
        LancamentoRequestDTO requestDTO = new LancamentoRequestDTO("Dados Atualizados", BigDecimal.TEN, LocalDate.now(), TipoLancamento.RECEITA);
        Usuario usuarioLogado = new Usuario(1L, "Teste", "teste@email.com", "senha");
        Long idInexistente = 99L;

        when(lancamentoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            lancamentoService.atualizarLancamento(idInexistente, requestDTO, usuarioLogado);
        });
    }

    @Test
    void deveLancarExcecao_QuandoAtualizarLancamentoDeOutroUsuario() {
        LancamentoRequestDTO requestDTO = new LancamentoRequestDTO("Dados Atualizados", BigDecimal.TEN, LocalDate.now(), TipoLancamento.RECEITA);
        Long lancamentoId = 1L;

        Usuario usuarioLogado = new Usuario(1L, "Usuário Logado", "logado@email.com", "senha1");

        Usuario usuarioDono = new Usuario(2L, "Dono do Lançamento", "dono@email.com", "senha2");

        Lancamento lancamentoExistente = new Lancamento(lancamentoId, "Lançamento Original", BigDecimal.ONE, LocalDate.now(), TipoLancamento.DESPESA, usuarioDono);

        when(lancamentoRepository.findById(lancamentoId)).thenReturn(Optional.of(lancamentoExistente));

        assertThrows(SecurityException.class, () -> {
            lancamentoService.atualizarLancamento(lancamentoId, requestDTO, usuarioLogado);
        });
    }

    @Test
    void deveLancarExcecao_QuandoDeletarLancamentoDeOutroUsuario() {
        Long lancamentoId = 1L;

        Usuario usuarioLogado = new Usuario(1L, "Usuário Logado", "logado@email.com", "senha1");

        Usuario usuarioDono = new Usuario(2L, "Dono do Lançamento", "dono@email.com", "senha2");

        Lancamento lancamentoExistente = new Lancamento(lancamentoId, "Lançamento Original", BigDecimal.ONE, LocalDate.now(), TipoLancamento.DESPESA, usuarioDono);

        when(lancamentoRepository.findById(lancamentoId)).thenReturn(Optional.of(lancamentoExistente));

        assertThrows(SecurityException.class, () -> {
            lancamentoService.deletarLancamento(lancamentoId, usuarioLogado);
        });
    }
}