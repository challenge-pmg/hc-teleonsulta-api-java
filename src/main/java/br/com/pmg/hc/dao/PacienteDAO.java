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

    public Optional<Paciente> findByUsuarioId(Long usuarioId) {
        var query = entityManager.createQuery("""
                select p from Paciente p
                join fetch p.usuario u
                where u.id = :usuarioId
                """, Paciente.class);
        query.setParameter("usuarioId", usuarioId);
        return query.getResultStream().findFirst();
    }

    public List<Paciente> findAll() {
        return entityManager.createQuery("""
                select p from Paciente p
                join fetch p.usuario
                """, Paciente.class).getResultList();
    }

    public void delete(Paciente paciente) {
        var managed = entityManager.contains(paciente) ? paciente : entityManager.merge(paciente);
        entityManager.remove(managed);
    }

    public Optional<Paciente> findByEmail(String email) {
        var query = entityManager.createQuery("""
                select p from Paciente p
                join fetch p.usuario u
                where upper(u.email) = :email
                """, Paciente.class);
        query.setParameter("email", email.toUpperCase());
        return query.getResultStream().findFirst();
    }

    public Optional<Paciente> findByCpf(String cpf) {
        var query = entityManager.createQuery("""
                select p from Paciente p
                join fetch p.usuario
                where p.cpf = :cpf
                """, Paciente.class);
        query.setParameter("cpf", cpf);
        return query.getResultStream().findFirst();
    }
}
