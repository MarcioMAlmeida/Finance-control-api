package br.com.marcio.financecontrol.dto;

import jakarta.validation.constraints.NotBlank;

public record AutenticacaoRequestDTO(
        @NotBlank(message = "O email não pode ser vazio")
        String email,

        @NotBlank(message = "A senha não pode ser vazia")
        String senha
) {
}