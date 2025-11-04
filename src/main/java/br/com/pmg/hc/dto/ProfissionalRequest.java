package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.StatusCadastro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfissionalRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 255) String senha,
        @NotNull Long tipoProfissionalId,
        @Size(max = 20) String crm,
        StatusCadastro status) {
}
