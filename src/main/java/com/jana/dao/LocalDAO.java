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

    private static final String FIND_BY_ID_SQL = "SELECT * FROM local WHERE localId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM local";
    private static final String INSERT_SQL = "INSERT INTO local (userId, local, ano, turma) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE local SET userId = ?, local = ?, ano = ?, turma = ? WHERE localId = ?";
    private static final String DELETE_SQL = "DELETE FROM local WHERE localId = ?";

    public void saveLocal(Local local) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

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

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar local, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    local.setLocalId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao salvar local, ID não obtido.");
                }
            }
        }
    }

    public Local findLocalById(Integer localId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, localId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToLocal(resultSet);
                } else {
                    throw new LocalNaoEncontradoException("Local com id: " + localId + " não encontrado!");
                }
            }
        }
    }

    public List<Local> findAll() throws SQLException {
        List<Local> locais = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                locais.add(resultSetToLocal(resultSet));
            }
        }
        return locais;
    }

    public void updateLocal(Local local) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

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
                throw new LocalNaoEncontradoException("Local com id: " + local.getLocalId() + " não encontrado para atualização.");
            }
        }
    }

    public void deleteById(Integer localId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, localId);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new LocalNaoEncontradoException("Local com id: " + localId + " não encontrado para exclusão.");
            }
        }
    }

    private Local resultSetToLocal(ResultSet resultSet) throws SQLException {
        int localId = resultSet.getInt("localId");
        int userId = resultSet.getInt("userId");

        TipoLocal tipoLocal = TipoLocal.fromString(resultSet.getString("local"));

        String anoString = resultSet.getString("ano");
        Ano ano = (anoString != null) ? Ano.fromString(anoString) : null;

        String turmaString = resultSet.getString("turma");
        Turma turma = (turmaString != null) ? Turma.valueOf(turmaString) : null;

        return new Local(localId, userId, tipoLocal, ano, turma);
    }
}