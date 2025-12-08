package com.jana.dao;


import com.jana.dtos.registro.RegistroHistoricoDTO;
import com.jana.dtos.registro.RegistroPendenteDto;
import com.jana.model.Registro;
import com.jana.utils.Conexao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    public void criar(Registro r) throws SQLException {
        String sql = "INSERT INTO registro " +
                "(reservaId, userId, recursoId, localId, movimentacaoId, " +
                "nome, item, numero, ano, turma, periodo, " +
                "momento_retirada, statusRecurso, statusEntrega) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (r.getReservaId() != null) {
                ps.setInt(1, r.getReservaId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getResourceId());
            ps.setInt(4, r.getLocalId());
            ps.setInt(5, r.getMovimentacaoId());


            ps.setString(6, r.getNome());
            ps.setString(7, r.getItem());

            if (r.getNumero() != null) {
                ps.setInt(8, r.getNumero());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setString(9, r.getAno());
            ps.setString(10, r.getTurma());
            ps.setString(11, r.getPeriodo());


            ps.setTimestamp(12, Timestamp.valueOf(r.getMomentoRetirada()));

            // Status
            ps.setString(13, r.getStatusRecurso());
            ps.setString(14, r.getStatusEntrega());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setRegistroId(rs.getInt(1));
                }
            }
        }
    }


    public void atualizar(Registro r) throws SQLException {
        String sql = "UPDATE registro SET " +
                "momento_devolucao = ?, statusRecurso = ?, statusEntrega = ? " +
                "WHERE registroId = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (r.getMomentoDevolucao() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(r.getMomentoDevolucao()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setString(2, r.getStatusRecurso());
            ps.setString(3, r.getStatusEntrega());
            ps.setInt(4, r.getRegistroId());

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Registro não encontrado para atualização.");
            }
        }
    }


    public Registro buscarPorId(int registroId) throws SQLException {
        String sql = "SELECT * FROM registro WHERE registroId = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, registroId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRegistro(rs);
                }
            }
        }
        return null;
    }


    public List<Registro> listarPorUsuario(int userId) throws SQLException {
        String sql = "SELECT * FROM registro WHERE userId = ? ORDER BY momento_retirada DESC";
        List<Registro> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRegistro(rs));
                }
            }
        }
        return lista;
    }


    public List<Registro> listarTodos() throws SQLException {
        String sql = "SELECT * FROM registro ORDER BY momento_retirada DESC";
        List<Registro> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRegistro(rs));
            }
        }
        return lista;
    }


    public List<RegistroPendenteDto> listarPendentes() throws SQLException {
        String sql = "SELECT registroId, nome, item, numero, turma, " +
                "momento_retirada, statusEntrega " +
                "FROM registro " +
                "WHERE momento_devolucao IS NULL " +
                "ORDER BY momento_retirada ASC";

        List<RegistroPendenteDto> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("momento_retirada");
                LocalDateTime momentoRetirada = ts != null ? ts.toLocalDateTime() : null;

                lista.add(new RegistroPendenteDto(
                        rs.getInt("registroId"),
                        rs.getString("nome"),
                        rs.getString("item"),
                        rs.getInt("numero"),
                        rs.getString("turma"),
                        momentoRetirada,
                        rs.getString("statusEntrega")
                ));
            }
        }
        return lista;
    }


    public List<RegistroHistoricoDTO> listarHistorico() throws SQLException {
        String sql = "SELECT r.registroId, r.nome, r.item, r.numero, " +
                "r.ano, r.turma, r.periodo, " +
                "r.momento_retirada, r.momento_devolucao, r.statusEntrega, " +
                "l.local " +
                "FROM registro r " +
                "INNER JOIN local l ON r.localId = l.localId " +
                "ORDER BY r.momento_retirada DESC";

        List<RegistroHistoricoDTO> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String localCompleto = construirLocalCompleto(
                        rs.getString("local"),
                        rs.getString("ano"),
                        rs.getString("turma")
                );

                Timestamp tsRet = rs.getTimestamp("momento_retirada");
                Timestamp tsDev = rs.getTimestamp("momento_devolucao");

                lista.add(new RegistroHistoricoDTO(
                        rs.getInt("registroId"),
                        rs.getString("nome"),
                        rs.getString("item"),
                        (Integer) rs.getObject("numero"),
                        localCompleto,
                        rs.getString("periodo"),
                        tsRet != null ? tsRet.toLocalDateTime() : null,
                        tsDev != null ? tsDev.toLocalDateTime() : null,
                        rs.getString("statusEntrega")
                ));
            }
        }
        return lista;
    }


    public void atualizarStatusEntrega(int registroId, String novoStatus) throws SQLException {
        String sql = "UPDATE registro SET statusEntrega = ? WHERE registroId = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, novoStatus);
            ps.setInt(2, registroId);

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Registro não encontrado.");
            }
        }
    }


    public void registrarDevolucao(int registroId, String statusEntrega) throws SQLException {
        String sql = "UPDATE registro SET " +
                "momento_devolucao = CURRENT_TIMESTAMP, " +
                "statusRecurso = 'Disponível', " +
                "statusEntrega = ? " +
                "WHERE registroId = ?";

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, statusEntrega);
            ps.setInt(2, registroId);

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Registro não encontrado.");
            }
        }
    }



    //auxiliares
    private Registro mapRegistro(ResultSet rs) throws SQLException {
        Registro r = new Registro();

        r.setRegistroId(rs.getInt("registroId"));
        r.setReservaId((Integer) rs.getObject("reservaId"));
        r.setUserId(rs.getInt("userId"));
        r.setResourceId(rs.getInt("recursoId"));
        r.setLocalId(rs.getInt("localId"));
        r.setMovimentacaoId(rs.getInt("movimentacaoId"));

        r.setNome(rs.getString("nome"));
        r.setItem(rs.getString("item"));
        r.setNumero((Integer) rs.getObject("numero"));
        r.setAno(rs.getString("ano"));
        r.setTurma(rs.getString("turma"));
        r.setPeriodo(rs.getString("periodo"));

        Timestamp tsRet = rs.getTimestamp("momento_retirada");
        if (tsRet != null) {
            r.setMomentoRetirada(tsRet.toLocalDateTime());
        }

        Timestamp tsDev = rs.getTimestamp("momento_devolucao");
        if (tsDev != null) {
            r.setMomentoDevolucao(tsDev.toLocalDateTime());
        }

        r.setStatusRecurso(rs.getString("statusRecurso"));
        r.setStatusEntrega(rs.getString("statusEntrega"));

        return r;
    }

    private String construirLocalCompleto(String local, String ano, String turma) {
        StringBuilder sb = new StringBuilder(local);

        if (ano != null && !ano.isEmpty()) {
            sb.append(" ").append(ano);
        }

        if (turma != null && !turma.isEmpty()) {
            sb.append(" ").append(turma);
        }

        return sb.toString();
    }
}