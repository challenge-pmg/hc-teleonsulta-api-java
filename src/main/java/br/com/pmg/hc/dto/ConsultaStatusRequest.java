package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.StatusConsulta;
import jakarta.validation.constraints.NotNull;

public record ConsultaStatusRequest(@NotNull StatusConsulta status) {
}
