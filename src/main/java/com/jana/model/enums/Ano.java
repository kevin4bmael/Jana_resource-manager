package main.java.com.jana.model.enums;

public enum Ano {
    PRIMEIRO("1º"),
    SEGUNDO("2º"),
    TERCEIRO("3º"),
    QUARTO("4º"),
    QUINTO("5º"),
    SEXTO("6º"),
    SETIMO("7º"),
    OITAVO("8º"),
    NONO("9º");

    private final String valor;

    Ano(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return this.valor;
    }

    public static Ano fromString(String text) {
        if (text == null) return null;
        
        for (Ano a : Ano.values()) {
            if (a.valor.equalsIgnoreCase(text)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Valor de 'ano' inválido no banco: " + text);
    }
}