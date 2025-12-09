package com.jana.model;


import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

import java.time.LocalDateTime;

public class Registro {

    private Integer registroId;
    private Integer reservaId;
    private Integer userId;
    private Integer resourceId;
    private Integer localId;
    private Integer movimentacaoId;
    private String nome;
    private String item;
    private Integer numero;
    private String ano;
    private String turma;
    private String periodo;

    private LocalDateTime momentoRetirada;
    private LocalDateTime momentoDevolucao;

    private StatusRecurso statusRecurso;
    private StatusEntrega statusEntrega;

    public Registro(Integer registroId, Integer reservaId, Integer userId, Integer resourceId, Integer localId, Integer movimentacaoId, String nome, String item, Integer numero, String ano, String turma, String periodo, LocalDateTime momentoRetirada, LocalDateTime momentoDevolucao, StatusRecurso statusRecurso, StatusEntrega statusEntrega) {
        this.registroId = registroId;
        this.reservaId = reservaId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.localId = localId;
        this.movimentacaoId = movimentacaoId;
        this.nome = nome;
        this.item = item;
        this.numero = numero;
        this.ano = ano;
        this.turma = turma;
        this.periodo = periodo;
        this.momentoRetirada = momentoRetirada;
        this.momentoDevolucao = momentoDevolucao;
        this.statusRecurso = statusRecurso;
        this.statusEntrega = statusEntrega;
    }
    public Registro(){

    }
    public Integer getRegistroId() {
        return registroId;
    }

    public void setRegistroId(Integer registroId) {
        this.registroId = registroId;
    }

    public Integer getReservaId() {
        return reservaId;
    }

    public void setReservaId(Integer reservaId) {
        this.reservaId = reservaId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public Integer getMovimentacaoId() {
        return movimentacaoId;
    }

    public void setMovimentacaoId(Integer movimentacaoId) {
        this.movimentacaoId = movimentacaoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDateTime getMomentoRetirada() {
        return momentoRetirada;
    }

    public void setMomentoRetirada(LocalDateTime momentoRetirada) {
        this.momentoRetirada = momentoRetirada;
    }

    public LocalDateTime getMomentoDevolucao() {
        return momentoDevolucao;
    }

    public void setMomentoDevolucao(LocalDateTime momentoDevolucao) {
        this.momentoDevolucao = momentoDevolucao;
    }

    public StatusRecurso getStatusRecurso() {
        return statusRecurso;
    }

    public void setStatusRecurso(StatusRecurso statusRecurso) {
        this.statusRecurso = statusRecurso;
    }

    public StatusEntrega getStatusEntrega() {
        return statusEntrega;
    }

    public void setStatusEntrega(StatusEntrega statusEntrega) {
        this.statusEntrega = statusEntrega;
    }
}