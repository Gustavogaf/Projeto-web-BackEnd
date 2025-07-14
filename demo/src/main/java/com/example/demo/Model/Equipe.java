// src/main/java/com/example/demo/Model/Equipe.java
package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Adicione este campo se ele não existir
    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Esporte esporte;

    @OneToOne
    @JoinColumn(nullable = false)
    private Tecnico tecnico;

    @OneToMany(mappedBy = "equipe")
    private List<Atleta> atletas = new ArrayList<>();

    // --- GARANTA QUE OS GETTERS E SETTERS ESTÃO COMPLETOS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    // Certifique-se de que este método está implementado corretamente
    public void setNome(String nome) {
        this.nome = nome;
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