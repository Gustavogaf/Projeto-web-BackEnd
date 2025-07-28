package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula") 
public class Atleta extends Usuario {

    private String apelido;

    @Column(nullable = false)
    private String telefone;

    @ManyToOne // Vários atletas pertencem a uma equipe
    @JsonBackReference
    private Equipe equipe;

    @ManyToOne // Vários atletas podem ser cadastrados por um técnico
    @JoinColumn(name = "tecnico_cadastro_matricula") // Nome da coluna no banco
    private Tecnico cadastradoPor;

    // Getters e Setters...
    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Tecnico getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(Tecnico cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }
}