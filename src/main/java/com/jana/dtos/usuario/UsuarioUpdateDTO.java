package com.jana.dtos.usuario;


public record UsuarioUpdateDTO(
        String nome,
        String email,
        String senha
) {}