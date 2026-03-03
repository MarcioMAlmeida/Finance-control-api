package br.com.marcio.financecontrol.service;

import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.mapper.UsuarioMapper;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
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

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            usuario.setCodigoRecuperacao(codigo);
            usuarioRepository.save(usuario);

            emailService.enviarEmailRecuperacao(email, codigo);
        }
    }

    @Transactional
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getCodigoRecuperacao() == null || !usuario.getCodigoRecuperacao().equals(codigo)) {
            throw new IllegalArgumentException("Código de recuperação inválido ou expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setCodigoRecuperacao(null);

        usuarioRepository.save(usuario);
    }
}