package br.com.marcio.financecontrol.controller;

import br.com.marcio.financecontrol.dto.AutenticacaoRequestDTO;
import br.com.marcio.financecontrol.dto.TokenResponseDTO;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid AutenticacaoRequestDTO authDto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(authDto.email(), authDto.senha());
        var authentication = manager.authenticate(authenticationToken);
        var usuario = (Usuario) authentication.getPrincipal();

        var tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }
}
