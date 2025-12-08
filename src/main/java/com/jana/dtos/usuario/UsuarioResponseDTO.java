package com.jana.dtos.usuario;


import com.jana.model.enums.Perfil;

public record UsuarioResponseDTO(
        Integer id,
        int matricula,
        String nome,
        String email,
        Perfil perfil
) {}