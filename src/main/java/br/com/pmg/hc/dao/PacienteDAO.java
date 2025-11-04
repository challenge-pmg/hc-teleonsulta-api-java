package br.com.pmg.hc.dao;

import java.util.List;
import java.util.Optional;

import br.com.pmg.hc.model.Paciente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PacienteDAO {

    @Inject
    EntityManager entityManager;

    public Paciente persist(Paciente paciente) {
        entityManager.persist(paciente);
        return paciente;
    }

    public Paciente merge(Paciente paciente) {
        return entityManager.merge(paciente);
    }

    public Optional<Paciente> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Paciente.class, id));
    }

    public List<Paciente> findAll() {
        return entityManager.createQuery("from Paciente", Paciente.class).getResultList();
    }

    public void delete(Paciente paciente) {
        var managed = entityManager.contains(paciente) ? paciente : entityManager.merge(paciente);
        entityManager.remove(managed);
    }

    public Optional<Paciente> findByEmail(String email) {
        var query = entityManager.createQuery("from Paciente where email = :email", Paciente.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }
}
