package com.jana.dtos.registro;

import com.jana.model.enums.*;

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
        Ano ano,
        Turma turma,
        Periodo periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}
