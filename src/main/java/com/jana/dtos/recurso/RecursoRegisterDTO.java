package com.jana.dtos.recurso;


import com.jana.model.enums.Funcional;

public record RecursoRegisterDTO(
        String codPatrimonio,
        String item,
        Integer numero,
        Funcional funcional,
        String observacao
) {}