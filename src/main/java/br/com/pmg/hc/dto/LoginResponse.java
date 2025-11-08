package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.Role;

public record LoginResponse(
        Long usuarioId,
        String nome,
        String email,
        Role role,
        Long pacienteId,
        Long profissionalId) {
}
