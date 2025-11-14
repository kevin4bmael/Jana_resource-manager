package main.java.com.jana.dtos.recurso;

import main.java.com.jana.model.enums.Funcional;

public record RecursoUpdateDTO(
        String codPatrimonio,
        String item,
        Integer numero, 
        Funcional funcional,
        String observacao
) {}