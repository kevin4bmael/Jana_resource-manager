package com.jana.dtos.registro;

import com.jana.model.enums.StatusEntrega;

import java.time.LocalDateTime;

public record RegistroPendenteDto(
        Integer registroId,
        String nomeUsuario,
        String nomeRecurso,
        int numeroRecurso,
        String turma,
        LocalDateTime momentoRetirada,
        StatusEntrega statusEntrega
) {}
