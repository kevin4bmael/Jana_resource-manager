package com.jana.model.enums;

public enum StatusRecurso {
    DISPONIVEL("Disponível"),
    OCUPADO("Ocupado"),
    RESERVADO("Reservado");

    private final String descricao;

    StatusRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
