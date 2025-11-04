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
        return entityManager.createQuery("from Feedback", Feedback.class).getResultList();
    }

    public void delete(Feedback feedback) {
        var managed = entityManager.contains(feedback) ? feedback : entityManager.merge(feedback);
        entityManager.remove(managed);
    }
}
