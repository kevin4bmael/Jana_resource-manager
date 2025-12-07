package main.java.com.jana.dao;
import main.java.com.jana.model.Usuario;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.utils.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario findUsuarioById(Integer usuarioID) throws SQLException {
        String sql = "select * from usuarios where id = ?";
        try(Connection connection = Conexao.getConnection();
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
    public List<Usuario> findAll() throws SQLException{
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "select * from usuarios";
        try(Connection connection = Conexao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery()){
            while (resultSet.next()) {
                usuarios.add(resultSetToUsuario(resultSet));
            }
        }
        return usuarios;
    }
    public void deleteById(Integer id) throws SQLException{
        String sql = "delete from usuarios where id= ?";
        try(Connection connection = Conexao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Usuario usuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, usuario.getUserId());
            preparedStatement.executeUpdate();
        }
    }
    public void saveUsuario(Usuario usuario) throws SQLException{
        int matricula= usuario.getMatricula();
        String nome = usuario.getNome();
        String email = usuario.getEmail();
        String senhaHash = usuario.getSenhaHash();
        Perfil perfil = usuario.getPerfil();
        String sql = "insert into usuarios (matricula,nome, email, senhaHash, perfil) values (?, ?, ?, ? , ?)";
        try(Connection connection = Conexao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, matricula);
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, senhaHash);
            preparedStatement.setString(5, perfil.toString());
            int result = preparedStatement.executeUpdate();
            if(result ==0){
                throw new IllegalArgumentException();
            }
        }
    }
    public void updateUsuario(Integer id, Usuario usuario) throws SQLException{
        int matricula= usuario.getMatricula();
        String nome = usuario.getNome();
        String email = usuario.getEmail();
        String senhaHash = usuario.getSenhaHash();
        Perfil perfil = usuario.getPerfil();
        String sql = "update usuarios set matricula = ?, nome = ?, email = ?, senhaHash = ?, perfil = ? where id = ?";
        try(Connection connection = Conexao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, matricula);
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, senhaHash);
            preparedStatement.setString(5, perfil.toString());
            preparedStatement.setInt(6, id);
            int result = preparedStatement.executeUpdate();
            if(result ==0){
                throw new IllegalArgumentException();
            }
        }

    }
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "select 1 from usuarios where email = ?";
        try(Connection connection = Conexao.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, email);
            try(ResultSet resultSet =preparedStatement.executeQuery()){
                if(resultSet.next()){
                    return true;
                }
            }
        }
        return false;
    }
    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT matricula, nome, email, senha, perfil FROM usuarios WHERE email = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int matricula = rs.getInt("matricula");
                    String nome = rs.getString("nome");
                    String emailDb = rs.getString("email");
                    String senha = rs.getString("senha");
                    Perfil perfil = Perfil.valueOf(rs.getString("perfil"));
                    return new Usuario(matricula, nome, emailDb, senha, perfil);
                } else {
                    return null;
                }
            }
        }
    }
    //método auxiiar
    private Usuario resultSetToUsuario(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("id");
        int matricula = resultSet.getInt("matricula");
        String nome = resultSet.getString("nome");
        String email = resultSet.getString("email");
        String senhaHash = resultSet.getString("senhaHash");
        Perfil perfil = Perfil.valueOf(resultSet.getString("perfil"));

        return new Usuario(userId, matricula, nome, email, senhaHash, perfil);
    }

}
