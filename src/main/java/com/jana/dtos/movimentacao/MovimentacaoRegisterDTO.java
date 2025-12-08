package com.jana.dtos.movimentacao;

import com.jana.model.enums.Periodo;

public record MovimentacaoRegisterDTO(
        Integer recursoId,
        Periodo periodo
) {}