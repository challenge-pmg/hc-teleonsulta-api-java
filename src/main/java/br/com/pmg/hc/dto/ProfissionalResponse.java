package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.Role;

public record ProfissionalResponse(
        Long id,
        String nome,
        String email,
        Role role,
        String especialidade,
        String registroProfissional,
        String telefone,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}
