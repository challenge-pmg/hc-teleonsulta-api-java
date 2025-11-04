package br.com.pmg.hc.dao;

import java.util.Optional;

import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class UsuarioDAO {

    @Inject
    EntityManager entityManager;

    public Usuario persist(Usuario usuario) {
        entityManager.persist(usuario);
        return usuario;
    }

    public Usuario merge(Usuario usuario) {
        return entityManager.merge(usuario);
    }

    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Usuario.class, id));
    }

    public Optional<Usuario> findByEmail(String email) {
        var query = entityManager.createQuery("""
                select u from Usuario u where upper(u.email) = :email
                """, Usuario.class);
        query.setParameter("email", email.toUpperCase());
        return query.getResultStream().findFirst();
    }
}
