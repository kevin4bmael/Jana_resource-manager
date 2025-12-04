package main.java.com.jana.dao;

import main.java.com.jana.model.Registro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;;
import java.util.Date;

public class RegistroDAO {

    public void criar(Registro r) throws SQLException {
        String sql = "INSERT INTO registro " +
                "(reserveId, userId, resourceId, dataRetirada, dataDevolucao) " +
                "VALUES (?,?,?,?,?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getReserveId());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getResourceId());
            ps.setTimestamp(4, new Timestamp(r.getDataRetirada().getTime()));

            if (r.getDataDevolucao() != null) {
                ps.setTimestamp(5, new Timestamp(r.getDataDevolucao().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

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
                "reserveId = ?, userId = ?, resourceId = ?, " +
                "dataRetirada = ?, dataDevolucao = ? " +
                "WHERE registroId = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getReserveId());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getResourceId());
            ps.setTimestamp(4, new Timestamp(r.getDataRetirada().getTime()));

            if (r.getDataDevolucao() != null) {
                ps.setTimestamp(5, new Timestamp(r.getDataDevolucao().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setInt(6, r.getRegistroId());

            ps.executeUpdate();
        }
    }

    public Registro buscarPorId(int registroId) throws SQLException {
        String sql = "SELECT * FROM registro WHERE registroId = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, registroId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Registro> listarPorUsuario(int userId) throws SQLException {
        String sql = "SELECT * FROM registro WHERE userId = ?";
        List<Registro> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    public List<Registro> listarTodos() throws SQLException {
        String sql = "SELECT * FROM registro";
        List<Registro> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private Registro map(ResultSet rs) throws SQLException {
        Registro r = new Registro();

        r.setRegistroId(rs.getInt("registroId"));
        r.setReserveId(rs.getInt("reserveId"));
        r.setUserId(rs.getInt("userId"));
        r.setResourceId(rs.getInt("resourceId"));

        Timestamp retirada = rs.getTimestamp("dataRetirada");
        if (retirada != null) {
            r.setDataRetirada(new Date(retirada.getTime()));
        }

        Timestamp devolucao = rs.getTimestamp("dataDevolucao");
        if (devolucao != null) {
            r.setDataDevolucao(new Date(devolucao.getTime()));
        }

        return r;
    }
}
