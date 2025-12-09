package com.jana.dtos.registro;

import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

public record RegistroInsertDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        Integer movimentacaoId,
        String nome,
        String item,
        Integer numero,
        String ano,
        String turma,
        String periodo,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
