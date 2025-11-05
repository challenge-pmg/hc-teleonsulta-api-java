package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.Consulta;
import br.com.pmg.hc.model.Paciente;
import br.com.pmg.hc.model.Profissional;
import br.com.pmg.hc.model.Role;
import br.com.pmg.hc.model.Sexo;
import br.com.pmg.hc.model.StatusCadastro;
import br.com.pmg.hc.model.StatusConsulta;
import br.com.pmg.hc.model.TipoConsulta;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import br.com.pmg.hc.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConsultaDAO {

    private static final String BASE_SELECT = """
            select c.id_consulta,
                   c.id_paciente,
                   c.id_profissional,
                   c.id_usuario_agendador,
                   c.data_hora,
                   c.tipo_consulta,
                   c.link_acesso,
                   c.status,
                   c.criado_em,
                   p.cpf as paciente_cpf,
                   p.sexo as paciente_sexo,
                   p.dt_nascimento as paciente_dt_nascimento,
                   p.telefone as paciente_telefone,
                   p.cidade as paciente_cidade,
                   p.status as paciente_status,
                   pu.id_usuario as paciente_usuario_id,
                   pu.nome as paciente_usuario_nome,
                   pu.email as paciente_usuario_email,
                   pu.senha as paciente_usuario_senha,
                   pu.role as paciente_usuario_role,
                   pu.criado_em as paciente_usuario_criado,
                   pr.crm as profissional_crm,
                   pr.status as profissional_status,
                   pr.id_tipo_profissional,
                   tp.nome_tipo,
                   pru.id_usuario as profissional_usuario_id,
                   pru.nome as profissional_usuario_nome,
                   pru.email as profissional_usuario_email,
                   pru.senha as profissional_usuario_senha,
                   pru.role as profissional_usuario_role,
                   pru.criado_em as profissional_usuario_criado,
                   au.id_usuario as agendador_id,
                   au.nome as agendador_nome,
                   au.email as agendador_email,
                   au.senha as agendador_senha,
                   au.role as agendador_role,
                   au.criado_em as agendador_criado
            from T_TDSPW_PGR_CONSULTA c
                     join T_TDSPW_PGR_PACIENTE p on p.id_paciente = c.id_paciente
                     join T_TDSPW_PGR_USUARIO pu on pu.id_usuario = p.id_usuario
                     join T_TDSPW_PGR_PROFISSIONAL_SAUDE pr on pr.id_profissional = c.id_profissional
                     join T_TDSPW_PGR_USUARIO pru on pru.id_usuario = pr.id_usuario
                     join T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE tp on tp.id_tipo_profissional = pr.id_tipo_profissional
                     left join T_TDSPW_PGR_USUARIO au on au.id_usuario = c.id_usuario_agendador
            """;

    @Inject
    DataSource dataSource;

    public Consulta create(Consulta consulta) {
        String sql = """
                insert into T_TDSPW_PGR_CONSULTA (id_paciente, id_profissional, id_usuario_agendador, data_hora, tipo_consulta, link_acesso, status)
                values (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, consulta.getPaciente().getId());
                statement.setLong(2, consulta.getProfissional().getId());
                if (consulta.getUsuarioAgendador() != null) {
                    statement.setLong(3, consulta.getUsuarioAgendador().getId());
                } else {
                    statement.setNull(3, java.sql.Types.NUMERIC);
                }
                statement.setTimestamp(4, java.sql.Timestamp.valueOf(consulta.getDataHora()));
                statement.setString(5, consulta.getTipoConsulta().name());
                statement.setString(6, consulta.getLinkAcesso());
                statement.setString(7, consulta.getStatus().name());
                statement.executeUpdate();
            }

            try (PreparedStatement idQuery = connection.prepareStatement("""
                    select id_consulta from T_TDSPW_PGR_CONSULTA
                    where id_paciente = ? and id_profissional = ? and data_hora = ?
                    order by id_consulta desc
                    fetch first 1 row only
                    """)) {
                idQuery.setLong(1, consulta.getPaciente().getId());
                idQuery.setLong(2, consulta.getProfissional().getId());
                idQuery.setTimestamp(3, java.sql.Timestamp.valueOf(consulta.getDataHora()));
                try (ResultSet rs = idQuery.executeQuery()) {
                    if (rs.next()) {
                        consulta.setId(rs.getLong(1));
                    }
                }
            }
            connection.commit();
            return consulta;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar consulta", e);
        }
    }

    public Consulta update(Consulta consulta) {
        String sql = """
                update T_TDSPW_PGR_CONSULTA
                set id_paciente = ?,
                    id_profissional = ?,
                    id_usuario_agendador = ?,
                    data_hora = ?,
                    tipo_consulta = ?,
                    link_acesso = ?
                where id_consulta = ?
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, consulta.getPaciente().getId());
            statement.setLong(2, consulta.getProfissional().getId());
            if (consulta.getUsuarioAgendador() != null) {
                statement.setLong(3, consulta.getUsuarioAgendador().getId());
            } else {
                statement.setNull(3, java.sql.Types.NUMERIC);
            }
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(consulta.getDataHora()));
            statement.setString(5, consulta.getTipoConsulta().name());
            statement.setString(6, consulta.getLinkAcesso());
            statement.setLong(7, consulta.getId());
            statement.executeUpdate();
            return consulta;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar consulta", e);
        }
    }

    public void updateStatus(Long consultaId, StatusConsulta status) {
        String sql = "update T_TDSPW_PGR_CONSULTA set status = ? where id_consulta = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, consultaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar status da consulta", e);
        }
    }

    public void delete(Long id) {
        String sql = "delete from T_TDSPW_PGR_CONSULTA where id_consulta = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover consulta", e);
        }
    }

    public Optional<Consulta> findById(Long id) {
        String sql = BASE_SELECT + " where c.id_consulta = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapConsulta(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar consulta", e);
        }
    }

    public List<Consulta> findAll() {
        String sql = BASE_SELECT + " order by c.id_consulta";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            List<Consulta> consultas = new ArrayList<>();
            while (rs.next()) {
                consultas.add(mapConsulta(rs));
            }
            return consultas;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar consultas", e);
        }
    }

    public boolean existsByPaciente(Long pacienteId) {
        String sql = """
                select count(1) as total from T_TDSPW_PGR_CONSULTA where id_paciente = ?
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pacienteId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total") > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar consultas por paciente", e);
        }
    }

    public boolean existsByProfissional(Long profissionalId) {
        String sql = """
                select count(1) as total from T_TDSPW_PGR_CONSULTA where id_profissional = ?
                """;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, profissionalId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total") > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar consultas por profissional", e);
        }
    }

    private Consulta mapConsulta(ResultSet rs) throws SQLException {
        Usuario pacienteUsuario = new Usuario(
                rs.getLong("paciente_usuario_id"),
                rs.getString("paciente_usuario_nome"),
                rs.getString("paciente_usuario_email"),
                rs.getString("paciente_usuario_senha"),
                Role.valueOf(rs.getString("paciente_usuario_role")),
                rs.getTimestamp("paciente_usuario_criado").toLocalDateTime());

        Sexo sexo = null;
        String sexoValue = rs.getString("paciente_sexo");
        if (sexoValue != null && !sexoValue.isBlank()) {
            sexo = Sexo.valueOf(sexoValue);
        }
        StatusCadastro pacienteStatus = StatusCadastro.ATIVO;
        String pacienteStatusValue = rs.getString("paciente_status");
        if (pacienteStatusValue != null && !pacienteStatusValue.isBlank()) {
            pacienteStatus = StatusCadastro.valueOf(pacienteStatusValue);
        }
        LocalDate nascimento = rs.getObject("paciente_dt_nascimento", LocalDate.class);

        Paciente paciente = new Paciente(
                rs.getLong("id_paciente"),
                pacienteUsuario,
                rs.getString("paciente_cpf"),
                sexo,
                nascimento,
                rs.getString("paciente_telefone"),
                rs.getString("paciente_cidade"),
                pacienteStatus);

        Usuario profissionalUsuario = new Usuario(
                rs.getLong("profissional_usuario_id"),
                rs.getString("profissional_usuario_nome"),
                rs.getString("profissional_usuario_email"),
                rs.getString("profissional_usuario_senha"),
                Role.valueOf(rs.getString("profissional_usuario_role")),
                rs.getTimestamp("profissional_usuario_criado").toLocalDateTime());

        StatusCadastro profissionalStatus = StatusCadastro.ATIVO;
        String profStatusValue = rs.getString("profissional_status");
        if (profStatusValue != null && !profStatusValue.isBlank()) {
            profissionalStatus = StatusCadastro.valueOf(profStatusValue);
        }

        TipoProfissionalSaude tipo = new TipoProfissionalSaude(
                rs.getLong("id_tipo_profissional"),
                rs.getString("nome_tipo"));

        Profissional profissional = new Profissional(
                rs.getLong("id_profissional"),
                profissionalUsuario,
                tipo,
                rs.getString("profissional_crm"),
                profissionalStatus);

        Usuario agendador = null;
        Long agendadorId = rs.getLong("agendador_id");
        if (!rs.wasNull()) {
            agendador = new Usuario(
                    agendadorId,
                    rs.getString("agendador_nome"),
                    rs.getString("agendador_email"),
                    rs.getString("agendador_senha"),
                    Role.valueOf(rs.getString("agendador_role")),
                    rs.getTimestamp("agendador_criado").toLocalDateTime());
        }

        TipoConsulta tipoConsulta = TipoConsulta.valueOf(rs.getString("tipo_consulta"));
        StatusConsulta status = StatusConsulta.valueOf(rs.getString("status"));
        LocalDateTime criadoEm = rs.getTimestamp("criado_em") != null
                ? rs.getTimestamp("criado_em").toLocalDateTime()
                : null;

        return new Consulta(
                rs.getLong("id_consulta"),
                paciente,
                profissional,
                agendador,
                rs.getTimestamp("data_hora").toLocalDateTime(),
                tipoConsulta,
                rs.getString("link_acesso"),
                status,
                criadoEm);
    }
}
