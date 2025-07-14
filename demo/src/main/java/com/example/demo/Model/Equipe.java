// src/main/java/com/example/demo/Model/Equipe.java
package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Muitas equipes podem ser de um mesmo curso
    @JoinColumn(nullable = false)
    private Curso curso;

    @ManyToOne // Muitas equipes podem ser de um mesmo esporte
    @JoinColumn(nullable = false)
    private Esporte esporte;
    
    @OneToOne // Cada equipe tem um único técnico responsável
    @JoinColumn(nullable = false)
    private Tecnico tecnico;

    @OneToMany(mappedBy = "equipe") // Uma equipe tem muitos atletas
    private List<Atleta> atletas;

    // Construtores, Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Esporte getEsporte() {
        return esporte;
    }

    public void setEsporte(Esporte esporte) {
        this.esporte = esporte;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public List<Atleta> getAtletas() {
        return atletas;
    }

    public void setAtletas(List<Atleta> atletas) {
        this.atletas = atletas;
    }
}
