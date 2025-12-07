package main.java.com.jana.model;


import java.time.LocalDateTime;

public class Registro {

    private int registroId;
    private Integer reservaId;
    private int userId;
    private int resourceId;
    private int localId;
    private int movimentacaoId;
    private String nome;
    private String item;
    private Integer numero;
    private String ano;
    private String turma;
    private String periodo;

    private LocalDateTime momentoRetirada;
    private LocalDateTime momentoDevolucao;

    private String statusRecurso;
    private String statusEntrega;

    public Registro(int registroId, Integer reservaId, int userId, int resourceId, int localId, int movimentacaoId, String nome, String item, Integer numero, String ano, String turma, String periodo, LocalDateTime momentoRetirada, LocalDateTime momentoDevolucao, String statusRecurso, String statusEntrega) {
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

    public Registro() {
    }

    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    public Integer getReservaId() {
        return reservaId;
    }

    public void setReservaId(Integer reservaId) {
        this.reservaId = reservaId;
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

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getMovimentacaoId() {
        return movimentacaoId;
    }

    public void setMovimentacaoId(int movimentacaoId) {
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

    public String getStatusRecurso() {
        return statusRecurso;
    }

    public void setStatusRecurso(String statusRecurso) {
        this.statusRecurso = statusRecurso;
    }

    public String getStatusEntrega() {
        return statusEntrega;
    }

    public void setStatusEntrega(String statusEntrega) {
        this.statusEntrega = statusEntrega;
    }
}