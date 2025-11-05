package br.com.pmg.hc.dto;

import java.time.LocalDate;

import br.com.pmg.hc.model.Sexo;
import br.com.pmg.hc.model.StatusCadastro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PacienteRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 255) String senha,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        Sexo sexo,
        @NotNull @Past LocalDate dataNascimento,
        @Size(max = 15) String telefone,
        @Size(max = 100) String cidade,
        StatusCadastro status) {
}
