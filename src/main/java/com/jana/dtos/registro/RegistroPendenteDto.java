package com.jana.dtos.registro;

import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.Turma;

import java.time.LocalDateTime;

public record RegistroPendenteDto(
        Integer registroId,
        String nomeUsuario,
        String nomeRecurso,
        int numeroRecurso,
        Turma turma,
        LocalDateTime momentoRetirada,
        StatusEntrega statusEntrega
) {}
