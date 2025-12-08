package main.java.com.jana.dtos.movimentacao;

import main.java.com.jana.model.enums.Periodo;

public record MovimentacaoRegisterDTO(
        Integer recursoId,
        Periodo periodo
) {}