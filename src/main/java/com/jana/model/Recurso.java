package com.jana.model;


import com.jana.model.enums.Funcional;

public class Recurso {
    private Integer recursoId;
    private Integer userId;
    private String codPatrimonio;
    private String item;
    private Integer numero;
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

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
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