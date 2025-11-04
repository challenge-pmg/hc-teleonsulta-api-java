package br.com.pmg.hc.dto;

import java.time.LocalDate;

import br.com.pmg.hc.model.Sexo;
import br.com.pmg.hc.model.StatusCadastro;

public record PacienteResponse(
        Long id,
        UsuarioResumo usuario,
        String cpf,
        Sexo sexo,
        LocalDate dataNascimento,
        String telefone,
        String cidade,
        StatusCadastro status) {
}
