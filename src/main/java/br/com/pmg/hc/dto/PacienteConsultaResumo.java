package br.com.pmg.hc.dto;

public record PacienteConsultaResumo(
        Long pacienteId,
        Long usuarioId,
        String nome,
        String cpf) {
}
