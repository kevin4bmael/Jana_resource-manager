package com.jana.dtos.registro;

import java.time.LocalDateTime;

public record RegistroUpdateDTO(
        Integer registroId,
        LocalDateTime momentoDevolucao,
        String statusRecurso,
        String statusEntrega
) {}