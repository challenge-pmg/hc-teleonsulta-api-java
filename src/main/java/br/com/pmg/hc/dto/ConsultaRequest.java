package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.TipoConsulta;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull Long pacienteId,
        @NotNull Long profissionalId,
        Long usuarioAgendadorId,
        @NotNull Long disponibilidadeId,
        @NotNull TipoConsulta tipoConsulta,
        String linkAcesso) {
}
