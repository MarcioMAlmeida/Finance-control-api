package br.com.marcio.financecontrol.controller;

import br.com.marcio.financecontrol.dto.UsuarioRequestDTO;
import br.com.marcio.financecontrol.dto.UsuarioResponseDTO;
import br.com.marcio.financecontrol.service.EmailService;
import br.com.marcio.financecontrol.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO novoUsuario = usuarioService.criarUsuario(usuarioRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoUsuario.id())
                .toUri();

        return ResponseEntity.created(location).body(novoUsuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        var usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    public record EsqueciSenhaRequestDTO(String email) {}

    @Autowired
    private EmailService emailService;

    // Lembre-se de liberar esta rota no Spring Security (permitAll)
    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> solicitarRecuperacao(@RequestBody EsqueciSenhaRequestDTO request) {

        // 1. Aqui, no futuro, você vai buscar se o usuário existe no Banco de Dados:
        // Optional<Usuario> usuario = usuarioRepository.findByEmail(request.email());
        // if (usuario.isEmpty()) return ResponseEntity.ok().build(); // Retorna OK para não vazar quem tem conta

        // 2. Gera um token/código temporário falso por enquanto (só pra testar o e-mail)
        String tokenTemporario = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 3. Pede para o serviço enviar a mensagem
        emailService.enviarEmailRecuperacao(request.email(), tokenTemporario);

        return ResponseEntity.ok().build();
    }
}