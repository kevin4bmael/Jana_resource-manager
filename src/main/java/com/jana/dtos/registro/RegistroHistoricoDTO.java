package com.jana.dtos.registro;

import com.jana.model.enums.Periodo;
import com.jana.model.enums.StatusEntrega;

import java.time.LocalDateTime;

public record RegistroHistoricoDTO(
        Integer registroId,
        String nomeUsuario,
        String nomeRecurso,
        Integer numeroRecurso,
        String localCompleto,
        Periodo periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        StatusEntrega statusEntrega
) {
}
