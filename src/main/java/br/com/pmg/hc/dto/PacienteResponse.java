package br.com.pmg.hc.dto;

import java.time.LocalDate;

import br.com.pmg.hc.model.Sexo;
import br.com.pmg.hc.model.StatusCadastro;

public record PacienteResponse(
        Long id,
        Long usuarioId,
        String nome,
        String email,
        Sexo sexo,
        LocalDate dataNascimento,
        String cpf,
        String telefone,
        String cidade,
        StatusCadastro status) {
}
