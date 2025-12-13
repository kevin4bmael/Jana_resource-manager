package com.jana.dtos.movimentacao;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

public record MovimentacaoUpdateDTO(
        Integer movimentacaoId,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
