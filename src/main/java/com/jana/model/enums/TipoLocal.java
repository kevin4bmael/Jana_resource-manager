package com.jana.model.enums;

public enum TipoLocal {
    SALA_DE_AULA("Sala de Aula"),
    AUDITORIO("Auditório"),
    PATIO("Patio"),
    BIBLIOTECA("Biblioteca"),
    QUADRA("Quadra"),
    REFEITORIO("Refeitório"),
    SECRETARIA("Secretaria"),
    LABORATORIO("Laboratório"),
    INFORMATICA("Informática"),
    OUTRO("Outro");

    private final String valor;

    TipoLocal(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return this.valor;
    }

    public static TipoLocal fromString(String text) {
        if (text == null) return null;
        
        for (TipoLocal tl : TipoLocal.values()) {
            if (tl.valor.equalsIgnoreCase(text)) {
                return tl;
            }
        }
        throw new IllegalArgumentException("Valor de 'local' inválido no banco: " + text);
    }
}