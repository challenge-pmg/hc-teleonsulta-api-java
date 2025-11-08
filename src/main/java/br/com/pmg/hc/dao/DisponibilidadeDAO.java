package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.DisponibilidadeAtendimento;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.StatusDisponibilidade;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DisponibilidadeDAO {

    private static final String BASE_SELECT = """
            select d.id_disponibilidade,
                   d.data_hora as disponibilidade_data_hora,
                   d.status as disponibilidade_status,
                   d.criado_em as disponibilidade_criado_em,
                   pr.id_profissional,
                   pr.crm as profissional_crm,
                   pr.status as profissional_status,
                   pr.id_tipo_profissional,
                   tp.nome_tipo as profissional_tipo_nome,
                   u.id_usuario as profissional_usuario_id,
                   u.nome as profissional_usuario_nome,
                   u.email as profissional_usuario_email,
                   u.senha as profissional_usuario_senha,
                   u.role as profissional_usuario_role,
                   u.criado_em as profissional_usuario_criado
            from T_TDSPW_PGR_DISPONIBILIDADE d
                     join T_TDSPW_PGR_PROFISSIONAL_SAUDE pr on pr.id_profissional = d.id_profissional
                     join T_TDSPW_PGR_USUARIO u on u.id_usuario = pr.id_usuario
                     join T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE tp on tp.id_tipo_profissional = pr.id_tipo_profissional
            """;

    @Inject
    DataSource dataSource;

    public DisponibilidadeAtendimento create(DisponibilidadeAtendimento disponibilidade) {
        String sql = """
                insert into T_TDSPW_PGR_DISPONIBILIDADE (id_profissional, data_hora, status)
                values (?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, disponibilidade.getProfissional().getId());
                statement.setTimestamp(2, Timestamp.valueOf(disponibilidade.getDataHora()));
                statement.setString(3, disponibilidade.getStatus().name());
                statement.executeUpdate();
            }

            try (PreparedStatement idQuery = connection.prepareStatement("""
                    select id_disponibilidade
                    from T_TDSPW_PGR_DISPONIBILIDADE
                    where id_profissional = ? and data_hora = ?
                    """)) {
                idQuery.setLong(1, disponibilidade.getProfissional().getId());
                idQuery.setTimestamp(2, Timestamp.valueOf(disponibilidade.getDataHora()));
                try (ResultSet rs = idQuery.executeQuery()) {
                    if (rs.next()) {
                        disponibilidade.setId(rs.getLong(1));
                    }
                }
            }
            connection.commit();
            return disponibilidade;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar disponibilidade", e);
        }
    }

    public Optional<DisponibilidadeAtendimento> findById(Long id) {
        String sql = BASE_SELECT + " where d.id_disponibilidade = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDisponibilidade(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar disponibilidade", e);
        }
    }

    public List<DisponibilidadeAtendimento> findDisponiveis(Long profissionalId, LocalDate dataInicial,
            LocalDate dataFinal) {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" where d.id_profissional = ? and d.status = 'LIVRE' and d.data_hora >= SYSTIMESTAMP");
        if (dataInicial != null) {
            sql.append(" and d.data_hora >= ?");
        }
        if (dataFinal != null) {
            sql.append(" and d.data_hora <= ?");
        }
        sql.append(" order by d.data_hora");

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            statement.setLong(index++, profissionalId);
            if (dataInicial != null) {
                statement.setTimestamp(index++, Timestamp.valueOf(dataInicial.atStartOfDay()));
            }
            if (dataFinal != null) {
                statement.setTimestamp(index, Timestamp.valueOf(dataFinal.plusDays(1).atStartOfDay()));
            }
            try (ResultSet rs = statement.executeQuery()) {
                List<DisponibilidadeAtendimento> slots = new ArrayList<>();
                while (rs.next()) {
                    slots.add(mapDisponibilidade(rs));
                }
                return slots;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar disponibilidades", e);
        }
    }

    public List<DisponibilidadeAtendimento> findByProfissional(Long profissionalId) {
        String sql = BASE_SELECT + " where d.id_profissional = ? order by d.data_hora";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, profissionalId);
            try (ResultSet rs = statement.executeQuery()) {
                List<DisponibilidadeAtendimento> slots = new ArrayList<>();
                while (rs.next()) {
                    slots.add(mapDisponibilidade(rs));
                }
                return slots;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar disponibilidades do profissional", e);
        }
    }

    public boolean reservar(Long disponibilidadeId) {
        String sql = """
                update T_TDSPW_PGR_DISPONIBILIDADE
                set status = 'RESERVADA'
                where id_disponibilidade = ? and status = 'LIVRE'
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, disponibilidadeId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao reservar disponibilidade", e);
        }
    }

    public void liberar(Long disponibilidadeId) {
        String sql = """
                update T_TDSPW_PGR_DISPONIBILIDADE
                set status = 'LIVRE'
                where id_disponibilidade = ?
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, disponibilidadeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao liberar disponibilidade", e);
        }
    }

    public void delete(Long id) {
        String sql = "delete from T_TDSPW_PGR_DISPONIBILIDADE where id_disponibilidade = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover disponibilidade", e);
        }
    }

    public boolean existsByProfissionalAndHorario(Long profissionalId, LocalDateTime dataHora) {
        String sql = """
                select count(1)
                from T_TDSPW_PGR_DISPONIBILIDADE
                where id_profissional = ? and data_hora = ?
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, profissionalId);
            statement.setTimestamp(2, Timestamp.valueOf(dataHora));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar disponibilidade duplicada", e);
        }
    }

    private DisponibilidadeAtendimento mapDisponibilidade(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
                rs.getLong("profissional_usuario_id"),
                rs.getString("profissional_usuario_nome"),
                rs.getString("profissional_usuario_email"),
                rs.getString("profissional_usuario_senha"),
                Role.valueOf(rs.getString("profissional_usuario_role")),
                rs.getTimestamp("profissional_usuario_criado").toLocalDateTime());

        TipoProfissionalSaude tipo = new TipoProfissionalSaude(
                rs.getLong("id_tipo_profissional"),
                rs.getString("profissional_tipo_nome"));

        Profissional profissional = new Profissional(
                rs.getLong("id_profissional"),
                usuario,
                tipo,
                rs.getString("profissional_crm"),
                StatusCadastro.valueOf(rs.getString("profissional_status")));

        return new DisponibilidadeAtendimento(
                rs.getLong("id_disponibilidade"),
                profissional,
                rs.getTimestamp("disponibilidade_data_hora").toLocalDateTime(),
                StatusDisponibilidade.valueOf(rs.getString("disponibilidade_status")),
                rs.getTimestamp("disponibilidade_criado_em").toLocalDateTime());
    }
}
