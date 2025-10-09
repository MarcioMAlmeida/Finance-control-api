package br.com.marcio.financecontrol.dto;

import br.com.marcio.financecontrol.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email
) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}