package main.java.com.jana.model;

import main.java.com.jana.model.enums.Periodo;

import java.sql.Time;
import java.time.LocalTime; // Substituindo java.sql.Time
import java.util.Date;

public class Reserva {

    private Integer reservaId;
    private Integer userId;
    private Integer recursoId;
    private Integer localId;

    private Date dataReservada;
    private LocalTime horaRetirada;
    private Time horaEntrega;

    private String observacao;
    private Periodo periodo;

    public Reserva() {
    }

    public Reserva(Integer reservaId, Integer userId, Integer recursoId, Integer localId,
                   Date dataReservada, String observacao, Periodo periodo,
                   LocalTime horaRetirada, Time horaEntrega) {
        this.reservaId = reservaId;
        this.userId = userId;
        this.recursoId = recursoId;
        this.localId = localId;
        this.dataReservada = dataReservada;
        this.observacao = observacao;
        this.periodo = periodo;
        this.horaRetirada = horaRetirada;
        this.horaEntrega = horaEntrega;
    }

    public Reserva(Integer userId, Integer recursoId, Integer localId,
                   Date dataReservada, String observacao, Periodo periodo,
                   LocalTime horaRetirada, Time horaEntrega) {
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

    public Date getDataReservada() {
        return dataReservada;
    }

    public void setDataReservada(Date dataReservada) {
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

    public Time getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(Time horaEntrega) {
        this.horaEntrega = horaEntrega;
    }
}