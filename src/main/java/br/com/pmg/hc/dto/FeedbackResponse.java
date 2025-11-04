package br.com.pmg.hc.dto;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        ConsultaResponse consulta,
        Integer nota,
        String comentario,
        LocalDateTime criadoEm) {
}
