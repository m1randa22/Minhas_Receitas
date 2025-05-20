package com.example.minhas_receitas;
// Receita.java
import java.util.List;

public class Receita {
    private int id;
    private String userEmail;
    private String titulo;
    private byte[] imagem;
    private List<String> ingredientes;
    private String modoPreparo;

    public Receita() {
    }

    public Receita(int id, String userEmail, String titulo, byte[] imagem, List<String> ingredientes, String modoPreparo) {
        this.id = id;
        this.userEmail = userEmail;
        this.titulo = titulo;
        this.imagem = imagem;
        this.ingredientes = ingredientes;
        this.modoPreparo = modoPreparo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }
}