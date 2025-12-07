package main.java.com.jana.model;

import main.java.com.jana.model.enums.Perfil;

public class Usuario {
    private int userId;
    private int matricula;
    private String nome;
    private String email;
    private String senhaHash;
    private Perfil perfil;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Usuario(int userId, int matricula, String nome, String email, String senhaHash, Perfil perfil) {
        this.userId = userId;
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }
    public Usuario(int matricula, String nome, String email, String senhaHash, Perfil perfil) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }
    public Usuario() {}

}
