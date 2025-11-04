package br.com.pmg.hc.dto;

public record ProfissionalConsultaResumo(
        Long profissionalId,
        Long usuarioId,
        String nome,
        String crm,
        String especialidade) {
}
