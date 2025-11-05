package br.com.pmg.hc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoProfissionalSaudeRequest(
        @NotBlank
        @Size(max = 100)
        String nomeTipo) {
}
