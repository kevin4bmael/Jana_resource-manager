package main.java.com.jana.dtos.recurso;

import main.java.com.jana.model.enums.Funcional;

public record RecursoResponseDTO(
        int recursoId,
        int userId, 
        String codPatrimonio,
        String item,
        Integer numero,
        Funcional funcional,
        String observacao
) {
}