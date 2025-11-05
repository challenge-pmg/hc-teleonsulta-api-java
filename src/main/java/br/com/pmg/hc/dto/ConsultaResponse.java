package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.TipoConsulta;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long profissionalId,
        String profissionalNome,
        Long usuarioAgendadorId,
        String usuarioAgendadorNome,
        LocalDateTime dataHora,
        TipoConsulta tipoConsulta,
        String linkAcesso,
        StatusConsulta status,
        LocalDateTime criadoEm) {
}
