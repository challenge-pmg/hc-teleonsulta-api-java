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

    public Optional<Profissional> findByUsuarioId(Long usuarioId) {
        var query = entityManager.createQuery("""
                select p from Profissional p
                join fetch p.usuario u
                join fetch p.tipoProfissional
                where u.id = :usuarioId
                """, Profissional.class);
        query.setParameter("usuarioId", usuarioId);
        return query.getResultStream().findFirst();
    }

    public Optional<Profissional> findByCrm(String crm) {
        var query = entityManager.createQuery("""
                select p from Profissional p
                join fetch p.usuario
                join fetch p.tipoProfissional
                where p.crm = :crm
                """, Profissional.class);
        query.setParameter("crm", crm);
        return query.getResultStream().findFirst();
    }

    public List<Profissional> findAll() {
        return entityManager.createQuery("""
                select p from Profissional p
                join fetch p.usuario
                join fetch p.tipoProfissional
                """, Profissional.class).getResultList();
    }

    public void delete(Profissional profissional) {
        var managed = entityManager.contains(profissional) ? profissional : entityManager.merge(profissional);
        entityManager.remove(managed);
    }

    public Optional<Profissional> findByEmail(String email) {
        var query = entityManager.createQuery("""
                select p from Profissional p
                join fetch p.usuario u
                join fetch p.tipoProfissional
                where upper(u.email) = :email
                """, Profissional.class);
        query.setParameter("email", email.toUpperCase());
        return query.getResultStream().findFirst();
    }
}
