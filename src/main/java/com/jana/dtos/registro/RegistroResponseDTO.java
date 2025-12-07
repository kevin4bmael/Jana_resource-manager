package main.java.com.jana.dtos.registro;

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
        String ano,
        String turma,
        String periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        String statusRecurso,
        String statusEntrega
) {}
