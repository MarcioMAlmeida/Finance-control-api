package br.com.marcio.financecontrol.controller;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.service.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    @Autowired
    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> criarLancamento(
            @RequestBody @Valid LancamentoRequestDTO requestDTO,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        LancamentoResponseDTO responseDTO = lancamentoService.criarLancamento(requestDTO, usuarioLogado);

        return ResponseEntity.ok(responseDTO);
    }
}