package com.jana.dao;

import com.jana.exceptions.recurso.RecursoNaoEncontradoException;
import com.jana.model.Recurso;
import com.jana.model.enums.Funcional;
import com.jana.utils.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    private static final String FIND_BY_ID_SQL = "SELECT * FROM recurso WHERE recursoId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM recurso";
    private static final String INSERT_SQL = "INSERT INTO recurso (userId, codigoPat, item, numero, funcional, observacao) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE recurso SET userId = ?, codigoPat = ?, item = ?, numero = ?, funcional = ?, observacao = ? WHERE recursoId = ?";
    private static final String DELETE_SQL = "DELETE FROM recurso WHERE recursoId = ?";

    public void saveRecurso(Recurso recurso) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, recurso.getUserId());
            preparedStatement.setString(2, recurso.getCodPatrimonio());
            preparedStatement.setString(3, recurso.getItem());

            if (recurso.getNumero() != null) {
                preparedStatement.setInt(4, recurso.getNumero());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            preparedStatement.setString(5, recurso.getFuncional().toString());
            preparedStatement.setString(6, recurso.getObservacao());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar recurso, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recurso.setRecursoId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao salvar recurso, ID não obtido.");
                }
            }
        }
    }

    public Recurso findRecursoById(Integer recursoId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, recursoId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToRecurso(resultSet);
                } else {
                    throw new RecursoNaoEncontradoException("Recurso com id: " + recursoId + " não encontrado!");
                }
            }
        }
    }

    public List<Recurso> findAll() throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                recursos.add(resultSetToRecurso(resultSet));
            }
        }
        return recursos;
    }

    public void updateRecurso(Recurso recurso) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setInt(1, recurso.getUserId());
            preparedStatement.setString(2, recurso.getCodPatrimonio());
            preparedStatement.setString(3, recurso.getItem());

            if (recurso.getNumero() != null) {
                preparedStatement.setInt(4, recurso.getNumero());
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }

            preparedStatement.setString(5, recurso.getFuncional().toString());
            preparedStatement.setString(6, recurso.getObservacao());
            preparedStatement.setInt(7, recurso.getRecursoId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new RecursoNaoEncontradoException("Recurso com id: " + recurso.getRecursoId() + " não encontrado para atualização.");
            }
        }
    }

    public void deleteById(Integer recursoId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, recursoId);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new RecursoNaoEncontradoException("Recurso com id: " + recursoId + " não encontrado para exclusão.");
            }
        }
    }

    private Recurso resultSetToRecurso(ResultSet resultSet) throws SQLException {
        int recursoId = resultSet.getInt("recursoId");
        int userId = resultSet.getInt("userId");
        String codPatrimonio = resultSet.getString("codigoPat");
        String item = resultSet.getString("item");

        int numeroTemp = resultSet.getInt("numero");
        Integer numero = resultSet.wasNull() ? null : numeroTemp;

        String funcionalString = resultSet.getString("funcional");
        Funcional funcional = (funcionalString != null) ? Funcional.valueOf(funcionalString) : null;

        String observacao = resultSet.getString("observacao");

        return new Recurso(recursoId, userId, codPatrimonio, item, numero, funcional, observacao);
    }
}