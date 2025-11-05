package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.StatusCadastro;

public record ProfissionalResponse(
        Long id,
        Long usuarioId,
        String nome,
        String email,
        Long tipoProfissionalId,
        String tipoProfissionalNome,
        String crm,
        StatusCadastro status) {
}
