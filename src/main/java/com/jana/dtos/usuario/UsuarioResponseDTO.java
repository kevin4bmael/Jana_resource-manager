package main.java.com.jana.dtos.usuario;

import main.java.com.jana.model.enums.Perfil;

public record UsuarioResponseDTO(
        Integer id,
        int matricula,
        String nome,
        String email,
        Perfil perfil
) {}