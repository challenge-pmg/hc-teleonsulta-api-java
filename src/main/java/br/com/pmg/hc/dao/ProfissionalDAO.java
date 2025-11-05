package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProfissionalDAO {

    private static final String BASE_SELECT = """
            select pr.id_profissional,
                   pr.crm,
                   pr.status,
                   pr.id_tipo_profissional,
                   tp.nome_tipo,
                   u.id_usuario,
                   u.nome,
                   u.email,
                   u.senha,
                   u.role,
                   u.criado_em
            from T_TDSPW_PGR_PROFISSIONAL_SAUDE pr
            join T_TDSPW_PGR_USUARIO u on u.id_usuario = pr.id_usuario
            join T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE tp on tp.id_tipo_profissional = pr.id_tipo_profissional
            """;

    @Inject
    DataSource dataSource;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    TipoProfissionalSaudeDAO tipoProfissionalSaudeDAO;

    public Profissional create(Profissional profissional) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Usuario usuario = profissional.getUsuario();
            usuario.setRole(Role.PROFISSIONAL);
            usuarioDAO.create(connection, usuario);

            String sql = """
                    insert into T_TDSPW_PGR_PROFISSIONAL_SAUDE (id_usuario, id_tipo_profissional, crm, status)
                    values (?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, usuario.getId());
                statement.setLong(2, profissional.getTipoProfissional().getId());
                statement.setString(3, profissional.getCrm());
                statement.setString(4, profissional.getStatus() != null ? profissional.getStatus().name() : StatusCadastro.ATIVO.name());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        profissional.setId(keys.getLong(1));
                    }
                }
            }
            connection.commit();
            return profissional;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar profissional", e);
        }
    }

    public Profissional update(Profissional profissional) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            usuarioDAO.update(connection, profissional.getUsuario());

            String sql = """
                    update T_TDSPW_PGR_PROFISSIONAL_SAUDE
                    set id_tipo_profissional = ?,
                        crm = ?,
                        status = ?
                    where id_profissional = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, profissional.getTipoProfissional().getId());
                statement.setString(2, profissional.getCrm());
                statement.setString(3, profissional.getStatus() != null ? profissional.getStatus().name() : StatusCadastro.ATIVO.name());
                statement.setLong(4, profissional.getId());
                statement.executeUpdate();
            }
            connection.commit();
            return profissional;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar profissional", e);
        }
    }

    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Optional<Profissional> profissionalOpt = findById(connection, id);
            if (profissionalOpt.isEmpty()) {
                connection.rollback();
                return;
            }

            String sql = "delete from T_TDSPW_PGR_PROFISSIONAL_SAUDE where id_profissional = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
            usuarioDAO.delete(connection, profissionalOpt.get().getUsuario().getId());
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover profissional", e);
        }
    }

    public Optional<Profissional> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            return findById(connection, id);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar profissional", e);
        }
    }

    private Optional<Profissional> findById(Connection connection, Long id) throws SQLException {
        String sql = BASE_SELECT + " where pr.id_profissional = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProfissional(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Profissional> findByUsuarioId(Long usuarioId) {
        String sql = BASE_SELECT + " where pr.id_usuario = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, usuarioId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProfissional(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar profissional por usu?rio", e);
        }
    }

    public Optional<Profissional> findByCrm(String crm) {
        String sql = BASE_SELECT + " where pr.crm = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, crm);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProfissional(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar profissional por CRM", e);
        }
    }

    public Optional<Profissional> findByEmail(String email) {
        String sql = BASE_SELECT + " where upper(u.email) = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.toUpperCase());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProfissional(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar profissional por e-mail", e);
        }
    }

    public List<Profissional> findAll() {
        String sql = BASE_SELECT + " order by pr.id_profissional";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            List<Profissional> profissionais = new ArrayList<>();
            while (rs.next()) {
                profissionais.add(mapProfissional(rs));
            }
            return profissionais;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar profissionais", e);
        }
    }

    private Profissional mapProfissional(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("criado_em").toLocalDateTime());

        StatusCadastro status = StatusCadastro.ATIVO;
        String valor = rs.getString("status");
        if (valor != null && !valor.isBlank()) {
            status = StatusCadastro.valueOf(valor);
        }

        TipoProfissionalSaude tipo = new TipoProfissionalSaude(
                rs.getLong("id_tipo_profissional"),
                rs.getString("nome_tipo"));

        return new Profissional(
                rs.getLong("id_profissional"),
                usuario,
                tipo,
                rs.getString("crm"),
                status);
    }
}
