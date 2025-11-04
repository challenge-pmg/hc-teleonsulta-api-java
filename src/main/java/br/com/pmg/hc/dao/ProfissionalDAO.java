package br.com.pmg.hc.dao;

import java.util.List;
import java.util.Optional;

import br.com.pmg.hc.model.Profissional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ProfissionalDAO {

    @Inject
    EntityManager entityManager;

    public Profissional persist(Profissional profissional) {
        entityManager.persist(profissional);
        return profissional;
    }

    public Profissional merge(Profissional profissional) {
        return entityManager.merge(profissional);
    }

    public Optional<Profissional> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Profissional.class, id));
    }

    public List<Profissional> findAll() {
        return entityManager.createQuery("from Profissional", Profissional.class).getResultList();
    }

    public void delete(Profissional profissional) {
        var managed = entityManager.contains(profissional) ? profissional : entityManager.merge(profissional);
        entityManager.remove(managed);
    }

    public Optional<Profissional> findByEmail(String email) {
        var query = entityManager.createQuery("from Profissional where email = :email", Profissional.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }
}
