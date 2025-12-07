package main.java.com.jana.dtos.registro;

import java.time.LocalDateTime;

public record RegistroResponseDTO(
        int registroId,
        Integer reservaId,
        int userId,
        int recursoId,
        int localId,
        int movimentacaoId,
        String nome,
        String item,
        Integer numero,
        String ano,
        String turma,
        String periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        String statusRecurso,
        String statusEntrega
) {}
