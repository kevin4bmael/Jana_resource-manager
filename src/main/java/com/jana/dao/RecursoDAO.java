package com.jana.dao;


import com.jana.exceptions.recurso.RecursoNaoEncontradoException;
import com.jana.model.Recurso;
import com.jana.model.enums.Funcional;
import com.jana.utils.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    public Recurso findRecursoById(Integer recursoId) throws SQLException {
        String sql = "SELECT * FROM recurso WHERE recursoId = ?";
        
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, recursoId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToRecurso(resultSet);
                } else {
                    throw new RecursoNaoEncontradoException("Recurso com id: " + recursoId + " nao encontrado!");
                }
            }
        }
    }

    public List<Recurso> findAll() throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM recurso";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                recursos.add(resultSetToRecurso(resultSet));
            }
        }
        return recursos;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM recurso WHERE recursoId = ?";
        
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();

            return result > 0;
        }
    }

    public void saveRecurso(Recurso recurso) throws SQLException {
        String sql = "INSERT INTO recurso (userId, codigoPat, item, numero, funcional, observacao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, recurso.getUserId());
            ps.setString(2, recurso.getCodPatrimonio());
            ps.setString(3, recurso.getItem());

            // Tratar numero como nullable
            if (recurso.getNumero() != null) {
                ps.setInt(4, recurso.getNumero());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, recurso.getFuncional().toString());
            ps.setString(6, recurso.getObservacao());

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Falha ao salvar recurso.");
            }

            // Recuperar o ID gerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    recurso.setRecursoId(rs.getInt(1));
                }
            }
        }
    }

    public void updateRecurso(Recurso recurso) throws SQLException {
        String sql = "UPDATE recurso SET userId = ?, codigoPat = ?, item = ?, numero = ?, funcional = ?, observacao = ? WHERE recursoId = ?";
        
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, recurso.getUserId());
            preparedStatement.setString(2, recurso.getCodPatrimonio());
            preparedStatement.setString(3, recurso.getItem());
            preparedStatement.setInt(4, recurso.getNumero());
            preparedStatement.setString(5, recurso.getFuncional().toString());
            preparedStatement.setString(6, recurso.getObservacao());
            preparedStatement.setInt(7, recurso.getRecursoId()); // ID para o WHERE

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new RecursoNaoEncontradoException("Recurso com id: " + recurso.getRecursoId() + " nao encontrado para atualizar.");
            }
        }
    }

    private Recurso resultSetToRecurso(ResultSet resultSet) throws SQLException {
        int recursoId = resultSet.getInt("recursoId");
        int userId = resultSet.getInt("userId");
        String codPatrimonio = resultSet.getString("codigoPat");
        String item = resultSet.getString("item");
        int numero = resultSet.getInt("numero");
        Funcional funcional = Funcional.valueOf(resultSet.getString("funcional"));
        String observacao = resultSet.getString("observacao");

        return new Recurso(recursoId, userId, codPatrimonio, item, numero, funcional, observacao);
    }
}