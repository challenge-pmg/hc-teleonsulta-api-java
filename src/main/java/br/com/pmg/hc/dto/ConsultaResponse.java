package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.TipoConsulta;

public record ConsultaResponse(
        Long id,
        PacienteConsultaResumo paciente,
        ProfissionalConsultaResumo profissional,
        LocalDateTime dataHora,
        TipoConsulta tipoConsulta,
        String linkAcesso,
        StatusConsulta status,
        Long usuarioAgendadorId,
        LocalDateTime criadoEm) {
}
