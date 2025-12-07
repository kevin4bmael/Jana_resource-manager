package main.java.com.jana.model;

import main.java.com.jana.model.enums.Ano;
import main.java.com.jana.model.enums.TipoLocal;
import main.java.com.jana.model.enums.Turma;

public class Local {
    private int localId;
    private int userId; 
    private TipoLocal local; 
    private Ano ano; 
    private Turma turma; 

    public Local(int localId, int userId, TipoLocal local, Ano ano, Turma turma) {
        this.localId = localId;
        this.userId = userId;
        this.local = local;
        this.ano = ano;
        this.turma = turma;
    }

    public Local(int userId, TipoLocal local, Ano ano, Turma turma) {
        this.userId = userId;
        this.local = local;
        this.ano = ano;
        this.turma = turma;
    }
    public Local(){

    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public TipoLocal getLocal() {
        return local;
    }

    public void setLocal(TipoLocal local) {
        this.local = local;
    }

    public Ano getAno() {
        return ano;
    }

    public void setAno(Ano ano) {
        this.ano = ano;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }
}