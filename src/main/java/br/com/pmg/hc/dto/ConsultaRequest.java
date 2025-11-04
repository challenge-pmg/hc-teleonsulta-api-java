package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull Long pacienteId,
        @NotNull Long profissionalId,
        @NotNull @FutureOrPresent LocalDateTime dataHora,
        String descricao,
        @NotBlank String status) {
}
