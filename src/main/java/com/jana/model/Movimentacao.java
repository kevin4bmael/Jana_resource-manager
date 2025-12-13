package com.jana.model;

import com.jana.model.enums.Periodo;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

import java.time.LocalDateTime;

public class Movimentacao {
    private Integer movimentacaoId;
    private Integer userId;
    private Integer recursoId;
    private Periodo periodo;
    private LocalDateTime momentoRetirada;
    private LocalDateTime momentoDevolucao;
    private StatusRecurso statusRecurso;
    private StatusEntrega statusEntrega;


    public Movimentacao() {
    }

    public Movimentacao(Integer movimentacaoId, Integer userId, Integer recursoId,
                        Periodo periodo, LocalDateTime momentoRetirada,
                        LocalDateTime momentoDevolucao, StatusRecurso statusRecurso,
                        StatusEntrega statusEntrega) {
        this.movimentacaoId = movimentacaoId;
        this.userId = userId;
        this.recursoId = recursoId;
        this.periodo = periodo;
        this.momentoRetirada = momentoRetirada;
        this.momentoDevolucao = momentoDevolucao;
        this.statusRecurso = statusRecurso;
        this.statusEntrega = statusEntrega;
    }
    public Movimentacao(Integer userId, Integer recursoId, Periodo periodo,
                        StatusRecurso statusRecurso, StatusEntrega statusEntrega) {
        this.userId = userId;
        this.recursoId = recursoId;
        this.periodo = periodo;
        this.statusRecurso = statusRecurso;
        this.statusEntrega = statusEntrega;
    }
    // Getters e Setters
    public Integer getMovimentacaoId() {
        return movimentacaoId;
    }

    public void setMovimentacaoId(Integer movimentacaoId) {
        this.movimentacaoId = movimentacaoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
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
