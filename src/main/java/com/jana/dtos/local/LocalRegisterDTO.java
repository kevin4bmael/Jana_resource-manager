package com.jana.dtos.local;


import com.jana.model.enums.Ano;
import com.jana.model.enums.TipoLocal;
import com.jana.model.enums.Turma;

public record LocalRegisterDTO(
        TipoLocal local,
        Ano ano,
        Turma turma
) {}