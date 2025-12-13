package com.jana.dtos.movimentacao;

import com.jana.model.enums.Periodo;

public record MovimentacaoInsertDTO(
        Integer userId,
        Integer recursoId,
        Periodo periodo
) {}
