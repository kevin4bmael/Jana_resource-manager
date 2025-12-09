package com.jana.dtos.registro;

import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

import java.time.LocalDateTime;

public record RegistroResponseDTO(
        Integer registroId,
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
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
