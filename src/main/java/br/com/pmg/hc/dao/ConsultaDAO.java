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
        return entityManager.createQuery("""
                select distinct c from Consulta c
                join fetch c.paciente p
                join fetch p.usuario
                join fetch c.profissional pr
                join fetch pr.usuario
                join fetch pr.tipoProfissional
                left join fetch c.usuarioAgendador
                """, Consulta.class).getResultList();
    }

    public List<Consulta> findByPaciente(Long pacienteId) {
        var query = entityManager.createQuery("""
                select distinct c from Consulta c
                join fetch c.paciente p
                join fetch p.usuario
                join fetch c.profissional pr
                join fetch pr.usuario
                join fetch pr.tipoProfissional
                left join fetch c.usuarioAgendador
                where p.id = :pacienteId
                """, Consulta.class);
        query.setParameter("pacienteId", pacienteId);
        return query.getResultList();
    }

    public List<Consulta> findByProfissional(Long profissionalId) {
        var query = entityManager.createQuery("""
                select distinct c from Consulta c
                join fetch c.paciente p
                join fetch p.usuario
                join fetch c.profissional pr
                join fetch pr.usuario
                join fetch pr.tipoProfissional
                left join fetch c.usuarioAgendador
                where pr.id = :profissionalId
                """, Consulta.class);
        query.setParameter("profissionalId", profissionalId);
        return query.getResultList();
    }

    public boolean existsByPaciente(Long pacienteId) {
        var query = entityManager.createQuery("""
                select count(c) from Consulta c
                where c.paciente.id = :pacienteId
                """, Long.class);
        query.setParameter("pacienteId", pacienteId);
        return query.getSingleResult() > 0;
    }

    public boolean existsByProfissional(Long profissionalId) {
        var query = entityManager.createQuery("""
                select count(c) from Consulta c
                where c.profissional.id = :profissionalId
                """, Long.class);
        query.setParameter("profissionalId", profissionalId);
        return query.getSingleResult() > 0;
    }

    public boolean existsByUsuarioAgendador(Long usuarioId) {
        var query = entityManager.createQuery("""
                select count(c) from Consulta c
                where c.usuarioAgendador.id = :usuarioId
                """, Long.class);
        query.setParameter("usuarioId", usuarioId);
        return query.getSingleResult() > 0;
    }

    public void delete(Consulta consulta) {
        var managed = entityManager.contains(consulta) ? consulta : entityManager.merge(consulta);
        entityManager.remove(managed);
    }

    public boolean existsBetweenPacienteEProfissionalEmHorario(Long pacienteId, Long profissionalId, LocalDateTime dataHora) {
        var query = entityManager.createQuery("""
                select count(c) from Consulta c
                where c.paciente.id = :pacienteId
                and c.profissional.id = :profissionalId
                and c.dataHora = :dataHora
                """, Long.class);
        query.setParameter("pacienteId", pacienteId);
        query.setParameter("profissionalId", profissionalId);
        query.setParameter("dataHora", dataHora);
        return query.getSingleResult() > 0;
    }
}
