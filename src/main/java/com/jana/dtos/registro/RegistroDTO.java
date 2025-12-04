package main.java.com.jana.dtos.registro;

import java.util.Date;

public class RegistroDTO {

    private int registroId;
    private int reserveId;
    private int userId;
    private int resourceId;
    private Date dataRetirada;
    private Date dataDevolucao;

    private String statusMovimentacao; 

    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    public int getReserveId() {
        return reserveId;
    }

    public void setReserveId(int reserveId) {
        this.reserveId = reserveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Date getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(Date dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatusMovimentacao() {
        return statusMovimentacao;
    }

    public void setStatusMovimentacao(String statusMovimentacao) {
        this.statusMovimentacao = statusMovimentacao;
    }
}
