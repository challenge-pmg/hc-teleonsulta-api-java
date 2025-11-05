package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioDAO {

    private static final String BASE_SELECT = """
            select id_usuario, nome, email, senha, role, criado_em
            from T_TDSPW_PGR_USUARIO
            """;

    @Inject
    DataSource dataSource;

    public Usuario create(Usuario usuario) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Usuario created = create(connection, usuario);
            connection.commit();
            return created;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar usuario", e);
        }
    }

    public Usuario create(Connection connection, Usuario usuario) throws SQLException {
        String sql = """
                insert into T_TDSPW_PGR_USUARIO (nome, email, senha, role)
                values (?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getRole().name());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getLong(1));
                }
            }
            return usuario;
        }
    }

    public void update(Usuario usuario) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            update(connection, usuario);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar usuario", e);
        }
    }

    public void update(Connection connection, Usuario usuario) throws SQLException {
        String sql = """
                update T_TDSPW_PGR_USUARIO
                set nome = ?, email = ?, senha = ?, role = ?
                where id_usuario = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getRole().name());
            statement.setLong(5, usuario.getId());
            statement.executeUpdate();
        }
    }

    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            delete(connection, id);
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover usuario", e);
        }
    }

    public void delete(Connection connection, Long id) throws SQLException {
        String sql = "delete from T_TDSPW_PGR_USUARIO where id_usuario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<Usuario> findById(Long id) {
        String sql = BASE_SELECT + " where id_usuario = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuario(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuario", e);
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        String sql = BASE_SELECT + " where upper(email) = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.toUpperCase());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuario(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuario por e-mail", e);
        }
    }

    public List<Usuario> findAll() {
        String sql = BASE_SELECT + " order by id_usuario";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            List<Usuario> usuarios = new ArrayList<>();
            while (rs.next()) {
                usuarios.add(mapUsuario(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar usuarios", e);
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id_usuario");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        Role role = Role.valueOf(rs.getString("role"));
        LocalDateTime criadoEm = rs.getTimestamp("criado_em").toLocalDateTime();
        return new Usuario(id, nome, email, senha, role, criadoEm);
    }
}

