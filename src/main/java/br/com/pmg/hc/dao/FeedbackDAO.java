package br.com.pmg.hc.dao;

import java.util.List;
import java.util.Optional;

import br.com.pmg.hc.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class FeedbackDAO {

    @Inject
    EntityManager entityManager;

    public Feedback persist(Feedback feedback) {
        entityManager.persist(feedback);
        return feedback;
    }

    public Feedback merge(Feedback feedback) {
        return entityManager.merge(feedback);
    }

    public Optional<Feedback> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Feedback.class, id));
    }

    public List<Feedback> findAll() {
        return entityManager.createQuery("""
                select f from Feedback f
                join fetch f.consulta c
                join fetch c.paciente p
                join fetch p.usuario
                join fetch c.profissional pr
                join fetch pr.usuario
                join fetch pr.tipoProfissional
                """, Feedback.class).getResultList();
    }

    public List<Feedback> findByPacienteUsuario(Long usuarioId) {
        var query = entityManager.createQuery("""
                select f from Feedback f
                join fetch f.consulta c
                join fetch c.paciente p
                join fetch p.usuario u
                join fetch c.profissional pr
                join fetch pr.usuario
                join fetch pr.tipoProfissional
                where u.id = :usuarioId
                """, Feedback.class);
        query.setParameter("usuarioId", usuarioId);
        return query.getResultList();
    }

    public Optional<Feedback> findByConsultaId(Long consultaId) {
        var query = entityManager.createQuery("""
                select f from Feedback f
                join fetch f.consulta c
                where c.id = :consultaId
                """, Feedback.class);
        query.setParameter("consultaId", consultaId);
        return query.getResultStream().findFirst();
    }

    public void delete(Feedback feedback) {
        var managed = entityManager.contains(feedback) ? feedback : entityManager.merge(feedback);
        entityManager.remove(managed);
    }
}
