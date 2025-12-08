package com.jana.dtos.usuario;


public record UsuarioRegisterDTO(
        int matricula,
        String nome,
        String email,
        String senha
) {}