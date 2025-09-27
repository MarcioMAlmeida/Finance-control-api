package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.mapper.UsuarioMapper;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.findByEmail(usuarioRequestDTO.email()).isPresent()) {
                throw new IllegalStateException("Email já cadastrado.");
        }

        Usuario novoUsuario = usuarioMapper.toEntity(usuarioRequestDTO);

        novoUsuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }
}