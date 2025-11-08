package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

import br.com.pmg.hc.model.StatusDisponibilidade;

public record DisponibilidadeResponse(
        Long id,
        Long profissionalId,
        String profissionalNome,
        LocalDateTime dataHora,
        StatusDisponibilidade status) {
}
