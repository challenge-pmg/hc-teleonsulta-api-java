package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.StatusConsulta;

public record ConsultaResponse(
        Long id,
        PacienteResponse paciente,
        ProfissionalResponse profissional,
        LocalDateTime dataHora,
        String descricao,
        StatusConsulta status) {
}
