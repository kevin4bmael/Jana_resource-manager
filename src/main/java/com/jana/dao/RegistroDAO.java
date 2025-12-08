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

    private static final String INSERT_SQL = "INSERT INTO registro (reservaId, userId, recursoId, localId, movimentacaoId, nome, item, numero, ano, turma, periodo, momento_retirada, statusRecurso, statusEntrega) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_DEVOLUCAO_INFO_SQL = "UPDATE registro SET momento_devolucao = ?, statusRecurso = ?, statusEntrega = ? WHERE registroId = ?";
    private static final String UPDATE_STATUS_ENTREGA_SQL = "UPDATE registro SET statusEntrega = ? WHERE registroId = ?";
    private static final String REGISTRAR_DEVOLUCAO_SQL = "UPDATE registro SET momento_devolucao = CURRENT_TIMESTAMP, statusRecurso = 'Disponível', statusEntrega = ? WHERE registroId = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM registro WHERE registroId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM registro ORDER BY momento_retirada DESC";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM registro WHERE userId = ? ORDER BY momento_retirada DESC";
    private static final String FIND_PENDENTES_SQL = "SELECT registroId, nome, item, numero, turma, momento_retirada, statusEntrega FROM registro WHERE momento_devolucao IS NULL ORDER BY momento_retirada ASC";
    private static final String FIND_HISTORICO_SQL = "SELECT r.registroId, r.nome, r.item, r.numero, r.ano, r.turma, r.periodo, r.momento_retirada, r.momento_devolucao, r.statusEntrega, l.local FROM registro r INNER JOIN local l ON r.localId = l.localId ORDER BY r.momento_retirada DESC";

    public void saveRegistro(Registro r) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

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
            ps.setString(13, r.getStatusRecurso());
            ps.setString(14, r.getStatusEntrega());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar registro, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    r.setRegistroId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateRegistro(Registro r) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_DEVOLUCAO_INFO_SQL)) {

            if (r.getMomentoDevolucao() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(r.getMomentoDevolucao()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setString(2, r.getStatusRecurso());
            ps.setString(3, r.getStatusEntrega());
            ps.setInt(4, r.getRegistroId());

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Registro com id: " + r.getRegistroId() + " não encontrado para atualização.");
            }
        }
    }

    public Registro findRegistroById(int registroId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, registroId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultSetToRegistro(rs);
                }
            }
        }
        return null;
    }

    public List<Registro> findAll() throws SQLException {
        List<Registro> lista = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(resultSetToRegistro(rs));
            }
        }
        return lista;
    }

    public List<Registro> findByUserId(int userId) throws SQLException {
        List<Registro> lista = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(resultSetToRegistro(rs));
                }
            }
        }
        return lista;
    }

    public List<RegistroPendenteDto> findPendentes() throws SQLException {
        List<RegistroPendenteDto> lista = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_PENDENTES_SQL);
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

    public List<RegistroHistoricoDTO> findHistorico() throws SQLException {
        List<RegistroHistoricoDTO> lista = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_HISTORICO_SQL);
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

    public void updateStatusEntrega(int registroId, String novoStatus) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS_ENTREGA_SQL)) {

            ps.setString(1, novoStatus);
            ps.setInt(2, registroId);

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Registro não encontrado para atualização de status.");
            }
        }
    }

    public void registrarDevolucao(int registroId, String statusEntrega) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(REGISTRAR_DEVOLUCAO_SQL)) {

            ps.setString(1, statusEntrega);
            ps.setInt(2, registroId);

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Registro não encontrado para devolução.");
            }
        }
    }

    private Registro resultSetToRegistro(ResultSet rs) throws SQLException {
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