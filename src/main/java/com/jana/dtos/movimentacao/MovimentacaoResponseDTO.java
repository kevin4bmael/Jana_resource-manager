package com.jana.dtos.movimentacao;

import com.jana.model.enums.Periodo;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Integer movimentacaoId,
        Integer userId,
        Integer recursoId,
        Periodo periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
