package br.com.pmg.hc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import br.com.pmg.hc.exception.DatabaseException;
import br.com.pmg.hc.model.TipoProfissionalSaude;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TipoProfissionalSaudeDAO {

    private static final String BASE_SELECT = """
            select id_tipo_profissional, nome_tipo
            from T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE
            """;

    @Inject
    DataSource dataSource;

    public TipoProfissionalSaude create(TipoProfissionalSaude tipo) {
        String sql = """
                insert into T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE (nome_tipo)
                values (?)
                """;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, tipo.getNome());
                statement.executeUpdate();
            }

            try (PreparedStatement idQuery = connection.prepareStatement("""
                    select id_tipo_profissional
                    from T_TDSPW_PGR_TIPO_PROFISSIONAL_SAUDE
                    where upper(nome_tipo) = ?
                    order by id_tipo_profissional desc
                    fetch first 1 row only
                    """)) {
                idQuery.setString(1, tipo.getNome().toUpperCase());
                try (ResultSet rs = idQuery.executeQuery()) {
                    if (rs.next()) {
                        tipo.setId(rs.getLong(1));
                    }
                }
            }
            connection.commit();
            return tipo;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar tipo de profissional", e);
        }
    }

    public Optional<TipoProfissionalSaude> findById(Long id) {
        String sql = BASE_SELECT + " where id_tipo_profissional = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTipo(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar tipo de profissional", e);
        }
    }

    public List<TipoProfissionalSaude> findAll() {
        String sql = BASE_SELECT + " order by nome_tipo";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            List<TipoProfissionalSaude> tipos = new ArrayList<>();
            while (rs.next()) {
                tipos.add(mapTipo(rs));
            }
            return tipos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar tipos de profissional", e);
        }
    }

    private TipoProfissionalSaude mapTipo(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id_tipo_profissional");
        String nome = rs.getString("nome_tipo");
        return new TipoProfissionalSaude(id, nome);
    }
}
