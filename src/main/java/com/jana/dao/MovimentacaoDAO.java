package com.jana.dao;

import com.jana.model.Movimentacao;
import com.jana.model.enums.Periodo;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;
import com.jana.utils.Conexao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MovimentacaoDAO {

    private static final String INSERT_SQL = "INSERT INTO movimentacao (userId, recursoId, periodo, statusRecurso, statusEntrega) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE movimentacao SET momento_devolucao = ?, statusRecurso = ?, statusEntrega = ? WHERE movimentacaoId = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM movimentacao WHERE movimentacaoId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM movimentacao ORDER BY momento_retirada DESC";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM movimentacao WHERE userId = ? ORDER BY momento_retirada DESC";
    private static final String FIND_PENDENTES_SQL = "SELECT * FROM movimentacao WHERE momento_devolucao IS NULL ORDER BY momento_retirada ASC";


    public Movimentacao create(Movimentacao movimentacao) throws SQLException {

        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, movimentacao.getUserId());
            ps.setInt(2, movimentacao.getRecursoId());
            ps.setString(3, movimentacao.getPeriodo().name());
            ps.setString(4, movimentacao.getStatusRecurso().name());
            ps.setString(5, movimentacao.getStatusEntrega().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar a movimentação, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movimentacao.setMovimentacaoId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar a movimentação, nenhum ID obtido.");
                }
            }
            return movimentacao;
        }
    }


    public void update(Movimentacao movimentacao) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {


            if (movimentacao.getMomentoDevolucao() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(movimentacao.getMomentoDevolucao()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setString(2, movimentacao.getStatusRecurso().name());
            ps.setString(3, movimentacao.getStatusEntrega().name());
            ps.setInt(4, movimentacao.getMovimentacaoId());

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Movimentacao com id: " + movimentacao.getMovimentacaoId() + " não encontrado para atualização.");
            }
        }
    }

    public Movimentacao findById(int id) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultSetToMovimentacao(rs);
                }
            }
        }
        return null;
    }

    public List<Movimentacao> findAll() throws SQLException {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                movimentacoes.add(resultSetToMovimentacao(rs));
            }
        }
        return movimentacoes;
    }


    public List<Movimentacao> findByUserId(int userId) throws SQLException {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(resultSetToMovimentacao(rs));
                }
            }
        }
        return movimentacoes;
    }

    public List<Movimentacao> findPendentes() throws SQLException {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_PENDENTES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                movimentacoes.add(resultSetToMovimentacao(rs));
            }
        }
        return movimentacoes;
    }


    private Movimentacao resultSetToMovimentacao(ResultSet rs) throws SQLException {

        Integer movimentacaoId = rs.getInt("movimentacaoId");
        Integer userId = rs.getInt("userId");
        Integer recursoId = rs.getInt("recursoId");

        Periodo periodo = Periodo.valueOf(rs.getString("periodo").toUpperCase());

        Timestamp momentoRetiradaTS = rs.getTimestamp("momento_retirada");
        LocalDateTime momentoRetirada = momentoRetiradaTS != null ? momentoRetiradaTS.toLocalDateTime() : null;

        Timestamp momentoDevolucaoTS = rs.getTimestamp("momento_devolucao");
        LocalDateTime momentoDevolucao = momentoDevolucaoTS != null ? momentoDevolucaoTS.toLocalDateTime() : null;

        StatusRecurso statusRecurso = StatusRecurso.valueOf(rs.getString("statusRecurso").toUpperCase());
        StatusEntrega statusEntrega = StatusEntrega.valueOf(rs.getString("statusEntrega").toUpperCase());

        return new Movimentacao(
                movimentacaoId,
                userId,
                recursoId,
                periodo,
                momentoRetirada,
                momentoDevolucao,
                statusRecurso,
                statusEntrega
        );
    }
}