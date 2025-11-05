package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.TipoConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull Long pacienteId,
        @NotNull Long profissionalId,
        Long usuarioAgendadorId,
        @NotNull @FutureOrPresent LocalDateTime dataHora,
        @NotNull TipoConsulta tipoConsulta,
        String linkAcesso) {
}
