package com.jana.dtos.usuario;


import com.jana.model.enums.Perfil;

public record UsuarioRegisterDTO(
        int matricula,
        String nome,
        String email,
        String senha,
        Perfil perfil
) {}