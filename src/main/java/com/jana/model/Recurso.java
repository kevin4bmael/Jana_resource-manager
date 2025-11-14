package main.java.com.jana.model;

import main.java.com.jana.model.enums.Funcional;

public class Recurso {
    private int recursoId;
    private int userId; 
    private String codPatrimonio; 
    private String item; 
    private int numero;
    private Funcional funcional;
    private String observacao;

    public Recurso(int recursoId, int userId, String codPatrimonio, String item, int numero, Funcional funcional, String observacao) {
        this.recursoId = recursoId;
        this.userId = userId;
        this.codPatrimonio = codPatrimonio;
        this.item = item;
        this.numero = numero;
        this.funcional = funcional;
        this.observacao = observacao;
    }

    public Recurso(int userId, String codPatrimonio, String item, int numero, Funcional funcional, String observacao) {
        this.userId = userId;
        this.codPatrimonio = codPatrimonio;
        this.item = item;
        this.numero = numero;
        this.funcional = funcional;
        this.observacao = observacao;
    }

    public int getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(int recursoId) {
        this.recursoId = recursoId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCodPatrimonio() {
        return codPatrimonio;
    }

    public void setCodPatrimonio(String codPatrimonio) {
        this.codPatrimonio = codPatrimonio;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Funcional getFuncional() {
        return funcional;
    }

    public void setFuncional(Funcional funcional) {
        this.funcional = funcional;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}