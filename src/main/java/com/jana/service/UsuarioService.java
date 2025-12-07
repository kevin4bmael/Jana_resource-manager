package main.java.com.jana.service;


import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.dtos.usuario.UsuarioUpdateDTO;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioResponseDTO getUsuario(Integer id) throws SQLException {
        Usuario usuario = findUserOrThrow(id);
            return new UsuarioResponseDTO(
                    usuario.getUserId(),
                    usuario.getMatricula(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil()
            );
    }

    public List<UsuarioResponseDTO> getAllUsuarios() throws SQLException {
        return usuarioDAO.findAll().stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getUserId(),
                        usuario.getMatricula(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPerfil()))
                .toList();
    }

    public void updateUsuario(Integer id, UsuarioUpdateDTO usuarioUpdateDTO) throws SQLException {
        Usuario usuario = findUserOrThrow(id);
        if (usuarioUpdateDTO.nome() != null ) usuario.setNome(usuarioUpdateDTO.nome());
        if (usuarioUpdateDTO.email() != null) usuario.setEmail(usuarioUpdateDTO.email());
        if (usuarioUpdateDTO.perfil() != null) usuario.setPerfil(usuarioUpdateDTO.perfil());

        usuarioDAO.updateUsuario(id,usuario);
    }

    public void deleteUsuario(Integer id) throws SQLException {
        Usuario usuario = findUserOrThrow(id);
        usuarioDAO.delete(usuario);
    }
    private Usuario findUserOrThrow(Integer id) throws SQLException {
       Usuario usuario = usuarioDAO.findUsuarioById(id);
       if(usuario==null){
           throw new UsuarioNaoEncontradoException("Usuario com id: " + id + "não encontrado!");
       }
       return usuario;
    }
}
