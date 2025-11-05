package br.com.pmg.hc.service;

import java.util.List;

import br.com.pmg.hc.dao.TipoProfissionalSaudeDAO;
import br.com.pmg.hc.dto.TipoProfissionalSaudeRequest;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TipoProfissionalSaudeService {

    @Inject
    TipoProfissionalSaudeDAO tipoProfissionalSaudeDAO;

    public List<TipoProfissionalSaude> listarTodos() {
        return tipoProfissionalSaudeDAO.findAll();
    }

    public TipoProfissionalSaude criar(TipoProfissionalSaudeRequest request) {
        var tipo = new TipoProfissionalSaude();
        tipo.setNome(request.nomeTipo());
        return tipoProfissionalSaudeDAO.create(tipo);
    }
}
