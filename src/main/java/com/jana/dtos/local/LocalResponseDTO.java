package main.java.com.jana.dtos.local;

import main.java.com.jana.model.enums.Ano;
import main.java.com.jana.model.enums.TipoLocal;
import main.java.com.jana.model.enums.Turma;

public record LocalResponseDTO(
        int localId,
        int userId, 
        TipoLocal local,
        Ano ano,
        Turma turma
) {}