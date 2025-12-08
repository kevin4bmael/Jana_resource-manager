package com.jana.dao;

import com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import com.jana.model.Usuario;
import com.jana.model.enums.Perfil;
import com.jana.utils.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {


    private static final String FIND_BY_ID_SQL = "SELECT * FROM usuario WHERE userId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM usuario";
    private static final String INSERT_SQL = "INSERT INTO usuario (matricula, nome, email, senha, perfil) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE usuario SET matricula = ?, nome = ?, email = ?, senha = ?, perfil = ? WHERE userId = ?";
    private static final String DELETE_SQL = "DELETE FROM usuario WHERE userId = ?";
    private static final String EXISTS_BY_EMAIL_SQL = "SELECT 1 FROM usuario WHERE email = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM usuario WHERE email = ?";

    public void saveUsuario(Usuario usuario) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, usuario.getMatricula());
            preparedStatement.setString(2, usuario.getNome());
            preparedStatement.setString(3, usuario.getEmail());
            preparedStatement.setString(4, usuario.getSenha());
            preparedStatement.setString(5, usuario.getPerfil().toString());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar usuário, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setUserId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao salvar usuário, ID não obtido.");
                }
            }
        }
    }

    public Usuario findUsuarioById(Integer usuarioId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, usuarioId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToUsuario(resultSet);
                } else {

                    throw new UsuarioNaoEncontradoException("Usuário com id: " + usuarioId + " não encontrado!");
                }
            }
        }
    }

    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(resultSetToUsuario(resultSet));
            }
        }
        return usuarios;
    }

    // Padrão: Recebe o objeto completo e usa o ID dele
    public void updateUsuario(Usuario usuario) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setInt(1, usuario.getMatricula());
            preparedStatement.setString(2, usuario.getNome());
            preparedStatement.setString(3, usuario.getEmail());
            preparedStatement.setString(4, usuario.getSenha());
            preparedStatement.setString(5, usuario.getPerfil().toString()); // Adicionado update de perfil que faltava
            preparedStatement.setInt(6, usuario.getUserId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new UsuarioNaoEncontradoException("Usuário com id: " + usuario.getUserId() + " não encontrado para atualização.");
            }
        }
    }

    public void deleteById(Integer usuarioId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, usuarioId);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new UsuarioNaoEncontradoException("Usuário com id: " + usuarioId + " não encontrado para exclusão.");
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_BY_EMAIL_SQL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public Usuario findByEmail(String email) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToUsuario(resultSet);
                } else {
                    return null;
                }
            }
        }
    }

    private Usuario resultSetToUsuario(ResultSet resultSet) throws SQLException {
        Integer userId = resultSet.getInt("userId");
        Integer matricula = resultSet.getInt("matricula");
        String nome = resultSet.getString("nome");
        String email = resultSet.getString("email");
        String senha = resultSet.getString("senha");

        String perfilString = resultSet.getString("perfil");
        Perfil perfil = (perfilString != null) ? Perfil.valueOf(perfilString.toUpperCase()) : Perfil.COMUM;


        Usuario usuario = new Usuario();
        usuario.setUserId(userId);
        usuario.setMatricula(matricula);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setPerfil(perfil);

        return usuario;
    }
}