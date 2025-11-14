package main.java.com.jana.dtos.recurso;

import main.java.com.jana.model.enums.Funcional;

public record RecursoRegisterDTO(
        String codPatrimonio,
        String item,
        int numero,
        Funcional funcional,
        String observacao
) {}