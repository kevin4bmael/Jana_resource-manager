package com.jana.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.dtos.usuario.UsuarioUpdateDTO;
import com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import com.jana.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioResponseDTO getUsuario(Integer id) throws SQLException {
        Usuario usuario = usuarioDAO.findUsuarioById(id);
        return toResponseDTO(usuario);
    }

    public List<UsuarioResponseDTO> getAllUsuarios() throws SQLException {
        return usuarioDAO.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public void updateUsuario(Integer id, UsuarioUpdateDTO usuarioUpdateDTO) throws SQLException {
        Usuario usuario = usuarioDAO.findUsuarioById(id);

        if (usuarioUpdateDTO.nome() != null) usuario.setNome(usuarioUpdateDTO.nome());
        if (usuarioUpdateDTO.email() != null) usuario.setEmail(usuarioUpdateDTO.email());

        if (usuarioUpdateDTO.senha() != null) {
            String hashSenha = BCrypt.withDefaults().hashToString(12, usuarioUpdateDTO.senha().toCharArray());
            usuario.setSenha(hashSenha);
        }

        usuarioDAO.updateUsuario(usuario);
    }

    public UsuarioResponseDTO findByEmail(String email) throws SQLException {
        Usuario usuario = usuarioDAO.findByEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado com email: " + email);
        }

        return toResponseDTO(usuario);
    }

    public void deleteUsuario(Integer id) throws SQLException {
        usuarioDAO.deleteById(id);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUserId(),
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}