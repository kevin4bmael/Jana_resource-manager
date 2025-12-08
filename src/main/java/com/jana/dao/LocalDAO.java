package com.jana.dao;


import com.jana.exceptions.local.LocalNaoEncontradoException;
import com.jana.model.Local;
import com.jana.model.enums.Ano;
import com.jana.model.enums.TipoLocal;
import com.jana.model.enums.Turma;
import com.jana.utils.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDAO {

    public Local findLocalById(Integer localId) throws SQLException {
        String sql = "SELECT * FROM local WHERE localId = ?";
        
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, localId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToLocal(resultSet);
                } else {
                    throw new LocalNaoEncontradoException("Local com id: " + localId + " nao encontrado");
                }
            }
        }
    }

    public List<Local> findAll() throws SQLException {
        List<Local> locais = new ArrayList<>();
        String sql = "SELECT * FROM local";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                locais.add(resultSetToLocal(resultSet));
            }
        }
        return locais;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM local WHERE localId = ?";
        
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();
            
            return result > 0;
        }
    }

    public void saveLocal(Local local) throws SQLException {
        String sql = "INSERT INTO local (userId, local, ano, turma) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, local.getUserId());
            ps.setString(2, local.getLocal().toString());

            if (local.getAno() != null) {
                ps.setString(3, local.getAno().toString());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            if (local.getTurma() != null) {
                ps.setString(4, local.getTurma().toString());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Falha ao salvar local.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    local.setLocalId(rs.getInt(1));
                }
            }
        }
    }

    public void updateLocal(Local local) throws SQLException {
        String sql = "UPDATE local SET userId = ?, local = ?, ano = ?, turma = ? WHERE localId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, local.getUserId());
            preparedStatement.setString(2, local.getLocal().toString());

            if (local.getAno() != null) {
                preparedStatement.setString(3, local.getAno().toString());
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }

            if (local.getTurma() != null) {
                preparedStatement.setString(4, local.getTurma().toString());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }

            preparedStatement.setInt(5, local.getLocalId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new LocalNaoEncontradoException("Local com id: " + local.getLocalId() + " nao encontrado para atualizar.");
            }
        }
    }

    private Local resultSetToLocal(ResultSet resultSet) throws SQLException {
        int localId = resultSet.getInt("localId");
        int userId = resultSet.getInt("userId");
        TipoLocal tipoLocal = TipoLocal.fromString(resultSet.getString("local"));
        String anoString = resultSet.getString("ano");
        Ano ano = (anoString == null) ? null : Ano.fromString(anoString);
        String turmaString = resultSet.getString("turma");
        Turma turma = (turmaString == null) ? null : Turma.valueOf(turmaString);

        return new Local(localId, userId, tipoLocal, ano, turma);
    }
}