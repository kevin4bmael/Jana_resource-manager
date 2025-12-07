package main.java.com.jana.dtos.registro;

import java.time.LocalDateTime;

public record RegistroUpdateDTO(
        int registroId,
        LocalDateTime momentoDevolucao,
        String statusRecurso,
        String statusEntrega
) {}