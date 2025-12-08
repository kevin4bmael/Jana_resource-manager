package com.jana.model.enums;

public enum Periodo {
    MANHA("Manhã"),
    TARDE("Tarde");

    private final String descricao;

    Periodo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}