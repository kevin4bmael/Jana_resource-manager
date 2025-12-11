package com.jana.dtos.registro;

import com.jana.model.enums.*;

public record RegistroInsertDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        Integer movimentacaoId,
        String nome,
        String item,
        Integer numero,
        Ano ano,
        Turma turma,
        Periodo periodo,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
