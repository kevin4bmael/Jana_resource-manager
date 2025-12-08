package main.java.com.jana.dtos.movimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Integer movimentacaoId,
        Integer userId,
        String nomeUsuario,
        Integer recursoId,
        String nomeRecurso,
        String periodo,
        LocalDateTime momentoRetirada,
        LocalDateTime momentoDevolucao,
        String statusRecurso,
        String statusEntrega
) {}
