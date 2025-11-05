package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.Sexo;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PacienteDAO {

    private static final String BASE_SELECT = """
            select p.id_paciente,
                   p.cpf,
                   p.sexo,
                   p.dt_nascimento,
                   p.telefone,
                   p.cidade,
                   p.status,
                   u.id_usuario,
                   u.nome,
                   u.email,
                   u.senha,
                   u.role,
                   u.criado_em
            from T_TDSPW_PGR_PACIENTE p
            join T_TDSPW_PGR_USUARIO u on u.id_usuario = p.id_usuario
            """;

    @Inject
    DataSource dataSource;

    @Inject
    UsuarioDAO usuarioDAO;

    public Paciente create(Paciente paciente) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Usuario usuario = paciente.getUsuario();
            usuario.setRole(Role.PACIENTE);
            usuarioDAO.create(connection, usuario);

            String sql = """
                    insert into T_TDSPW_PGR_PACIENTE (id_usuario, cpf, sexo, dt_nascimento, telefone, cidade, status)
                    values (?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, usuario.getId());
                statement.setString(2, paciente.getCpf());
                statement.setString(3, paciente.getSexo() != null ? paciente.getSexo().name() : null);
                if (paciente.getDataNascimento() != null) {
                    statement.setDate(4, java.sql.Date.valueOf(paciente.getDataNascimento()));
                } else {
                    statement.setNull(4, java.sql.Types.DATE);
                }
                statement.setString(5, paciente.getTelefone());
                statement.setString(6, paciente.getCidade());
                statement.setString(7, paciente.getStatus() != null ? paciente.getStatus().name() : StatusCadastro.ATIVO.name());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        paciente.setId(keys.getLong(1));
                    }
                }
            }
            connection.commit();
            return paciente;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar paciente", e);
        }
    }

    public Paciente update(Paciente paciente) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            usuarioDAO.update(connection, paciente.getUsuario());

            String sql = """
                    update T_TDSPW_PGR_PACIENTE
                    set cpf = ?,
                        sexo = ?,
                        dt_nascimento = ?,
                        telefone = ?,
                        cidade = ?,
                        status = ?
                    where id_paciente = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, paciente.getCpf());
                statement.setString(2, paciente.getSexo() != null ? paciente.getSexo().name() : null);
                if (paciente.getDataNascimento() != null) {
                    statement.setDate(3, java.sql.Date.valueOf(paciente.getDataNascimento()));
                } else {
                    statement.setNull(3, java.sql.Types.DATE);
                }
                statement.setString(4, paciente.getTelefone());
                statement.setString(5, paciente.getCidade());
                statement.setString(6, paciente.getStatus() != null ? paciente.getStatus().name() : StatusCadastro.ATIVO.name());
                statement.setLong(7, paciente.getId());
                statement.executeUpdate();
            }
            connection.commit();
            return paciente;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar paciente", e);
        }
    }

    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            Optional<Paciente> pacienteOpt = findById(connection, id);
            if (pacienteOpt.isEmpty()) {
                connection.rollback();
                return;
            }

            String sql = "delete from T_TDSPW_PGR_PACIENTE where id_paciente = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
            usuarioDAO.delete(connection, pacienteOpt.get().getUsuario().getId());
            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover paciente", e);
        }
    }

    public Optional<Paciente> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            return findById(connection, id);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar paciente", e);
        }
    }

    private Optional<Paciente> findById(Connection connection, Long id) throws SQLException {
        String sql = BASE_SELECT + " where p.id_paciente = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaciente(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Paciente> findByUsuarioId(Long usuarioId) {
        String sql = BASE_SELECT + " where p.id_usuario = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, usuarioId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaciente(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar paciente por usuário", e);
        }
    }

    public Optional<Paciente> findByCpf(String cpf) {
        String sql = BASE_SELECT + " where p.cpf = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaciente(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar paciente por CPF", e);
        }
    }

    public List<Paciente> findAll() {
        String sql = BASE_SELECT + " order by p.id_paciente";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            List<Paciente> pacientes = new ArrayList<>();
            while (rs.next()) {
                pacientes.add(mapPaciente(rs));
            }
            return pacientes;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar pacientes", e);
        }
    }

    private Paciente mapPaciente(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("criado_em").toLocalDateTime());

        Sexo sexo = null;
        String sexoValue = rs.getString("sexo");
        if (sexoValue != null && !sexoValue.isBlank()) {
            sexo = Sexo.valueOf(sexoValue);
        }

        StatusCadastro status = StatusCadastro.ATIVO;
        String statusValue = rs.getString("status");
        if (statusValue != null && !statusValue.isBlank()) {
            status = StatusCadastro.valueOf(statusValue);
        }

        java.sql.Date nascimentoDate = rs.getDate("dt_nascimento");
        LocalDate nascimento = nascimentoDate != null ? nascimentoDate.toLocalDate() : null;

        return new Paciente(
                rs.getLong("id_paciente"),
                usuario,
                rs.getString("cpf"),
                sexo,
                nascimento,
                rs.getString("telefone"),
                rs.getString("cidade"),
                status);
    }
}
