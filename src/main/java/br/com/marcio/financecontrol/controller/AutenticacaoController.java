package br.com.marcio.financecontrol.controller;

import br.com.marcio.financecontrol.dto.AutenticacaoRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid AutenticacaoRequestDTO authDto) {


    }
}
