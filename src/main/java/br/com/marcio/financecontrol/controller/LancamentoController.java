package br.com.marcio.financecontrol.controller;

import br.com.marcio.financecontrol.dto.LancamentoRequestDTO;
import br.com.marcio.financecontrol.dto.LancamentoResponseDTO;
import br.com.marcio.financecontrol.model.Usuario;
import br.com.marcio.financecontrol.service.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<LancamentoResponseDTO>> listarLancamentos(
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        List<LancamentoResponseDTO> lancamentos = lancamentoService.listarLancamentosPorUsuario(usuarioLogado);

        return ResponseEntity.ok(lancamentos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> atualizarLancamento(
            @PathVariable Long id,
            @RequestBody @Valid LancamentoRequestDTO requestDTO,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        LancamentoResponseDTO lancamentoAtualizado = lancamentoService.atualizarLancamento(id, requestDTO, usuarioLogado);

        return ResponseEntity.ok(lancamentoAtualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> buscarLancamentoPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        LancamentoResponseDTO lancamentoDTO = lancamentoService.buscarLancamentoPorId(id, usuarioLogado);

        return ResponseEntity.ok(lancamentoDTO);
    }
}