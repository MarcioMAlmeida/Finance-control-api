package br.com.marcio.financecontrol.dto;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email
) {
}