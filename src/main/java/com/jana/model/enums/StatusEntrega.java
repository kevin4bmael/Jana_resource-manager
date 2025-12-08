package com.jana.model.enums;

public enum StatusEntrega {
    ENTREGUE("Entregue"),
    AUSENTE("Ausente");

    private final String descricao;

    StatusEntrega(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}