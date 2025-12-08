package com.jana.model;


import com.jana.model.enums.Periodo;

import java.time.LocalDate;
import java.time.LocalTime;


public class Reserva {

    private Integer reservaId;
    private Integer userId;
    private Integer recursoId;
    private Integer localId;
    private LocalDate dataReservada;
    private LocalTime horaRetirada;
    private LocalTime horaEntrega;
    private String observacao;
    private Periodo periodo;

    public Reserva() {
    }



    public Reserva(Integer userId, Integer recursoId, Integer localId,
                   LocalDate dataReservada, String observacao, Periodo periodo,
                   LocalTime horaRetirada, LocalTime horaEntrega) {
        this.userId = userId;
        this.recursoId = recursoId;
        this.localId = localId;
        this.dataReservada = dataReservada;
        this.observacao = observacao;
        this.periodo = periodo;
        this.horaRetirada = horaRetirada;
        this.horaEntrega = horaEntrega;
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

    public Integer getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public LocalDate getDataReservada() {
        return dataReservada;
    }

    public void setDataReservada(LocalDate dataReservada) {
        this.dataReservada = dataReservada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public LocalTime getHoraRetirada() {
        return horaRetirada;
    }

    public void setHoraRetirada(LocalTime horaRetirada) {
        this.horaRetirada = horaRetirada;
    }

    public LocalTime getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(LocalTime horaEntrega) {
        this.horaEntrega = horaEntrega;
    }
}