package br.com.pmg.hc.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.pmg.hc.model.Consulta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ConsultaDAO {

    @Inject
    EntityManager entityManager;

    public Consulta persist(Consulta consulta) {
        entityManager.persist(consulta);
        return consulta;
    }

    public Consulta merge(Consulta consulta) {
        return entityManager.merge(consulta);
    }

    public Optional<Consulta> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Consulta.class, id));
    }

    public List<Consulta> findAll() {
        return entityManager.createQuery("from Consulta", Consulta.class).getResultList();
    }

    public void delete(Consulta consulta) {
        var managed = entityManager.contains(consulta) ? consulta : entityManager.merge(consulta);
        entityManager.remove(managed);
    }

    public boolean existsBetweenPacienteEProfissionalEmHorario(Long pacienteId, Long profissionalId, LocalDateTime dataHora) {
        var query = entityManager.createQuery(
                "select count(c) from Consulta c where c.paciente.id = :pacienteId and c.profissional.id = :profissionalId and c.dataHora = :dataHora",
                Long.class);
        query.setParameter("pacienteId", pacienteId);
        query.setParameter("profissionalId", profissionalId);
        query.setParameter("dataHora", dataHora);
        return query.getSingleResult() > 0;
    }
}
