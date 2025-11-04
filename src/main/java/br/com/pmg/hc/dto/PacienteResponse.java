package br.com.pmg.hc.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.pmg.hc.model.Role;

public record PacienteResponse(
        Long id,
        String nome,
        String email,
        Role role,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}
