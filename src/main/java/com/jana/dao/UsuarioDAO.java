package com.jana.dao;

import com.jana.model.Usuario;
import com.jana.model.enums.Perfil;
import com.jana.utils.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario findUsuarioById(Integer usuarioID) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE userId = ?";
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, usuarioID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToUsuario(resultSet);
                }
                return null;
            }
        }
    }

    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(resultSetToUsuario(resultSet));
            }
        }

        return usuarios;
    }

    public void deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE userId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Usuario usuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE userId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, usuario.getUserId());
            preparedStatement.executeUpdate();
        }
    }

    public void saveUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (matricula, nome, email, senha, perfil) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, usuario.getMatricula());
            preparedStatement.setString(2, usuario.getNome());
            preparedStatement.setString(3, usuario.getEmail());
            preparedStatement.setString(4, usuario.getSenha());
            preparedStatement.setString(5, usuario.getPerfil().toString());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new SQLException("Falha ao salvar usuário");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setUserId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateUsuario(Integer id, Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET matricula = ?, nome = ?, email = ?, senha = ? WHERE userId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, usuario.getMatricula());
            preparedStatement.setString(2, usuario.getNome());
            preparedStatement.setString(3, usuario.getEmail());
            preparedStatement.setString(4, usuario.getSenha());
            preparedStatement.setInt(5, id);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new SQLException("Falha ao atualizar usuário");
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToUsuario(rs);
                } else {
                    return null;
                }
            }
        }
    }

    private Usuario resultSetToUsuario(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("userId");
        int matricula = resultSet.getInt("matricula");
        String nome = resultSet.getString("nome");
        String email = resultSet.getString("email");
        String senhaHash = resultSet.getString("senha");

        String perfilString = resultSet.getString("perfil");
        Perfil perfil = (perfilString != null) ? Perfil.valueOf(perfilString.toUpperCase()) : Perfil.COMUM;

        return new Usuario(userId, matricula, nome, email, senhaHash, perfil);
    }
}