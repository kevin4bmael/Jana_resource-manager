package com.jana.dtos.registro;

import java.time.LocalDateTime;

public record RegistroPendenteDto(
        Integer registroId,
        String nomeUsuario,
        String nomeRecurso,
        int numeroRecurso,
        String turma,
        LocalDateTime momentoRetirada,
        String statusEntrega
) {}
