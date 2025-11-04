package br.com.pmg.hc.dao;

import java.util.List;
import java.util.Optional;

import br.com.pmg.hc.model.TipoProfissionalSaude;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class TipoProfissionalSaudeDAO {

    @Inject
    EntityManager entityManager;

    public TipoProfissionalSaude persist(TipoProfissionalSaude tipo) {
        entityManager.persist(tipo);
        return tipo;
    }

    public Optional<TipoProfissionalSaude> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TipoProfissionalSaude.class, id));
    }

    public List<TipoProfissionalSaude> findAll() {
        return entityManager.createQuery("from TipoProfissionalSaude", TipoProfissionalSaude.class).getResultList();
    }
}
