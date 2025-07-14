// src/main/java/com/example/demo/Model/Atleta.java
package com.example.demo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula") // <-- ALTERE DE "usuario_matricula" PARA "matricula"
public class Atleta extends Usuario {

    private String apelido;

    @Column(nullable = false)
    private String telefone;

    @ManyToOne // Vários atletas pertencem a uma equipe
    private Equipe equipe;

    // Getters e Setters...
    // (O resto da classe continua igual)
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
}