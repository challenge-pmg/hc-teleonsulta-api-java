package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record DisponibilidadeRequest(
        @NotNull Long profissionalId,
        @NotNull @FutureOrPresent LocalDateTime dataHora) {
}
