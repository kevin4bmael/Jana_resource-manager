package main.java.com.jana.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.usuario.UsuarioLoginDTO;
import main.java.com.jana.dtos.usuario.UsuarioRegisterDTO;
import main.java.com.jana.exceptions.usuario.EmailJaExisteException;
import main.java.com.jana.model.Usuario;

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

    public boolean login(UsuarioLoginDTO dto) throws SQLException {
        Usuario usuario = usuarioDAO.findByEmail(dto.email());
        if (usuario == null) return false;

        return BCrypt.verifyer().verify(dto.senha().toCharArray(), usuario.getSenhaHash()).verified;
    }
}
