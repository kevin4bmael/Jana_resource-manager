package main.java.com.jana.dtos.movimentacao;

import main.java.com.jana.model.enums.StatusEntrega;
import main.java.com.jana.model.enums.StatusRecurso;
import java.time.LocalDateTime;

public record MovimentacaoUpdateDTO(
        StatusRecurso statusRecurso,
        StatusEntrega statusEntrega,
        LocalDateTime momentoDevolucao // Pode ser nulo se não for devolução
) {}
