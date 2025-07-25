package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupo") 
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // Ex: "Grupo A", "Grupo B"

    @ManyToMany
    private List<Equipe> equipes = new ArrayList<>();

    @ManyToOne
    private Torneio torneio;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }
    public Torneio getTorneio() { return torneio; }
    public void setTorneio(Torneio torneio) { this.torneio = torneio; }
}

