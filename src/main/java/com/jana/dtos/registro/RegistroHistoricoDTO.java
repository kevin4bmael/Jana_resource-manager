package main.java.com.jana.dtos.registro;

import java.time.LocalDateTime;

public record RegistroHistoricoDTO(
        int registroId,
        String nomeUsuario,
        String nomeRecurso,
        Integer numeroRecurso,
        String localCompleto,
        String periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        String statusEntrega
) {
}
