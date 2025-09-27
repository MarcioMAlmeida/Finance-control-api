package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.mapper.UsuarioMapper;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void deveCriarUsuario_QuandoDadosValidos() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Teste", "teste@email.com", "senha123");
        Usuario usuarioMapeado = new Usuario(null, "Teste", "teste@email.com", null);
        UsuarioResponseDTO responseEsperado = new UsuarioResponseDTO(1L, "Teste", "teste@email.com");

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(requestDTO)).thenReturn(usuarioMapeado);
        when(usuarioMapper.toResponseDTO(any(Usuario.class))).thenReturn(responseEsperado);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponseDTO responseDTO = usuarioService.criarUsuario(requestDTO);

        assertNotNull(responseDTO.id());
        assertEquals("Teste", responseDTO.nome());
        assertEquals("teste@email.com", responseDTO.email());

        verify(usuarioRepository).findByEmail("teste@email.com");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void naoDeveCriarUsuario_QuandoDadosInvalidos() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Teste", "teste@email.com", "senha123");
        Usuario usuarioMapeado = new Usuario(null, "Teste", "teste@email.com", null);

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuarioMapeado));

        assertThrows(IllegalStateException.class, () -> usuarioService.criarUsuario(requestDTO));
    }
}
