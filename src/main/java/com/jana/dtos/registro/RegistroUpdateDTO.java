package com.jana.dtos.registro;

import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

import java.time.LocalDateTime;

public record RegistroUpdateDTO(
        Integer registroId,
        LocalDateTime momentoDevolucao,
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega
) {}