package com.jana.security;


import com.jana.dao.UsuarioDAO;
import com.jana.dtos.usuario.UsuarioLoginDTO;
import com.jana.dtos.usuario.UsuarioRegisterDTO;
import com.jana.exceptions.usuario.EmailJaExisteException;
import com.jana.model.Usuario;

import java.sql.SQLException;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void register(UsuarioRegisterDTO dto) throws SQLException {
        if (usuarioDAO.existsByEmail(dto.email())) {
            throw new EmailJaExisteException("O email: " + dto.email() + " já existe!");
        }

        String senhaHash = BCrypt.withDefaults().hashToString(12, dto.senha().toCharArray());
        Usuario usuario = new Usuario(
                dto.matricula(),
                dto.nome(),
                dto.email(),
                senhaHash,
                dto.perfil()
        );

        usuarioDAO.saveUsuario(usuario);
    }

    public Usuario login(UsuarioLoginDTO dto) throws SQLException {
        Usuario usuario = usuarioDAO.findByEmail(dto.email());

        if (usuario == null) {
            return null;
        }


        boolean senhaCorreta = BCrypt.verifyer().verify(dto.senha().toCharArray(), usuario.getSenhaHash()).verified;

        if (senhaCorreta) {
            return usuario;
        }

        return null;
    }
}
