package main.java.com.jana.model;

import main.java.com.jana.model.enums.Periodo;
import java.sql.Date;
import java.sql.Time;

public class Reserva {

    private Integer reservaId;
    private Integer userId;
    private Integer recursoId;
    private Integer localId;

    private Date dataReservada; // java.sql.Date
    private String observacao;
    private Periodo periodo; // Enum: Manhã, Tarde
    private Time horaRetirada; // java.sql.Time
    private Time horaEntrega; // java.sql.Time (pode ser NULL)

    public Reserva(Integer reservaId, Integer userId, Integer recursoId, Integer localId, Date dataReservada, String observacao, Periodo periodo, Time horaRetirada, Time horaEntrega) {
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


    public Reserva(Integer userId, Integer recursoId, Integer localId, Date dataReservada, String observacao, Periodo periodo, Time horaRetirada, Time horaEntrega) {
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

    public Integer getUserId() {
        return userId;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public Integer getLocalId() {
        return localId;
    }

    public Date getDataReservada() {
        return dataReservada;
    }

    public String getObservacao() {
        return observacao;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public Time getHoraRetirada() {
        return horaRetirada;
    }

    public Time getHoraEntrega() {
        return horaEntrega;
    }

    public void setReservaId(Integer reservaId) {
        this.reservaId = reservaId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setRecursoId(Integer recursoId) {
        this.recursoId = recursoId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public void setDataReservada(Date dataReservada) {
        this.dataReservada = dataReservada;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public void setHoraRetirada(Time horaRetirada) {
        this.horaRetirada = horaRetirada;
    }

    public void setHoraEntrega(Time horaEntrega) {
        this.horaEntrega = horaEntrega;
    }
}