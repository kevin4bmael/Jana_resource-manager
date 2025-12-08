package com.jana.dao;


import com.jana.dtos.movimentacao.MovimentacaoRegisterDTO;
import com.jana.dtos.movimentacao.MovimentacaoResponseDTO;
import com.jana.dtos.movimentacao.MovimentacaoUpdateDTO;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;
import com.jana.utils.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovimentacaoDAO {

    public MovimentacaoResponseDTO create(MovimentacaoRegisterDTO dto, Integer userId) throws SQLException {
        String sql = "INSERT INTO movimentacao (userId, recursoId, periodo, statusRecurso, statusEntrega) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, dto.recursoId());
            stmt.setString(3, dto.periodo().getDescricao());
            stmt.setString(4, StatusRecurso.OCUPADO.getDescricao());
            stmt.setString(5, StatusEntrega.AUSENTE.getDescricao());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById(generatedKeys.getInt(1)).orElseThrow();
                }
            }
        }
        throw new SQLException("Falha ao criar movimentação.");
    }

    public Optional<MovimentacaoResponseDTO> findById(Integer id) throws SQLException {
        String sql = """
            SELECT m.*, u.nome as nome_usuario, r.item as nome_recurso 
            FROM movimentacao m
            JOIN usuario u ON m.userId = u.userId
            JOIN recurso r ON m.recursoId = r.recursoId
            WHERE m.movimentacaoId = ?
        """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearResultSet(rs));
            }
        }
        return Optional.empty();
    }

    public List<MovimentacaoResponseDTO> findAll() throws SQLException {
        String sql = """
            SELECT m.*, u.nome as nome_usuario, r.item as nome_recurso 
            FROM movimentacao m
            JOIN usuario u ON m.userId = u.userId
            JOIN recurso r ON m.recursoId = r.recursoId
            ORDER BY m.momento_retirada DESC
        """;
        List<MovimentacaoResponseDTO> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        }
        return lista;
    }

    public void update(Integer id, MovimentacaoUpdateDTO dto) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE movimentacao SET statusRecurso = ?, statusEntrega = ?");

        if (dto.momentoDevolucao() != null) {
            sql.append(", momento_devolucao = ?");
        }
        sql.append(" WHERE movimentacaoId = ?");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, dto.statusRecurso().getDescricao());
            stmt.setString(2, dto.statusEntrega().getDescricao());

            int index = 3;
            if (dto.momentoDevolucao() != null) {
                stmt.setTimestamp(index++, Timestamp.valueOf(dto.momentoDevolucao()));
            }
            stmt.setInt(index, id);

            stmt.executeUpdate();
        }
    }

    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM movimentacao WHERE movimentacaoId = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private MovimentacaoResponseDTO mapearResultSet(ResultSet rs) throws SQLException {
        Timestamp devolucaoTs = rs.getTimestamp("momento_devolucao");

        return new MovimentacaoResponseDTO(
                rs.getInt("movimentacaoId"),
                rs.getInt("userId"),
                rs.getString("nome_usuario"),
                rs.getInt("recursoId"),
                rs.getString("nome_recurso"),
                rs.getString("periodo"),
                rs.getTimestamp("momento_retirada").toLocalDateTime(),
                devolucaoTs != null ? devolucaoTs.toLocalDateTime() : null,
                rs.getString("statusRecurso"),
                rs.getString("statusEntrega")
        );
    }
}