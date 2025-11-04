package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.Role;

public record UsuarioResumo(
        Long id,
        String nome,
        String email,
        Role role,
        LocalDateTime criadoEm) {
}
