package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.mapper.UsuarioMapper;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private EmailService emailService;

    private UsuarioRequestDTO requestDTO;

    private UsuarioResponseDTO responseEsperado;

    private Usuario usuarioMapeado;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario(1L, "Teste", "teste@dominio.com", "senhaVelha123", null);
        responseEsperado = new UsuarioResponseDTO(1L, "Teste", "teste@email.com");
        requestDTO = new UsuarioRequestDTO("Teste", "teste@email.com", "senha123");
        usuarioMapeado = new Usuario(null, "Teste", "teste@email.com", null, null);
    }

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso quando os dados forem válidos")
    void criarUsuario_ComDadosValidos_DeveRetornarUsuarioResponseDTO() {

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(requestDTO)).thenReturn(usuarioMapeado);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(usuarioMapper.toResponseDTO(any(Usuario.class))).thenReturn(responseEsperado);

        UsuarioResponseDTO responseDTO = usuarioService.criarUsuario(requestDTO);

        assertNotNull(responseDTO.id());
        assertEquals("Teste", responseDTO.nome());
        assertEquals("teste@email.com", responseDTO.email());

        verify(usuarioRepository, times(1)).findByEmail("teste@email.com");
        verify(usuarioMapper, times(1)).toEntity(requestDTO);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(usuarioMapper, times(1)).toResponseDTO(any(Usuario.class));
    }

    @Test
    @DisplayName("Não deve criar usuário e deve lançar exceção quando o e-mail já estiver cadastrado")
    void criarUsuario_ComEmailJaCadastrado_DeveLancarExcecao() {

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuarioTeste));


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            usuarioService.criarUsuario(requestDTO);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Deve gerar código e enviar e-mail quando o usuário existir")
    void solicitarRecuperacaoSenha_ComEmailExistente_DeveGerarCodigoEEnviarEmail() {
        when(usuarioRepository.findByEmail("teste@dominio.com")).thenReturn(Optional.of(usuarioTeste));

        usuarioService.solicitarRecuperacaoSenha("teste@dominio.com");

        assertNotNull(usuarioTeste.getCodigoRecuperacao());
        assertEquals(6, usuarioTeste.getCodigoRecuperacao().length());

        verify(usuarioRepository, times(1)).save(usuarioTeste);

        verify(emailService, times(1)).enviarEmailRecuperacao(eq("teste@dominio.com"), anyString());
    }

    @Test
    @DisplayName("Não deve fazer nada se o e-mail não existir no banco de dados")
    void solicitarRecuperacaoSenha_ComEmailInexistente_NaoDeveFazerNada() {
        when(usuarioRepository.findByEmail("fantasma@dominio.com")).thenReturn(Optional.empty());

        usuarioService.solicitarRecuperacaoSenha("fantasma@dominio.com");

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(emailService, never()).enviarEmailRecuperacao(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve redefinir a senha com sucesso quando os dados estiverem corretos")
    void redefinirSenha_ComDadosValidos_DeveAtualizarSenha() {
        usuarioTeste.setCodigoRecuperacao("A1B2C3");
        when(usuarioRepository.findByEmail("teste@dominio.com")).thenReturn(Optional.of(usuarioTeste));
        when(passwordEncoder.encode("novaSenhaSegura")).thenReturn("hashDaNovaSenha");

        usuarioService.redefinirSenha("teste@dominio.com", "A1B2C3", "novaSenhaSegura");

        assertEquals("hashDaNovaSenha", usuarioTeste.getSenha());
        assertNull(usuarioTeste.getCodigoRecuperacao());
        verify(usuarioRepository, times(1)).save(usuarioTeste);
    }

    @Test
    @DisplayName("Deve lançar exceção se o código enviado for inválido")
    void redefinirSenha_ComCodigoInvalido_DeveLancarExcecao() {
        usuarioTeste.setCodigoRecuperacao("A1B2C3");
        when(usuarioRepository.findByEmail("teste@dominio.com")).thenReturn(Optional.of(usuarioTeste));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.redefinirSenha("teste@dominio.com", "CODIGO_ERRADO", "novaSenha");
        });

        assertEquals("Código de recuperação inválido ou expirado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
