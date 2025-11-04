package br.com.pmg.hc.dto;

import br.com.pmg.hc.model.StatusCadastro;

public record ProfissionalResponse(
        Long id,
        UsuarioResumo usuario,
        Long tipoProfissionalId,
        String tipoProfissionalNome,
        String crm,
        StatusCadastro status) {
}
